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

/**
 * @author guangchen on 27/02/2017.
 */
public class IndirectBrInst extends TerminatorInst {

  public IndirectBrInst(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.INDIRECTBR;
  }

  @Override
  public int getNumSuccessors() {
    return getNumOperands() - 1;
  }

  @Override
  public BasicBlock getSuccessor(int i) {
    return cast(getOperand(i + 1), BasicBlock.class);
  }

  public Value getAddress() {
    return getOperand(0);
  }

  public int getNumDestinations() {
    return getNumOperands() - 1;
  }

  public BasicBlock getDestination(int i) {
    return getSuccessor(i);
  }
}
