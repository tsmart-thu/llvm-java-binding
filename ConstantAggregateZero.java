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

/** @author guangchen on 08/06/2017. */
public class ConstantAggregateZero extends Constant {
  public ConstantAggregateZero(Type type) {
    super("", type);
  }

  long getNumElements() {
    Type type = getType();
    VectorType vectorType = Casting.dyncast(type, VectorType.class);
    if (vectorType != null) {
      throw new NotImplementedException();
    }
    ArrayType arrayType = Casting.dyncast(type, ArrayType.class);
    if (arrayType != null) {
      return arrayType.getNumElements();
    }
    return type.getStructNumElements();
  }
}
