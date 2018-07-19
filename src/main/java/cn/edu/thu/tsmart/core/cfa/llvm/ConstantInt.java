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

import java.util.HashMap;

/**
 * Created by zhch on 2017/4/8.
 */
public class ConstantInt extends ConstantData {

  private APInt val;
  private static HashMap<APInt, ConstantInt> instances = new HashMap<>();

  private ConstantInt(String name, IntegerType type, APInt val) {
    super(name, type);
    this.val = val;
  }

  public static ConstantInt get(IntegerType type, APInt val) {
    if (instances.containsKey(val)) {
      return instances.get(val);
    } else {
      ConstantInt ci = new ConstantInt("", type, val);
      instances.put(val, ci);
      return ci;
    }
  }

  public final APInt getValue() {
    return val;
  }

  public int getBitWidth() {
    return val.getBitWidth();
  }

  // NOTICE return type uses long to store u_int64
  public long getZExtValue() {
    return val.getZExtValue();
  }

  public long getSExtValue() {
    return val.getSExtValue();
  }

  public boolean isNegative() {
    return val.isNegative();
  }

  public boolean isZero() {
    return val.isZero();
  }

  public boolean isOne() {
    return val.countLeadingZeros() == val.getBitWidth() - 1;
  }

  public boolean isMinusOne() {
    return val.isAllOnesValue();
  }

  public boolean isMaxValue(boolean isSigned) {
    if (isSigned) {
      return val.isMaxSignedValue();
    } else {
      return val.isMaxValue();
    }
  }

  public boolean isMinValue(boolean isSigned) {
    if (isSigned) {
      return val.isMinSignedValue();
    } else {
      return val.isMinValue();
    }
  }

  @Override
  public String getName() {
    return "CONSTANT_INT";
  }

  @Override
  public String toString() {
    if(val.getBitWidth() == 1)
      return String.valueOf(val.getBoolValue());
    else {
      return String.valueOf(val.getSExtValue());
      //return String.valueOf(this.val.getLimitedValue(Long.MAX_VALUE));
    }
  }
}
