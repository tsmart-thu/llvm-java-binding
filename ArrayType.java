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

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/** @author guangchen on 01/03/2017. */
public class ArrayType extends SequentialType {
  private Type elementType;
  private long numElements;

  protected ArrayType(Context context, Type elementType, long numElements) {
    super(context, TypeID.ArrayTyID);
    this.elementType = elementType;
    this.numElements = numElements;
  }

  public long getNumElements() {
    return numElements;
  }

  @Override
  public Type getElementType() {
    return elementType;
  }

  public static ArrayType get(Type elementType, long numElements) {
    return new ArrayType(elementType.getContext(), elementType, numElements);
  }
}
