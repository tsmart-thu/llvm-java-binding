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

import cn.edu.thu.tsmart.core.cfa.util.Formatter;
import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;
import javax.annotation.Nullable;

/**
 * @author guangchen on 27/02/2017.
 */
public class ReturnInst extends TerminatorInst {

  public ReturnInst(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.RET;
  }

  @Override
  public int getNumSuccessors() {
    return 0;
  }

  @Override
  @Nullable
  public BasicBlock getSuccessor(int i) {
    return null;
  }

  @Nullable
  public Value getReturnValue() {
    if (getNumOperands() == 0) {
      return null;
    } else {
      return getOperand(0);
    }
  }

  @Override
  public <R, E extends CPAException> R accept(InstructionVisitor<R, E> visitor) throws E {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    String res = "ret";
    if (getReturnValue() != null) {
      res += " " + getReturnValue().getType().toString() + " " + Formatter.asOperand(getReturnValue());
    }
    return res;
  }
}