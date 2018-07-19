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

import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OperatorFlags;
import static cn.edu.thu.tsmart.core.cfa.util.Casting.cast;
import static cn.edu.thu.tsmart.core.cfa.util.Casting.dyncast;

import cn.edu.thu.sse.common.UniqueIdGenerator;
import cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.AtomicOrdering;
import com.google.common.base.Optional;

public abstract class Instruction extends User implements IInstruction {

  private static final UniqueIdGenerator idGenerator = new UniqueIdGenerator();
  private int id;

  protected Instruction(String name, Type type) {
    super(name, type);
    this.id = idGenerator.getFreshId();
  }

  protected BasicBlock parent;
  // initialized in child class
  protected OpCode opCode;
  protected InstructionProperties.OperatorFlags operatorFlags;
  protected  Metadata metadata;

  public void setMetadata(Metadata m) { metadata = m; }

  // only for Converter
  public void setParent(BasicBlock block) {
    parent = block;
  }

  // only for Converter
  public void setOperatorFlags(OperatorFlags flags) {
    operatorFlags = flags;
  }

  public BasicBlock getParent() {
    return parent;
  }

  public LlvmModule getModule() {
    // TODO get module from parent function
    Optional<LlvmModule> module = Optional.absent();
    return module.get();
  }

  public LlvmFunction getFunction() {
    return getParent().getParent();
  }

  public OpCode getOpcode() {
    return opCode;
  }

  public boolean isTerminator() {
    if (opCode.ordinal() >= OpCode.RET.ordinal()
        && opCode.ordinal() <= OpCode.UNREACHABLE
        .ordinal()) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isBinaryOp() {
    if (opCode.ordinal() >= OpCode.ADD.ordinal()
        && opCode.ordinal() <= OpCode.XOR.ordinal()) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isShift() {
    if (opCode.ordinal() >= OpCode.SHL.ordinal()
        && opCode.ordinal() <= OpCode.ASHR.ordinal()) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isCast() {
    if (opCode.ordinal() >= OpCode.TRUNC.ordinal()
        && opCode.ordinal() <= OpCode.ADDRSPACECAST
        .ordinal()) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isFuncletPad() {
    if (opCode == OpCode.CLEANUPPAD
        || opCode == OpCode.CATCHPAD) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isLogicalShift() {
    if (opCode == OpCode.SHL || opCode == OpCode.LSHR) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isArithmeticShift() {
    if (opCode == OpCode.ASHR) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isBitwiseLogicOp() {
    if (opCode.ordinal() >= OpCode.AND.ordinal()
        && opCode.ordinal() <= OpCode.XOR.ordinal()) {
      return true;
    } else {
      return false;
    }
  }

  /*
  TODO api involving metadata
  / hasMetadata
  / hasMetadataOtherThanDebugLoc
  / getAllMetadata
  / getAllMetadataOtherThanDebugLoc
  / getAAMetadata
  / extractProfMetadata
  / extractProfTotalWeight
  / getDebugLoc
  */

  public Metadata getMetadata() { return metadata; }
  
  public OperatorFlags getOperatorFlags() {
    return this.operatorFlags;
  }

  protected boolean canHasWrapFlag() {
    return opCode == OpCode.ADD || opCode == OpCode.SUB
            || opCode == OpCode.MUL
            || opCode == OpCode.SHL;
  }

  public boolean hasNoUnsignedWrap() {
    assert canHasWrapFlag() : "No nuw flag!";
    return operatorFlags.hasNoUnsignedWrapFlag();
  }

  public boolean hasNoSignedWrap() {
    assert canHasWrapFlag() : "No nsw flag!";
    return operatorFlags.hasNoSignedWrapFlag();
  }

  public boolean isExact() {
    assert
        opCode == OpCode.UDIV || opCode == OpCode.SDIV
            || opCode == OpCode.LSHR
            || opCode == OpCode.ASHR : "No exact flag!";
    return operatorFlags.hasExactFlag();
  }

  public boolean hasUnsafeAlgebra() {
    assert
        opCode != OpCode.FADD && opCode != OpCode.FSUB
            && opCode != OpCode.FMUL
            && opCode != OpCode.FDIV
            && opCode != OpCode.FREM
            && opCode != OpCode.FCMP
            && opCode != OpCode.CALL : "No fast-math flags!";
    return operatorFlags.hasFastFlag();
  }
  
  protected boolean canHasFastMathFlag() {
    return opCode == OpCode.FADD || opCode == OpCode.FSUB
            || opCode == OpCode.FMUL
            || opCode == OpCode.FDIV
            || opCode == OpCode.FREM
            || opCode == OpCode.FCMP
            || opCode == OpCode.CALL;
  }

  public boolean hasNoNaNs() {
    assert canHasWrapFlag() : "No fast-math flags!";
    return operatorFlags.hasNoNaNFlag();
  }

  public boolean hasNoInfs() {
    assert canHasWrapFlag() : "No fast-math flags!";
    return operatorFlags.hasNoInfFlag();
  }

  public boolean hasNoSignedZeros() {
    assert canHasWrapFlag(): "No fast-math flags!";
    return operatorFlags.hasNoSignedZeroFlag();
  }

  public boolean hasAllowReciprocal() {
    assert canHasWrapFlag(): "No fast-math flags!";
    return operatorFlags.hasAllowReciprocalFlag();
  }

  public boolean isAssociative() {
    switch (opCode) {
      case AND:
      case OR:
      case XOR:
      case ADD:
      case MUL:
        return true;
      case FMUL:
      case FADD:
        return hasUnsafeAlgebra();
      default:
        return false;
    }
  }

  public boolean isCommutative() {
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

  public boolean isIdempotent() {
    if (opCode == OpCode.AND || opCode == OpCode.OR) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isNilpotent() {
    if (opCode == OpCode.XOR) {
      return true;
    } else {
      return false;
    }
  }

  public boolean mayWriteToMemory() {
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
        return !dyncast(this, CallInst.class).onlyReadsMemory();
      case INVOKE:
        // TODO involve InvokeInst
        // return !cast<InvokeInst>(this)->onlyReadsMemory();
        return false;
      case LOAD:
        return !dyncast(this, LoadInst.class).isUnordered();
      default:
        return false;
    }
  }

  public boolean mayReadFromMemory() {
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
        return !dyncast(this, CallInst.class).onlyReadsMemory();
      case INVOKE:
        // TODO involve InvokeInst
        // return !cast<InvokeInst>(this)->onlyReadsMemory();
        return false;
      case STORE:
        return !dyncast(this, StoreInst.class).isUnordered();
      default:
        return false;
    }
  }

  public boolean mayReadOrWriteMemory() {
    if (mayReadFromMemory() || mayWriteToMemory()) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isAtomic() {
    switch (opCode) {
      case ATOMICRMW:
      case CMPXCHG:
      case FENCE:
        return true;
      case LOAD:
        return dyncast(this, LoadInst.class).getOrdering() != AtomicOrdering.NOT_ATOMIC;
      case STORE:
        return dyncast(this, StoreInst.class).getOrdering() != AtomicOrdering.NOT_ATOMIC;
      default:
        return false;
    }
  }

  public boolean mayThrow() {
    switch (opCode) {
      case CALL:
        return !dyncast(this, CallInst.class).doesNotThrow();
      case CLEANUPRET:
        // TODO involve CleanupReturnInst
        // return dyn_cast<CleanupReturnInst>(this)->unwindsToCaller();
        return false;
      case CATCHSWITCH:
        // TODO involve CatchSwitchInst
        // return dyn_cast<CatchSwitchInst>(this)->unwindsToCaller();
        return false;
      case RESUME:
        return true;
      default:
        return false;
    }
  }

  public boolean isFenceLike() {
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

  public boolean mayHaveSideEffects() {
    if (mayWriteToMemory() || mayThrow()) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isEHPad() {
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

  public boolean isUsedOutsideOfBlock(BasicBlock block) {
    for (Use use = uses().get(0); use != null; use = use.getNext()) {
      Instruction inst = cast(use, Instruction.class);
      PhiNode pn = dyncast(use, PhiNode.class);
      if (pn == null && inst.getParent() != block) {
        return true;
      } else if (pn != null && pn.getIncomingBlock(use.get()) != block) {
        return true;
      }
    }
    return false;
  }

//  @Override
//  public String toString() {
//    return originalText;
//  }

  private String originalText = "";

  public void setOriginalText(String text) {
    originalText = text;
  }
  public String getOriginalText() { return originalText; }

  @Override
  public int hashCode() {
    return id;
  }

  public int getId() {
    return id;
  }
}
