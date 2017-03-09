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

import static cn.edu.thu.tsmart.core.cfa.util.Casting.*;

import static org.bytedeco.javacpp.LLVM.*;
import static org.bytedeco.javacpp.LLVM.LLVMGetInstructionOpcode;

import com.google.common.base.Optional;

public class Instruction extends User {

  protected Instruction(String name, Type type) {
    super(name, type);
  }

  protected BasicBlock parent;
  // initialized in child class
  protected OpCode opCode;

  // only for Converter
  void setParent(BasicBlock block) {
    parent = block;
  }

  BasicBlock getParent() {
    return parent;
  }

  Module getModule() {
    // TODO get module from parent function
    Optional<Module> module = Optional.absent();
    return module.get();
  }

  Function getFunction() {
    return getParent().getParent();
  }

  OpCode getOpcode() {
    return opCode;
  }

  boolean isTerminator() {
    if (opCode.ordinal() >= OpCode.RET.ordinal() && opCode.ordinal() <= OpCode.UNREACHABLE
        .ordinal()) {
      return true;
    } else {
      return false;
    }
  }

  boolean isBinaryOp() {
    if (opCode.ordinal() >= OpCode.ADD.ordinal() && opCode.ordinal() <= OpCode.XOR.ordinal()) {
      return true;
    } else {
      return false;
    }
  }

  boolean isShift() {
    if (opCode.ordinal() >= OpCode.SHL.ordinal() && opCode.ordinal() <= OpCode.ASHR.ordinal()) {
      return true;
    } else {
      return false;
    }
  }

  boolean isCast() {
    if (opCode.ordinal() >= OpCode.TRUNC.ordinal() && opCode.ordinal() <= OpCode.ADDRSPACECAST
        .ordinal()) {
      return true;
    } else {
      return false;
    }
  }

  boolean isFuncletPad() {
    if (opCode == OpCode.CLEANUPPAD || opCode == OpCode.CATCHPAD) {
      return true;
    } else {
      return false;
    }
  }

  boolean isLogicalShift() {
    if (opCode == OpCode.SHL || opCode == OpCode.LSHR) {
      return true;
    } else {
      return false;
    }
  }

  boolean isArithmeticShift() {
    if (opCode == OpCode.ASHR) {
      return true;
    } else {
      return false;
    }
  }

  boolean isBitwiseLogicOp() {
    if (opCode.ordinal() >= OpCode.AND.ordinal() && opCode.ordinal() <= OpCode.XOR.ordinal()) {
      return true;
    } else {
      return false;
    }
  }

  /*
  TODO api involving metadata
  / hasMetadata
  / hasMetadataOtherThanDebugLoc
  / getMetadata
  / getAllMetadata
  / getAllMetadataOtherThanDebugLoc
  / getAAMetadata
  / extractProfMetadata
  / extractProfTotalWeight
  / getDebugLoc
  */

  /*
  TODO api involving operator
  / hasNoUnsignedWrap
  / hasNoSignedWrap
  / dropPoisonGeneratingFlags
  / isExact
  / hasUnsafeAlgebra
  / hasNoNaNs
  / hasNoInfs
  / hasNoSignedZeros
  / hasAllowReciprocal
  / getFastMathFlags
  */

  boolean isAssociative() {
    switch (opCode) {
      case AND:
      case OR:
      case XOR:
      case ADD:
      case MUL:
        return true;
      case FMUL:
      case FADD:
        // TODO involve operator
        // return cast<FPMathOperator>(this)->hasUnsafeAlgebra();
      default:
        return false;
    }
  }

  boolean isCommutative() {
    switch (opCode) {
      case ADD:
      case FADD:
      case MUL:
      case FMUL:
      case AND:
      case OR:
      case XOR:
        return true;
      default:
        return false;
    }
  }

  boolean isIdempotent() {
    if (opCode == OpCode.AND || opCode == OpCode.OR) {
      return true;
    } else {
      return false;
    }
  }

  boolean isNilpotent() {
    if (opCode == OpCode.XOR) {
      return true;
    } else {
      return false;
    }
  }

  boolean mayWriteToMemory() {
    switch (opCode) {
      case FENCE:
      case STORE:
      case VA_ARG:
      case ATOMICRMW:
      case CMPXCHG:
      case CATCHPAD:
      case CATCHRET:
        return true;
      case CALL:
        // TODO involve CallInst
        // return !cast<CallInst>(this)->onlyReadsMemory();
      case INVOKE:
        // TODO involve InvokeInst
        // return !cast<InvokeInst>(this)->onlyReadsMemory();
      case LOAD:
        // TODO involve LoadInst
        // return !cast<LoadInst>(this)->isUnordered();
      default:
        return false;
    }
  }

  boolean mayReadFromMemory() {
    switch (opCode) {
      case VA_ARG:
      case LOAD:
      case FENCE:
      case ATOMICRMW:
      case CMPXCHG:
      case CATCHPAD:
      case CATCHRET:
        return true;
      case CALL:
        // TODO involve CallInst
        // return !cast<CallInst>(this)->onlyReadsMemory();
      case INVOKE:
        // TODO involve InvokeInst
        // return !cast<InvokeInst>(this)->onlyReadsMemory();
      case STORE:
        // TODO involve StoreInst
        // return !cast<StoreInst>(this)->isUnordered();
      default:
        return false;
    }
  }

  boolean mayReadOrWriteMemory() {
    if (mayReadFromMemory() || mayWriteToMemory()) {
      return true;
    } else {
      return false;
    }
  }

  boolean isAtomic() {
    switch (opCode) {
      case ATOMICRMW:
      case CMPXCHG:
      case FENCE:
        return true;
      case LOAD:
        // TODO involve LoadInst
        // return cast<LoadInst>(this)->getOrdering() != AtomicOrdering::NotAtomic;
      case STORE:
        // TODO involve StoreInst
        // return cast<StoreInst>(this)->getOrdering() != AtomicOrdering::NotAtomic;
      default:
        return false;
    }
  }

  boolean mayThrow() {
    // TODO involve CallInst
    // if (const CallInst *CI = dyn_cast<CallInst>(this))
    //   return !CI->doesNotThrow();
    // TODO involve CleanupReturnInst
    // if (const auto *CRI = dyn_cast<CleanupReturnInst>(this))
    //   return CRI->unwindsToCaller();
    // TODO involve CatchSwitchInst
    // if (const auto *CatchSwitch = dyn_cast<CatchSwitchInst>(this))
    //   return CatchSwitch->unwindsToCaller();
    if (opCode == OpCode.RESUME) {
      return true;
    } else {
      return false;
    }
  }

  boolean isFenceLike() {
    switch (opCode) {
      case FENCE:
      case CATCHPAD:
      case CATCHRET:
      case CALL:
      case INVOKE:
        return true;
      default:
        return false;
    }
  }

  boolean mayHaveSideEffects() {
    if (mayWriteToMemory() || mayThrow()) {
      return true;
    } else {
      return false;
    }
  }

  boolean isEHPad() {
    switch (opCode) {
      case CATCHSWITCH:
      case CATCHPAD:
      case CLEANUPPAD:
      case LANDINGPAD:
        return true;
      default:
        return false;
    }
  }

  // TODO require haveSameSpecialState()
  // isIdenticalTo
  // isIdenticalToWhenDefined
  // isSameOperationAs

  // TODO involve classes of mutiple kinds of instructions
  // haveSameSpecialState

  boolean isUsedOutsideOfBlock(BasicBlock block) {
    for (Use use = getOperandList(); use != null; use = use.getNext()) {
      Instruction inst = cast(use, Instruction.class);
      PhiNode pn = dyncast(use, PhiNode.class);
      if (pn == null) {
        if (inst.getParent() != block) {
          return true;
        }
        continue;
      }
      // TODO check the condition pn->getIncomingBlock(U) != block
      // if (pn->getIncomingBlock(U) != block) {return true;}
    }
    return false;
  }

}

enum OpCode {
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
