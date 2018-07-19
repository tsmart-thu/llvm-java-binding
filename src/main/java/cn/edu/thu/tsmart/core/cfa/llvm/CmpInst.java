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

import static cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.*;

import java.util.Arrays;
import java.util.List;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author guangchen on 27/02/2017.
 */
public abstract class CmpInst extends Instruction {

  // TODO initialize in Converter when constructing child classes
  protected Predicate predicate = null;

  protected CmpInst(String name, Type type) {
    super(name, type);
  }

  // only for Converter
  public void setPredicate(Predicate predicate) {
    this.predicate = predicate;
  }

  public Predicate getPredicate() {
    return predicate;
  }

  public boolean isFPPredicate() {
    return predicate.ordinal() >= Predicate.FCMP_FALSE.ordinal()
        && predicate.ordinal() <= Predicate.FCMP_TRUE.ordinal();
  }

  public boolean isIntPredicate() {
    return predicate.ordinal() >= Predicate.ICMP_EQ.ordinal()
        && predicate.ordinal() <= Predicate.ICMP_SLE.ordinal();
  }

  public Predicate getInversePredicate() {
    return getInversePredicate(predicate);
  }

  public static Predicate getInversePredicate(Predicate predicate) {
    switch (predicate) {
      case ICMP_EQ:
        return Predicate.ICMP_NE;
      case ICMP_NE:
        return Predicate.ICMP_EQ;
      case ICMP_UGT:
        return Predicate.ICMP_ULE;
      case ICMP_ULT:
        return Predicate.ICMP_UGE;
      case ICMP_UGE:
        return Predicate.ICMP_ULT;
      case ICMP_ULE:
        return Predicate.ICMP_UGT;
      case ICMP_SGT:
        return Predicate.ICMP_SLE;
      case ICMP_SLT:
        return Predicate.ICMP_SGE;
      case ICMP_SGE:
        return Predicate.ICMP_SLT;
      case ICMP_SLE:
        return Predicate.ICMP_SGT;
      case FCMP_OEQ:
        return Predicate.FCMP_UNE;
      case FCMP_ONE:
        return Predicate.FCMP_UEQ;
      case FCMP_OGT:
        return Predicate.FCMP_ULE;
      case FCMP_OLT:
        return Predicate.FCMP_UGE;
      case FCMP_OGE:
        return Predicate.FCMP_ULT;
      case FCMP_OLE:
        return Predicate.FCMP_UGT;
      case FCMP_UEQ:
        return Predicate.FCMP_ONE;
      case FCMP_UNE:
        return Predicate.FCMP_OEQ;
      case FCMP_UGT:
        return Predicate.FCMP_OLE;
      case FCMP_ULT:
        return Predicate.FCMP_OGE;
      case FCMP_UGE:
        return Predicate.FCMP_OLT;
      case FCMP_ULE:
        return Predicate.FCMP_OGT;
      case FCMP_ORD:
        return Predicate.FCMP_UNO;
      case FCMP_UNO:
        return Predicate.FCMP_ORD;
      case FCMP_TRUE:
        return Predicate.FCMP_FALSE;
      case FCMP_FALSE:
        return Predicate.FCMP_TRUE;
      default:
        assert false : "Unknown cmp predicate!";
        return null;
    }
  }

  public Predicate getSwappedPredicate() {
    return getSwappedPredicate(predicate);
  }

  public static Predicate getSwappedPredicate(Predicate predicate) {
    switch (predicate) {
      case ICMP_EQ:
      case ICMP_NE:
        return predicate;
      case ICMP_SGT:
        return Predicate.ICMP_SLT;
      case ICMP_SLT:
        return Predicate.ICMP_SGT;
      case ICMP_SGE:
        return Predicate.ICMP_SLE;
      case ICMP_SLE:
        return Predicate.ICMP_SGE;
      case ICMP_UGT:
        return Predicate.ICMP_ULT;
      case ICMP_ULT:
        return Predicate.ICMP_UGT;
      case ICMP_UGE:
        return Predicate.ICMP_ULE;
      case ICMP_ULE:
        return Predicate.ICMP_UGE;
      case FCMP_FALSE:
      case FCMP_TRUE:
      case FCMP_OEQ:
      case FCMP_ONE:
      case FCMP_UEQ:
      case FCMP_UNE:
      case FCMP_ORD:
      case FCMP_UNO:
        return predicate;
      case FCMP_OGT:
        return Predicate.FCMP_OLT;
      case FCMP_OLT:
        return Predicate.FCMP_OGT;
      case FCMP_OGE:
        return Predicate.FCMP_OLE;
      case FCMP_OLE:
        return Predicate.FCMP_OGE;
      case FCMP_UGT:
        return Predicate.FCMP_ULT;
      case FCMP_ULT:
        return Predicate.FCMP_UGT;
      case FCMP_UGE:
        return Predicate.FCMP_ULE;
      case FCMP_ULE:
        return Predicate.FCMP_UGE;
      default:
        assert false : "Unknown cmp predicate!";
        return null;
    }
  }

  @Override
  public abstract boolean isCommutative();

  public abstract boolean isEquality();

  public boolean isSigned() {
    return isSigned(predicate);
  }

  public static boolean isSigned(Predicate predicate) {
    switch (predicate) {
      case ICMP_SLT:
      case ICMP_SLE:
      case ICMP_SGT:
      case ICMP_SGE:
        return true;
      default:
        return false;
    }
  }

  public boolean isUnsigned() {
    return isUnsigned(predicate);
  }

  public static boolean isUnsigned(Predicate predicate) {
    switch (predicate) {
      case ICMP_ULT:
      case ICMP_ULE:
      case ICMP_UGT:
      case ICMP_UGE:
        return true;
      default:
        return false;
    }
  }

  public boolean isTrueWhenEqual() {
    return isTrueWhenEqual(predicate);
  }

  public static boolean isTrueWhenEqual(Predicate predicate) {
    switch (predicate) {
      case ICMP_EQ:
      case ICMP_UGE:
      case ICMP_ULE:
      case ICMP_SGE:
      case ICMP_SLE:
      case FCMP_TRUE:
      case FCMP_UEQ:
      case FCMP_UGE:
      case FCMP_ULE:
        return true;
      default:
        return false;
    }
  }

  public boolean isFalseWhenEqual() {
    return isFalseWhenEqual(predicate);
  }

  public static boolean isFalseWhenEqual(Predicate predicate) {
    switch (predicate) {
      case ICMP_NE:
      case ICMP_UGT:
      case ICMP_ULT:
      case ICMP_SGT:
      case ICMP_SLT:
      case FCMP_FALSE:
      case FCMP_ONE:
      case FCMP_OGT:
      case FCMP_OLT:
        return true;
      default:
        return false;
    }
  }

  public boolean isImpliedTrueByMatchingCmp(Predicate predicate) {
    return isImpliedTrueByMatchingCmp(this.predicate, predicate);
  }

  public static boolean isImpliedTrueByMatchingCmp(Predicate pred1, Predicate pred2) {
    if (pred1 == pred2) {
      return true;
    }

    switch (pred1) {
      case ICMP_EQ:
        return pred2 == Predicate.ICMP_UGE || pred2 == Predicate.ICMP_ULE
            || pred2 == Predicate.ICMP_SGE || pred2 == Predicate.ICMP_SLE;
      case ICMP_UGT:
        return pred2 == Predicate.ICMP_NE || pred2 == Predicate.ICMP_UGE;
      case ICMP_ULT:
        return pred2 == Predicate.ICMP_NE || pred2 == Predicate.ICMP_ULE;
      case ICMP_SGT:
        return pred2 == Predicate.ICMP_NE || pred2 == Predicate.ICMP_SGE;
      case ICMP_SLT:
        return pred2 == Predicate.ICMP_NE || pred2 == Predicate.ICMP_SLE;
      default:
        return false;
    }
  }

  public boolean isImpliedFalseByMatchingCmp(Predicate predicate) {
    return isImpliedFalseByMatchingCmp(this.predicate, predicate);
  }

  public static boolean isImpliedFalseByMatchingCmp(Predicate pred1, Predicate pred2) {
    return isImpliedTrueByMatchingCmp(pred1, getInversePredicate(pred2));
  }

  public static Instruction create(OpCode opCode, Predicate predicate, Value op1, Value op2) {
    List<Value> list = Arrays.asList(op1, op2);
    if(opCode == OpCode.ICMP) {
      ICmpInst instruction = new ICmpInst("", null, predicate);
      instruction.setOperands(list);
      return instruction;
    } else {
      FCmpInst instruction = new FCmpInst("", null, predicate);
      instruction.setOperands(list);
      return instruction;
    }
  }
}
