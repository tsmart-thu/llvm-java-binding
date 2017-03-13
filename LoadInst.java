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
public class LoadInst extends UnaryInstruction {

  // TODO initialize in Converter
  private boolean isVolatile = false;
  private int alignment = 0;
  private AtomicOrdering ordering = null;
  private SynchronizationScope synchScope = null;

  public LoadInst(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.LOAD;
  }

  // only for Converter
  public void setVolatile(boolean isVolatile) {
    this.isVolatile = isVolatile;
  }

  // only for Converter
  public void setAlignment(int alignment) {
    this.alignment = alignment;
  }

  // only for Converter
  public void setOrdering(AtomicOrdering ordering) {
    this.ordering = ordering;
  }

  // only for Converter
  public void setSynchScope(SynchronizationScope synchScope) {
    this.synchScope = synchScope;
  }

  public boolean isVolatile() {
    return isVolatile;
  }

  public int getAlignment() {
    return alignment;
  }

  public AtomicOrdering getOrdering() {
    return ordering;
  }

  public SynchronizationScope getSynchScope() {
    return synchScope;
  }

  public boolean isSimple() {
    return !isAtomic() && !isVolatile();
  }

  public boolean isUnordered() {
    return (getOrdering() == AtomicOrdering.NOT_ATOMIC || getOrdering() == AtomicOrdering.UNORDERED)
        && !isVolatile();
  }

  public Value getPointerOperand() {
    return getOperand(0);
  }

  public int getPointerAddressSpace() {
    return getPointerOperand().getType().getPointerAddressSpace();
  }
}
