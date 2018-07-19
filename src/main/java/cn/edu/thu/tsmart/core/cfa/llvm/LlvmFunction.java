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

import cn.edu.thu.tsmart.core.cfa.llvm.Attribute.AttributeKind;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

/**
 * @author guangchen on 26/02/2017.
 */
public class LlvmFunction extends GlobalObject {

  private List<BasicBlock> basicBlockList = new ArrayList<>();

  // TODO initialize in Converter
  private AttributeList attrs;
  private List<Argument> argumentList;
  private Metadata metadata;

  public LlvmFunction() {}

  public LlvmFunction(String name, Type type, List<BasicBlock> basicBlockList) {
    super(name, type);
    this.basicBlockList = basicBlockList;
    for (BasicBlock basicBlock : this.basicBlockList) {
      basicBlock.setParent(this);
    }
  }

  // only for Converter
  public void setAttrs(AttributeList attrs) {
    this.attrs = attrs;
  }

  public List<BasicBlock> getBasicBlockList() {
    return basicBlockList;
  }

  public void setBasicBlockList(List<BasicBlock> basicBlockList) {
    this.basicBlockList = basicBlockList;
  }

  public AttributeList getAttributes() {
    return attrs;
  }

  public ImmutableSet<Attribute> getFnAttributes() {
    return attrs.getFnAttributes();
  }

  public boolean hasFnAttribute(AttributeKind attrKind) {
    return attrs.hasFnAttribute(attrKind);
  }

  @Nullable
  public Attribute getFnAttribute(AttributeKind attrKind) {
    return getFnAttribute(attrKind);
  }

  public List<Argument> getArgumentList() {
    return argumentList;
  }

  public void setArgumentList(List<Argument> argumentList) {
    this.argumentList = argumentList;
  }

  @Override
  public String toString() {
    String res = "@";
    res += getName();
    return res;
  }

  @Override
  public int hashCode() {
    int result = basicBlockList.hashCode();
    if (getName() != null) {
      result += 31 * result + getName().hashCode();
    }
    return result;
  }

  public void setMetadata(Metadata metadata) {
    this.metadata = metadata;
  }

  public Metadata getMetadata() {
    return metadata;
  }
}
