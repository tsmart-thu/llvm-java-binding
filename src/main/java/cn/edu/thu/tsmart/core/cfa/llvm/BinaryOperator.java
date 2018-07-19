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
import java.util.Arrays;
import java.util.List;

import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OperatorFlags;

/**
 * @author guangchen on 27/02/2017.
 */
public class BinaryOperator extends Instruction {

  public BinaryOperator(String name, Type type, OpCode opcode) {
    super(name, type);
    super.opCode = opcode;
  }

  @Override
  public <R, E extends CPAException> R accept(InstructionVisitor<R, E> visitor) throws E {
    return visitor.visit(this);
  }

  private String operatorToString() {
    switch (opCode) {
      case ADD:
        return "add";
      case FADD:
        return "fadd";
      case SUB:
        return "sub";
      case FSUB:
        return "fsub";
      case MUL:
        return "mul";
      case FMUL:
        return "fmul";
      case SDIV:
        return "sdiv";
      case UDIV:
        return "udiv";
      case UREM:
        return "urem";
      case FREM:
        return "frem";
      case SHL:
        return "shl";
      case ASHR:
        return "ashr";
      case LSHR:
        return "lshr";
      case AND:
        return "and";
      case OR:
        return "or";
      case XOR:
        return "xor";
      case SREM:
        return "srem";
      case FDIV:
        return "fdiv";
    }
    return "";
  }

  public static Instruction create(OpCode opCode, Value operand1, Value operand2) {
    List<Value> list = Arrays.asList(operand1, operand2);
    BinaryOperator instruction = new BinaryOperator("", operand1.getType(), opCode);
    instruction.setOperands(list);
    return instruction;
  }

  @Override
  public String toString() {
    String res = "%" + getName() + " = " + operatorToString()  + " ";
    OperatorFlags operatorFlags = getOperatorFlags();
    if (operatorFlags != null && !operatorFlags.toString().equals("")) {
      res += operatorFlags.toString() + " ";
    }
    res +=
        getType().toString() + " " + Formatter.asOperand(getOperand(0)) + ", " + Formatter.asOperand(getOperand(1));
    return res;
  }
}
