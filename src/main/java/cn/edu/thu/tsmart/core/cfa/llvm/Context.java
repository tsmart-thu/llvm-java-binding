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
  private final Map<Type, Long> typeStoreSizeMap = new HashMap<>();
  private final BiMap<LLVMValueRef, Instruction> valueRefInstructionMap = HashBiMap.create();
  private final Map<LLVMBasicBlockRef, BasicBlock> basicBlockRefBasicBlockMap = new HashMap<>();
  private final Map<LLVMValueRef, LlvmFunction> functionMap = new HashMap<>();
  private final Map<LLVMValueRef, GlobalVariable> globalVariableMap = new HashMap<>();
  private final Map<LLVMValueRef, Argument> argumentMap = new HashMap<>();
  private final Map<Type, ConstantAggregateZero> cazMap = new HashMap<>();
  private final Map<Integer, String> filenameMap = new HashMap<>();
  private final Map<String, Metadata> globalVariableMetadataMap = new HashMap<>();
  private DataLayout dataLayout;
  private Map<String, Instruction> nameInstructionMap = new HashMap<>();

  public long getPointerSize() {
    return pointerSize;
  }

  private long pointerSize;

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
    //String name = LLVMGetValueName(valueRef).getString();
    String name = instruction.getName();
    if(!name.equals("")) {
      nameInstructionMap.put(name, instruction);
    }
  }

  public void putFunction(LLVMValueRef valueRef, LlvmFunction function) {
    functionMap.put(valueRef, function);
  }

  public Instruction getInst(LLVMValueRef valueRef) {
    return valueRefInstructionMap.get(valueRef);
  }

  public LLVMValueRef getLLVMValueRefByInst(Instruction instruction) { return valueRefInstructionMap.inverse().get(instruction); }

  public Instruction getInstByName(String name) {
    return nameInstructionMap.get(name);
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

  public DataLayout getDataLayout() {
    return dataLayout;
  }

  public void putTypeStoreSize(Type type, long storeSize) {
    typeStoreSizeMap.put(type, storeSize);
  }

  public long getTypeStoreSize(Type type) {
    return typeStoreSizeMap.get(type);
  }

  public void putArgument(LLVMValueRef valueRef, Argument argument) {
    this.argumentMap.put(valueRef, argument);
  }

  public Argument getArgument(LLVMValueRef valueRef) {
    return this.argumentMap.get(valueRef);
  }

  public ConstantAggregateZero getCAZConstants(Type type) {
    return cazMap.get(type);
  }

  public void putCAZConstants(Type type, ConstantAggregateZero entry) {
    this.cazMap.put(type, entry);
  }

  public String getFilename(Integer i) {return filenameMap.get(i);}

  public void putFilename(Integer i, String m) {
    this.filenameMap.put(i, m);
  }

  public Metadata getGlobalVariableMetadata(String name) {return globalVariableMetadataMap.get(name);}

  public void putGlobalVariableMetadata(String name, Metadata m) {
    this.globalVariableMetadataMap.put(name, m);
  }

  public void putPointerSize(long l) {
    this.pointerSize = l;
  }

  private Map<String, String> functionToFile = new HashMap<>();
  public void putFunctionFilename(String functionName, String fileName) {
    this.functionToFile.put(functionName, fileName);
  }
  public String getFunctionFilename(String functionName) {
    return this.functionToFile.get(functionName);
  }
  private Map<String, Integer> functionToLine = new HashMap<>();
  public void putFunctionLine(String functionName, int line) {
    this.functionToLine.put(functionName, line);
  }
  public int getFunctionLine(String functionName) {
    Integer line = this.functionToLine.get(functionName);
    return line != null ? line : 0;
  }
}
