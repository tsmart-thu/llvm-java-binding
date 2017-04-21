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

import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.AtomicOrdering;
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.SynchronizationScope;

import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;

/**
 * @author guangchen on 27/02/2017.
 */
public class AtomicCmpXchgInst extends Instruction {

  // TODO initialize in Converter
  private boolean isVolatile = false;
  private boolean isWeak = false;
  private AtomicOrdering successOrdering = null;
  private AtomicOrdering failureOrdering = null;
  private SynchronizationScope synchScope = null;

  public AtomicCmpXchgInst(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.CMPXCHG;
  }

  // only for Converter
  public void setVolatile(boolean aVolatile) {
    isVolatile = aVolatile;
  }

  // only for Converter
  public void setWeak(boolean weak) {
    isWeak = weak;
  }

  // only for Converter
  public void setSuccessOrdering(AtomicOrdering successOrdering) {
    this.successOrdering = successOrdering;
  }

  // only for Converter
  public void setFailureOrdering(AtomicOrdering failureOrdering) {
    this.failureOrdering = failureOrdering;
  }

  // only for Converter
  public void setSynchScope(SynchronizationScope synchScope) {
    this.synchScope = synchScope;
  }

  public boolean isVolatile() {
    return isVolatile;
  }

  public boolean isWeak() {
    return isWeak;
  }

  public AtomicOrdering getSuccessOrdering() {
    return successOrdering;
  }

  public AtomicOrdering getFailureOrdering() {
    return failureOrdering;
  }

  public SynchronizationScope getSynchScope() {
    return synchScope;
  }

  public Value getPointerOperand() {
    return getOperand(0);
  }

  public Value getCompareOperand() {
    return getOperand(1);
  }

  public Value getNewValOperand() {
    return getOperand(2);
  }

  public int getPointerAddressSpace() {
    return getPointerOperand().getType().getPointerAddressSpace();
  }

  @Override
  public <R, E extends CPAException> R accept(InstructionVisitor<R, E> visitor) throws E {
    return visitor.visit(this);
  }
}
