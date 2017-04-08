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

import javax.annotation.Nullable;

/**
 * @author guangchen on 27/02/2017.
 */
public abstract class TerminatorInst extends Instruction {

  protected TerminatorInst(String name, Type type) {
    super(name, type);
  }

  public abstract int getNumSuccessors();

  @Nullable
  public abstract BasicBlock getSuccessor(int i);

  public boolean isExceptional() {
    switch (getOpcode()) {
      case CATCHSWITCH:
      case CATCHRET:
      case CLEANUPRET:
      case INVOKE:
      case RESUME:
        return true;
      default:
        return false;
    }
  }
}
