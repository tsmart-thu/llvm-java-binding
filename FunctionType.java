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
public class FunctionType extends Type {
  protected FunctionType(Context context) {
    super(context, TypeID.FunctionTyID);
  }

  private boolean isVarArg;
  private Type returnType;
  private Type[] params;

  public boolean isVarArg() {
    return isVarArg;
  }

  public Type getReturnType() {
    return returnType;
  }

  public Type[] getParams() {
    return params;
  }

  public int getNumParams() {
    return params.length;
  }

  public static FunctionType get(Type result, Type[] params, boolean isVarArg) {
    return new FunctionType(result.getContext());
  }

  public static FunctionType get(Type result, boolean isVarArg) {
    return get(result, new Type[] {}, isVarArg);
  }

  public static boolean isValidReturnType(Type retTy) {
    throw new NotImplementedException();
  }

  public static boolean isValidArgumentType(Type argTy) {
    throw new NotImplementedException();
  }
}
