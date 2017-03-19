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
 * Created by yuiyin on 2017/3/12.
 */
// TODO might require refactoring inner classes
public class InstructionProperties {

  // used by all instructions and constant expressions
  public static enum OpCode {
    RET,
    BR,
    SWITCH,
    INDIRECTBR,
    INVOKE,
    RESUME,
    CATCHSWITCH,
    CATCHRET,
    CLEANUPRET,
    UNREACHABLE,

    ADD,
    FADD,
    SUB,
    FSUB,
    MUL,
    FMUL,
    UDIV,
    SDIV,
    FDIV,
    UREM,
    SREM,
    FREM,

    SHL,
    LSHR,
    ASHR,
    AND,
    OR,
    XOR,

    EXTRACTELEMENT,
    INSERTELEMENT,
    SHUFFLEVECTOR,

    EXTRACTVALUE,
    INSERTVALUE,

    ALLOCA,
    LOAD,
    STORE,
    FENCE,
    CMPXCHG,
    ATOMICRMW,
    GETELEMENTPTR,

    TRUNC,
    ZEXT,
    SEXT,
    FPTRUNC,
    FPEXT,
    FPTOUI,
    FPTOSI,
    UITOFP,
    SITOFP,
    PTRTOINT,
    INTTOPTR,
    BITCAST,
    ADDRSPACECAST,

    ICMP,
    FCMP,
    PHI,
    SELECT,
    CALL,
    VA_ARG,
    LANDINGPAD,
    CATCHPAD,
    CLEANUPPAD;
  }

  // used by instructions and constant expressions with opcode ADD/SUB/MUL/SHL (nuw, nsw), UDIV/SDIV/LSHR/ASHR (exact), FADD/FSUB/FMUL/FDIV/FREM/FCMP/CALL (nnan, ninf, nsz, arcp, fast)
  public static class OperatorFlags {

    private int flags = 0;

    // operator flags of integer type
    private static final int NUW = 1 << 0;
    private static final int NSW = 1 << 1;
    private static final int EXACT = 1 << 2;

    // operator flags of floating-point type
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

  // used by instructions and constant expressions with opcode CMPXCHG/ATOMICRMW/FENCE/LOAD/STORE
  public static enum AtomicOrdering {
    NOT_ATOMIC,
    UNORDERED,
    MONOTONIC,
    ACQUIRE,
    RELEASE,
    ACQUIRE_RELEASE,
    SEQUENTIALLY_CONSISTENT;
  }

  // used by instructions and constant expressions with opcode CMPXCHG/ATOMICRMW/FENCE/LOAD/STORE
  public static enum SynchronizationScope {
    SINGLE_THREAD,
    CROSS_THREAD;
  }

  // used by instructions and constant expressions with opcode ATOMICRMW
  public static enum BinOp {
    XCHG,
    ADD,
    SUB,
    AND,
    NAND,
    OR,
    XOR,
    MAX,
    MIN,
    UMAX,
    UMIN;
  }

  // used by instructions and constant expressions with opcode ICMP/FCMP
  public static enum Predicate {
    FCMP_FALSE,
    FCMP_OEQ,
    FCMP_OGT,
    FCMP_OGE,
    FCMP_OLT,
    FCMP_OLE,
    FCMP_ONE,
    FCMP_ORD,
    FCMP_UNO,
    FCMP_UEQ,
    FCMP_UGT,
    FCMP_UGE,
    FCMP_ULT,
    FCMP_ULE,
    FCMP_UNE,
    FCMP_TRUE,
    BAD_FCMP_PREDICATE,
    ICMP_EQ,
    ICMP_NE,
    ICMP_UGT,
    ICMP_UGE,
    ICMP_ULT,
    ICMP_ULE,
    ICMP_SGT,
    ICMP_SGE,
    ICMP_SLT,
    ICMP_SLE,
    BAD_ICMP_PREDICATE;
  }
}
