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
public class ICmpInst extends CmpInst {

  public ICmpInst(String name, Type type, Predicate predicate) {
    super(name, type);
    super.opCode = OpCode.ICMP;
    super.predicate = predicate;
  }

  @Override
  public boolean isCommutative() {
    return isEquality();
  }

  @Override
  public boolean isEquality() {
    return isEquality(getPredicate());
  }

  public static boolean isEquality(Predicate predicate) {
    return predicate == Predicate.ICMP_EQ || predicate == Predicate.ICMP_NE;
  }

  public boolean isRelational() {
    return !isEquality();
  }

  public static boolean isRelational(Predicate predicate) {
    return !isEquality(predicate);
  }

  public Predicate getSignedPredicate() {
    return getSignedPredicate(getPredicate());
  }

  public static Predicate getSignedPredicate(Predicate predicate) {
    switch (predicate) {
      case ICMP_UGT:
        return Predicate.ICMP_SGT;
      case ICMP_ULT:
        return Predicate.ICMP_SLT;
      case ICMP_UGE:
        return Predicate.ICMP_SGE;
      case ICMP_ULE:
        return Predicate.ICMP_SLE;
      case ICMP_EQ:
      case ICMP_NE:
      case ICMP_SGT:
      case ICMP_SLT:
      case ICMP_SGE:
      case ICMP_SLE:
        return predicate;
      default:
        assert false : "Unknown icmp predicate!";
        return null;
    }
  }

  public Predicate getUnsignedPredicate() {
    return getUnsignedPredicate(getPredicate());
  }

  public static Predicate getUnsignedPredicate(Predicate predicate) {
    switch (predicate) {
      case ICMP_SGT:
        return Predicate.ICMP_UGT;
      case ICMP_SLT:
        return Predicate.ICMP_ULT;
      case ICMP_SGE:
        return Predicate.ICMP_UGE;
      case ICMP_SLE:
        return Predicate.ICMP_ULE;
      case ICMP_EQ:
      case ICMP_NE:
      case ICMP_UGT:
      case ICMP_ULT:
      case ICMP_UGE:
      case ICMP_ULE:
        return predicate;
      default:
        assert false : "Unknown icmp predicate!";
        return null;
    }
  }

  @Override
  public <R, E extends CPAException> R accept(InstructionVisitor<R, E> visitor) throws E {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    String res = "%" + getName() + " = icmp " + getPredicate().toString() + " ";
    Value operand1 = getOperand(0);
    if (operand1 instanceof Constant) {
      res += operand1.getType().toString() + " " + operand1.toString();
    } else {
      res += operand1.getType().toString() + " %" + operand1.getName();
    }
    Value operand2 = getOperand(1);
    if (operand2 instanceof Constant) {
      res += ", " +operand2.toString();
    } else {
      res += ", " + "%" + operand2.getName();
    }
    return res;
  }
}
