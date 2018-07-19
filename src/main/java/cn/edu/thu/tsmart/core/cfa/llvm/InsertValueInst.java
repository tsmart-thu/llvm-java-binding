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

import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;

/**
 * @author guangchen on 27/02/2017.
 */
public class InsertValueInst extends Instruction {

  // TODO initialize in Converter
  private ImmutableList<Integer> indices;

  public InsertValueInst(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.INSERTVALUE;
  }

  // only for Converter
  public void setIndices(ImmutableList<Integer> indices) {
    this.indices = indices;
  }

  public Value getAggregateOperand() {
    return getOperand(0);
  }

  public Value 	getInsertedValueOperand() {
    return getOperand(1);
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

  public static Instruction create(Value operand1, Value operand2, ImmutableList<Integer> indices) {
    List<Value> list = Arrays.asList(operand1, operand2);
    ExtractValueInst instruction = new ExtractValueInst("", null);
    instruction.setOperands(list);
    instruction.setIndices(indices);
    return instruction;
  }
}
