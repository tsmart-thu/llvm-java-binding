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

import com.google.common.base.Optional;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.SizeTPointer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bytedeco.javacpp.LLVM.*;

import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;

/** @author guangchen on 03/03/2017. */
public class Converter {

  private final Context context;

  public Converter(Context context) {
    this.context = context;
  }

  public LlvmModule convert(LLVMModuleRef moduleRef) {
    SizeTPointer sizeTPointer = new SizeTPointer(64);
    String moduleIdentifier = LLVMGetModuleIdentifier(moduleRef, sizeTPointer).getString();
    Map<String, LlvmFunction> functionMap = new HashMap<>();
    for (LLVMValueRef f = LLVMGetFirstFunction(moduleRef); f != null; f = LLVMGetNextFunction(f)) {
      LlvmFunction func = convertValueToFunction(f);
      functionMap.put(func.getName(), func);
    }
    return new LlvmModule(moduleIdentifier, functionMap);
  }

  public LlvmFunction convertValueToFunction(LLVMValueRef function) {
    List<BasicBlock> basicBlockList = new ArrayList<>();
    for (LLVMBasicBlockRef bb = LLVMGetFirstBasicBlock(function);
        bb != null;
        bb = LLVMGetNextBasicBlock(bb)) {
      basicBlockList.add(convert(bb));
    }
    return new LlvmFunction(
        LLVMGetValueName(function).getString(), getType(LLVMTypeOf(function)), basicBlockList);
  }

  public BasicBlock convert(LLVMBasicBlockRef bb) {
    List<Instruction> instructionList = new ArrayList<>();
    for (LLVMValueRef inst = LLVMGetFirstInstruction(bb);
        inst != null;
        inst = LLVMGetNextInstruction(inst)) {
      instructionList.add(convertValueToInstruction(inst));
    }
    return new BasicBlock(
        LLVMGetValueName(LLVMBasicBlockAsValue(bb)).getString(),
        getType(LLVMTypeOf(LLVMBasicBlockAsValue(bb))),
        null,
        instructionList);
  }

  public Instruction convertValueToInstruction(LLVMValueRef inst) {
    int opcode = LLVMGetInstructionOpcode(inst);
    String name = LLVMGetValueName(inst).getString();
    Type type = getType(LLVMTypeOf(inst));
    switch (opcode) {
      case LLVMRet:
        return new ReturnInst(name, type);
      case LLVMBr:
        return new BranchInst(name, type);
      case LLVMSwitch:
        return new SwitchInst(name, type);
      case LLVMIndirectBr:
        return new IndirectBrInst(name, type);
      case LLVMInvoke:
        return new InvokeInst(name, type);
      case LLVMResume:
        return new ResumeInst(name, type);
      case LLVMUnreachable:
        return new UnreachableInst(name, type);
      case LLVMCleanupRet:
        return new CleanupReturnInst(name, type);
      case LLVMCatchRet:
        return new CatchReturnInst(name, type);
      case LLVMCatchSwitch:
        return new CatchSwitchInst(name, type);
      case LLVMAdd:
        return new BinaryOperator(name, type, OpCode.ADD);
      case LLVMFAdd:
        return new BinaryOperator(name, type, OpCode.FADD);
      case LLVMSub:
        return new BinaryOperator(name, type, OpCode.SUB);
      case LLVMFSub:
        return new BinaryOperator(name, type, OpCode.FSUB);
      case LLVMMul:
        return new BinaryOperator(name, type, OpCode.MUL);
      case LLVMFMul:
        return new BinaryOperator(name, type, OpCode.FMUL);
      case LLVMUDiv:
        return new BinaryOperator(name, type, OpCode.UDIV);
      case LLVMSDiv:
        return new BinaryOperator(name, type, OpCode.SDIV);
      case LLVMFDiv:
        return new BinaryOperator(name, type, OpCode.FDIV);
      case LLVMURem:
        return new BinaryOperator(name, type, OpCode.UREM);
      case LLVMSRem:
        return new BinaryOperator(name, type, OpCode.SREM);
      case LLVMFRem:
        return new BinaryOperator(name, type, OpCode.FREM);
      case LLVMShl:
        return new BinaryOperator(name, type, OpCode.SHL);
      case LLVMLShr:
        return new BinaryOperator(name, type, OpCode.LSHR);
      case LLVMAShr:
        return new BinaryOperator(name, type, OpCode.ASHR);
      case LLVMAnd:
        return new BinaryOperator(name, type, OpCode.AND);
      case LLVMOr:
        return new BinaryOperator(name, type, OpCode.OR);
      case LLVMXor:
        return new BinaryOperator(name, type, OpCode.XOR);
      case LLVMAlloca:
        return new AllocaInst(name, type);
      case LLVMLoad:
        return new LoadInst(name, type);
      case LLVMStore:
        return new StoreInst(name, type);
      case LLVMGetElementPtr:
        return new GetElementPtrInst(name, type);
      case LLVMFence:
        return new FenceInst(name, type);
      case LLVMAtomicCmpXchg:
        return new AtomicCmpXchgInst(name, type);
      case LLVMAtomicRMW:
        return new AtomicRMWInst(name, type);
      case LLVMTrunc:
        return new TruncInst(name, type);
      case LLVMZExt:
        return new ZExtInst(name, type);
      case LLVMSExt:
        return new SExtInst(name, type);
      case LLVMFPToUI:
        return new FPToUIInst(name, type);
      case LLVMFPToSI:
        return new FPToSIInst(name, type);
      case LLVMUIToFP:
        return new UIToFPInst(name, type);
      case LLVMSIToFP:
        return new SIToFPInst(name, type);
      case LLVMFPTrunc:
        return new FPTruncInst(name, type);
      case LLVMFPExt:
        return new FPExtInst(name, type);
      case LLVMPtrToInt:
        return new PtrToIntInst(name, type);
      case LLVMIntToPtr:
        return new IntToPtrInst(name, type);
      case LLVMBitCast:
        return new BitCastInst(name, type);
      case LLVMAddrSpaceCast:
        return new AddrSpaceCastInst(name, type);
      case LLVMCleanupPad:
        return new CleanupPadInst(name, type);
      case LLVMCatchPad:
        return new CatchPadInst(name, type);
      case LLVMICmp:
        return new ICmpInst(name, type);
      case LLVMFCmp:
        return new FCmpInst(name, type);
      case LLVMPHI:
        return new PhiNode(name, type);
      case LLVMCall:
        return new CallInst(name, type);
      case LLVMSelect:
        return new SelectInst(name, type);
      case LLVMUserOp1:
      case LLVMUserOp2:
        throw new IllegalArgumentException("UserOp1 / UserOp2 should not appear in ir file");
      case LLVMVAArg:
        return new VAArgInst(name, type);
      case LLVMExtractElement:
        return new ExtractElementInst(name, type);
      case LLVMInsertElement:
        return new InsertElementInst(name, type);
      case LLVMShuffleVector:
        return new ShuffleVectorInst(name, type);
      case LLVMExtractValue:
        return new ExtractValueInst(name, type);
      case LLVMInsertValue:
        return new InsertValueInst(name, type);
      case LLVMLandingPad:
        return new LandingPadInst(name, type);
      default:
        throw new IllegalArgumentException("Unhandled instruction: " + inst.toString());
    }
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
