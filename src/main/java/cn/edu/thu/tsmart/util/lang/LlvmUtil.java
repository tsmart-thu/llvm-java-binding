/*
 * MIT License
 *
 * Copyright (c) 2018 Institute on Software System and Engineering, School of Software, Tsinghua University
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package cn.edu.thu.tsmart.util.lang;

import cn.edu.thu.tsmart.core.cfa.llvm.Argument;
import cn.edu.thu.tsmart.core.cfa.llvm.ArrayType;
import cn.edu.thu.tsmart.core.cfa.llvm.CallInst;
import cn.edu.thu.tsmart.core.cfa.llvm.ConstantExpr;
import cn.edu.thu.tsmart.core.cfa.llvm.ConstantInt;
import cn.edu.thu.tsmart.core.cfa.llvm.FunctionType;
import cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import cn.edu.thu.tsmart.core.cfa.llvm.IntegerType;
import cn.edu.thu.tsmart.core.cfa.llvm.LlvmFunction;
import cn.edu.thu.tsmart.core.cfa.llvm.PointerType;
import cn.edu.thu.tsmart.core.cfa.llvm.StructType;
import cn.edu.thu.tsmart.core.cfa.llvm.Type;
import cn.edu.thu.tsmart.core.cfa.llvm.Value;
import cn.edu.thu.tsmart.core.cfa.llvm.VectorType;
import cn.edu.thu.tsmart.util.Trouble;
import com.google.common.base.Optional;
import javax.annotation.Nullable;

/**
 * Created by zhoumin on 5/26/17.
 */
public class LlvmUtil {
  /**
   * Return the size of certain type (in bit)
   */
  public static long bitSize(Type type) {
//    // TODO: chenguang do we have this kind of API in Llvm
//    switch (type.getTypeID()) {
//      case DoubleTyID:
//        return type.getScalarSizeInBits();
//      case IntegerTyID:
//        return ((IntegerType) type).getBitWidth();
//      case PointerTyID:
//        return 64;
//      case ArrayTyID: {
//        ArrayType arrayType = (ArrayType) type;
//        Type elementType = arrayType.getElementType();
//        return bitSize(elementType) * arrayType.getNumElements();
//      }
//      case StructTyID: {
//        StructType structType = (StructType) type;
//        long result = 0;
//        int num = structType.getNumElements();
//        for (int i = 0; i < num; i++) {
//          Type elementType = structType.getElementType(i);
//          result += bitSize(elementType);
//        }
//        return result;
//      }
//      case VoidTyID: {
//        throw new IllegalArgumentException("can not get void type's bitsize");
//      }
//      default:
//        throw new RuntimeException("not implemented");
//    }
    return type.sizeInBytes() * 8;
  }

  /**
   * Return the byte size of a type
   */
  public static long byteSize(Type type) {
    return type.sizeInBytes();
  }

  /**
   * check type equals castType w.r.t type information
   *
   * @return true if they are the same
   */
  public static boolean sameType(Type type, Type castType) {
    // TODO:guzuxing implement this
    if (type.getTypeID() == castType.getTypeID()) {
      if (type instanceof PointerType) {
        PointerType type1 = (PointerType) type;
        PointerType type2 = (PointerType) castType;
        return sameType(type1.getPointerElementType(), type2.getPointerElementType());
      }

      if (type instanceof IntegerType) {
        IntegerType type1 = (IntegerType) type;
        IntegerType type2 = (IntegerType) castType;
        return type1.getBitWidth() == type2.getBitWidth();
      }

      if (type instanceof StructType) {
        StructType type1 = (StructType) type;
        StructType type2 = (StructType) castType;
        if (type1.hasName()) {
          if (type2.hasName()) {
            return type1.getName().equals(type2.getName());
          } else {
            return false;
          }
        } else {
          if (type2.hasName()) {
            return false;
          } else {
            // TODO: guzuxing deal with two anonymous struct
            throw new RuntimeException("do not determine anonymous struct");
          }
        }
      }

      if (type instanceof ArrayType) {
        ArrayType type1 = (ArrayType) type;
        ArrayType type2 = (ArrayType) castType;
        return sameType(type1.getElementType(), type2.getElementType())
            && type1.getNumElements() == type2.getNumElements();
      }
    }
    throw new RuntimeException("do not implement");
  }

  /**
   * extract elementType from aggregate type by index
   * getelementptr ty, ty* label, index_0, index_1, ...
   * we deal with the index_0 out of this method
   */
  public static Type extractInnerTypeByIndex(Type pType, long index) {
    // index != 0, type should be aggregate Type
    if (pType instanceof ArrayType) {
      return ((ArrayType) pType).getElementType();
    } else if (pType instanceof StructType) {
      return ((StructType) pType).getElementType((int) index);
    } else if (pType instanceof VectorType) {
      return ((VectorType) pType).getElementType();
    } else {
      throw new RuntimeException("do not implement: " + pType.toString());
    }
  }

  public static long calculateOffsetByTypeAndIndex(Type deType, long index) {
    if (deType instanceof StructType) {
      long offset = 0;
      for (long i = 0; i < index; i++) {
        Type prev = extractInnerTypeByIndex(deType, i);
        offset += LlvmUtil.byteSize(prev);
      }
      return offset;
    }

    if (deType instanceof ArrayType) {
      Type elementType = ((ArrayType) deType).getElementType();
      long offset = LlvmUtil.byteSize(elementType) * index;
      return offset;
    }

    long offset = LlvmUtil.byteSize(deType) * index;
    return offset;
  }

  /**
   * Whether the called function has been defined in the CFA
   * @param callInst
   * @return
   */
  public static boolean hasDefinition(CallInst callInst) {
    LlvmFunction function = callInst.getCalledFunction();
    return function != null && function.getBasicBlockList().size() > 0;
  }

  /**
   * Whether the function has non-void return value
   * @param function
   * @return
   */
  public static boolean hasReturnValue(LlvmFunction function) {
    Optional<FunctionType> opt = getFunctionType(function);
    return (opt.isPresent() && !opt.get().getReturnType().isVoidTy());
  }

  public static Optional<FunctionType> getFunctionType(LlvmFunction function) {
    Type t1 = function.getType();
    if (t1 instanceof PointerType) {
      Type t2 = ((PointerType) t1).getElementType();
      if (t2 instanceof FunctionType) {
        return Optional.of((FunctionType) t2);
      }
    }
    return Optional.absent();
  }

  // Resolver for pointer
  public interface FunctionPointerResolver {
    @Nullable
    FunctionType resolve(Value pointer);
  }

  // Always return null
  public static class NullResolver implements FunctionPointerResolver {

    private static final FunctionPointerResolver INSTANCE = new NullResolver();

    public static FunctionPointerResolver getInstance() {
      return INSTANCE;
    }

    private NullResolver() {}

    @Override
    public FunctionType resolve(Value pointer) {
      return null;
    }
  }

  /**
   * Guess the name of called function
   * @param callInst
   * @return may be null
   */
  @Nullable
  public static String determineCalledFunctionName(CallInst callInst, FunctionPointerResolver resolver) {
    if (callInst.getCalledFunction() != null) {
      return callInst.getCalledFunction().getName();
    } else {
      return retrieveFunctionName(callInst.getCalledValue(), resolver);
    }
  }

  @Nullable
  public static String determineCalledFunctionName(CallInst callInst) {
    return determineCalledFunctionName(callInst, NullResolver.getInstance());
  }

  /**
   * Retrieve function name from a constant expression
   * @return may be null
   */
  @Nullable
  public static String retrieveFunctionName(Value expr, FunctionPointerResolver resolver) {
    if (expr instanceof LlvmFunction) {
      LlvmFunction fun = (LlvmFunction) expr;
      return fun.getName();
    } else if (expr instanceof ConstantExpr) {
      ConstantExpr ce = (ConstantExpr) expr;
      if (ce.getOpcode() == OpCode.BITCAST) {
        return retrieveFunctionName(ce.getOperand(0), resolver);
      } else {
        return Trouble.notImplementedFor(expr);
      }
    } else if (isFunctionPointer(expr)) {
      // NOTE: function pointer not handled now
      // If there are multiple candidates, also return null here
      return null;
    } else {
      return Trouble.notImplementedFor(expr);
    }
  }

  @Nullable
  public static String retrieveFunctionName(Value expr) {
    return retrieveFunctionName(expr, NullResolver.getInstance());
  }

  /**
   * Check if the value is a function pointer
   */
  public static boolean isFunctionPointer(Value expr) {
    Type t = expr.getType();
    return t.isPointerTy() && ((PointerType) t).getElementType().isFunctionTy();
  }

  /**
   * Find the actual parameter that is associated with the given formal paramter
   */
  public static Optional<Value> findActualParameter(CallInst callInst, Argument argument) {
    // TODO: sometimes, the called function does not exist
    // TODO: maybe we should use getCalledValue to have a more general implementation
    int idx = callInst.getCalledFunction().getArgumentList().indexOf(argument);
    if (idx >= 0 && idx < callInst.getNumArgOperands()) {
      return Optional.of(callInst.getArgOperand(idx));
    } else {
      return Optional.absent();
    }
  }

  /**
   * Convert LLVM constant to integer
   * TODO: replace this method -> by range
   * @param value
   * @return
   */
  public static Optional<Long> constToInt(Value value) {
    if (value instanceof ConstantInt) {
      ConstantInt ci = (ConstantInt) value;
      return Optional.of(ci.getValue().getValue().longValue());
    } else {
      return Optional.absent();
    }
  }
}
