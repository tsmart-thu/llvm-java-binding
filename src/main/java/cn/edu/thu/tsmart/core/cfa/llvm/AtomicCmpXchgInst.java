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

import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.AtomicOrdering;
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.SynchronizationScope;

import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;

/**
 * @author guangchen on 27/02/2017.
 */
public class AtomicCmpXchgInst extends Instruction {

  // TODO initialize in Converter
  private boolean isVolatile = false;
  private boolean isWeak = false;
  private AtomicOrdering successOrdering = null;
  private AtomicOrdering failureOrdering = null;
  private SynchronizationScope synchScope = null;

  public AtomicCmpXchgInst(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.CMPXCHG;
  }

  // only for Converter
  public void setVolatile(boolean aVolatile) {
    isVolatile = aVolatile;
  }

  // only for Converter
  public void setWeak(boolean weak) {
    isWeak = weak;
  }

  // only for Converter
  public void setSuccessOrdering(AtomicOrdering successOrdering) {
    this.successOrdering = successOrdering;
  }

  // only for Converter
  public void setFailureOrdering(AtomicOrdering failureOrdering) {
    this.failureOrdering = failureOrdering;
  }

  // only for Converter
  public void setSynchScope(SynchronizationScope synchScope) {
    this.synchScope = synchScope;
  }

  public boolean isVolatile() {
    return isVolatile;
  }

  public boolean isWeak() {
    return isWeak;
  }

  public AtomicOrdering getSuccessOrdering() {
    return successOrdering;
  }

  public AtomicOrdering getFailureOrdering() {
    return failureOrdering;
  }

  public SynchronizationScope getSynchScope() {
    return synchScope;
  }

  public Value getPointerOperand() {
    return getOperand(0);
  }

  public Value getCompareOperand() {
    return getOperand(1);
  }

  public Value getNewValOperand() {
    return getOperand(2);
  }

  // NOTICE return type uses int to store unsigned
  public int getPointerAddressSpace() {
    return getPointerOperand().getType().getPointerAddressSpace();
  }

  @Override
  public <R, E extends CPAException> R accept(InstructionVisitor<R, E> visitor) throws E {
    return visitor.visit(this);
  }
}
