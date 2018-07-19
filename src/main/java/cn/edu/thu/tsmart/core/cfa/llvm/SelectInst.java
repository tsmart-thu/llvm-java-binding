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

import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;
import java.util.Arrays;
import java.util.List;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author guangchen on 27/02/2017.
 */
public class SelectInst extends Instruction {

  public SelectInst(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.SELECT;
  }

  public Value getCondition() {
    return getOperand(0);
  }

  public Value getTrueValue() {
    return getOperand(1);
  }

  public Value getFalseValue() {
    return getOperand(2);
  }

  @Override
  public <R, E extends CPAException> R accept(InstructionVisitor<R, E> visitor) throws E {
    return visitor.visit(this);
  }

  public static Instruction create(Value operand, Value operand1, Value operand2) {
    List<Value> list = Arrays.asList(operand, operand1, operand2);
    SelectInst instruction = new SelectInst("", null);
    instruction.setOperands(list);
    return instruction;
  }

  @Override
  public String toString() {
    String res = "%" + getName().toString() + " = ";
    res += getOpcode() + " ";
    for(int i = 0; i < getNumOperands(); i++) {
      res += getOperand(i).getType().toString() + " ";
      if(getOperand(i).getType().isPointerTy()) {
        res += "@" + getOperand(i).getName().toString();
      } else if(getOperand(i).getName().equals("CONSTANT_INT")) {
        res += getOperand(i).toString();
      } else {
        res += "%" + getOperand(i).getName().toString();
      }
      if(i != getNumOperands() - 1) {
        res += ", ";
      }
    }
    return res;
  }
}
