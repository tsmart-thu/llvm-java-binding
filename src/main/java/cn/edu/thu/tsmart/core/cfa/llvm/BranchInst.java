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
import static cn.edu.thu.tsmart.core.cfa.util.Casting.castOrNull;

import cn.edu.thu.tsmart.core.cfa.util.Formatter;
import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;
import javax.annotation.Nullable;

/**
 * @author guangchen on 27/02/2017.
 */
public class BranchInst extends TerminatorInst {

  public BranchInst(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.BR;
  }

  public boolean isUnconditional() {
    return getNumOperands() == 1;
  }

  public boolean isConditional() {
    return getNumOperands() == 3;
  }

  public Value getCondition() {
    assert isConditional() : "Cannot get condition of an uncond branch!";
    return getOperand(0);
  }

  @Override
  public int getNumSuccessors() {
    if (isConditional()) {
      return 2;
    } else {
      return 1;
    }
  }

  @Override
  @Nullable
  public BasicBlock getSuccessor(int i) {
    assert i >= 0 && i < getNumSuccessors() : "Successor # out of range for branch!";
    if (isConditional()) {
      // note: successor order is reverse from operand order
      return castOrNull(getOperand(getNumSuccessors() - i), BasicBlock.class);
    } else {
      return castOrNull(getOperand(i), BasicBlock.class);
    }
  }

  @Override
  public <R, E extends CPAException> R accept(InstructionVisitor<R, E> visitor) throws E {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    String res = "br ";
    if (isConditional()) {
      res += getCondition().getType().toString() + " " + Formatter.asOperand(getCondition()) + ", ";
    }
    res += "label %" + getSuccessor(0).getName();
    if (isConditional()) {
      res += ", label %" + getSuccessor(1).getName();
    }
    return res;
  }
}
