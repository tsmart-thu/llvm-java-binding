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

import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;
import com.google.common.collect.ImmutableList;

/**
 * @author guangchen on 27/02/2017.
 */
public class PhiNode extends Instruction {

  // TODO initialize in Converter
  private ImmutableList<BasicBlock> incomingBlocks;

  public PhiNode(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.PHI;
  }

  // only for Converter
  public void setIncomingBlocks(ImmutableList<BasicBlock> list) {
    assert list.size()
        == getNumIncomingValues() : "Number of incoming blocks is not consistent with PHI values!";
    this.incomingBlocks = list;
  }

  public int getNumIncomingValues() {
    return getNumOperands();
  }

  public Value getIncomingValue(int i) {
    assert i >= 0 && i < getNumIncomingValues() : "Out of range of PHI incoming values!";
    return getOperand(i);
  }

  public BasicBlock getIncomingBlock(int i) {
    assert i >= 0 && i < getNumIncomingValues() : "Out of range of PHI incoming blocks!";
    return incomingBlocks.get(i);
  }

  public BasicBlock getIncomingBlock(Value v) {
    for (int i = 0; i < getNumIncomingValues(); ++ i) {
      if (v == getIncomingValue(i)) {
        return incomingBlocks.get(i);
      }
    }

    assert false : "Value doesn't point to PHI's Uses?";
    return null;
  }

  public int getBasicBlockIndex(BasicBlock block) {
    for (int i = 0; i < getNumIncomingValues(); ++ i) {
      if (block == incomingBlocks.get(i)) {
        return i;
      }
    }
    return -1;
  }

  public Value getIncomingValueForBlock(BasicBlock block) {
    int index = getBasicBlockIndex(block);
    assert index >= 0 : "Invalid basic block argument!";
    return getIncomingValue(index);
  }

  public Value hasConstantValue() {
    Value constantValue = getIncomingValue(0);
    for (int i = 1; i < getNumIncomingValues(); ++ i) {
      if (getIncomingValue(i) != constantValue && getIncomingValue(i) != this) {
        if (constantValue != this) {
          return null;
        } else {
          constantValue = getIncomingValue(i);
        }
      }
    }
    if (constantValue == this) {
      // TODO require UndefValue
      // return UndefValue::get(getType())
      return null;
    } else {
      return constantValue;
    }
  }

  public boolean hasConstantOrUndefValue() {
    Value constantValue = null;
    for (int i = 0; i < getNumIncomingValues(); ++ i) {
      Value incoming = getIncomingValue(i);
      // TODO require UndefValue
      // if (incoming != this && !isa<UndefValue>(incoming)) {
      if (incoming != this) {
        if (constantValue != null && constantValue != incoming) {
          return false;
        } else {
          constantValue = incoming;
        }
      }
    }
    return true;
  }

  @Override
  public <R, E extends CPAException> R accept(InstructionVisitor<R, E> visitor) throws E {
    return visitor.visit(this);
  }
}
