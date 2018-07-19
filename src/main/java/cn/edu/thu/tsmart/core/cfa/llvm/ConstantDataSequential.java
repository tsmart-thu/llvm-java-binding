/*
 * Copyright (c) 2018
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

import cn.edu.thu.tsmart.core.cfa.util.Casting;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author guangchen on 12/06/2017.
 */
public class ConstantDataSequential extends Constant {

  public ConstantDataSequential(String name, Type type) {
    super(name, type);
  }

  public long getElementAsInteger(int i) {
    if (getElementType().isIntegerTy()) {
      if (i < getNumOperands()) {
        return ((ConstantInt) getOperand(i)).getValue().getValue().longValue();
      } else {
        assert false :
            "Try to visit element in " + (i + 1) + " while operands num is" + getNumOperands();
      }
    } else {
      assert false : "Accessor can only be used when element is an integer";
    }
    return 0;
  }
  //
  //    public APFloat getElementAsAPFloat(int i) {
  //        throw new NotImplementedException();
  //    }

  public float getElementAsFloat(int i) {
    if (getElementType().isFloatTy()) {
      if (i < getNumOperands()) {
        return ((ConstantInt) getOperand(i)).getValue().getValue().floatValue();
      } else {
        assert false :
            "Try to visit element in " + (i + 1) + " while operands num is" + getNumOperands();
      }
    } else {
      assert false : "Accessor can only be used when element is an integer";
    }
    return 0;
  }

  public double getElementAsDouble(int i) {
    if (getElementType().isDoubleTy()) {
      if (i < getNumOperands()) {
        return ((ConstantInt) getOperand(i)).getValue().getValue().doubleValue();
      } else {
        assert false :
            "Try to visit element in " + (i + 1) + " while operands num is" + getNumOperands();
      }
    } else {
      assert false : "Accessor can only be used when element is an integer";
    }
    return 0;
  }

  public Constant getElementAsConstant(int i) {
    Type elementType = getElementType();
    if (elementType.isHalfTy()) {
      if (i < getNumOperands()) {
        return new ConstantFP("", elementType,
            ((ConstantInt) getOperand(i)).getValue().getValue().doubleValue(), false);
      } else {
        assert false :
            "Try to visit element in " + (i + 1) + " while operands num is" + getNumOperands();
      }
    } else if (elementType.isFloatTy()) {
      return new ConstantFP("", elementType, getElementAsFloat(i), false);
    } else if (elementType.isDoubleTy()) {
      return new ConstantFP("", elementType, getElementAsDouble(i), false);
    }
    IntegerType integerType = Casting.cast(elementType, IntegerType.class);
    return ConstantInt.get(integerType,
        new APInt(integerType.getBitWidth(), String.valueOf(getElementAsInteger(i))));
  }

  public SequentialType getType() {
    return Casting.cast(super.getType(), SequentialType.class);
  }

  public Type getElementType() {
    return getType().getElementType();
  }

  public long getNumElements() {
    ArrayType arrayType = Casting.dyncast(getType(), ArrayType.class);
    if (arrayType != null) {
      return arrayType.getNumElements();
    }
    return getType().getVectorNumElements();
  }

  public long getElementByteSize() {
    return getElementType().getPrimitiveSizeInBits() / 8;
  }

  public boolean isString() {
    if (getElementType().isIntegerTy()) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isCString() {
    throw new NotImplementedException();
  }

  public String getAsString() {
    if (isString()) {
      StringBuffer sbu = new StringBuffer();
      java.util.List<Value> value = getOperands();
      for (Value v : value) {
        sbu.append((char) Integer.parseInt(v.toString()));
      }
      return sbu.toString();
    } else {
      return "";
    }
  }

  public String getAsCString() {
    throw new NotImplementedException();
  }

  public String getRawDataValues() {
    throw new NotImplementedException();
  }
}
