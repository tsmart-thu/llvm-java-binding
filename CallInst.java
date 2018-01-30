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

import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.CallingConvention;
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.TailCallKind;
import static cn.edu.thu.tsmart.core.cfa.util.Casting.dyncast;

import cn.edu.thu.tsmart.core.cfa.llvm.Attribute.AttributeKind;
import cn.edu.thu.tsmart.core.cfa.util.Formatter;
import cn.edu.thu.tsmart.core.cfa.util.visitor.InstructionVisitor;
import cn.edu.thu.tsmart.core.exceptions.CPAException;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import javax.annotation.Nullable;

/**
 * @author guangchen on 27/02/2017.
 */
public class CallInst extends Instruction {

  // TODO initialize in Converter
  private FunctionType functionType;
  private TailCallKind tailCallKind;
  private int numArgs;
  private CallingConvention callingConvention;
  private AttributeList attrs;
  private boolean isInlineAsm;

  public CallInst(String name, Type type) {
    super(name, type);
    super.opCode = OpCode.CALL;
  }

  // only for Converter
  public void setFunctionType(FunctionType functionType) {
    this.functionType = functionType;
  }

  // only for Converter
  public void setTailCallKind(TailCallKind tailCallKind) {
    this.tailCallKind = tailCallKind;
  }

  // only for Converter
  public void setNumArgs(int numArgs) {
    assert numArgs >= 0 : "Invalid value for numArgs!";
    this.numArgs = numArgs;
  }

  // only for Converter
  public void setCallingConvention(CallingConvention callingConvention) {
    this.callingConvention = callingConvention;
  }

  // only for Converter
  public void setAttrs(AttributeList attrs) {
    this.attrs = attrs;
  }

  // only for Converter
  public void setInlineAsm(boolean inlineAsm) {
    isInlineAsm = inlineAsm;
  }

  public FunctionType getFunctionType() {
    return functionType;
  }

  public TailCallKind getTailCallKind() {
    return tailCallKind;
  }

  public boolean isTailCall() {
    return tailCallKind == TailCallKind.TCK_TAIL || tailCallKind == TailCallKind.TCK_MUST_TAIL;
  }

  public boolean isMustTailCall() {
    return tailCallKind == TailCallKind.TCK_MUST_TAIL;
  }

  public boolean isNoTailCall() {
    return tailCallKind == TailCallKind.TCK_NO_TAIL;
  }

  public int getNumArgOperands() {
    return numArgs;
  }

  public Value getArgOperand(int i) {
    assert i >= 0 && i <= getNumArgOperands() : "Out of bounds!";
    return getOperand(i);
  }

  public Use getArgOperandUse(int i) {
    assert i >= 0 && i <= getNumArgOperands() : "Out of bounds!";
    return getOperandUse(i);
  }

  public CallingConvention getCallingConvention() {
    return callingConvention;
  }

  public AttributeList getAttributes() {
    return attrs;
  }

  @Nullable
  public ImmutableSet<Attribute> getParamAttributes(int index) {
    if (index < 0 || index >= attrs.getParamAttributes().size()) {
      return null;
    } else {
      return attrs.getParamAttributes().get(index);
    }
  }

  public ImmutableSet<Attribute> getRetAttributes() {
    return attrs.getRetAttributes();
  }

  public ImmutableSet<Attribute> getFnAttributes() {
    return attrs.getRetAttributes();
  }

  public boolean hasParamAttribute(int index, AttributeKind attrKind) {
    return attrs.hasParamAttribute(index, attrKind);
  }

  public boolean hasRetAttribute(AttributeKind attrKind) {
    return attrs.hasRetAttribute(attrKind);
  }

  public boolean hasFnAttribute(AttributeKind attrKind) {
    return attrs.hasFnAttribute(attrKind);
  }

  @Nullable
  public Attribute getParamAttribute(int index, AttributeKind attrKind) {
    return attrs.getParamAttribute(index, attrKind);
  }

  @Nullable
  public Attribute getRetAttribute(AttributeKind attrKind) {
    return attrs.getRetAttribute(attrKind);
  }

  @Nullable
  public Attribute getFnAttribute(AttributeKind attrKind) {
    return attrs.getFnAttribute(attrKind);
  }

  public boolean isInlineAsm() {
    return isInlineAsm;
  }

  public boolean doesNotAccessMemory() {
    return hasFnAttribute(AttributeKind.READNONE);
  }

  public boolean onlyReadsMemory() {
    return doesNotAccessMemory() || hasFnAttribute(AttributeKind.WRITEONLY);
  }

  public boolean doesNotThrow() {
    return hasFnAttribute(AttributeKind.NOUNWIND);
  }

  @Nullable
  public LlvmFunction getCalledFunction() {
    return dyncast(getOperand(getNumOperands() - 1), LlvmFunction.class);
  }

  public Value getCalledValue() {
    return getOperand(getNumOperands() - 1);
  }

  @Override
  public <R, E extends CPAException> R accept(InstructionVisitor<R, E> visitor) throws E {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    String str = "";
    if (isInlineAsm()) {
      str = "call asm " + getType().toString();
      return str;
    }
    if ("".equals(getName()))
      str = "call ";
    else
      str = "%" + getName() + " = call ";
    str += getType().toString() + " ";
    str += getOperand(getNumOperands() - 1).toString() + "(";
    for (int i = 0; i <= getNumArgOperands(); i ++) {
      if (getOperand(0) instanceof Metadata) {
        str += "metadata ??";
      } else {
        str += getOperand(0).getType().toString() + " ";
        str += Formatter.asOperand(getOperand(i));
      }
      if (i < getNumArgOperands()) {
        str += ", ";
      }
    }
    str += ")";
    return str;
  }
}
