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

import cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.Predicate;
import cn.edu.thu.tsmart.core.cfa.util.Casting;
import com.google.common.collect.ImmutableList;

/**
 * Created by zhch on 2017/5/15.
 */
public abstract class ConstantExpr extends Constant {

  protected final OpCode opCode;

  protected ConstantExpr(String name, Type type, OpCode opCode) {
    super(name, type);
    this.opCode = opCode;
  }

  public boolean isCast() {
    if (opCode.ordinal() >= OpCode.TRUNC.ordinal()
        && opCode.ordinal() <= OpCode.ADDRSPACECAST
        .ordinal()) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isCompare() {
    return opCode == OpCode.ICMP || opCode == OpCode.FCMP;
  }

  public boolean hasIndices() {
    return opCode == OpCode.EXTRACTVALUE || opCode == OpCode.EXTRACTELEMENT;
  }

  // TODO require generic_gep_type_iterator
  // isGEPWithNoNotionalOverIndexing()

  public OpCode getOpcode() {
    return opCode;
  }

  // override in CompareConstantExpr
  public Predicate getPredicate() {
    assert this.getClass().isInstance(CompareConstantExpr.class) : "getPredicate from non-CompareConstantExpr";
    return null;
  }

  // override in ExtractValueConstantExpr and InsertValueConstantExpr
  public ImmutableList<Integer> getIndices() {
    assert this.getClass().isInstance(ExtractValueConstantExpr.class) || this.getClass().isInstance(InsertValueConstantExpr.class) : "getIndices from non-ExtractValueConstantExpr-nor-InsertValueConstantExpr";
    return null;
  }
}