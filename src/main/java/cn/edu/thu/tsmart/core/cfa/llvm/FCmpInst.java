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
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.Predicate;

import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;

/**
 * @author guangchen on 27/02/2017.
 */
public class FCmpInst extends CmpInst {

  public FCmpInst(String name, Type type, Predicate predicate) {
    super(name, type);
    super.opCode = OpCode.FCMP;
    super.predicate = predicate;
  }

  @Override
  public boolean isCommutative() {
    return isEquality() || getPredicate() == Predicate.FCMP_FALSE
        || getPredicate() == Predicate.FCMP_TRUE || getPredicate() == Predicate.FCMP_ORD
        || getPredicate() == Predicate.FCMP_UNO;
  }

  @Override
  public boolean isEquality() {
    return isEquality(getPredicate());
  }

  public static boolean isEquality(Predicate predicate) {
    return predicate == Predicate.FCMP_OEQ || predicate == Predicate.FCMP_ONE
        || predicate == Predicate.FCMP_UEQ || predicate == Predicate.FCMP_UNE;
  }

  public boolean isRelational() {
    return !isEquality();
  }

  @Override
  public <R, E extends CPAException> R accept(InstructionVisitor<R, E> visitor) throws E {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    String res = "%" + getName() + " = " + getOpcode().toString();
    res += " " + getPredicate().toString();
    res += " " + getOperand(0).getType().toString() + " ";
    for(int i = 0; i < getNumOperands(); i++) {
      Value operand = getOperand(i);
      if (operand instanceof Constant) {
        res += operand.toString();
      } else {
        res += "%" + operand.getName();
      }
      if(i != getNumOperands() - 1)
        res += ", ";
    }
    return res;
  }
}
