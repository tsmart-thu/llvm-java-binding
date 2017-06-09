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

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bytedeco.javacpp.LLVM;

import static org.bytedeco.javacpp.LLVM.*;

/**
 * @author guangchen on 26/02/2017.
 */
public class Context {

  private final LLVMContextRef contextRef;
  private final BiMap<LLVMTypeRef, Type> typeRefTypeMap = HashBiMap.create();
  private final Map<LLVMValueRef, Instruction> valueRefInstructionMap = new HashMap<>();
  private final Map<LLVMBasicBlockRef, BasicBlock> basicBlockRefBasicBlockMap = new HashMap<>();
  private final Map<LLVMValueRef, LlvmFunction> functionMap = new HashMap<>();
  private final Map<LLVMValueRef, GlobalVariable> globalVariableMap = new HashMap<>();
  private final Map<LLVMValueRef, Argument> argumentMap = new HashMap<>();
  private DataLayout dataLayout;

  /**
   * Create an empty context
   */
  public static Context create() {
    return new Context(LLVM.LLVMContextCreate());
  }

  public Context(LLVMContextRef contextRef) {
    this.contextRef = contextRef;
  }

  LLVMContextRef getContextRef() {
    return this.contextRef;
  }

  @Override
  protected void finalize() throws Throwable {
    dispose();
    super.finalize();
  }

  public void dispose() {
    LLVMContextDispose(contextRef);
  }

  public Map<LLVMValueRef, LlvmFunction> getFunctionMap() {
    return functionMap;
  }

  public void putType(LLVMTypeRef typeRef, Type type) {
    typeRefTypeMap.put(typeRef, type);
  }

  public Type getType(LLVMTypeRef typeRef) {
    return typeRefTypeMap.get(typeRef);
  }

  public LLVMTypeRef getTypeRef(Type type) {
    return this.typeRefTypeMap.inverse().get(type);
  }

  public void putInst(LLVMValueRef valueRef, Instruction instruction) {
    valueRefInstructionMap.put(valueRef, instruction);
  }

  public void putFunction(LLVMValueRef valueRef, LlvmFunction function) {
    functionMap.put(valueRef, function);
  }

  public Instruction getInst(LLVMValueRef valueRef) {
    return valueRefInstructionMap.get(valueRef);
  }

  public void putBasicBlock(LLVMBasicBlockRef bb, BasicBlock basicBlock) {
    basicBlockRefBasicBlockMap.put(bb, basicBlock);
  }

  public BasicBlock getBasicBlock(LLVMBasicBlockRef bb) {
    return basicBlockRefBasicBlockMap.get(bb);
  }

  public LlvmFunction getFunction(LLVMValueRef valueRef) {
    return functionMap.get(valueRef);
  }

  public Map<LLVMBasicBlockRef, BasicBlock> getBasicBlockMap() {
    return this.basicBlockRefBasicBlockMap;
  }

  public void putGlobalVariable(LLVMValueRef g, GlobalVariable variable) {
    this.globalVariableMap.put(g, variable);
  }

  public GlobalVariable getGlobalVariable(LLVMValueRef g) {
    return globalVariableMap.get(g);
  }

  public void setDataLayout(DataLayout dataLayout) {
    this.dataLayout = dataLayout;
  }

  public DataLayout getDataLayout() {
    return dataLayout;
  }

  public void putArgument(LLVMValueRef valueRef, Argument argument) {
    this.argumentMap.put(valueRef, argument);
  }

  public Argument getArgument(LLVMValueRef valueRef) {
    return this.argumentMap.get(valueRef);
  }
}
