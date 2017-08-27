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

import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedInteger;
import java.math.BigInteger;
import javolution.io.Union;
import org.apfloat.Apfloat;

/**
 * @author guangchen on 05/07/2017.
 */
public class APFloat {
  private FltSemantics semantics;
  private Significand significand;
  private int sign = 1;
  private fltCategory category;
  private int exponent;

  public static class Significand extends Union {
    int part;
    int[] parts;
  }
  public enum fltCategory {
  fcInfinity,
       fcNaN,
       fcNormal,
       fcZero
   }
  public static class FltSemantics {
    public short maxExponent;
    public short minExponent;
    public int precision;
    public int sizeInBits;

    public FltSemantics(short maxExponent, short minExponent, int precision, int sizeInBits) {
      this.maxExponent = maxExponent;
      this.minExponent = minExponent;
      this.precision = precision;
      this.sizeInBits = sizeInBits;
    }
    public FltSemantics() {}

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      FltSemantics that = (FltSemantics) o;

      if (maxExponent != that.maxExponent) {
        return false;
      }
      if (minExponent != that.minExponent) {
        return false;
      }
      if (precision != that.precision) {
        return false;
      }
      return sizeInBits == that.sizeInBits;
    }

    @Override
    public int hashCode() {
      int result = (int) maxExponent;
      result = 31 * result + (int) minExponent;
      result = 31 * result + precision;
      result = 31 * result + sizeInBits;
      return result;
    }
  }
  public static final UnsignedInteger integerPartWidth = UnsignedInteger.valueOf(64);
  public static final FltSemantics semIEEEhalf = new FltSemantics((short)15, (short)-14, 11, 16);
  public static final FltSemantics semIEEEsingle = new FltSemantics((short)127, (short)-126, 24, 32);
  public static final FltSemantics semIEEEdouble = new FltSemantics((short)1023, (short)-1022, 53, 64);
  public static final FltSemantics semIEEEquad = new FltSemantics((short)16383, (short)-16382, 113, 128);
  public static final FltSemantics semIEEEX87DoubleExtended = new FltSemantics((short)16383, (short)-16382, 64, 80);
  public static final FltSemantics semIEEEX87Bogus = new FltSemantics((short)0, (short)-0, 0, 0);
  private void initFromAPInt(FltSemantics fltSemantics, APInt apInt) {
    if (fltSemantics.equals(semIEEEsingle)) {
      initFromFloatAPInt(apInt);
    } else if (fltSemantics.equals(semIEEEdouble)) {
      initFromDoubleAPInt(apInt);
    } else {
      assert false;
    }
  }

  private void initFromDoubleAPInt(APInt apInt) {
    Preconditions.checkArgument(apInt.getBitWidth() == 64);
    long i = apInt.getValue().longValue();
    long myexponent = (i>>52) & 0x7ff;
    long mysignificand = i & 0xfffffffffffffL;

    initialize(semIEEEdouble);
    Preconditions.checkArgument(partCount().compareTo(UnsignedInteger.valueOf(1)) == 0);

    sign = (int) (i>>63);
    if (myexponent==0 && mysignificand ==0) {
      category = fltCategory.fcZero;
    } else if (myexponent == 0x7ff && mysignificand ==0) {
      category = fltCategory.fcInfinity;
    } else if (myexponent == 0x7ff && myexponent != 0) {
      category = fltCategory.fcNaN;
      significand.parts = new int[]{(int) mysignificand, (int) (mysignificand>>32)};
    } else {
      category = fltCategory.fcNormal;
      exponent = (int) (myexponent - 1023);
      significand.parts = new int[]{(int) mysignificand, (int) (mysignificand>>32)};
      if (myexponent == 0) {
        exponent = -1022;
      } else {
        mysignificand = mysignificand | 0x10000000000000L;
        significand.parts = new int[]{(int) mysignificand, (int) (mysignificand>>32)};
      }
    }
  }

  private void initFromFloatAPInt(APInt apInt) {
    Preconditions.checkArgument(apInt.getBitWidth()==32);
    int i = apInt.getValue().intValue();
    int myexponent = (i >> 23) & 0xff;
    int mysignificand = i & 0x7fffff;
    initialize(semIEEEsingle);
    Preconditions.checkArgument(partCount().compareTo(UnsignedInteger.valueOf(1))==0);
    sign = i >> 31;
    if (myexponent == 0 && mysignificand ==0) {
      category = fltCategory.fcZero;
    } else if (myexponent==0xff && mysignificand == 0) {
      category = fltCategory.fcInfinity;
    } else if (myexponent==0xff && mysignificand!=0) {
      category = fltCategory.fcNaN;
      significand.part = mysignificand;
    } else {
      category = fltCategory.fcNormal;
      exponent = myexponent - 127; // bias
      significand.part = mysignificand;
      if (myexponent == 0) {
        exponent = -126;
      } else {
        significand.part = significand.part | 0x800000;
      }
    }
  }
  private void initialize(FltSemantics ourSemantics) {
    semantics = ourSemantics;
    significand = new Significand();
    UnsignedInteger count = partCount();
    if (count.compareTo(UnsignedInteger.valueOf(1)) > 0) {
      significand.parts = new int[count.intValue()];
    }
  }
  private UnsignedInteger partCount() {
    return partCountForBits(UnsignedInteger.valueOf(semantics.precision + 1));
  }
  private static UnsignedInteger partCountForBits(UnsignedInteger bits) {
    return ((bits.plus(integerPartWidth).minus(UnsignedInteger.valueOf(1))).dividedBy(integerPartWidth));
  }
  private double dValue;
  private boolean initFromDValue = false;
  public APFloat(double d) {
    this.dValue = d;
    this.initFromDValue = true;
//    initFromAPInt(semIEEEdouble, APInt.doubleToBits(d));
  }
  public APFloat(float f) {
    initFromAPInt(semIEEEsingle, APInt.floatToBits(f));
  }
  public enum OpStatus {
    OpOK,
    OpInvalidOp,
    OpDivByZero,
    OpOverflow,
    OpUnderflow,
    OpInexact
  }
  public enum RoundingMode {
    RmNearestTiesToEven,
    RmTowardPositive,
    RmTowardNegative,
    RmTowardZero,
    RmNearestTiesToAway
  }
  public enum CmpResult {
    CmpLessThan,
    CmpEqual,
    CmpGreaterThan,
    CmpUnordered
  }
  public OpStatus add(APFloat rhs, RoundingMode roundingMode) {
    return addOrSubtract(rhs, roundingMode, false);
  }

  private OpStatus addOrSubtract(APFloat rhs, RoundingMode roundingMode, boolean subtract) {
    OpStatus fs;
    fs = addOrSubtractSpecials(rhs, subtract);
    if (fs == OpStatus.OpDivByZero) {
      assert false;
    }
    if (category == fltCategory.fcZero) {
      if (rhs.category != fltCategory.fcZero || (sign == rhs.sign) == subtract) {
        sign = (roundingMode == RoundingMode.RmTowardNegative) ? 1 : 0;
      }
    }
    return fs;
  }

  private OpStatus addOrSubtractSpecials(APFloat rhs, boolean subtract) {
    if ((category == fltCategory.fcNaN) ||
        (category == fltCategory.fcNormal && rhs.category == fltCategory.fcZero) ||
        (category == fltCategory.fcInfinity && (rhs.category == fltCategory.fcNormal || rhs.category == fltCategory.fcZero))) {
      return OpStatus.OpOK;
    }
    if ((category == fltCategory.fcZero || category == fltCategory.fcNormal || category == fltCategory.fcInfinity) && rhs.category == fltCategory.fcNaN) {
      sign = rhs.sign ^ (subtract ? 1 : 0);
      category = fltCategory.fcNaN;
      significand = rhs.significand;
      return OpStatus.OpOK;
    }
    if (category == fltCategory.fcZero && rhs.category == fltCategory.fcNormal) {
      assign(rhs);
      sign = rhs.sign ^ (subtract ? 1 : 0);
      return OpStatus.OpOK;
    }
    if (category == fltCategory.fcZero && rhs.category == fltCategory.fcZero) {
      return OpStatus.OpOK;
    }
    if (category == fltCategory.fcInfinity && rhs.category == fltCategory.fcInfinity) {
      if (((sign ^ rhs.sign) != 0) != subtract) {
        makeNaN();
        return OpStatus.OpInvalidOp;
      }
      return OpStatus.OpOK;
    }
    if (category == fltCategory.fcNormal && category == fltCategory.fcNormal) {
      return OpStatus.OpDivByZero;
    }
    assert false;
    return null;
  }

  private void makeNaN() {
    assert false;
  }

  private void assign(APFloat rhs) {
    Preconditions.checkArgument(semantics == rhs.semantics);
    sign = rhs.sign;
    category = rhs.category;
    exponent = rhs.exponent;
    if (isFiniteNonZero() || category == fltCategory.fcNaN) {
      significand = rhs.significand;
    }
  }

  public OpStatus subtract(APFloat rhs, RoundingMode roundingMode) {
    return addOrSubtract(rhs, roundingMode, true);
  }
  public OpStatus multiply(APFloat rhs, RoundingMode roundingMode) {
    OpStatus fs;
    sign = sign ^ rhs.sign;
    fs = multiplySpecials(rhs);
    if (isFiniteNonZero()) {
      assert false;
    }
    return fs;
  }

  private OpStatus multiplySpecials(APFloat rhs) {
    if (category == fltCategory.fcNaN) {
      sign = 0;
      return OpStatus.OpOK;
    }
    if ((category == fltCategory.fcZero || category == fltCategory.fcNormal || category == fltCategory.fcInfinity) && rhs.category == fltCategory.fcNaN) {
      sign = 0;
      category = fltCategory.fcNaN;
      significand = rhs.significand;
      return OpStatus.OpOK;
    }
    if ((category == fltCategory.fcNormal && rhs.category == fltCategory.fcInfinity) ||
        (category == fltCategory.fcInfinity && (rhs.category == fltCategory.fcNormal || rhs.category == fltCategory.fcInfinity))) {
      category = fltCategory.fcInfinity;
      return OpStatus.OpOK;
    }
    if ((category == fltCategory.fcZero && rhs.category == fltCategory.fcInfinity) ||
        (category == fltCategory.fcInfinity && rhs.category == fltCategory.fcZero)) {
      makeNaN();
      return OpStatus.OpInvalidOp;
    }
    if (category == fltCategory.fcNormal && rhs.category == fltCategory.fcNormal) {
      return OpStatus.OpOK;
    }
    assert false;
    return null;
  }

  public OpStatus divide(APFloat rhs, RoundingMode roundingMode) {
    return OpStatus.OpOK;
  }
  public OpStatus remainder(APFloat rhs) {
    return OpStatus.OpOK;
  }
  public OpStatus mod(APFloat rhs) {
    return OpStatus.OpOK;
  }
  public OpStatus fusedMultiplyAdd(APFloat multiplicand, APFloat addend, RoundingMode roundingMode) {
    return OpStatus.OpOK;
  }
  public OpStatus roundToIntegral(RoundingMode roundingMode) {
    return OpStatus.OpOK;
  }
  public OpStatus next(boolean nextDown) {
    return OpStatus.OpOK;
  }
  public void changeSign() {
  }
  public void clearSign() {
  }
  public void copySign(Apfloat rhs) {
  }
  public APInt bitcastToAPInt() {
    return new APInt(0, new BigInteger("0"));
  }
  public double convertToDouble() {
    return 0.0;
  }
  public float convertToFloat() {
    return 0.0f;
  }
  public CmpResult compare(Apfloat rhs) {
    return CmpResult.CmpEqual;
  }
  public boolean bitwiseIsEqual(APFloat rhs) {
    return true;
  }
  public boolean isZero() {
    return false;
  }
  public boolean isInfinity() {
    return false;
  }
  public boolean isNaN() {
    return false;
  }
  public boolean isNegative() {
    return false;
  }
  public boolean isDenormal() {
    return false;
  }
  public boolean isSignaling() {
    return false;
  }
  public boolean isNormal() {
    return false;
  }
  public boolean isFinite() {
    return false;
  }
  public boolean isNonZero() {
    return false;
  }
  public boolean isFiniteNonZero() {
    return false;
  }
  public boolean isPosZero() {
    return false;
  }
  public boolean isNegZero() {
    return false;
  }
  public boolean isSmallest() {
    return false;
  }
  public boolean isLargest() {
    return false;
  }
  public boolean isInteger() {
    return false;
  }

  @Override
  public String toString() {
    if (this.initFromDValue) {
      return String.format("%e", dValue);
    }
    switch (category) {
      case fcInfinity:
        if (isNegative()) {
          return "-Inf";
        } else {
          return "+Inf";
        }
      case fcNaN:
        return "NaN";
      case fcZero: {
        String result = "";
        if (isNegative()) {
          result += "-";
        }
        result += "0.0E+0";
        return result;
      }
      case fcNormal:
        break;
    }
    String str = "";
    if (isNegative()) {
      str += "-";
    }
    int exp = exponent - ((int) semantics.precision - 1);
    return "";
  }
}
