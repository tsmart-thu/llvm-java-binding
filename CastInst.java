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

import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode.BITCAST;

import cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import cn.edu.thu.tsmart.core.cfa.util.Formatter;
import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;

/**
 * @author guangchen on 27/02/2017.
 */
public class CastInst extends UnaryInstruction {

  protected CastInst(String name, Type type) {
    super(name, type);
  }

  public boolean isIntegerCast() {
    switch (getOpcode()) {
      case ZEXT:
      case SEXT:
      case TRUNC:
        return true;
      case BITCAST:
        return getOperand(0).getType().isIntegerTy() && getType().isIntegerTy();
      default:
        return false;
    }
  }

  public boolean isLosslessCast() {
    if (getOpcode() != BITCAST) {
      return false;
    }

    Type srcTy = getOperand(0).getType();
    Type dstTy = getType();
    if (srcTy == dstTy) {
      return true;
    }

    if (srcTy.isPointerTy()) {
      return dstTy.isPointerTy();
    }

    return false;
  }

  public boolean isNoopCast(OpCode opCode, Type srcTy, Type destTy, Type intPtrTy) {
    switch (opCode) {
      case TRUNC:
      case ZEXT:
      case SEXT:
      case FPTRUNC:
      case FPEXT:
      case UITOFP:
      case SITOFP:
      case FPTOUI:
      case FPTOSI:
      case ADDRSPACECAST:
        return false;
      case BITCAST:
        return true;
      case PTRTOINT:
        return intPtrTy.getScalarSizeInBits() == destTy.getScalarSizeInBits();
      case INTTOPTR:
        return intPtrTy.getScalarSizeInBits() == srcTy.getScalarSizeInBits();
      default:
        assert false : "Invalid CastOp";
        return false;
    }
  }

  public boolean isNoopCast(Type intPtrTy) {
    return isNoopCast(getOpcode(), getOperand(0).getType(), getType(), intPtrTy);
  }

  // TODO require DataLayout
  // isNoopCast(DataLayout dl)

  public Type getSrcTy() {
    return getOperand(0).getType();
  }

  public Type getDestTy() {
    return getType();
  }

  @Override
  public <R, E extends CPAException> R accept(InstructionVisitor<R, E> visitor) throws E {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    String res = "%" + getName();
    res += " = " + getOpcode().toString() + " ";
    res += getSrcTy().toString() + " ";
    res += Formatter.asOperand(getOperand(0));
    res += " to ";
    res += getDestTy().toString();
    return res;
  }
}
