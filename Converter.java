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

import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import static org.bytedeco.javacpp.LLVM.*;

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
    BasicBlock basicBlock = context.getBasicBlock(bb);
    if (basicBlock != null) {
      return basicBlock;
    }
    List<Instruction> instructionList = new ArrayList<>();
    basicBlock = new BasicBlock(LLVMGetValueName(LLVMBasicBlockAsValue(bb)).getString(),
            getType(LLVMTypeOf(LLVMBasicBlockAsValue(bb))), null);
    context.putBasicBlock(bb, basicBlock);
    for (LLVMValueRef inst = LLVMGetFirstInstruction(bb);
        inst != null;
        inst = LLVMGetNextInstruction(inst)) {
      instructionList.add(convertValueToInstruction(inst));
    }
    basicBlock.setInstList(instructionList);
    return basicBlock;
  }

  public Instruction convertValueToInstruction(LLVMValueRef inst) {
    int opcode = LLVMGetInstructionOpcode(inst);
    String name = LLVMGetValueName(inst).getString();
    Type type = getType(LLVMTypeOf(inst));
    Instruction instruction = context.getInst(inst);
    if (instruction != null) {
      return instruction;
    }
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
        instruction = new BinaryOperator(name, type, OpCode.ADD);
        break;
      case LLVMFAdd:
        instruction = new BinaryOperator(name, type, OpCode.FADD);
        break;
      case LLVMSub:
        instruction = new BinaryOperator(name, type, OpCode.SUB);
        break;
      case LLVMFSub:
        instruction = new BinaryOperator(name, type, OpCode.FSUB);
        break;
      case LLVMMul:
        instruction = new BinaryOperator(name, type, OpCode.MUL);
        break;
      case LLVMFMul:
        instruction = new BinaryOperator(name, type, OpCode.FMUL);
        break;
      case LLVMUDiv:
        instruction = new BinaryOperator(name, type, OpCode.UDIV);
        break;
      case LLVMSDiv:
        instruction = new BinaryOperator(name, type, OpCode.SDIV);
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
        instruction = new AllocaInst(name, type);
        break;
      case LLVMLoad:
        instruction = new LoadInst(name, type);
        break;
      case LLVMStore:
        instruction = new StoreInst(name, type);
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
        instruction = new ICmpInst(name, type);
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
    List<Use> uses = new ArrayList<>();
    int i = 0;
    for (LLVMUseRef useRef = LLVMGetFirstUse(inst); useRef != null; useRef = LLVMGetNextUse(useRef)) {
      LLVMValueRef userRef = LLVMGetUser(useRef);
      uses.add(new Use(instruction, convertValueToInstruction(userRef), i));
      i++;
    }
    instruction.setUses(uses);
    List<Value> operands = new ArrayList<>();
    for (i = 0; i < LLVMGetNumOperands(inst); i ++) {
      operands.add(convert(LLVMGetOperand(inst, i)));
    }
    instruction.setOperands(operands);
    return instruction;
  }

  public Value convert(LLVMValueRef valueRef) {
    if (LLVMIsAInstruction(valueRef) != null) {
      return convertValueToInstruction(valueRef);
    } else if (LLVMIsConstant(valueRef) != 0) {
      return convertValueToConstant(valueRef);
    } else if (LLVMIsABasicBlock(valueRef) != null) {
      return convert(LLVMValueAsBasicBlock(valueRef));
    }
    assert false : "unhandled convert llvm value ref";
    return null;
  }

  public Constant convertValueToConstant(LLVMValueRef valueRef) {
    return new Constant("", getType(LLVMTypeOf(valueRef)));
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
