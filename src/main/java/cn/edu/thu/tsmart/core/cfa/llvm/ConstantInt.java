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
