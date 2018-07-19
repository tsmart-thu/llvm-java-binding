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

import cn.edu.thu.tsmart.core.cfa.util.Casting;
import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;

/**
 * @author guangchen on 27/02/2017.
 */
public class ExtractValueInst extends UnaryInstruction {

  // TODO initialize in Converter
  private ImmutableList<Integer> indices;

  public ExtractValueInst(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.EXTRACTVALUE;
  }

  // only for Converter
  public void setIndices(ImmutableList<Integer> indices) {
    this.indices = indices;
  }

  public Value getAggregateOperand() {
    return getOperand(0);
  }

  public boolean hasIndices() {
    return true;
  }

  public int getNumIndices() {
    return indices.size();
  }

  public ImmutableList<Integer> getIndices() {
    return indices;
  }

  @Override
  public <R, E extends CPAException> R accept(InstructionVisitor<R, E> visitor) throws E {
    return visitor.visit(this);
  }

  public static Instruction create(Value operand, ImmutableList<Integer> indices) {
    List<Value> list = Arrays.asList(operand);
    ExtractValueInst instruction = new ExtractValueInst("", null);
    instruction.setOperands(list);
    instruction.setIndices(indices);
    return instruction;
  }

  @Override
  public String toString() {
    CallInst callInst = Casting.dyncast(getOperand(0), CallInst.class);
    if (callInst == null) {
      return "";
    }
    String res = "%" + getName().toString() + " = " + getOpcode().toString() + " " + callInst.getType().toString();
    res += " %" + callInst.getName().toString() + ", ";
    for(int i = 0; i <= callInst.getNumArgOperands(); i++) {
      if(callInst.getArgOperandUse(i).getUser() == this) {
        res += callInst.getNumArgOperands() - i;
        break;
      }
    }
    return res;
  }
}
