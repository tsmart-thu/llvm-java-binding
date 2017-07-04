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
import cn.edu.thu.tsmart.core.cfa.llvm.Type.TypeID;
import cn.edu.thu.tsmart.core.cfa.util.Casting;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.SizeTPointer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
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
    // DataLayout
    targetDataRef = LLVMGetModuleDataLayout(moduleRef);
    SizeTPointer sizeTPointer = new SizeTPointer(64);
    String moduleIdentifier = LLVMGetModuleIdentifier(moduleRef, sizeTPointer).getString();
    Map<String, LlvmFunction> functionMap = new HashMap<>();
    // create globals
    List<GlobalVariable> globalList = new ArrayList<>();
    for (LLVMValueRef g = LLVMGetFirstGlobal(moduleRef); g != null; g = LLVMGetNextGlobal(g)) {
      String name = LLVMGetValueName(g).getString();
      Type type = getType(LLVMTypeOf(g));
      Constant init = Casting.castOrNull(convert(LLVMGetInitializer(g)), Constant.class);
      GlobalVariable variable = new GlobalVariable(name, type, init);
      context.putGlobalVariable(g, variable);
      globalList.add(variable);
    }
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
    return new LlvmModule(context, moduleIdentifier, functionMap, globalList);
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
      argumentList.add(convertValueToArgument(arg));
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
      basicBlockRefs.add(bb);
      context.putBasicBlock(bb, block);
    }
    // convert
    for (int i = 0; i < basicBlockList.size(); i++) {
      LLVMBasicBlockRef ref = basicBlockRefs.get(i);
      BasicBlock block = basicBlockList.get(i);
      convertValueToBasicBlock(ref, block);
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
      Instruction instruction = convertValueToInstruction(inst);
      instructionList.add(instruction);
      context.putInst(inst, instruction);
    }
    for (LLVMValueRef inst = LLVMGetFirstInstruction(ref);
        inst != null;
        inst = LLVMGetNextInstruction(inst)) {
      Instruction instruction = convertValueToInstruction(inst);
      List<Use> uses = new ArrayList<>();
      int i = 0;
      for (LLVMUseRef useRef = LLVMGetFirstUse(inst);
          useRef != null;
          useRef = LLVMGetNextUse(useRef)) {
        LLVMValueRef userRef = LLVMGetUser(useRef);
        uses.add(new Use(instruction, context.getInst(userRef), i));
        i++;
      }
      instruction.setUses(uses);
    }
    block.setInstList(instructionList);
  }

  private Instruction convertValueToInstruction(LLVMValueRef inst) {
    Instruction instruction = context.getInst(inst);
    if (instruction != null) {
      return instruction;
    }
    BytePointer bytePointer = LLVMPrintValueToString(inst);
    String originalText = bytePointer.getString().trim();
    LLVMDisposeMessage(bytePointer);
    int opcode = LLVMGetInstructionOpcode(inst);
    String name = LLVMGetValueName(inst).getString();
    if (needName(originalText) && "".equals(name)) {
      name = "" + unnamedIndex;
      unnamedIndex++;
    }
    if (LLVMHasMetadata(inst) != 0) {
      LLVMValueRef dbg = LLVMGetMetadata(inst, LLVMGetMDKindID("dbg", "dbg".length()));
      //      LLVMDumpValue(dbg);

    }
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
          parseFlag(originalText, flag);
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
          parseFlag(originalText, flag);
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
          parseFlag(originalText, flag);
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
          instruction = new LoadInst(name, type, alignment);
        }
        break;
      case LLVMStore:
        {
          int alignment = LLVMGetAlignment(inst);
          instruction = new StoreInst(name, type, alignment);
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
    context.putInst(inst, instruction);
    List<Value> operands = new ArrayList<>();
    for (int i = 0; i < LLVMGetNumOperands(inst); i++) {
      operands.add(convert(LLVMGetOperand(inst, i)));
    }
    instruction.setOperands(operands);
    instruction.setOriginalText(originalText);
    return instruction;
  }

  private void parseFlag(String originalText, OperatorFlags flag) {
    if (originalText.contains("nsw")) {
      flag.setNoSignedWrapFlag();
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

  private boolean needName(String text) {
    return text.startsWith("%");
  }

  public Value convert(LLVMValueRef valueRef) {
    if (valueRef == null) {
      return null;
    }
    //System.out.println(LLVMGetValueKind(valueRef));
    switch (LLVMGetValueKind(valueRef)) {
      case LLVMInstructionValueKind:
        return convertValueToInstruction(valueRef);
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
        return convertValueToConstant(valueRef);
      case LLVMBasicBlockValueKind:
        return context.getBasicBlock(LLVMValueAsBasicBlock(valueRef));
      case LLVMMetadataAsValueValueKind:
        // TODO metadata
        throw new NotImplementedException();
      case LLVMArgumentValueKind:
        {
          Argument argument = context.getArgument(valueRef);
          Preconditions.checkNotNull(argument, "argument not created");
          return argument;
        }
      case LLVMInlineAsmValueKind:
        // TODO inline asm
        throw new NotImplementedException();
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
        return new ConstantDataArray();
      case LLVMConstantExprValueKind:
        return convertValueToConstantExpr(valueRef);
      case LLVMConstantPointerNullValueKind:
        return new ConstantPointerNull(getType(LLVMTypeOf(valueRef)));
      case LLVMConstantFPValueKind:
        throw new NotImplementedException();
      case LLVMFunctionValueKind:
        return context.getFunction(valueRef);
      case LLVMGlobalVariableValueKind:
        return context.getGlobalVariable(valueRef);
      case LLVMConstantArrayValueKind:
        return new ConstantArray();
      case LLVMConstantStructValueKind:
        throw new NotImplementedException();
        //return convertValueToConstantStruct(valueRef);
      default:
        assert false : "unhandled value kind:" + valueKind;
    }
    return null;
  }

  private Constant convertValueToConstantStruct(LLVMValueRef valueRef) {
    LLVMDumpValue(valueRef);
    int n = LLVMCountStructElementTypes(LLVMTypeOf(valueRef));
    //LLVMTypeRef[] t = new LLVMTypeRef[n];
    //PointerPointer<LLVMTypeRef> p = new PointerPointer<LLVMTypeRef>(t);
    //LLVMGetStructElementTypes(LLVMTypeOf(valueRef), p);
    Constant[] a = new ConstantData[n];
    for(int i = 0; i < n; i++) {
      //System.out.println(LLVMGetStructName(LLVMTypeOf(valueRef)));
      LLVMDumpValue(LLVMConstNamedStruct(LLVMStructGetTypeAtIndex(LLVMTypeOf(valueRef), 3), valueRef, 0));
      a[i] = convertValueToConstant(LLVMConstNamedStruct(LLVMStructGetTypeAtIndex(LLVMTypeOf(valueRef), i), valueRef, i));
    }
    return new ConstantStruct(a);
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
    switch (opcode) {
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
                convertValueToConstant(LLVMGetOperand(valueRef, 0)));
        // TODO put it into context
        for (int i = 0; i < LLVMGetNumOperands(valueRef); i++) {
          operands.add(convert(LLVMGetOperand(valueRef, i)));
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
          operands.add(convert(LLVMGetOperand(valueRef, i)));
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
          operands.add(convert(LLVMGetOperand(valueRef, i)));
        }
        pti.setOperands(operands);
        return pti;
      default:
        System.out.println(opcode);
        assert false : "unhandled constant expr type";
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
          break;
        }
      case LLVMVectorTypeKind:
        throw new NotImplementedException();
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
    if (result.isFunctionTy() || result.isVoidTy()) {
      return result;
    }
    context.putTypeStoreSize(result, LLVMStoreSizeOfType(targetDataRef, typeRef));
    return result;
  }
}
