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

import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import static org.bytedeco.javacpp.LLVM.LLVMAShr;
import static org.bytedeco.javacpp.LLVM.LLVMAdd;
import static org.bytedeco.javacpp.LLVM.LLVMAddrSpaceCast;
import static org.bytedeco.javacpp.LLVM.LLVMAlloca;
import static org.bytedeco.javacpp.LLVM.LLVMAnd;
import static org.bytedeco.javacpp.LLVM.LLVMArgumentValueKind;
import static org.bytedeco.javacpp.LLVM.LLVMArrayTypeKind;
import static org.bytedeco.javacpp.LLVM.LLVMAtomicCmpXchg;
import static org.bytedeco.javacpp.LLVM.LLVMAtomicRMW;
import static org.bytedeco.javacpp.LLVM.LLVMBasicBlockAsValue;
import static org.bytedeco.javacpp.LLVM.LLVMBasicBlockRef;
import static org.bytedeco.javacpp.LLVM.LLVMBasicBlockValueKind;
import static org.bytedeco.javacpp.LLVM.LLVMBitCast;
import static org.bytedeco.javacpp.LLVM.LLVMBr;
import static org.bytedeco.javacpp.LLVM.LLVMCall;
import static org.bytedeco.javacpp.LLVM.LLVMCatchPad;
import static org.bytedeco.javacpp.LLVM.LLVMCatchRet;
import static org.bytedeco.javacpp.LLVM.LLVMCatchSwitch;
import static org.bytedeco.javacpp.LLVM.LLVMCleanupPad;
import static org.bytedeco.javacpp.LLVM.LLVMCleanupRet;
import static org.bytedeco.javacpp.LLVM.LLVMConstIntGetZExtValue;
import static org.bytedeco.javacpp.LLVM.LLVMConstantAggregateZeroValueKind;
import static org.bytedeco.javacpp.LLVM.LLVMConstantExprValueKind;
import static org.bytedeco.javacpp.LLVM.LLVMConstantFPValueKind;
import static org.bytedeco.javacpp.LLVM.LLVMConstantIntValueKind;
import static org.bytedeco.javacpp.LLVM.LLVMConstantPointerNullValueKind;
import static org.bytedeco.javacpp.LLVM.LLVMCountParamTypes;
import static org.bytedeco.javacpp.LLVM.LLVMCountStructElementTypes;
import static org.bytedeco.javacpp.LLVM.LLVMDisposeMessage;
import static org.bytedeco.javacpp.LLVM.LLVMDoubleTypeKind;
import static org.bytedeco.javacpp.LLVM.LLVMDumpValue;
import static org.bytedeco.javacpp.LLVM.LLVMExtractElement;
import static org.bytedeco.javacpp.LLVM.LLVMExtractValue;
import static org.bytedeco.javacpp.LLVM.LLVMFAdd;
import static org.bytedeco.javacpp.LLVM.LLVMFCmp;
import static org.bytedeco.javacpp.LLVM.LLVMFDiv;
import static org.bytedeco.javacpp.LLVM.LLVMFMul;
import static org.bytedeco.javacpp.LLVM.LLVMFP128TypeKind;
import static org.bytedeco.javacpp.LLVM.LLVMFPExt;
import static org.bytedeco.javacpp.LLVM.LLVMFPToSI;
import static org.bytedeco.javacpp.LLVM.LLVMFPToUI;
import static org.bytedeco.javacpp.LLVM.LLVMFPTrunc;
import static org.bytedeco.javacpp.LLVM.LLVMFRem;
import static org.bytedeco.javacpp.LLVM.LLVMFSub;
import static org.bytedeco.javacpp.LLVM.LLVMFence;
import static org.bytedeco.javacpp.LLVM.LLVMFloatTypeKind;
import static org.bytedeco.javacpp.LLVM.LLVMFunctionTypeKind;
import static org.bytedeco.javacpp.LLVM.LLVMFunctionValueKind;
import static org.bytedeco.javacpp.LLVM.LLVMGetAlignment;
import static org.bytedeco.javacpp.LLVM.LLVMGetArrayLength;
import static org.bytedeco.javacpp.LLVM.LLVMGetElementPtr;
import static org.bytedeco.javacpp.LLVM.LLVMGetElementType;
import static org.bytedeco.javacpp.LLVM.LLVMGetFirstBasicBlock;
import static org.bytedeco.javacpp.LLVM.LLVMGetFirstFunction;
import static org.bytedeco.javacpp.LLVM.LLVMGetFirstGlobal;
import static org.bytedeco.javacpp.LLVM.LLVMGetFirstInstruction;
import static org.bytedeco.javacpp.LLVM.LLVMGetFirstUse;
import static org.bytedeco.javacpp.LLVM.LLVMGetICmpPredicate;
import static org.bytedeco.javacpp.LLVM.LLVMGetInitializer;
import static org.bytedeco.javacpp.LLVM.LLVMGetInstructionOpcode;
import static org.bytedeco.javacpp.LLVM.LLVMGetIntTypeWidth;
import static org.bytedeco.javacpp.LLVM.LLVMGetMDKindID;
import static org.bytedeco.javacpp.LLVM.LLVMGetMetadata;
import static org.bytedeco.javacpp.LLVM.LLVMGetModuleDataLayout;
import static org.bytedeco.javacpp.LLVM.LLVMGetModuleIdentifier;
import static org.bytedeco.javacpp.LLVM.LLVMGetNextBasicBlock;
import static org.bytedeco.javacpp.LLVM.LLVMGetNextFunction;
import static org.bytedeco.javacpp.LLVM.LLVMGetNextGlobal;
import static org.bytedeco.javacpp.LLVM.LLVMGetNextInstruction;
import static org.bytedeco.javacpp.LLVM.LLVMGetNextUse;
import static org.bytedeco.javacpp.LLVM.LLVMGetNumOperands;
import static org.bytedeco.javacpp.LLVM.LLVMGetOperand;
import static org.bytedeco.javacpp.LLVM.LLVMGetParamTypes;
import static org.bytedeco.javacpp.LLVM.LLVMGetPointerAddressSpace;
import static org.bytedeco.javacpp.LLVM.LLVMGetReturnType;
import static org.bytedeco.javacpp.LLVM.LLVMGetStructElementTypes;
import static org.bytedeco.javacpp.LLVM.LLVMGetStructName;
import static org.bytedeco.javacpp.LLVM.LLVMGetTypeKind;
import static org.bytedeco.javacpp.LLVM.LLVMGetUser;
import static org.bytedeco.javacpp.LLVM.LLVMGetValueKind;
import static org.bytedeco.javacpp.LLVM.LLVMGetValueName;
import static org.bytedeco.javacpp.LLVM.LLVMGlobalVariableValueKind;
import static org.bytedeco.javacpp.LLVM.LLVMHalfTypeKind;
import static org.bytedeco.javacpp.LLVM.LLVMHasMetadata;
import static org.bytedeco.javacpp.LLVM.LLVMICmp;
import static org.bytedeco.javacpp.LLVM.LLVMIndirectBr;
import static org.bytedeco.javacpp.LLVM.LLVMInlineAsmValueKind;
import static org.bytedeco.javacpp.LLVM.LLVMInsertElement;
import static org.bytedeco.javacpp.LLVM.LLVMInsertValue;
import static org.bytedeco.javacpp.LLVM.LLVMInstructionValueKind;
import static org.bytedeco.javacpp.LLVM.LLVMIntEQ;
import static org.bytedeco.javacpp.LLVM.LLVMIntNE;
import static org.bytedeco.javacpp.LLVM.LLVMIntSGE;
import static org.bytedeco.javacpp.LLVM.LLVMIntSGT;
import static org.bytedeco.javacpp.LLVM.LLVMIntSLE;
import static org.bytedeco.javacpp.LLVM.LLVMIntSLT;
import static org.bytedeco.javacpp.LLVM.LLVMIntToPtr;
import static org.bytedeco.javacpp.LLVM.LLVMIntUGE;
import static org.bytedeco.javacpp.LLVM.LLVMIntUGT;
import static org.bytedeco.javacpp.LLVM.LLVMIntULE;
import static org.bytedeco.javacpp.LLVM.LLVMIntULT;
import static org.bytedeco.javacpp.LLVM.LLVMIntegerTypeKind;
import static org.bytedeco.javacpp.LLVM.LLVMInvoke;
import static org.bytedeco.javacpp.LLVM.LLVMIsAConstantInt;
import static org.bytedeco.javacpp.LLVM.LLVMIsFunctionVarArg;
import static org.bytedeco.javacpp.LLVM.LLVMIsOpaqueStruct;
import static org.bytedeco.javacpp.LLVM.LLVMIsPackedStruct;
import static org.bytedeco.javacpp.LLVM.LLVMLShr;
import static org.bytedeco.javacpp.LLVM.LLVMLabelTypeKind;
import static org.bytedeco.javacpp.LLVM.LLVMLandingPad;
import static org.bytedeco.javacpp.LLVM.LLVMLoad;
import static org.bytedeco.javacpp.LLVM.LLVMMetadataAsValueValueKind;
import static org.bytedeco.javacpp.LLVM.LLVMMetadataTypeKind;
import static org.bytedeco.javacpp.LLVM.LLVMModuleRef;
import static org.bytedeco.javacpp.LLVM.LLVMMul;
import static org.bytedeco.javacpp.LLVM.LLVMOr;
import static org.bytedeco.javacpp.LLVM.LLVMPHI;
import static org.bytedeco.javacpp.LLVM.LLVMPPC_FP128TypeKind;
import static org.bytedeco.javacpp.LLVM.LLVMPointerTypeKind;
import static org.bytedeco.javacpp.LLVM.LLVMPrintValueToString;
import static org.bytedeco.javacpp.LLVM.LLVMPtrToInt;
import static org.bytedeco.javacpp.LLVM.LLVMResume;
import static org.bytedeco.javacpp.LLVM.LLVMRet;
import static org.bytedeco.javacpp.LLVM.LLVMSDiv;
import static org.bytedeco.javacpp.LLVM.LLVMSExt;
import static org.bytedeco.javacpp.LLVM.LLVMSIToFP;
import static org.bytedeco.javacpp.LLVM.LLVMSRem;
import static org.bytedeco.javacpp.LLVM.LLVMSelect;
import static org.bytedeco.javacpp.LLVM.LLVMShl;
import static org.bytedeco.javacpp.LLVM.LLVMShuffleVector;
import static org.bytedeco.javacpp.LLVM.LLVMStore;
import static org.bytedeco.javacpp.LLVM.LLVMStructTypeKind;
import static org.bytedeco.javacpp.LLVM.LLVMSub;
import static org.bytedeco.javacpp.LLVM.LLVMSwitch;
import static org.bytedeco.javacpp.LLVM.LLVMTargetDataRef;
import static org.bytedeco.javacpp.LLVM.LLVMTokenTypeKind;
import static org.bytedeco.javacpp.LLVM.LLVMTrunc;
import static org.bytedeco.javacpp.LLVM.LLVMTypeOf;
import static org.bytedeco.javacpp.LLVM.LLVMTypeRef;
import static org.bytedeco.javacpp.LLVM.LLVMUDiv;
import static org.bytedeco.javacpp.LLVM.LLVMUIToFP;
import static org.bytedeco.javacpp.LLVM.LLVMURem;
import static org.bytedeco.javacpp.LLVM.LLVMUnreachable;
import static org.bytedeco.javacpp.LLVM.LLVMUseRef;
import static org.bytedeco.javacpp.LLVM.LLVMUserOp1;
import static org.bytedeco.javacpp.LLVM.LLVMUserOp2;
import static org.bytedeco.javacpp.LLVM.LLVMVAArg;
import static org.bytedeco.javacpp.LLVM.LLVMValueAsBasicBlock;
import static org.bytedeco.javacpp.LLVM.LLVMValueRef;
import static org.bytedeco.javacpp.LLVM.LLVMVectorTypeKind;
import static org.bytedeco.javacpp.LLVM.LLVMVoidTypeKind;
import static org.bytedeco.javacpp.LLVM.LLVMX86_FP80TypeKind;
import static org.bytedeco.javacpp.LLVM.LLVMX86_MMXTypeKind;
import static org.bytedeco.javacpp.LLVM.LLVMXor;
import static org.bytedeco.javacpp.LLVM.LLVMZExt;

import cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OperatorFlags;
import cn.edu.thu.tsmart.core.cfa.util.Casting;
import com.google.common.base.Optional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.SizeTPointer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/** @author guangchen on 03/03/2017. */
public class Converter {

  private final Context context;
  private int unnamedValueIndex = 0;

  public Converter(Context context) {
    this.context = context;
  }

  public LlvmModule convert(LLVMModuleRef moduleRef) {
    SizeTPointer sizeTPointer = new SizeTPointer(64);
    String moduleIdentifier = LLVMGetModuleIdentifier(moduleRef, sizeTPointer).getString();
    Map<String, LlvmFunction> functionMap = new HashMap<>();
    // create globals
    List<GlobalVariable> globalList = new ArrayList<>();
    for (LLVMValueRef g = LLVMGetFirstGlobal(moduleRef); g != null; g = LLVMGetNextGlobal(g)) {
      String name = LLVMGetValueName(g).getString();
      Type type = getType(LLVMTypeOf(g));
      Constant init = Casting.cast(convert(LLVMGetInitializer(g)), Constant.class);
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
    // DataLayout
    LLVMTargetDataRef targetDataRef = LLVMGetModuleDataLayout(moduleRef);
    DataLayout dataLayout = new DataLayout(context, targetDataRef);
    context.setDataLayout(dataLayout);
    return new LlvmModule(context, moduleIdentifier, functionMap, globalList, dataLayout);
  }

  private void convertValueToFunction(LLVMValueRef key, LlvmFunction value) {
    // reset counter
    this.unnamedValueIndex = 0;
    // set name
    value.setName(LLVMGetValueName(key).getString());
    // set type
    value.setType(getType(LLVMTypeOf(key)));
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
    for (int i = 0; i < basicBlockList.size(); i ++) {
      LLVMBasicBlockRef ref = basicBlockRefs.get(i);
      BasicBlock block = basicBlockList.get(i);
      convertValueToBasicBlock(ref, block);
    }
    // set
    value.setBasicBlockList(basicBlockList);
  }

  private void convertValueToBasicBlock(LLVMBasicBlockRef ref, BasicBlock block) {
    block.setName(LLVMGetValueName(LLVMBasicBlockAsValue(ref)).getString());
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
      for (LLVMUseRef useRef = LLVMGetFirstUse(inst); useRef != null; useRef = LLVMGetNextUse(useRef)) {
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
    if (needName(opcode) && "".equals(name)) {
      name = "" + unnamedValueIndex;
      unnamedValueIndex++;
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
      case LLVMAdd: {
        instruction = new BinaryOperator(name, type, OpCode.ADD);
        OperatorFlags flag = new OperatorFlags();
        flag.setNoSignedWrapFlag();
        instruction.setOperatorFlags(flag);
      }
        break;
      case LLVMFAdd:
        instruction = new BinaryOperator(name, type, OpCode.FADD);
        break;
      case LLVMSub: {
        instruction = new BinaryOperator(name, type, OpCode.SUB);
        OperatorFlags flag = new OperatorFlags();
        flag.setNoSignedWrapFlag();
        instruction.setOperatorFlags(flag);
      }
        break;
      case LLVMFSub:
        instruction = new BinaryOperator(name, type, OpCode.FSUB);
        break;
      case LLVMMul: {
        instruction = new BinaryOperator(name, type, OpCode.MUL);
        OperatorFlags flag = new OperatorFlags();
        flag.setNoSignedWrapFlag();
        instruction.setOperatorFlags(flag);
      }
        break;
      case LLVMFMul:
        instruction = new BinaryOperator(name, type, OpCode.FMUL);
        break;
      case LLVMUDiv: {
        instruction = new BinaryOperator(name, type, OpCode.UDIV);
        OperatorFlags flag = new OperatorFlags();
        instruction.setOperatorFlags(flag);
      }
        break;
      case LLVMSDiv: {
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
      case LLVMAlloca: {
        int alignment = LLVMGetAlignment(inst);
        instruction = new AllocaInst(name, type, alignment);
      }
        break;
      case LLVMLoad: {
        int alignment = LLVMGetAlignment(inst);
        instruction = new LoadInst(name, type, alignment);
      }
        break;
      case LLVMStore: {
        int alignment = LLVMGetAlignment(inst);
        instruction = new StoreInst(name, type, alignment);
      }
        break;
      case LLVMGetElementPtr:
        instruction = new GetElementPtrInst(name, type);
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
        instruction = new FCmpInst(name, type);
        break;
      case LLVMPHI:
        instruction = new PhiNode(name, type);
        break;
      case LLVMCall:
        instruction = new CallInst(name, type);
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
    for (int i = 0; i < LLVMGetNumOperands(inst); i ++) {
      operands.add(convert(LLVMGetOperand(inst, i)));
    }
    instruction.setOperands(operands);
    instruction.setOriginalText(originalText);
    return instruction;
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

  private boolean needName(int opcode) {
    switch (opcode) {
      default:
        return true;
      case LLVMStore:
      case LLVMCall:
      case LLVMBr:
      case LLVMSwitch:
        return false;
    }
  }

  public Value convert(LLVMValueRef valueRef) {
    switch (LLVMGetValueKind(valueRef)) {
      case LLVMInstructionValueKind:
        return convertValueToInstruction(valueRef);
      case LLVMConstantIntValueKind:
        return convertValueToConstantInt(valueRef);
      case LLVMConstantExprValueKind:
        return null;
      case LLVMBasicBlockValueKind:
        return context.getBasicBlock(LLVMValueAsBasicBlock(valueRef));
      case LLVMMetadataAsValueValueKind:
        // TODO metadata
        throw new NotImplementedException();
      case LLVMArgumentValueKind:
        // TODO argument
        return convertValueToArgument(valueRef);
      case LLVMGlobalVariableValueKind:
        return context.getGlobalVariable(valueRef);
      case LLVMConstantPointerNullValueKind:
        // TODO null
        return new ConstantPointerNull(getType(LLVMTypeOf(valueRef)));
      case LLVMFunctionValueKind:
        return context.getFunction(valueRef);
      case LLVMInlineAsmValueKind:
        // TODO inline asm
        throw new NotImplementedException();
      case LLVMConstantFPValueKind:
        // TODO constant fp
        throw new NotImplementedException();
      case LLVMConstantAggregateZeroValueKind:
        return new ConstantAggregateZero();
    }
    LLVMDumpValue(valueRef);
    System.out.println(LLVMGetValueKind(valueRef));
    assert false : "unhandled convert llvm value ref";
    return null;
  }

  private Value convertValueToArgument(LLVMValueRef valueRef) {
    return new Argument(LLVMGetValueName(valueRef).getString(), getType(LLVMTypeOf(valueRef)));
  }

  public Constant convertValueToConstantInt(LLVMValueRef valueRef) {
    LLVMValueRef constantInt = LLVMIsAConstantInt(valueRef);
    assert constantInt != null : "constant int should not be null";
    IntegerType integerType = (IntegerType) getType(LLVMTypeOf(valueRef));
    int width = integerType.getBitWidth();
    long value = LLVMConstIntGetZExtValue(valueRef);
    return ConstantInt.get(integerType, new APInt(width, value, false));
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
        return Type.getIntNTy(context, size);
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
          Optional<String> name = Optional.fromNullable(LLVMGetStructName(typeRef)).transform(new com.google.common.base.Function<BytePointer, String>() {
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
    return result;
  }
}
