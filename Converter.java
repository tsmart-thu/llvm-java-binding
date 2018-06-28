/*
 * Copyright (c) 2017
 * ------------------
 * Institute on Software System and Engineering
 * School of Software, Tsinghua University
 *
 * All Rights Reserved.
 *
 * NOTICE:
 * All information contained herein is, and remains the property of Tsinghua University.
 *
 * The intellectual and technical concepts contained herein are proprietary to
 * Tsinghua University and may be covered by China and Foreign Patents, patents in process,
 * and are protected by copyright law.
 *
 * Dissemination of this information or reproduction of this material is strictly forbidden
 * unless prior written permission is obtained from Tsinghua University.
 *
 */
package cn.edu.thu.tsmart.core.cfa.llvm;

import cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OperatorFlags;
import cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.Predicate;
import cn.edu.thu.tsmart.core.cfa.util.Casting;
import cn.edu.thu.tsmart.util.Trouble;
import cn.edu.thu.tsmart.util.globalinfo.GlobalInfo;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.SizeTPointer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import static cn.edu.thu.tsmart.util.lang.LlvmUtil.determineCalledFunctionName;
import static org.bytedeco.javacpp.LLVM.*;

/** @author guangchen on 03/03/2017. */
public class Converter {

  private final Context context;
  private LLVMTargetDataRef targetDataRef;
  private int unnamedIndex = 0;

  public Converter(Context context) {
    this.context = context;
  }

  public LlvmModule convert(LLVMModuleRef moduleRef) {
    //get filename
    File file = new File(GlobalInfo.getInstance().getIoManager().getProgramNames());
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(file));
      String line = null;
      while((line = reader.readLine()) != null) {
        if(line.length() < 2)
          continue;
        if(line.charAt(0) == '!' && line.charAt(1) >= '0' && line.charAt(1) <= '9') {
          String[] s = line.split(" = |\\(|\\)");
          if(s.length < 2)
            continue;
          if(s[1].equals("!DIFile")) {
            String[] ss = (s[2]).split(", |: ");
            int fileNum = Integer.parseInt(s[0].replace("!", ""));
            context.putFilename(fileNum, ss[1].replace("\"", ""));
          } else if(s[1].contains("!DIGlobalVariable")) {
            String[] ss = (s[2]).split(", |: ");
            Metadata m = new Metadata();
            m.setFile(context.getFilename(Integer.valueOf(ss[5].replace("!", ""))));
            m.setLine(Integer.valueOf(ss[7]));
            context.putGlobalVariableMetadata(ss[1].replace("\"", ""), m);
          } else if(s[1].contains("!DILexicalBlock")) {
            String[] ss = (s[2]).split(", |: ");
            int fileNum = Integer.parseInt(ss[3].replace("!", ""));
            int myNum = Integer.parseInt(s[0].replace("!", ""));
            String fileName = context.getFilename(fileNum);
            context.putFilename(myNum, fileName);
          } else if(s[1].contains("!DISubprogram")) {
            String[] ss = (s[2]).split(", |: ");
            String functionName = ss[1].replace("\"", "");
            int myLine = Integer.parseInt(ss[7]);
            int fileNum = Integer.parseInt(ss[5].replace("!", ""));
            int myNum = Integer.parseInt(s[0].replace("!", ""));
            String fileName = context.getFilename(fileNum);
            context.putFilename(myNum, fileName);
            context.putFunctionFilename(functionName, fileName);
            context.putFunctionLine(functionName, myLine);
          }
        }
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // DataLayout
    targetDataRef = LLVMGetModuleDataLayout(moduleRef);
    SizeTPointer sizeTPointer = new SizeTPointer(64);
    String moduleIdentifier = LLVMGetModuleIdentifier(moduleRef, sizeTPointer).getString();
    // Important make function order natural
    Map<String, LlvmFunction> functionMap = new TreeMap<>();
    // create globals
    List<GlobalVariable> globalList = getGlobalVariables(moduleRef);
    // first create
    for (LLVMValueRef f = LLVMGetFirstFunction(moduleRef); f != null; f = LLVMGetNextFunction(f)) {
      LlvmFunction func = new LlvmFunction();
      context.putFunction(f, func);
    }
    for (Map.Entry<LLVMValueRef, LlvmFunction> pair : context.getFunctionMap().entrySet()) {
      LlvmFunction value = pair.getValue();
      convertValueToFunction(pair.getKey(), value);
      functionMap.put(value.getName(), value);
    }
    LlvmModule llvmModule = new LlvmModule(context, moduleIdentifier, functionMap, globalList);
    postProcess(llvmModule);
    return llvmModule;
  }

  public List<GlobalVariable> getGlobalVariables(LLVMModuleRef moduleRef) {
    List<GlobalVariable> globalList = new ArrayList<>();
    for (LLVMValueRef g = LLVMGetFirstGlobal(moduleRef); g != null; g = LLVMGetNextGlobal(g)) {
      String name = LLVMGetValueName(g).getString();
      Type type = getType(LLVMTypeOf(g));
      Constant init = Casting.castOrNull(convert(LLVMGetInitializer(g), null), Constant.class);
      Metadata m = context.getGlobalVariableMetadata(name);
      GlobalVariable variable = new GlobalVariable(name, type, init, m);
      context.putGlobalVariable(g, variable);
      globalList.add(variable);
    }
    return globalList;
  }

  private void postProcess(LlvmModule llvmModule) {
    for (LlvmFunction function : llvmModule.functions()) {
      Metadata fMd = new Metadata();
      fMd.setFile(context.getFunctionFilename(function.getName()));
      fMd.setLine(context.getFunctionLine(function.getName()));
      function.setMetadata(fMd);
      for (BasicBlock basicBlock : function.getBasicBlockList()) {
        for (Instruction instruction : basicBlock.getInstList()) {
          if (instruction.getOpcode() == OpCode.CALL) {
            CallInst callInst = (CallInst)instruction;
            String calledFunctionName = determineCalledFunctionName(callInst);
            if (calledFunctionName == null) {
              continue;
            }else if (calledFunctionName.equals("llvm.dbg.declare")) {
              LLVMValueRef valueRef = llvmModule.getContext().getLLVMValueRefByInst(instruction);
              if (instruction.getNumOperands() < 2)
                continue;
              String variableName = "";
              if (instruction.getOperand(0) != null)
                variableName = instruction.getOperand(0).getName();
              Instruction variableInst = llvmModule.getContext().getInstByName(variableName);
              if (variableInst == null)
                continue;
              LLVMValueRef dbg = LLVMGetMetadata(valueRef, LLVMGetMDKindID("dbg", "dbg".length()));
              LLVMValueRef dbg2 = LLVMGetMetadata(valueRef, LLVMGetMDKindID("dbg", "dbg".length()));
              LLVMGetMDNodeOperands(dbg, dbg);
              int numOperands;
              while(true) {
                numOperands = 0;
                numOperands = LLVMGetNumOperands(dbg);
                if (numOperands <= 0 || numOperands > 1000) {
                  break;
                }
                dbg = LLVMGetOperand(dbg, 0);
              }
              BytePointer bp = LLVMPrintValueToString(dbg2);
              String fileName = LLVMPrintValueToString(dbg).getString();
              fileName = fileName.replace("\"", "").replace("!", "");
              String ot = bp.getString().trim();
              String[] s = ot.split(" = |\\(|\\)");
              Metadata md = new Metadata();
              if(s[1].equals("!DILocation")) {
                md.setFile(fileName);
                String[] ss = (s[2]).split(", |: ");
                for(int i = 0; i < ss.length; i = i + 2) {
                  if(ss[i].equals("line")) {
                    md.setLine(Integer.parseInt(ss[i + 1]));
                  } else if(ss[i].equals("column")) {
                    md.setColumn(Integer.parseInt(ss[i + 1]));
                  }
                }
              }
              variableInst.setMetadata(md);
            }
          }
        }
      }
    }
  }

  private void convertValueToFunction(LLVMValueRef key, LlvmFunction value) {
    // reset counter
    this.unnamedIndex = 0;
    // set name
    value.setName(LLVMGetValueName(key).getString());
    // set type
    value.setType(getType(LLVMTypeOf(key)));
    // set argument
    List<Argument> argumentList = new ArrayList<>();
    for (LLVMValueRef arg = LLVMGetFirstParam(key); arg != null; arg = LLVMGetNextParam(arg)) {
      Argument convArg = convertValueToArgument(arg);
      convArg.setFunction(value);
      argumentList.add(convArg);
    }
    value.setArgumentList(argumentList);
    // set basicBlockList
    // first create
    List<BasicBlock> basicBlockList = new ArrayList<>();
    List<LLVMBasicBlockRef> basicBlockRefs = new ArrayList<>();
    for (LLVMBasicBlockRef bb = LLVMGetFirstBasicBlock(key);
        bb != null;
        bb = LLVMGetNextBasicBlock(bb)) {
      BasicBlock block = new BasicBlock();
      basicBlockList.add(block);
      block.setParent(value);
      basicBlockRefs.add(bb);
      context.putBasicBlock(bb, block);
    }
    // convert
    for (int i = 0; i < basicBlockList.size(); i++) {
      LLVMBasicBlockRef ref = basicBlockRefs.get(i);
      BasicBlock block = basicBlockList.get(i);
      convertValueToBasicBlock(ref, block);
    }
    // set use
    for (int i = 0; i < basicBlockList.size(); i++) {
      LLVMBasicBlockRef ref = basicBlockRefs.get(i);
      for (LLVMValueRef inst = LLVMGetFirstInstruction(ref);
          inst != null;
          inst = LLVMGetNextInstruction(inst)) {
        Instruction instruction = context.getInst(inst);
        List<Use> uses = new ArrayList<>();
        int j = 0;
        for (LLVMUseRef useRef = LLVMGetFirstUse(inst);
            useRef != null;
            useRef = LLVMGetNextUse(useRef)) {
          LLVMValueRef userRef = LLVMGetUser(useRef);
          uses.add(new Use(instruction, context.getInst(userRef), j));
          j++;
        }
        instruction.setUses(uses);
      }
    }
    // set
    value.setBasicBlockList(basicBlockList);
  }

  private void convertValueToBasicBlock(LLVMBasicBlockRef ref, BasicBlock block) {
    String name = LLVMGetValueName(LLVMBasicBlockAsValue(ref)).getString();
    if ("".equals(name)) {
      name = "" + unnamedIndex;
      unnamedIndex++;
    }
    block.setName(name);
    block.setType(getType(LLVMTypeOf(LLVMBasicBlockAsValue(ref))));
    List<Instruction> instructionList = new ArrayList<>();
    for (LLVMValueRef inst = LLVMGetFirstInstruction(ref);
        inst != null;
        inst = LLVMGetNextInstruction(inst)) {
      Instruction instruction = convertValueToInstruction(block, inst);
      instructionList.add(instruction);
      instruction.setParent(block);
      context.putInst(inst, instruction);
    }
    block.setInstList(instructionList);
  }

  private Instruction convertValueToInstruction(BasicBlock parent, LLVMValueRef inst) {
    Instruction instruction = context.getInst(inst);
    if (instruction != null) {
      return instruction;
    }
    //BytePointer bytePointer = LLVMPrintValueToString(inst);
    //String originalText = bytePointer.getString().trim();
    //LLVMDisposeMessage(bytePointer);
    int opcode = LLVMGetInstructionOpcode(inst);
    String name = LLVMGetValueName(inst).getString();
    /*if ("".equals(name)) {
      if (needName(LLVMPrintValueToString(inst).getString().trim())) {
        name = "" + unnamedIndex;
        unnamedIndex++;
      }
    }*/

    Type type = getType(LLVMTypeOf(inst));
    switch (opcode) {
      case LLVMRet:
        instruction = new ReturnInst(name, type);
        break;
      case LLVMBr:
        instruction = new BranchInst(name, type);
        break;
      case LLVMSwitch:
        instruction = new SwitchInst(name, type);
        break;
      case LLVMIndirectBr:
        instruction = new IndirectBrInst(name, type);
        break;
      case LLVMInvoke:
        instruction = new InvokeInst(name, type);
        break;
      case LLVMResume:
        instruction = new ResumeInst(name, type);
        break;
      case LLVMUnreachable:
        instruction = new UnreachableInst(name, type);
        break;
      case LLVMCleanupRet:
        instruction = new CleanupReturnInst(name, type);
        break;
      case LLVMCatchRet:
        instruction = new CatchReturnInst(name, type);
        break;
      case LLVMCatchSwitch:
        instruction = new CatchSwitchInst(name, type);
        break;
      case LLVMAdd:
        {
          instruction = new BinaryOperator(name, type, OpCode.ADD);
          OperatorFlags flag = new OperatorFlags();
          //parseFlag(LLVMPrintValueToString(inst).getString().trim(), flag);
          instruction.setOperatorFlags(flag);
        }
        break;
      case LLVMFAdd:
        instruction = new BinaryOperator(name, type, OpCode.FADD);
        break;
      case LLVMSub:
        {
          instruction = new BinaryOperator(name, type, OpCode.SUB);
          OperatorFlags flag = new OperatorFlags();
          //flag.setNoSignedWrapFlag();
          //parseFlag(LLVMPrintValueToString(inst).getString().trim(), flag);
          instruction.setOperatorFlags(flag);
        }
        break;
      case LLVMFSub:
        instruction = new BinaryOperator(name, type, OpCode.FSUB);
        break;
      case LLVMMul:
        {
          instruction = new BinaryOperator(name, type, OpCode.MUL);
          OperatorFlags flag = new OperatorFlags();
          //parseFlag(LLVMPrintValueToString(inst).getString().trim(), flag);
          instruction.setOperatorFlags(flag);
        }
        break;
      case LLVMFMul:
        instruction = new BinaryOperator(name, type, OpCode.FMUL);
        break;
      case LLVMUDiv:
        {
          instruction = new BinaryOperator(name, type, OpCode.UDIV);
          OperatorFlags flag = new OperatorFlags();
          instruction.setOperatorFlags(flag);
        }
        break;
      case LLVMSDiv:
        {
          instruction = new BinaryOperator(name, type, OpCode.SDIV);
          OperatorFlags flag = new OperatorFlags();
          instruction.setOperatorFlags(flag);
        }
        break;
      case LLVMFDiv:
        instruction = new BinaryOperator(name, type, OpCode.FDIV);
        break;
      case LLVMURem:
        instruction = new BinaryOperator(name, type, OpCode.UREM);
        break;
      case LLVMSRem:
        instruction = new BinaryOperator(name, type, OpCode.SREM);
        break;
      case LLVMFRem:
        instruction = new BinaryOperator(name, type, OpCode.FREM);
        break;
      case LLVMShl:
        instruction = new BinaryOperator(name, type, OpCode.SHL);
        break;
      case LLVMLShr:
        instruction = new BinaryOperator(name, type, OpCode.LSHR);
        break;
      case LLVMAShr:
        instruction = new BinaryOperator(name, type, OpCode.ASHR);
        break;
      case LLVMAnd:
        instruction = new BinaryOperator(name, type, OpCode.AND);
        break;
      case LLVMOr:
        instruction = new BinaryOperator(name, type, OpCode.OR);
        break;
      case LLVMXor:
        instruction = new BinaryOperator(name, type, OpCode.XOR);
        break;
      case LLVMAlloca:
        {
          int alignment = LLVMGetAlignment(inst);
          instruction = new AllocaInst(name, type, alignment);
        }
        break;
      case LLVMLoad:
        {
          int alignment = LLVMGetAlignment(inst);
          boolean isVolatile;
          if(LLVMGetVolatile(inst) != 0)
            isVolatile = true;
          else
            isVolatile = false;
          instruction = new LoadInst(name, type, alignment);
          ((LoadInst)instruction).setVolatile(isVolatile);
        }
        break;
      case LLVMStore:
        {
          int alignment = LLVMGetAlignment(inst);
          boolean isVolatile;
          if(LLVMGetVolatile(inst) != 0)
            isVolatile = true;
          else
            isVolatile = false;
          instruction = new StoreInst(name, type, alignment);
          ((StoreInst)instruction).setVolatile(isVolatile);
        }
        break;
      case LLVMGetElementPtr:
        {
          GetElementPtrInst getElementPtrInst = new GetElementPtrInst(name, type);
          int isInbounds = LLVMIsInBounds(inst);
          getElementPtrInst.setIsInBounds(isInbounds != 0);
          instruction = getElementPtrInst;
        }
        break;
      case LLVMFence:
        instruction = new FenceInst(name, type);
        break;
      case LLVMAtomicCmpXchg:
        instruction = new AtomicCmpXchgInst(name, type);
        break;
      case LLVMAtomicRMW:
        instruction = new AtomicRMWInst(name, type);
        break;
      case LLVMTrunc:
        instruction = new TruncInst(name, type);
        break;
      case LLVMZExt:
        instruction = new ZExtInst(name, type);
        break;
      case LLVMSExt:
        instruction = new SExtInst(name, type);
        break;
      case LLVMFPToUI:
        instruction = new FPToUIInst(name, type);
        break;
      case LLVMFPToSI:
        instruction = new FPToSIInst(name, type);
        break;
      case LLVMUIToFP:
        instruction = new UIToFPInst(name, type);
        break;
      case LLVMSIToFP:
        instruction = new SIToFPInst(name, type);
        break;
      case LLVMFPTrunc:
        instruction = new FPTruncInst(name, type);
        break;
      case LLVMFPExt:
        instruction = new FPExtInst(name, type);
        break;
      case LLVMPtrToInt:
        instruction = new PtrToIntInst(name, type);
        break;
      case LLVMIntToPtr:
        instruction = new IntToPtrInst(name, type);
        break;
      case LLVMBitCast:
        instruction = new BitCastInst(name, type);
        break;
      case LLVMAddrSpaceCast:
        instruction = new AddrSpaceCastInst(name, type);
        break;
      case LLVMCleanupPad:
        instruction = new CleanupPadInst(name, type);
        break;
      case LLVMCatchPad:
        instruction = new CatchPadInst(name, type);
        break;
      case LLVMICmp:
        instruction = new ICmpInst(name, type, getICmpPredicate(inst));
        break;
      case LLVMFCmp:
        instruction = new FCmpInst(name, type, getFCmpPredicate(inst));
        break;
      case LLVMPHI:
        {
          PhiNode phiNode = new PhiNode(name, type);
          int size = LLVMCountIncoming(inst);
          List<BasicBlock> inComingBlocks = new ArrayList<>(size);
          for (int i = 0; i < size; i++) {
            inComingBlocks.add(i, context.getBasicBlock(LLVMGetIncomingBlock(inst, i)));
          }
          phiNode.setIncomingBlocks(ImmutableList.copyOf(inComingBlocks));
          instruction = phiNode;
        }
        break;
      case LLVMCall:
        {
          CallInst callInst = new CallInst(name, type);
          callInst.setNumArgs(LLVMGetNumArgOperands(inst));
          instruction = callInst;
        }
        break;
      case LLVMSelect:
        instruction = new SelectInst(name, type);
        break;
      case LLVMUserOp1:
      case LLVMUserOp2:
        throw new IllegalArgumentException("UserOp1 / UserOp2 should not appear in ir file");
      case LLVMVAArg:
        instruction = new VAArgInst(name, type);
        break;
      case LLVMExtractElement:
        instruction = new ExtractElementInst(name, type);
        break;
      case LLVMInsertElement:
        instruction = new InsertElementInst(name, type);
        break;
      case LLVMShuffleVector:
        instruction = new ShuffleVectorInst(name, type);
        break;
      case LLVMExtractValue:
        instruction = new ExtractValueInst(name, type);
        break;
      case LLVMInsertValue:
        instruction = new InsertValueInst(name, type);
        break;
      case LLVMLandingPad:
        instruction = new LandingPadInst(name, type);
        break;
      default:
        throw new IllegalArgumentException("Unhandled instruction: " + inst.toString());
    }
    if (parent != null)
      instruction.setParent(parent);
    if (LLVMHasMetadata(inst) != 0) {
      LLVMValueRef dbg = LLVMGetMetadata(inst, LLVMGetMDKindID("dbg", "dbg".length()));
      LLVMValueRef dbg2 = LLVMGetMetadata(inst, LLVMGetMDKindID("dbg", "dbg".length()));
      if (dbg != null) {
        LLVMGetMDNodeOperands(dbg, dbg);
        int numOperands;
        while (true) {
          numOperands = 0;
          numOperands = LLVMGetNumOperands(dbg);
          if (numOperands <= 0 || numOperands > 1000) {
            break;
          }
          dbg = LLVMGetOperand(dbg, 0);
        }
        BytePointer bp = LLVMPrintValueToString(dbg2);
        String fileName = LLVMPrintValueToString(dbg).getString();
        fileName = fileName.replace("\"", "").replace("!", "");
        String ot = bp.getString().trim();
        String[] s = ot.split(" = |\\(|\\)");
        Metadata md = new Metadata();
        if (s[1].equals("!DILocation")) {
          md.setFile(fileName);
          String[] ss = (s[2]).split(", |: ");
          for (int i = 0; i < ss.length; i = i + 2) {
            if (ss[i].equals("line")) {
              md.setLine(Integer.parseInt(ss[i + 1]));
            } else if (ss[i].equals("column")) {
              md.setColumn(Integer.parseInt(ss[i + 1]));
            }
          }
        }
        instruction.setMetadata(md);
      }
    }
    context.putInst(inst, instruction);
    List<Value> operands = new ArrayList<>();
    for (int i = 0; i < LLVMGetNumOperands(inst); i++) {
      operands.add(convert(LLVMGetOperand(inst, i), parent));
    }
    instruction.setOperands(operands);
    instruction.setOriginalText("");
    if ("".equals(instruction.getName())) {
      if (needName(instruction)) {
        instruction.setName("" + unnamedIndex);
        unnamedIndex++;
      }
    }
    return instruction;
  }

  private void parseFlag(String originalText, OperatorFlags flag) {
    if (originalText.contains("nsw")) {
      flag.setNoSignedWrapFlag();
    } else if(originalText.contains(("nuw"))) {
      flag.setNoUnsignedWrapFlag();
    }
  }

  private InstructionProperties.Predicate getICmpPredicate(LLVMValueRef inst) {
    int i = LLVMGetICmpPredicate(inst);
    switch (i) {
      case LLVMIntEQ:
        return InstructionProperties.Predicate.ICMP_EQ;
      case LLVMIntNE:
        return InstructionProperties.Predicate.ICMP_NE;
      case LLVMIntUGT:
        return InstructionProperties.Predicate.ICMP_UGT;
      case LLVMIntUGE:
        return InstructionProperties.Predicate.ICMP_UGE;
      case LLVMIntULT:
        return InstructionProperties.Predicate.ICMP_ULT;
      case LLVMIntULE:
        return InstructionProperties.Predicate.ICMP_ULE;
      case LLVMIntSGT:
        return InstructionProperties.Predicate.ICMP_SGT;
      case LLVMIntSGE:
        return InstructionProperties.Predicate.ICMP_SGE;
      case LLVMIntSLT:
        return InstructionProperties.Predicate.ICMP_SLT;
      case LLVMIntSLE:
        return InstructionProperties.Predicate.ICMP_SLE;
    }
    return null;
  }

  private InstructionProperties.Predicate getFCmpPredicate(LLVMValueRef inst) {
    int i = LLVMGetFCmpPredicate(inst);
    switch (i) {
      case LLVMRealPredicateFalse:
        return Predicate.FCMP_FALSE;
      case LLVMRealOEQ:
        return Predicate.FCMP_OEQ;
      case LLVMRealOGT:
        return Predicate.FCMP_OGT;
      case LLVMRealOGE:
        return Predicate.FCMP_OGE;
      case LLVMRealOLT:
        return Predicate.FCMP_OLT;
      case LLVMRealOLE:
        return Predicate.FCMP_OLE;
      case LLVMRealONE:
        return Predicate.FCMP_ONE;
      case LLVMRealORD:
        return Predicate.FCMP_ORD;
      case LLVMRealUNO:
        return Predicate.FCMP_UNO;
      case LLVMRealUEQ:
        return Predicate.FCMP_UEQ;
      case LLVMRealUGT:
        return Predicate.FCMP_UGT;
      case LLVMRealUGE:
        return Predicate.FCMP_UGE;
      case LLVMRealULT:
        return Predicate.FCMP_ULT;
      case LLVMRealULE:
        return Predicate.FCMP_ULE;
      case LLVMRealUNE:
        return Predicate.FCMP_UNE;
    }
    return null;
  }

  private boolean needName(Instruction instruction) {
    if (instruction.getType().isVoidTy()) {
      return false;
    }
    return true;
  }

  public Value convert(LLVMValueRef valueRef, BasicBlock parent) {
    if (valueRef == null) {
      return null;
    }
    //System.out.println(LLVMGetValueKind(valueRef));
    switch (LLVMGetValueKind(valueRef)) {
      case LLVMInstructionValueKind:
        return convertValueToInstruction(parent, valueRef);
      case LLVMConstantIntValueKind:
      case LLVMConstantExprValueKind:
      case LLVMConstantPointerNullValueKind:
      case LLVMConstantFPValueKind:
      case LLVMConstantAggregateZeroValueKind:
      case LLVMConstantDataArrayValueKind:
      case LLVMFunctionValueKind:
      case LLVMGlobalVariableValueKind:
      case LLVMConstantArrayValueKind:
      case LLVMConstantStructValueKind:
      case LLVMConstantVectorValueKind:
      case LLVMConstantDataVectorValueKind:
      case LLVMUndefValueValueKind:
        return convertValueToConstant(valueRef);
      case LLVMBasicBlockValueKind:
        return context.getBasicBlock(LLVMValueAsBasicBlock(valueRef));
      case LLVMMetadataAsValueValueKind:
        // TODO metadata
        Metadata metadata = new Metadata();
        if (LLVMGetNumOperands(valueRef) == 1) {
          String variableName = LLVMGetValueName(LLVMGetOperand(valueRef, 0)).getString();
          metadata.setName(variableName);
        }
        return metadata;
        //System.out.println(originalText);
        //throw new NotImplementedException();
      case LLVMArgumentValueKind:
        {
          Argument argument = context.getArgument(valueRef);
          Preconditions.checkNotNull(argument, "argument not created");
          return argument;
        }
      case LLVMInlineAsmValueKind:
        // TODO inline asm
        CallInst callInst = new CallInst(LLVMGetValueName(valueRef).getString(), getType(LLVMTypeOf(valueRef)));
        callInst.setInlineAsm(true);
        callInst.setOriginalText("");
        return callInst;
    }
    LLVMDumpValue(valueRef);
    System.out.println(LLVMGetValueKind(valueRef));
    assert false : "unhandled convert llvm value ref";
    return null;
  }

  private Constant convertValueToConstant(LLVMValueRef valueRef) {
    int valueKind = LLVMGetValueKind(valueRef);
    switch (valueKind) {
      case LLVMConstantIntValueKind:
        return convertValueToConstantInt(valueRef);
      case LLVMConstantAggregateZeroValueKind:
        return new ConstantAggregateZero(getType(LLVMTypeOf(valueRef)));
      case LLVMConstantDataArrayValueKind:
        return convertValueToConstantDataArray(valueRef);
      case LLVMConstantExprValueKind:
        return convertValueToConstantExpr(valueRef);
      case LLVMConstantPointerNullValueKind:
        return new ConstantPointerNull(getType(LLVMTypeOf(valueRef)));
      case LLVMConstantFPValueKind:
        return convertValueToConstantFP(valueRef);
      case LLVMFunctionValueKind:
        return context.getFunction(valueRef);
      case LLVMGlobalVariableValueKind:
        return context.getGlobalVariable(valueRef);
      case LLVMConstantArrayValueKind:
        return convertValueToConstantArray(valueRef);
      case LLVMConstantStructValueKind:
        return convertValueToConstantStruct(valueRef);
      case LLVMConstantDataVectorValueKind:
      case LLVMConstantVectorValueKind:
        return convertValueToConstantVector(valueRef);
      case LLVMUndefValueValueKind:
        return new UndefValue("", getType(LLVMTypeOf(valueRef)));
      default:
        assert false : "unhandled value kind:" + valueKind;
    }
    return null;
  }

  private Constant convertValueToConstantArray(LLVMValueRef valueRef) {
    List<Value> operands = new ArrayList<>();
    for(int i = 0; i < LLVMGetNumOperands(valueRef); i++) {
      operands.add(convert(LLVMGetOperand(valueRef, i), null));
    }
    ConstantArray constantArray = new ConstantArray("", getType(LLVMTypeOf(valueRef)));
    constantArray.setOperands(operands);
    return constantArray;
  }

  private Constant convertValueToConstantDataArray(LLVMValueRef valueRef) {
    List<Value> operands = new ArrayList<>();
    for (int i = 0; i < LLVMGetArrayLength(LLVMTypeOf(valueRef)); i++) {
      operands.add(convert(LLVMGetElementAsConstant(valueRef, i), null));
    }
    ConstantDataArray constantDataArray = new ConstantDataArray("", getType(LLVMTypeOf(valueRef)));
    constantDataArray.setOperands(operands);
    return constantDataArray;
  }

  private Constant convertValueToConstantVector(LLVMValueRef valueRef) {
    List<Value> operands = new ArrayList<>();
    for (int i = 0; i < LLVMGetNumOperands(valueRef); i++) {
      operands.add(convert(LLVMGetOperand(valueRef, i), null));
    }
    ConstantDataVector constantDataVector = new ConstantDataVector("", getType(LLVMTypeOf(valueRef)));
    constantDataVector.setOperands(operands);
    return constantDataVector;
  }

  private Constant convertValueToConstantFP(LLVMValueRef valueRef) {
    Type type = getType(LLVMTypeOf(valueRef));
    int[] losses = new int[10];
    double value = LLVMConstRealGetDouble(valueRef, losses);
    BytePointer bytePointer = LLVMPrintValueToString(valueRef);
    String originalText = bytePointer.getString().trim();
    ConstantFP constantFP;
    if (originalText.indexOf('x') >= 0) {
      constantFP = new ConstantFP("", type, value, true);
    } else {
      constantFP = new ConstantFP("", type, value, false);
    }
    return constantFP;
  }

  private Constant convertValueToConstantStruct(LLVMValueRef valueRef) {
    List<Constant> data = new ArrayList<>();
    for (int i = 0; i < LLVMGetNumOperands(valueRef); i++) {
      data.add(convertValueToConstant(LLVMGetOperand(valueRef, i)));
    }
    return new ConstantStruct(LLVMGetValueName(valueRef).toString(), getType(LLVMTypeOf(valueRef)), data);
  }

  private Argument convertValueToArgument(LLVMValueRef valueRef) {
    String name = LLVMGetValueName(valueRef).getString();
    if (name.equals("")) {
      name = "" + this.unnamedIndex;
      unnamedIndex++;
    }
    Argument argument = new Argument(name, getType(LLVMTypeOf(valueRef)));
    context.putArgument(valueRef, argument);
    return argument;
  }

  public Constant convertValueToConstantInt(LLVMValueRef valueRef) {
    LLVMValueRef constantInt = LLVMIsAConstantInt(valueRef);
    assert constantInt != null : "constant int should not be null";
    IntegerType integerType = (IntegerType) getType(LLVMTypeOf(valueRef));
    int width = integerType.getBitWidth();
    long value = LLVMConstIntGetSExtValue(valueRef);
    return ConstantInt.get(integerType, new APInt(width, value, true));
  }

  private ConstantExpr convertValueToConstantExpr(LLVMValueRef valueRef) {
    int opcode = LLVMGetConstOpcode(valueRef);
    Type type;
    List<Value> operands = new ArrayList<>();
    OperatorFlags flag = new OperatorFlags();
    Predicate pred;
    switch (opcode) {
      case LLVMAdd:
        type = getType(LLVMTypeOf(valueRef));
        //parseFlag(LLVMPrintValueToString(valueRef).getString().trim(), flag);
        BinaryConstantExpr add =
            BinaryConstantExpr.getInstance(
                LLVMGetValueName(valueRef).getString(),
                type,
                OpCode.ADD,
                flag,
                convertValueToConstant(LLVMGetOperand(valueRef, 0)),
                convertValueToConstant(LLVMGetOperand(valueRef, 1)));
        for (int i = 0; i < LLVMGetNumOperands(valueRef); i++) {
          operands.add(convert(LLVMGetOperand(valueRef, i), null));
        }
        add.setOperands(operands);
        return add;
      case LLVMSub :
        type = getType(LLVMTypeOf(valueRef));
        //parseFlag(LLVMPrintValueToString(valueRef).getString().trim(), flag);
        BinaryConstantExpr sub =
            BinaryConstantExpr.getInstance(
                LLVMGetValueName(valueRef).getString(),
                type,
                OpCode.SUB,
                flag,
                convertValueToConstant(LLVMGetOperand(valueRef, 0)),
                convertValueToConstant(LLVMGetOperand(valueRef, 1)));
        for (int i = 0; i < LLVMGetNumOperands(valueRef); i++) {
          operands.add(convert(LLVMGetOperand(valueRef, i), null));
        }
        sub.setOperands(operands);
        return sub;
      case LLVMGetElementPtr:
        //LLVMDumpValue(valueRef);
        List<Constant> idxList = new ArrayList<>();
        for (int i = 1; i < LLVMGetNumOperands(valueRef); i++) {
          idxList.add(convertValueToConstant(LLVMGetOperand(valueRef, i)));
        }
        GetElementPtrConstantExpr ce =
            GetElementPtrConstantExpr.getInstance(
                "",
                getType(LLVMTypeOf(valueRef)),
                ImmutableList.copyOf(idxList),
                convertValueToConstant(LLVMGetOperand(valueRef, 0)),
                LLVMIsInBounds(valueRef) != 0);
        // TODO put it into context
        for (int i = 0; i < LLVMGetNumOperands(valueRef); i++) {
          operands.add(convert(LLVMGetOperand(valueRef, i), null));
        }
        ce.setOperands(operands);
        // TODO may require setting originalText
        return ce;
        // TODO create other constant expressions
      case LLVMBitCast:
        type = getType(LLVMTypeOf(valueRef));
        UnaryConstantExpr bc =
            UnaryConstantExpr.getInstance(
                LLVMGetValueName(valueRef).getString(),
                type,
                OpCode.BITCAST,
                convertValueToConstant(LLVMGetOperand(valueRef, 0)),
                type);
        for (int i = 0; i < LLVMGetNumOperands(valueRef); i++) {
          operands.add(convert(LLVMGetOperand(valueRef, i), null));
        }
        bc.setOperands(operands);
        return bc;
      case LLVMPtrToInt:
        type = getType(LLVMTypeOf(valueRef));
        //LLVMDumpValue(valueRef);
        UnaryConstantExpr pti =
            UnaryConstantExpr.getInstance(
                LLVMGetValueName(valueRef).getString(),
                type,
                OpCode.PTRTOINT,
                convertValueToConstant(LLVMGetOperand(valueRef, 0)),
                type);
        for (int i = 0; i < LLVMGetNumOperands(valueRef); i++) {
          operands.add(convert(LLVMGetOperand(valueRef, i), null));
        }
        pti.setOperands(operands);
        return pti;
      case LLVMIntToPtr:
        type = getType(LLVMTypeOf(valueRef));
        //LLVMDumpValue(valueRef);
        UnaryConstantExpr itp =
            UnaryConstantExpr.getInstance(
                LLVMGetValueName(valueRef).getString(),
                type,
                OpCode.INTTOPTR,
                convertValueToConstant(LLVMGetOperand(valueRef, 0)),
                type);
        for (int i = 0; i < LLVMGetNumOperands(valueRef); i++) {
          operands.add(convert(LLVMGetOperand(valueRef, i), null));
        }
        itp.setOperands(operands);
        return itp;
      case LLVMTrunc:
        type = getType(LLVMTypeOf(valueRef));
        UnaryConstantExpr trunc =
            UnaryConstantExpr.getInstance(
                LLVMGetValueName(valueRef).getString(),
                type,
                OpCode.TRUNC,
                convertValueToConstant(LLVMGetOperand(valueRef, 0)),
                type);
        for (int i = 0; i < LLVMGetNumOperands(valueRef); i++) {
          operands.add(convert(LLVMGetOperand(valueRef, i), null));
        }
        trunc.setOperands(operands);
        return trunc;
      case LLVMICmp:
        type = getType(LLVMTypeOf(valueRef));
        pred = getICmpPredicate(valueRef);
        CompareConstantExpr icmp =
            CompareConstantExpr.getInstance(
                LLVMGetValueName(valueRef).getString(),
                type,
                OpCode.ICMP,
                pred,
                convertValueToConstant(LLVMGetOperand(valueRef, 0)),
                convertValueToConstant(LLVMGetOperand(valueRef, 1)));
        for (int i = 0; i < LLVMGetNumOperands(valueRef); i++) {
          operands.add(convert(LLVMGetOperand(valueRef, i), null));
        }
        icmp.setOperands(operands);
        return icmp;
      default:
        Trouble
            .futureWork("unhandled constant expr type for opcode: " + opcode
                + ", origin statement is: "
                + LLVMPrintValueToString(valueRef).getString());
        break;
    }
    return null;
  }

  public Type getType(LLVMTypeRef typeRef) {
    Type cache = context.getType(typeRef);
    if (cache != null) {
      return cache;
    }
    Type result;
    int typeKind = LLVMGetTypeKind(typeRef);
    switch (typeKind) {
      case LLVMVoidTypeKind:
        result = Type.getVoidTy(context);
        break;
      case LLVMHalfTypeKind:
        result = Type.getHalfTy(context);
        break;
      case LLVMFloatTypeKind:
        result = Type.getFloatTy(context);
        break;
      case LLVMDoubleTypeKind:
        result = Type.getDoubleTy(context);
        break;
      case LLVMX86_FP80TypeKind:
        result = Type.getX86_FP80Ty(context);
        break;
      case LLVMFP128TypeKind:
        result = Type.getFP128Ty(context);
        break;
      case LLVMPPC_FP128TypeKind:
        result = Type.getPPC_FP128Ty(context);
        break;
      case LLVMLabelTypeKind:
        result = Type.getLabelTy(context);
        break;
      case LLVMIntegerTypeKind:
        int size = LLVMGetIntTypeWidth(typeRef);
        result = Type.getIntNTy(context, size);
        break;
      case LLVMFunctionTypeKind:
        {
          LLVMTypeRef returnTypeRef = LLVMGetReturnType(typeRef);
          int paramsCount = LLVMCountParamTypes(typeRef);
          PointerPointer<LLVMTypeRef> params = new PointerPointer<>(paramsCount);
          LLVMGetParamTypes(typeRef, params);
          Type[] paramsType = new Type[paramsCount];
          for (int i = 0; i < paramsCount; i++) {
            paramsType[i] = getType(params.get(LLVMTypeRef.class, i));
          }
          boolean isVarArg = LLVMIsFunctionVarArg(typeRef) != 0;
          result = FunctionType.get(getType(returnTypeRef), paramsType, isVarArg);
          break;
        }
      case LLVMStructTypeKind:
        {
          Optional<String> name =
              Optional.fromNullable(LLVMGetStructName(typeRef))
                  .transform(
                      new com.google.common.base.Function<BytePointer, String>() {
                        @Override
                        public String apply(BytePointer input) {
                          return input.getString();
                        }
                      });
          StructType structType = StructType.create(context, name.orNull());
          context.putType(typeRef, structType);
          int elementsCount = LLVMCountStructElementTypes(typeRef);
          PointerPointer<LLVMTypeRef> elems = new PointerPointer<>(elementsCount);
          LLVMGetStructElementTypes(typeRef, elems);
          Type[] elementType = new Type[elementsCount];
          for (int i = 0; i < elementsCount; i++) {
            elementType[i] = getType(elems.get(LLVMTypeRef.class, i));
          }
          boolean isPacked = LLVMIsPackedStruct(typeRef) != 0;
          structType.setBody(elementType, isPacked);
          LLVMIsOpaqueStruct(typeRef);
          result = structType;
          break;
        }
      case LLVMArrayTypeKind:
        {
          Type elementType = getType(LLVMGetElementType(typeRef));
          long numElements = LLVMGetArrayLength(typeRef);
          result = ArrayType.get(elementType, numElements);
          break;
        }
      case LLVMPointerTypeKind:
        {
          Type elementType = getType(LLVMGetElementType(typeRef));
          int addressSpace = LLVMGetPointerAddressSpace(typeRef);
          result = PointerType.get(elementType, addressSpace);
          context.putPointerSize(LLVMStoreSizeOfType(targetDataRef, typeRef));
          break;
        }
      case LLVMVectorTypeKind:
      {
        Type elementType = getType(LLVMGetElementType(typeRef));
        int numElements = LLVMGetArrayLength(typeRef);
        result = VectorType.get(elementType, numElements);
        break;
      }
      case LLVMMetadataTypeKind:
        result = Type.getMetadataTy(context);
        break;
      case LLVMX86_MMXTypeKind:
        result = Type.getX86_MMXTy(context);
        break;
      case LLVMTokenTypeKind:
        result = Type.getTokenTy(context);
        break;
      default:
        throw new NotImplementedException();
    }
    context.putType(typeRef, result);
    if (result.isFunctionTy() || result.isVoidTy() || result.isMetadataTy()) {
      return result;
    }
    context.putTypeStoreSize(result, LLVMStoreSizeOfType(targetDataRef, typeRef));
    return result;
  }
}
