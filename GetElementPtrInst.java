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
import static cn.edu.thu.tsmart.core.cfa.util.Casting.*;

/**
 * @author guangchen on 27/02/2017.
 */
public class GetElementPtrInst extends Instruction {

  // TODO initialize in Converter
  private Type sourceElementType = null;
  private Type resultElementType = null;
  private boolean isInBounds = false;

  public GetElementPtrInst(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.GETELEMENTPTR;
  }

  // only for Converter
  public void setSourceElementType(Type sourceElementType) {
    this.sourceElementType = sourceElementType;
  }

  // only for Converter
  public void setResultElementType(Type resultElementType) {
    this.resultElementType = resultElementType;
  }

  // only for Converter
  public void setIsInBounds(boolean inBounds) {
    isInBounds = inBounds;
  }

  public Type getSourceElementType() {
    return sourceElementType;
  }

  public Type getResultElementType() {
    // TODO require Type.getElementType()
    // assert resultElementType == cast(getType().getScalarType().getElementType(), PointerType.class);
    return resultElementType;
  }

  public Value getPointerOperand() {
    return getOperand(0);
  }

  public Type getPointerOperandType() {
    return getPointerOperand().getType();
  }

  public int getPointerAddressSpace() {
    return getPointerOperandType().getPointerAddressSpace();
  }

  public int getAddressSpace() {
    return getPointerAddressSpace();
  }

  public int getNumIndices() {
    return getNumOperands() - 1;
  }

  public boolean hasIndices() {
    return getNumOperands() > 1;
  }

  // TODO require ConstantInt
  // hasAllZeroIndices
  // hasAllConstantIndices

  public boolean isInBounds() {
    return isInBounds;
  }

  // TODO require APInt and DataLayout
  // accumulateConstantOffset
}
