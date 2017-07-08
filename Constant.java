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

import cn.edu.thu.tsmart.core.cfa.util.Casting;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author guangchen on 26/02/2017.
 */
public abstract class Constant extends User {

  public Constant() {
  }

  protected Constant(String name, Type type) {
    super(name, type);
  }

  public Constant getAggregateElement(int index) {
    if (this instanceof ConstantAggregate) {
      ConstantAggregate that = Casting.cast(this, ConstantAggregate.class);
      return index < that.getNumOperands() ? (Constant) that.getOperand(index) : null;
    }
    if (this instanceof ConstantAggregateZero) {
      ConstantAggregateZero that = Casting.cast(this, ConstantAggregateZero.class);
      return index < that.getNumElements() ? that.getElementValue(index) : null;
    }
    assert false : "not implemented aggregate type";
    return null;
  }

  public static Constant getNullValue(Type type) {
    switch (type.getTypeID()) {
      case IntegerTyID:
        return ConstantInt.get((IntegerType) type, new APInt(((IntegerType) type).getBitWidth(), "0"));
      case HalfTyID:
      case FloatTyID:
      case DoubleTyID:
      case X86_FP80TyID:
      case FP128TyID:
      case PPC_FP128TyID:
        throw new NotImplementedException();
      case PointerTyID:
        return ConstantPointerNull.get((PointerType) type);
      case StructTyID:
      case ArrayTyID:
      case VectorTyID:
        return ConstantAggregateZero.get(type);
      case TokenTyID:
        throw new NotImplementedException();
      default:
        assert false : "Cannot create a null constant of that type!";
    }
    return null;
  }
}
