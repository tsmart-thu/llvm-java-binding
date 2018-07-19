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

import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;

import javax.annotation.Nullable;

import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import static cn.edu.thu.tsmart.core.cfa.util.Casting.dyncast;

/**
 * @author guangchen on 27/02/2017.
 */
public class AllocaInst extends UnaryInstruction {

  // TODO initialize in Converter
  private int alignment = 0;
  private Type allocatedType = null;
  private boolean isUsedWithAlloca = false;
  private boolean isSwiftError = false;

  public AllocaInst(String name, Type type, int alignment) {
    super(name, type);
    super.opCode = OpCode.ALLOCA;
    this.allocatedType = type.getPointerElementType();
    this.alignment = alignment;
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
    ConstantInt ci = dyncast(getOperand(0), ConstantInt.class);
    if (ci != null) {
      return !ci.isOne();
    }
    return true;
  }

  @Nullable
  public Value getArraySize() {
    return getOperand(0);
  }

//  @Override
//  public PointerType getType() {
//    return cast(super.getType(), PointerType.class);
//  }

  public Type getAllocatedType() {
    return allocatedType;
  }

  // NOTICE return type uses int to store unsigned
  public int getAlignment() {
    return alignment;
  }

  public boolean isStaticAlloca() {
    if (getArraySize() != null && !ConstantInt.class.isInstance(getArraySize())) {
      return false;
    }
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
    res += getAllocatedType().toString();
    for(int i = 0; i < getNumOperands(); i++) {
      if(!getOperand(i).getName().toString().equals("CONSTANT_INT")) {
        res += ", " + getOperand(i).getType().toString();
        res += " %" + getOperand(i).getName().toString();
      } else if(getAllocatedType().toString().equals("i8") && getAlignment() == 0) {
        res += ", " + getOperand(i).getType().toString();
        res += " " + getOperand(i);
      }
    }
    if(getAlignment() != 0)
      res += ", align " + getAlignment();
    return res;
  }
}
