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

import cn.edu.thu.tsmart.core.cfa.util.Formatter;
import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;

import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import static cn.edu.thu.tsmart.core.cfa.util.Casting.cast;
import static cn.edu.thu.tsmart.core.cfa.util.Casting.dyncast;

/**
 * operands of switch are arranged as follows (assume n cases)
 * 0: the value on which the decision is made
 * 1: the default destination
 * 2: the value labeled on the 1st case
 * 3: the destination of the 1st case
 * ...
 * 2n: the value labeled on the n-th case
 * 2n+1: the destination of the n-th case
 *
 * Assume the total number of operands is m (=2n+2), the following holds:
 * 1. The switch instructions has (m/2)-1 cases
 * 2. The value labeled on the i-th case (count from 0) is indexed as 2(i+1)
 * 3. The destination of the i-th case (count from 0) is indexed as 2(i+1)+1
 *
 * -- Note by zhoumin, which needs to be confirmed.
 *
 * @author guangchen on 27/02/2017.
 */
public class SwitchInst extends TerminatorInst {

  public SwitchInst(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.SWITCH;
  }

  public Value getCondition() {
    return getOperand(0);
  }

  public BasicBlock getDefaultDest() {
    return cast(getOperand(1), BasicBlock.class);
  }

  public int getNumCases() {
    return getNumOperands() / 2 - 1;
  }

  /**
   * return the i-th (count from 0) value that corresponds to the case label
   * @param i
   * @return
   */
  public ConstantInt getCaseValue(int i) {
    return dyncast(getOperand((i + 1) * 2), ConstantInt.class);
  }

  /**
   * return the i-th (count from 0) destination basic block
   * @param i
   * @return
   */
  public BasicBlock getCaseDest(int i) {
    return cast(getOperand((i + 1) * 2 + 1), BasicBlock.class);
  }

  @Override
  public int getNumSuccessors() {
    return getNumOperands() / 2;
  }

  @Override
  public BasicBlock getSuccessor(int i) {
    assert i >= 0 && i < getNumSuccessors() : "Successor # out of range for switch!";
    return cast(getOperand(2 * i + 1), BasicBlock.class);
  }

  @Override
  public <R, E extends CPAException> R accept(InstructionVisitor<R, E> visitor) throws E {
    return visitor.visit(this);
  }

  // return i if i-th case meets given ConstantInt; -1 if goes for default case
  // Parameter should have same type as switch condition; otherwise always return default case
  public int findCaseValue(ConstantInt ci) {
    for (int i = 0; i < getNumCases(); ++ i) {
      if (getOperand(2 * (i + 1)) == ci) {
        return i;
      }
    }
    return -1;
  }

  // return null if successor is not found, not unique, or is the default case
  public ConstantInt findCaseDest(BasicBlock block) {
    if (block == getDefaultDest()) {
      return null;
    }
    ConstantInt ci = null;
    for (int i = 0; i < getNumCases(); ++ i) {
      if (getSuccessor(i + 1) == block) {
        if (ci != null) {
          return null;
        } else {
          ci = dyncast(getOperand(2 * (i + 1)), ConstantInt.class);
        }
      }
    }
    return ci;
  }

  @Override
  public String toString() {
    /*String res = "switch ";
    res += getCondition().getType().toString() + " ";
    res += Formatter.asOperand(getCondition());
    res += ", label %" + getDefaultDest().getName();
    res += " [\n";
    for (int i = 1; i <= getNumCases(); i++) {
      BasicBlock basicBlock = getSuccessor(i);
      ConstantInt constantInt = findCaseDest(basicBlock);
      res += "    " + constantInt.getType().toString() + " " + constantInt.toString() + ", label %" + basicBlock.getName() + "\n";
    }
    res += "  ]";*/
    String res = getOpcode().toString() + " ";
    for(int i = 0; i < getNumOperands(); i++) {
      res += getOperand(i).getType().toString() + " ";
      if(getOperand(i).getName().equals("CONSTANT_INT")) {
        res += getOperand(i).toString();
        res += ", ";
      } else {
        res += "%" + getOperand(i).getName();
        if(i == 0)
          res += ", ";
        else if(i == 1)
          res += " [\n    ";
        else if(i == getNumOperands() - 1)
          res += "\n  ]";
        else
          res += "\n    ";
      }
    }
    return res;
  }
}
