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

import cn.edu.thu.tsmart.core.cfa.llvm.Attribute.AttributeKind;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * @author guangchen on 26/02/2017.
 */
public class LlvmFunction extends GlobalObject {

  private List<BasicBlock> basicBlockList = new ArrayList<>();

  // TODO initialize in Converter
  private AttributeList attrs;

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
}
