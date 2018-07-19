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
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.BinOp;
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.SynchronizationScope;

import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;

/**
 * @author guangchen on 27/02/2017.
 */
public class AtomicRMWInst extends Instruction {

  // TODO initialize in Converter
  private BinOp operation = null;
  private boolean isVolatile = false;
  private AtomicOrdering ordering = null;
  private SynchronizationScope synchScope = null;

  public AtomicRMWInst(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.ATOMICRMW;
  }

  // only for Converter
  public void setOperation(BinOp operation) {
    this.operation = operation;
  }

  // only for Converter
  public void setVolatile(boolean aVolatile) {
    isVolatile = aVolatile;
  }

  // only for Converter
  public void setOrdering(AtomicOrdering ordering) {
    this.ordering = ordering;
  }

  // only for Converter
  public void setSynchScope(SynchronizationScope synchScope) {
    this.synchScope = synchScope;
  }

  public BinOp getOperation() {
    return operation;
  }

  public boolean isVolatile() {
    return isVolatile;
  }

  public AtomicOrdering getOrdering() {
    return ordering;
  }

  public SynchronizationScope getSynchScope() {
    return synchScope;
  }

  public Value getPointerOperand() {
    return getOperand(0);
  }

  public Value getValOperand() {
    return getOperand(1);
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
