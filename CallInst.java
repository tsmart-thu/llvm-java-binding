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

import static cn.edu.thu.tsmart.core.cfa.util.Casting.*;
import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.*;

import cn.edu.thu.tsmart.core.cfa.llvm.AttributeList.Attribute;
import cn.edu.thu.tsmart.core.cfa.llvm.AttributeList.AttrKind;
import com.google.common.collect.ImmutableSet;
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
    assert i >= 0 && i < getNumArgOperands() : "Out of bounds!";
    return getOperand(i);
  }

  public Use getArgOperandUse(int i) {
    assert i >= 0 && i < getNumArgOperands() : "Out of bounds!";
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
    if (index < 0 || index >= attrs.paramAttributes.size()) {
      return null;
    } else {
      return attrs.paramAttributes.get(index);
    }
  }

  public ImmutableSet<Attribute> getRetAttributes() {
    return attrs.retAttributes;
  }

  public ImmutableSet<Attribute> getFnAttributes() {
    return attrs.fnAttributes;
  }

  public boolean hasParamAttribute(int index, AttrKind attrKind) {
    return attrs.hasParamAttribute(index, attrKind);
  }

  public boolean hasRetAttribute(AttrKind attrKind) {
    return attrs.hasRetAttribute(attrKind);
  }

  public boolean hasFnAttribute(AttrKind attrKind) {
    return attrs.hasFnAttribute(attrKind);
  }

  @Nullable
  public Attribute getParamAttribute(int index, AttrKind attrKind) {
    return attrs.getParamAttribute(index, attrKind);
  }

  @Nullable
  public Attribute getRetAttribute(AttrKind attrKind) {
    return attrs.getRetAttribute(attrKind);
  }

  @Nullable
  public Attribute getFnAttribute(AttrKind attrKind) {
    return attrs.getFnAttribute(attrKind);
  }

  public boolean isInlineAsm() {
    return isInlineAsm;
  }

  @Nullable
  public LlvmFunction getCalledFunction() {
    return dyncast(getOperand(getNumOperands() - 1), LlvmFunction.class);
  }

  public Value getCalledValue() {
    return getOperand(getNumOperands() - 1);
  }
}
