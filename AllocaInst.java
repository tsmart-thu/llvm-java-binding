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

import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;

/**
 * @author guangchen on 27/02/2017.
 */
public class AllocaInst extends UnaryInstruction {

  // TODO initialize in Converter
  private int alignment = 0;
  private Type allocatedType = null;
  private boolean isUsedWithAlloca = false;
  private boolean isSwiftError = false;

  public AllocaInst(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.ALLOCA;
  }

  // only for Converter
  // NOTICE parameter type uses int to store unsigned
  public void setAlignment(int alignment) {
    this.alignment = alignment;
  }

  // only for Converter
  public void setAllocatedType(Type allocatedType) {
    this.allocatedType = allocatedType;
  }

  // only for Converter
  public void setUsedWithAlloca(boolean isUsedWithAlloca) {
    this.isUsedWithAlloca = isUsedWithAlloca;
  }

  // only for Converter
  public void setSwiftError(boolean isSwiftError) {
    this.isSwiftError = isSwiftError;
  }

  public boolean isArrayAllocation() {
    // TODO require ConstantInt
    // if (ConstantInt *CI = dyn_cast<ConstantInt>(getOperand(0)))
    //   return !CI->isOne();
    return true;
  }

  public Value getArraySize() {
    return getOperand(0);
  }

  @Override
  public PointerType getType() {
    return cast(super.getType(), PointerType.class);
  }

  public Type getAllocatedType() {
    return allocatedType;
  }

  // NOTICE return type uses int to store unsigned
  public int getAlignment() {
    return alignment;
  }

  public boolean isStaticAlloca() {
    // TODO require ConstantInt
    // if (!isa<ConstantInt>(getArraySize())) return false;
    BasicBlock parent = getParent();
    return parent == parent.getParent().getBasicBlockList().get(0) && !isUsedWithInAlloca();
  }

  public boolean isUsedWithInAlloca() {
    return isUsedWithAlloca;
  }

  public boolean isSwiftError() {
    return isSwiftError;
  }

  @Override
  public <R, E extends CPAException> R accept(InstructionVisitor<R, E> visitor) throws E {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    String res = "%" + getName() + " = alloca ";
    res += getType().toString();
    res += ", align " + getAlignment();
    return res;
  }
}
