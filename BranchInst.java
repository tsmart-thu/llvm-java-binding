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

import static cn.edu.thu.tsmart.core.cfa.util.Casting.*;
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.*;

import com.sun.istack.internal.Nullable;

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
      return 0;
    }
  }

  @Override
  @Nullable
  public BasicBlock getSuccessor(int i) {
    assert i >= 0 && i < getNumSuccessors() : "Successor # out of range for branch!";
    return castOrNull(getOperand(i + 1), BasicBlock.class);
  }
}
