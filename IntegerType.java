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

import cn.edu.thu.tsmart.core.util.math.MathExtras;

/** @author guangchen on 01/03/2017. */
public class IntegerType extends Type {
  private final int bitWidth;

  public IntegerType(Context context, int n) {
    super(context, TypeID.IntegerTyID);
    assert n >= 1 : "n >= 1";
    this.bitWidth = n;
  }

  public int getBitWidth() {
    return this.bitWidth;
  }

  public long getBitMask() {
    return ~0L >> (64 - getBitWidth());
  }

  public long getSignBit() {
    return 1L << (getBitWidth() - 1);
  }

  public boolean isPowerOf2ByteWidth() {
      int bitWidth = getBitWidth();
      return (bitWidth > 7) && MathExtras.isPowerOf2_32(bitWidth);
  }
}
