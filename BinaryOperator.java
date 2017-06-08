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

import cn.edu.thu.tsmart.core.cfa.util.Formatter;
import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;

import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.*;
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;

/**
 * @author guangchen on 27/02/2017.
 */
public class BinaryOperator extends Instruction {

  public BinaryOperator(String name, Type type, OpCode opcode) {
    super(name, type);
    super.opCode = opcode;
  }

  @Override
  public <R, E extends CPAException> R accept(InstructionVisitor<R, E> visitor) throws E {
    return visitor.visit(this);
  }

  private String operatorToString() {
    switch (opCode) {
      case ADD:
        return "add";
      case FADD:
        return "fadd";
      case SUB:
        return "sub";
      case FSUB:
        return "fsub";
      case MUL:
        return "mul";
      case FMUL:
        return "fmul";
      case SDIV:
        return "sdiv";
      case UDIV:
        return "udiv";
      case UREM:
        return "urem";
      case FREM:
        return "frem";
      case SHL:
        return "shl";
      case ASHR:
        return "ashr";
      case LSHR:
        return "lshr";
      case AND:
        return "and";
      case OR:
        return "or";
      case XOR:
        return "xor";
      case SREM:
        return "srem";
    }
    return "";
  }

  @Override
  public String toString() {
    String res = "%" + getName() + " = " + operatorToString()  + " ";
    OperatorFlags operatorFlags = getOperatorFlags();
    if (operatorFlags != null) {
      res += operatorFlags.toString() + " ";
    }
    res +=
        getType().toString() + " " + Formatter.asOperand(getOperand(0)) + ", " + Formatter.asOperand(getOperand(1));
    return res;
  }
}
