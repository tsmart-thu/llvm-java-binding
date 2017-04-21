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
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.BinOp;
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.SynchronizationScope;

import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;

/**
 * @author guangchen on 27/02/2017.
 */
public class AtomicRMWInst extends Instruction {

  // TODO initialize in Converter
  private BinOp operation = null;
  private boolean isVolatile = false;
  private AtomicOrdering ordering = null;
  private SynchronizationScope synchScope = null;

  public AtomicRMWInst(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.ATOMICRMW;
  }

  // only for Converter
  public void setOperation(BinOp operation) {
    this.operation = operation;
  }

  // only for Converter
  public void setVolatile(boolean aVolatile) {
    isVolatile = aVolatile;
  }

  // only for Converter
  public void setOrdering(AtomicOrdering ordering) {
    this.ordering = ordering;
  }

  // only for Converter
  public void setSynchScope(SynchronizationScope synchScope) {
    this.synchScope = synchScope;
  }

  public BinOp getOperation() {
    return operation;
  }

  public boolean isVolatile() {
    return isVolatile;
  }

  public AtomicOrdering getOrdering() {
    return ordering;
  }

  public SynchronizationScope getSynchScope() {
    return synchScope;
  }

  public Value getPointerOperand() {
    return getOperand(0);
  }

  public Value getValOperand() {
    return getOperand(1);
  }

  public int getPointerAddressSpace() {
    return getPointerOperand().getType().getPointerAddressSpace();
  }

  @Override
  public <R, E extends CPAException> R accept(InstructionVisitor<R, E> visitor) throws E {
    return visitor.visit(this);
  }
}
