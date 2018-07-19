/*
 * MIT License
 *
 * Copyright (c) 2018 Institute on Software System and Engineering, School of Software, Tsinghua University
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package cn.edu.thu.tsmart.core.cfa.llvm;

import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;

import cn.edu.thu.tsmart.core.cfa.util.Formatter;
import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

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
//    assert list.size()
//        == getNumIncomingValues() : "Number of incoming blocks is not consistent with PHI values!";
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

  @Override
  public String toString() {
    String res = "%" + getName() + " = ";
    res += getOpcode().toString();
    res += " " + getType().toString() + " ";
    List<String> opStr = new ArrayList<>();
    for (int i = 0; i < getNumIncomingValues(); i++) {
      String item = "[ ";
      String valueStr = Formatter.asOperand(getOperand(i));
      if (valueStr.equals("1")) {
        valueStr = "true";
      }
      item += valueStr + ", ";
      item += "%" + getIncomingBlock(i).getName();
      item += " ]";
      opStr.add(item);
    }
    res += Joiner.on(", ").join(opStr);
    return res;
  }
}
