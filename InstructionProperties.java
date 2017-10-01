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
 * Created by zhch on 2017/3/12.
 */
// TODO might require refactoring inner classes
public class InstructionProperties {

  // used by all instructions and constant expressions
  public static enum OpCode {
    RET("ret"),
    BR("br"),
    SWITCH("switch"),
    INDIRECTBR("indirectbr"),
    INVOKE("invoke"),
    RESUME("resume"),
    CATCHSWITCH("catchswitch"),
    CATCHRET("catchret"),
    CLEANUPRET("cleanupret"),
    UNREACHABLE("unreachable"),

    ADD("add"),
    FADD("fadd"),
    SUB("sub"),
    FSUB("fsub"),
    MUL("mul"),
    FMUL("fmul"),
    UDIV("udiv"),
    SDIV("sdiv"),
    FDIV("fdiv"),
    UREM("urem"),
    SREM("srem"),
    FREM("frem"),

    SHL("shl"),
    LSHR("lshr"),
    ASHR("ashr"),
    AND("and"),
    OR("or"),
    XOR("xor"),

    EXTRACTELEMENT("extractelement"),
    INSERTELEMENT("insertelement"),
    SHUFFLEVECTOR("shufflevector"),

    EXTRACTVALUE("extractvalue"),
    INSERTVALUE("insertvalue"),

    ALLOCA("alloca"),
    LOAD("load"),
    STORE("store"),
    FENCE("fence"),
    CMPXCHG("cmpxchg"),
    ATOMICRMW("atomicrmw"),
    GETELEMENTPTR("getelementptr"),

    TRUNC("trunc"),
    ZEXT("zext"),
    SEXT("sext"),
    FPTRUNC("fptrunc"),
    FPEXT("fpext"),
    FPTOUI("fptoui"),
    FPTOSI("fptosi"),
    UITOFP("uitofp"),
    SITOFP("sitofp"),
    PTRTOINT("ptrtoint"),
    INTTOPTR("inttoptr"),
    BITCAST("bitcast"),
    ADDRSPACECAST("addrspacecast"),

    ICMP("icmp"),
    FCMP("fcmp"),
    PHI("phi"),
    SELECT("select"),
    CALL("call"),
    VA_ARG("va_arg"),
    LANDINGPAD("landingpad"),
    CATCHPAD("catchpad"),
    CLEANUPPAD("cleanuppad");

    private final String str;

    OpCode(String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return this.str;
    }
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
    public void setNoSignedWrapFlag() {
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

    public int getValue() {
      return flags;
    }

    @Override
    public String toString() {
      if (hasNoSignedWrapFlag()) {
        return "nsw";
      } else if (hasNoUnsignedWrapFlag()) {
        return "nuw";
      } else if (hasExactFlag()) {
        return "exact";
      } else if (hasNoNaNFlag()) {
        return "nonan";
      } else if (hasNoInfFlag()) {
        return "noinf";
      } else if (hasNoSignedZeroFlag()) {
        return "nsz";
      }
      return "";
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
    FCMP_FALSE("false"),
    FCMP_OEQ("oeq"),
    FCMP_OGT("ogt"),
    FCMP_OGE("oge"),
    FCMP_OLT("olt"),
    FCMP_OLE("ole"),
    FCMP_ONE("one"),
    FCMP_ORD("ord"),
    FCMP_UNO("uno"),
    FCMP_UEQ("ueq"),
    FCMP_UGT("ugt"),
    FCMP_UGE("uge"),
    FCMP_ULT("ult"),
    FCMP_ULE("ule"),
    FCMP_UNE("une"),
    FCMP_TRUE("true"),
    BAD_FCMP_PREDICATE("bad"),
    ICMP_EQ("eq"),
    ICMP_NE("ne"),
    ICMP_UGT("ugt"),
    ICMP_UGE("uge"),
    ICMP_ULT("ult"),
    ICMP_ULE("ule"),
    ICMP_SGT("sgt"),
    ICMP_SGE("sge"),
    ICMP_SLT("slt"),
    ICMP_SLE("sle"),
    BAD_ICMP_PREDICATE("bad");

    private String s;

    Predicate(String s) {
      this.s = s;
    }


    @Override
    public String toString() {
      return s;
    }
  }

  // used by instructions and constant expressions with opcode CALL
  public static enum TailCallKind {
    TCK_NONE,
    TCK_TAIL,
    TCK_MUST_TAIL,
    TCK_NO_TAIL;
  }

  public static enum CallingConvention {
    C,
    FAST,
    COLD,
    GHC,
    HIPE,
    WEBKIT_JS,
    ANY_REG,
    PRESERVE_MOST,
    PRESERVE_ALL,
    SWIFT,
    CXX_FAST_TLS,
    FIRST_TARGET_CC,
    X86_STD_CALL,
    X86_FAST_CALL,
    ARM_APCS,
    ARM_AAPCS,
    ARM_AAPCS_VFP,
    MSP430_INTR,
    X86_THIS_CALL,
    PTX_KERNEL,
    PTX_DEVICE,
    SPIR_FUNC,
    SPIR_KERNEL,
    INTEL_OCL_BI,
    X86_64_SYSV,
    X86_64_WIN64,
    X86_VECTOR_CALL,
    HHVM,
    HHVM_C,
    X86_INTR,
    AVR_INTR,
    AVR_SIGNAL,
    AVR_BUILTIN,
    AMDGPU_VS,
    AMDGPU_GS,
    AMDGPU_PS,
    AMDGPU_CS,
    AMDGPU_KERNEL,
    X86_REG_CALL,
    MAX_ID;
  }
}
