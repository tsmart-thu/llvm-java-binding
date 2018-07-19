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

import cn.edu.thu.sse.common.util.Pair;
import com.google.common.base.Objects;

import java.math.BigInteger;

/**
 * Created by zhch on 2017/4/10.
 */
public class APInt {

    private final BigInteger val;
    private final int bitWidth;

    public APInt(int numBits, String val) {
        assert numBits > 0 : "Require positive numBits!";

        this.bitWidth = numBits;
        BigInteger bi = new BigInteger(val);

        if (val.startsWith("-") && !val.equals("-0")) {
            assert bi.compareTo(BigInteger.ONE.shiftLeft(numBits - 1).negate())
                    >= 0 : "Require larger numBits!";
            bi = BigInteger.ONE.shiftLeft(numBits).add(bi);
        } else {
            assert bi.compareTo(BigInteger.ONE.shiftLeft(numBits)) < 0 : "numBits is too small!";
        }

        this.val = bi;
    }

    // NOTICE the 2nd parameter type uses long to store u_int64
    public APInt(int numBits, long val, boolean isSigned) {
        assert numBits > 0 : "Require positive numBits!";

        this.bitWidth = numBits;
        String sVal = Long.toString(val);
        BigInteger bi = new BigInteger(sVal);

        if (val >= 0) {
            assert bi.compareTo(BigInteger.ONE.shiftLeft(numBits)) < 0 : "numBits is too small!";
        } else if (!isSigned) {
            assert numBits >= 64 : "Require larger numBits!";
            bi = BigInteger.ONE.shiftLeft(64).add(bi);
        } else {
            assert bi.compareTo(BigInteger.ONE.shiftLeft(numBits - 1).negate())
                    >= 0 : "Require larger numBits!";
            bi = BigInteger.ONE.shiftLeft(numBits).add(bi);
        }

        this.val = bi;
    }

    public APInt(int numBits, BigInteger val) {
        assert
                val.compareTo(BigInteger.ZERO) >= 0 && val.bitLength() <= numBits : "Invalid construction";
        this.bitWidth = numBits;
        this.val = val;
    }

    public boolean isZero() {
        return val.compareTo(BigInteger.ZERO) == 0;
    }

    public boolean isNegative() {
        return val.testBit(bitWidth - 1);
    }

    public boolean isNonNegative() {
        return !isNegative();
    }

    public boolean isStrictlyPositive() {
        return isNonNegative() && !isZero();
    }

    public boolean isAllOnesValue() {
        return val.compareTo(BigInteger.ONE.shiftLeft(bitWidth).subtract(BigInteger.ONE)) == 0;
    }

    public boolean isMaxValue() {
        return isAllOnesValue();
    }

    public boolean isMaxSignedValue() {
        return val.compareTo(BigInteger.ONE.shiftLeft(bitWidth - 1).subtract(BigInteger.ONE)) == 0;
    }

    public boolean isMinValue() {
        return isZero();
    }

    public boolean isMinSignedValue() {
        return val.compareTo(BigInteger.ONE.shiftLeft(bitWidth - 1)) == 0;
    }

    public boolean isIntN(int n) {
        assert n > 0 : "Invalid value for n!";
        return getActiveBits() <= n;
    }

    public boolean isSignedIntN(int n) {
        assert n > 0 : "Invalid value for n!";
        return getMinSignedBits() <= n;
    }

    public boolean isPowerOf2() {
        return val.bitCount() == 1;
    }

    public boolean isSignBit() {
        return isMinSignedValue();
    }

    public boolean getBoolValue() {
        return !isZero();
    }

    // NOTICE both parameter and return type uses long to store u_int64
    public long getLimitedValue(long limit) {
        BigInteger limitVal = new BigInteger(Long.toString(limit));
        if (limit < 0) {
            limitVal = BigInteger.ONE.shiftLeft(64).add(limitVal);
        }
        if (val.compareTo(limitVal) < 0) {
            return getZExtValue();
        } else {
            return limit;
        }
    }

    public boolean isSplat(int n) {
        assert bitWidth % n == 0 : "Bit width must be devided by n!";
        for (int i = 1; i < bitWidth / n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (val.testBit(j + i * n) != val.testBit(j)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isMask(int numBits) {
        assert numBits > 0 : "numBits must be positive!";
        assert numBits <= bitWidth : "numBits out of range!";
        return isMask() && val.bitLength() == numBits;
    }

    public boolean isMask() {
        return val.bitCount() == val.bitLength();
    }

    public boolean isShiftedMask() {
        return val.bitCount() + val.getLowestSetBit() == val.bitLength();
    }

    public boolean eq(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Comparison requires equal bit widths";
        return val.compareTo(rhs.getValue()) == 0;
    }

    public boolean neq(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Comparison requires equal bit widths";
        return val.compareTo(rhs.getValue()) != 0;
    }

    public boolean ult(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Comparison requires equal bit widths";
        return val.compareTo(rhs.getValue()) < 0;
    }

    public boolean ule(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Comparison requires equal bit widths";
        return val.compareTo(rhs.getValue()) <= 0;
    }

    public boolean ugt(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Comparison requires equal bit widths";
        return val.compareTo(rhs.getValue()) > 0;
    }

    public boolean uge(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Comparison requires equal bit widths";
        return val.compareTo(rhs.getValue()) >= 0;
    }

    public boolean slt(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Comparison requires equal bit widths";
        if (isNegative() == rhs.isNegative()) {
            return val.compareTo(rhs.getValue()) < 0;
        } else {
            return val.compareTo(rhs.getValue()) > 0;
        }
    }

    public boolean sle(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Comparison requires equal bit widths";
        if (isNegative() == rhs.isNegative()) {
            return val.compareTo(rhs.getValue()) <= 0;
        } else {
            return val.compareTo(rhs.getValue()) >= 0;
        }
    }

    public boolean sgt(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Comparison requires equal bit widths";
        if (isNegative() == rhs.isNegative()) {
            return val.compareTo(rhs.getValue()) > 0;
        } else {
            return val.compareTo(rhs.getValue()) < 0;
        }
    }

    public boolean sge(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Comparison requires equal bit widths";
        if (isNegative() == rhs.isNegative()) {
            return val.compareTo(rhs.getValue()) >= 0;
        } else {
            return val.compareTo(rhs.getValue()) <= 0;
        }
    }

    public boolean intersects(APInt rhs) {
        return val.and(rhs.getValue()).compareTo(BigInteger.ZERO) == 0;
    }

    public APInt trunc(int width) {
        assert width > 0 && width < bitWidth : "Invalid APInt Truncate request";
        BigInteger newVal = val;
        for (int i = width; i < bitWidth; ++i) {
            newVal = newVal.clearBit(i);
        }
        return new APInt(width, newVal);
    }

    public APInt sext(int width) {
        assert width > bitWidth : "Invalid APInt SignExtend request";
        if (isNonNegative()) {
            return zext(width);
        }
        BigInteger newVal = val;
        for (int i = bitWidth; i < width; ++i) {
            newVal = newVal.setBit(i);
        }
        return new APInt(width, newVal);
    }

    public APInt zext(int width) {
        assert width > bitWidth : "Invalid APInt ZeroExtend request";
        return new APInt(width, val);
    }

    public APInt sextOrTrunc(int width) {
        assert width > 0 : "Invalid APInt SignExtendOrTruncate request";
        if (width < bitWidth) {
            return trunc(width);
        } else if (width > bitWidth) {
            return sext(width);
        } else {
            return new APInt(width, val);
        }
    }

    public APInt zextOrTrunc(int width) {
        assert width > 0 : "Invalid APInt ZeroExtendOrTruncate request";
        if (width < bitWidth) {
            return trunc(width);
        } else if (width > bitWidth) {
            return zext(width);
        } else {
            return new APInt(width, val);
        }
    }

    public APInt sextOrSelf(int width) {
        assert width >= 0 : "Invalid APInt SignExtendOrSelf request";
        if (width > bitWidth) {
            return sext(width);
        } else {
            return new APInt(width, val);
        }
    }

    public APInt zextOrSelf(int width) {
        assert width >= 0 : "Invalid APInt ZeroExtendOrSelf request";
        if (width > bitWidth) {
            return zext(width);
        } else {
            return new APInt(width, val);
        }
    }

    public APInt setAllBits() {
        BigInteger newVal = val;
        for (int i = 0; i < bitWidth; ++i) {
            newVal = newVal.setBit(i);
        }
        return new APInt(bitWidth, newVal);
    }

    public APInt setBit(int bitPosition) {
        assert bitPosition >= 0 : "Invalid bitPosition";
        BigInteger newVal = val.setBit(bitPosition);
        return new APInt(bitWidth, newVal);
    }

    public APInt setSignBit() {
        BigInteger newVal = val.setBit(bitWidth - 1);
        return new APInt(bitWidth, newVal);
    }

    public APInt clearAllBits() {
        BigInteger newVal = val;
        for (int i = 0; i < bitWidth; ++i) {
            newVal = newVal.clearBit(i);
        }
        return new APInt(bitWidth, newVal);
    }

    public APInt clearBit(int bitPosition) {
        assert bitPosition >= 0 : "Invalid bitPosition";
        BigInteger newVal = val.clearBit(bitPosition);
        return new APInt(bitWidth, newVal);
    }

    public APInt flipAllBits() {
        BigInteger newVal = val;
        for (int i = 0; i < bitWidth; ++i) {
            newVal = newVal.flipBit(i);
        }
        return new APInt(bitWidth, newVal);
    }

    public APInt flipBit(int bitPosition) {
        assert bitPosition >= 0 : "Invalid bitPosition";
        BigInteger newVal = val.flipBit(bitPosition);
        return new APInt(bitWidth, newVal);
    }

    public APInt insertBits(APInt subBits, int bitPosition) {
        int subBitWidth = subBits.getBitWidth();
        assert subBitWidth > 0 && bitPosition >= 0
                && bitPosition + subBitWidth <= bitWidth : "Illegal bit insertion";
        BigInteger newVal = val;
        for (int i = 0; i < subBitWidth; ++i) {
            if (subBits.getValue().testBit(i)) {
                newVal = newVal.setBit(bitPosition + i);
            } else {
                newVal = newVal.clearBit(bitPosition + i);
            }
        }
        return new APInt(bitWidth, newVal);
    }

    public APInt extractBits(int numBits, int bitPosition) {
        assert numBits > 0 && bitPosition >= 0
                && bitPosition + numBits <= bitWidth : "Illegal bit extraction";
        BigInteger newVal = BigInteger.ZERO;
        for (int i = 0; i < numBits; ++i) {
            if (val.testBit(bitPosition + i)) {
                newVal = newVal.setBit(i);
            } else {
                newVal = newVal.clearBit(i);
            }
        }
        return new APInt(numBits, newVal);
    }

    // it is defined: log2(0) = UNSIGNED_MAX (store as -1 in int type)
    public int logBase2() {
        return val.bitLength() - 1;
    }

    // it is defined: log2(0) = bitWidth
    public int ceilLogBase2() {
        if (isZero()) {
            return bitWidth;
        } else {
            return val.subtract(BigInteger.ONE).bitLength();
        }
    }

    // it is defined: log2(0) = UNSIGNED_MAX (store as -1 in int type)
    public int nearestLogBase2() {
        int pos = val.bitLength() - 1;
        if (pos > 0 && val.testBit(pos - 1)) {
            return pos + 1;
        } else {
            return pos;
        }
    }

    // return -1 if not an exact power of two
    public int exactLogBase2() {
        if (isPowerOf2()) {
            return logBase2();
        } else {
            return -1;
        }
    }

    // APInt sqrt()

    public APInt abs() {
        BigInteger newVal = val;
        if (isNegative()) {
            newVal = BigInteger.ONE.shiftLeft(bitWidth).subtract(val);
        }
        return new APInt(bitWidth, newVal);
    }

    public APInt ashr(int shiftAmt) {
        assert shiftAmt >= 0 : "Illegal arithmetic shift right";
        BigInteger newVal = val;
        for (int i = 0; i < shiftAmt; ++i) {
            newVal = val.shiftRight(1);
            if (isNegative()) {
                newVal.setBit(bitWidth - 1);
            }
        }
        return new APInt(bitWidth, newVal);
    }

    public APInt ashr(APInt shiftAmt) {
        return ashr((int) shiftAmt.getLimitedValue(bitWidth));
    }

    public APInt lshr(int shiftAmt) {
        assert shiftAmt >= 0 : "Illegal logical shift right";
        BigInteger newVal = val.shiftRight(shiftAmt);
        return new APInt(bitWidth, newVal);
    }

    public APInt lshr(APInt shiftAmt) {
        return lshr((int) shiftAmt.getLimitedValue(bitWidth));
    }

    public APInt shl(int shiftAmt) {
        assert shiftAmt >= 0 : "Illegal shift left";
        BigInteger newVal = val.shiftLeft(shiftAmt);
        return new APInt(bitWidth, newVal);
    }

    public APInt shl(APInt shiftAmt) {
        return shl((int) shiftAmt.getLimitedValue(bitWidth));
    }

    public Pair<APInt, Boolean> sshl_ov(APInt shiftAmt) {
        boolean overflow = shiftAmt.uge(new APInt(bitWidth, getBitWidth() + ""));
        if (overflow) {
            return Pair.of(new APInt(bitWidth, "0"), true);
        } else {
            if (isNonNegative()) {
                overflow = shiftAmt.uge(new APInt(bitWidth, countLeadingZeros() + ""));
            } else {
                overflow = shiftAmt.uge(new APInt(bitWidth, countLeadingOnes() + ""));
            }
            return Pair.of(shl(shiftAmt), overflow);
        }
    }

    public Pair<APInt, Boolean> ushl_ov(APInt shiftAmt) {
        boolean overflow = shiftAmt.uge(new APInt(bitWidth, getBitWidth() + ""));
        if (overflow) {
            return Pair.of(new APInt(bitWidth, "0"), true);
        } else {
            overflow = shiftAmt.ugt(new APInt(bitWidth, countLeadingZeros() + ""));
            return Pair.of(shl(shiftAmt), overflow);
        }
    }

    public APInt and(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Bit widths must be the same";
        return new APInt(bitWidth, val.and(rhs.getValue()));
    }

    public APInt or(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Bit widths must be the same";
        return new APInt(bitWidth, val.or(rhs.getValue()));
    }

    public APInt xor(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Bit widths must be the same";
        return new APInt(bitWidth, val.xor(rhs.getValue()));
    }

    public APInt minus() {
        APInt res = new APInt(bitWidth, val);
        res.flipAllBits();
        res = res.add(new APInt(bitWidth, "1"));
        return res;
    }

    public APInt add(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Bit widths must be the same";
        return new APInt(bitWidth, val.add(rhs.getValue()).clearBit(bitWidth));
    }

    public Pair<APInt, Boolean> sadd_ov(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Bit widths must be the same";
        APInt res = add(rhs);
        return Pair.of(res,
                isNonNegative() == rhs.isNonNegative() && res.isNonNegative() != isNonNegative());
    }

    public Pair<APInt, Boolean> uadd_ov(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Bit widths must be the same";
        APInt res = add(rhs);
        return Pair.of(res, res.ult(rhs));
    }

    public APInt sub(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Bit widths must be the same";
        return new APInt(bitWidth, val.subtract(rhs.getValue()).clearBit(bitWidth));
    }

    public Pair<APInt, Boolean> ssub_ov(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Bit widths must be the same";
        APInt res = sub(rhs);
        return Pair.of(res,
                isNonNegative() != rhs.isNonNegative() && res.isNonNegative() != isNonNegative());
    }

    public Pair<APInt, Boolean> usub_ov(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Bit widths must be the same";
        APInt res = sub(rhs);
        return Pair.of(res, res.ugt(this));
    }

    public APInt mul(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Bit widths must be the same";
        APInt res = new APInt(bitWidth, val.multiply(rhs.getValue()));
        for (int i = bitWidth; i < bitWidth + bitWidth; ++i) {
            res.clearBit(i);
        }
        return res;
    }

    public Pair<APInt, Boolean> smul_ov(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Bit widths must be the same";
        APInt res = mul(rhs);
        return Pair.of(res,
                !isZero() && !rhs.isZero() && (res.sdiv(rhs).neq(this) || res.sdiv(this).neq(rhs)));
    }

    public Pair<APInt, Boolean> umul_ov(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Bit widths must be the same";
        APInt res = mul(rhs);
        return Pair.of(res,
                !isZero() && !rhs.isZero() && (res.udiv(rhs).neq(this) || res.udiv(this).neq(rhs)));
    }

    public APInt udiv(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Bit widths must be the same";
        return new APInt(bitWidth, val.divide(rhs.getValue()));
    }

    public APInt sdiv(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Bit widths must be the same";
        if (isNegative()) {
            if (rhs.isNegative()) {
                return minus().udiv(rhs.minus());
            } else {
                return minus().udiv((rhs)).minus();
            }
        } else if (rhs.isNegative()) {
            return udiv(rhs.minus()).minus();
        } else {
            return udiv(rhs);
        }
    }

    public Pair<APInt, Boolean> sdiv_ov(APInt rhs) {
        return Pair.of(sdiv(rhs), isMinSignedValue() && rhs.isAllOnesValue());
    }

    public APInt urem(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Bit widths must be the same";
        return new APInt(bitWidth, val.remainder(rhs.getValue()));
    }

    public APInt srem(APInt rhs) {
        assert bitWidth == rhs.getBitWidth() : "Bit widths must be the same";
        if (isNegative()) {
            if (rhs.isNegative()) {
                return minus().urem(rhs.minus()).minus();
            } else {
                return minus().urem((rhs)).minus();
            }
        } else if (rhs.isNegative()) {
            return urem(rhs.minus());
        } else {
            return urem(rhs);
        }
    }

    public int getBitWidth() {
        return bitWidth;
    }

    public BigInteger getValue() {
        return val;
    }

    public int getActiveBits() {
        return val.bitLength();
    }

    public int getMinSignedBits() {
        if (isNegative()) {
            int ret = bitWidth;
            while (ret > 1 && val.testBit(ret - 2)) {
                --ret;
            }
            return ret;
        } else {
            return getActiveBits() + 1;
        }
    }

    public int countLeadingZeros() {
        return bitWidth - val.bitLength();
    }

    public int countLeadingOnes() {
        int i = 0;
        while (val.testBit(bitWidth - i - 1)) {
            ++i;
        }
        return i;
    }

    // NOTICE return type uses long to store u_int64
    public long getZExtValue() {
        assert bitWidth <= 64 : "Too many bits for uint64_t";
        return val.longValue();
    }

    public long getSExtValue() {
        assert bitWidth <= 64 : "Too many bits for uint64_t";
        BigInteger newVal = val;
        if (isNegative()) {
            for (int i = bitWidth; i < 64; ++i) {
                newVal = newVal.setBit(i);
            }
        }
        return newVal.longValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != APInt.class) {
            return false;
        }
        return bitWidth == ((APInt) obj).getBitWidth() && eq(((APInt) obj));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(val.toString(), bitWidth);
    }

    @Override
    public String toString() {
        long byteSize = (bitWidth % 8) == 0 ? bitWidth / 8 : bitWidth / 8 + 1;
        return byteSize + " byte, value = " + val.toString();
    }

    public static APInt floatToBits(float f) {
        return new APInt(32, Float.floatToRawIntBits(f), true);
    }

    public static APInt doubleToBits(double d) {
        return new APInt(64, Double.doubleToRawLongBits(d), true);
    }
}
