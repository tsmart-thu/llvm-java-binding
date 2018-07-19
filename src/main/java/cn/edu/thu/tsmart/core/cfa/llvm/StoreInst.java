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

import cn.edu.thu.tsmart.core.cfa.util.Formatter;
import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;

/**
 * @author guangchen on 27/02/2017.
 */
public class StoreInst extends Instruction {

  // TODO initialize in Converter
  private boolean isVolatile = false;
  private int alignment = 0;
  private AtomicOrdering ordering = null;
  private SynchronizationScope synchScope = null;

  public StoreInst(String name, Type type, int alignment) {
    super(name, type);
    super.opCode = OpCode.STORE;
    this.alignment = alignment;
  }

  // only for Converter
  public void setVolatile(boolean isVolatile) {
    this.isVolatile = isVolatile;
  }

  // only for Converter
  public void setOrdering(AtomicOrdering ordering) {
    this.ordering = ordering;
  }

  // only for Converter
  public void setSynchScope(SynchronizationScope synchScope) {
    this.synchScope = synchScope;
  }

  public boolean isVolatile() {
    return isVolatile;
  }

  // NOTICE return type uses int to store unsigned
  public int getAlignment() {
    return alignment;
  }

  public AtomicOrdering getOrdering() {
    return ordering;
  }

  public SynchronizationScope getSynchScope() {
    return synchScope;
  }

  public boolean isSimple() {
    return !isAtomic() && !isVolatile();
  }

  public boolean isUnordered() {
    return (getOrdering() == AtomicOrdering.NOT_ATOMIC || getOrdering() == AtomicOrdering.UNORDERED)
        && !isVolatile();
  }

  public Value getValueOperand() {
    return getOperand(0);
  }

  public Value getPointerOperand() {
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

  @Override
  public String toString() {
    String res = "store ";
    if(isVolatile())
      res += "volatile ";
    Value operand0 = getOperand(0);
    res += operand0.getType().toString() + " ";
    res += Formatter.asOperand(operand0);
    Value operand1 = getOperand(1);
    res += ", " + operand1.getType().toString() + " ";
    res += Formatter.asOperand(operand1);
    if (getAlignment() != 0) {
      res += ", align " + getAlignment();
    }
    return res;
  }
}
