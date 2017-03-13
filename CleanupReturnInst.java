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

import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.*;

/**
 * @author guangchen on 27/02/2017.
 */
public class CleanupReturnInst extends TerminatorInst {

  public CleanupReturnInst(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.CLEANUPRET;
    // this instruction should not be used when analysing C
    assert false : "Unhandled instruction: cleanupret";
  }

  @Override
  public int getNumSuccessors() {
    // return hasUnwindDest() ? 1 : 0;
    return 0;
  }

  @Override
  public BasicBlock getSuccessor(int i) {
    // assert i == 0; return getUnwindDest();
    return null;
  }
}
