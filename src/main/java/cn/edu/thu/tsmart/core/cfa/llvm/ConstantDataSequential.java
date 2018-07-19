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
