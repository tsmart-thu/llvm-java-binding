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

import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.Predicate;

import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;

/**
 * @author guangchen on 27/02/2017.
 */
public class ICmpInst extends CmpInst {

  public ICmpInst(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.ICMP;
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
    String res = "%" + getName() + " = icmp sgt ";
    Value operand1 = getOperand(0);
    if (operand1 instanceof Constant) {
      res += " " + operand1.toString();
    } else {
      res += operand1.getType().toString() + " %" + operand1.getName();
    }
    Value operand2 = getOperand(1);
    if (operand2 instanceof Constant) {
      res += ", " +operand2.toString();
    } else {
      res += ", " + operand2.getType().toString() + " %" + operand2.getName();
    }
    return res;
  }
}
