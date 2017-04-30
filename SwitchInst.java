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
import static cn.edu.thu.tsmart.core.cfa.util.Casting.cast;
import static cn.edu.thu.tsmart.core.cfa.util.Casting.dyncast;

import cn.edu.thu.sse.common.util.Pair;
import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;
import javax.annotation.Nullable;

/**
 * @author guangchen on 27/02/2017.
 */
public class SwitchInst extends TerminatorInst {

  public SwitchInst(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.SWITCH;
  }

  public Value getCondition() {
    return getOperand(0);
  }

  public BasicBlock getDefaultDest() {
    return cast(getOperand(1), BasicBlock.class);
  }

  public int getNumCases() {
    return getNumOperands() / 2 - 1;
  }

  @Override
  public int getNumSuccessors() {
    return getNumOperands() / 2;
  }

  @Override
  public BasicBlock getSuccessor(int i) {
    assert i >= 0 && i < getNumSuccessors() : "Successor # out of range for switch!";
    return cast(getOperand(i + 1), BasicBlock.class);
  }

  @Override
  public <R, E extends CPAException> R accept(InstructionVisitor<R, E> visitor) throws E {
    return visitor.visit(this);
  }

  // return i if i-th case meets given ConstantInt; -1 if goes for default case
  // Parameter should have same type as switch condition; otherwise always return default case
  public int findCaseValue(ConstantInt ci) {
    for (int i = 0; i < getNumCases(); ++ i) {
      if (getOperand(2 * (i + 1)) == ci) {
        return i;
      }
    }
    return -1;
  }

  // return null if successor is not found, not unique, or is the default case
  @Nullable
  public ConstantInt findCaseDest(BasicBlock block) {
    if (block == getDefaultDest()) {
      return null;
    }
    ConstantInt ci = null;
    for (int i = 0; i < getNumCases(); ++ i) {
      if (getSuccessor(2 * (i + 1) + 1) == block) {
        if (ci != null) {
          return null;
        } else {
          ci = dyncast(getOperand(2 * (i + 1)), ConstantInt.class);
        }
      }
    }
    return ci;
  }
}
