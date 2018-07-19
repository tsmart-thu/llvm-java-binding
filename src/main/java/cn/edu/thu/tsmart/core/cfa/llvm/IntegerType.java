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

// import cn.edu.thu.tsmart.core.util.math.MathExtras;

import java.util.Objects;

/**
 * @author guangchen on 01/03/2017.
 */
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
//      return (bitWidth > 7) && MathExtras.isPowerOf2_32(bitWidth);
    return false;
  }

  @Override
  public String toString() {
    return "i" + bitWidth;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IntegerType that = (IntegerType) o;
    return bitWidth == that.bitWidth;
  }

  @Override
  public int hashCode() {
    return bitWidth;
  }
}
