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

/**
 * Created by zhch on 2017/3/9.
 */
public class FastMathFlags {

  private int flags = 0;

  // math flags of integer type
  private static final int NUW = 1 << 0;
  private static final int NSW = 1 << 1;
  private static final int EXACT = 1 << 2;

  // math flags of floating-point type
  private static final int NNAN = 1 << 3;
  private static final int NINF = 1 << 4;
  private static final int NSZ = 1 << 5;
  private static final int ARCP = 1 << 6;
  private static final int FAST = 1 << 7;

  // only for Converter
  public void setNoUnsignedWrapFlag() {
    flags = flags | NUW;
  }

  // only for Converter
  public void setUnsignedWrapFlag() {
    flags = flags | NSW;
  }

  // only for Converter
  public void setExactFlag() {
    flags = flags | EXACT;
  }

  // only for Converter
  public void setNoNaNFlag() {
    flags = flags | NNAN;
  }

  // only for Converter
  public void setNoInfFlag() {
    flags = flags | NINF;
  }

  // only for Converter
  public void setNoSignedZeroFlag() {
    flags = flags | NSZ;
  }

  // only for Converter
  public void setAllowReciprocalFlag() {
    flags = flags | ARCP;
  }

  // only for Converter
  public void setFastFlag() {
    setNoNaNFlag();
    setNoInfFlag();
    setNoSignedZeroFlag();
    setAllowReciprocalFlag();
    flags = flags | FAST;
  }

  public boolean hasAnyFlag() {
    return flags != 0;
  }

  public boolean hasNoUnsignedWrapFlag() {
    return (flags & NUW) != 0;
  }

  public boolean hasNoSignedWrapFlag() {
    return (flags & NSW) != 0;
  }

  public boolean hasExactFlag() {
    return (flags & EXACT) != 0;
  }

  public boolean hasNoNaNFlag() {
    return (flags & NNAN) != 0;
  }

  public boolean hasNoInfFlag() {
    return (flags & NINF) != 0;
  }

  public boolean hasNoSignedZeroFlag() {
    return (flags & NSZ) != 0;
  }

  public boolean hasAllowReciprocalFlag() {
    return (flags & ARCP) != 0;
  }

  public boolean hasFastFlag() {
    return (flags & FAST) != 0;
  }

}