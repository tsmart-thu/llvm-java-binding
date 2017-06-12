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

import cn.edu.thu.tsmart.core.cfa.util.Casting;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/** @author guangchen on 12/06/2017. */
public class ConstantDataSequential extends Constant {
  public long getElementAsInteger(int i) {
    throw new NotImplementedException();
  }
  //
  //    public APFloat getElementAsAPFloat(int i) {
  //        throw new NotImplementedException();
  //    }

  public float getElementAsFloat(int i) {
    throw new NotImplementedException();
  }

  public double getElementAsDouble(int i) {
    throw new NotImplementedException();
  }

  public Constant getElementAsConstant(int i) {
      Type elementType = getElementType();
      if (elementType.isHalfTy() || elementType.isFloatTy() || elementType.isDoubleTy()) {
          // TODO APFloat
          throw new NotImplementedException();
      }
      IntegerType integerType = Casting.cast(elementType, IntegerType.class);
      return ConstantInt.get(integerType, new APInt(integerType.getBitWidth(), String.valueOf(getElementAsInteger(i))));
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
      return getElementType().getPrimitiveSizeInBits()/8;
  }

  public boolean isString() {
    throw new NotImplementedException();
  }

  public boolean isCString() {
    throw new NotImplementedException();
  }

  public String getAsString() {
    throw new NotImplementedException();
  }

  public String getAsCString() {
    throw new NotImplementedException();
  }

  public String getRawDataValues() {
    throw new NotImplementedException();
  }
}
