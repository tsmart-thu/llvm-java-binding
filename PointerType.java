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

/** @author guangchen on 01/03/2017. */
public class PointerType extends SequentialType {
  private int addressSpace;
  private Type elementType;

  protected PointerType(Context context, Type elementType, int addressSpace) {
    super(context, TypeID.PointerTyID);
    this.addressSpace = addressSpace;
    this.elementType = elementType;
  }

  public static PointerType get(Type elementType, int addressSpace) {
    return elementType.getPointerTo(addressSpace);
  }

  public static PointerType getUnqual(Type elementType) {
    return get(elementType, 0);
  }

  public int getAddressSpace() {
    return addressSpace;
  }

  @Override
  public int getPointerAddressSpace() {
    return getAddressSpace();
  }

  @Override
  public Type getPointerElementType() {
    return this.elementType;
  }
}
