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
    String res = "%" + getName().toString() + " = " + getOpcode().toString();
    res += " " + getPredicate().toString();
    res += " " + getOperand(0).getType().toString() + " ";
    for(int i = 0; i < getNumOperands(); i++) {
      res += "%" + getOperand(i).getName().toString();
      if(i != getNumOperands() - 1)
        res += ", ";
    }
    return res;
  }
}
