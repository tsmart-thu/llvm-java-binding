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

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/** @author guangchen on 01/03/2017. */
public class FunctionType extends Type {
  protected FunctionType(Type result, Type[] params, boolean isVarArg) {
    super(result.getContext(), TypeID.FunctionTyID);
    this.returnType = result;
    this.params = params;
    this.isVarArg = isVarArg;
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

  @Override
  public boolean isFunctionVarArg() {
    return isVarArg();
  }

  @Override
  public Type[] getFunctionParamType() {
    return getParams();
  }

  @Override
  public int getFunctionNumParams() {
    return getNumParams();
  }

  public static FunctionType get(Type result, Type[] params, boolean isVarArg) {
    return new FunctionType(result, params, isVarArg);
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

  @Override
  public String toString() {
    String res = returnType + " (";
    for(int i = 0; i < params.length; i++) {
      res += params[i].toString();
      if(i < params.length - 1 || isVarArg())
        res += ", ";
    }
    if(isVarArg())
      res += "...)";
    else
      res += ")";
    return res;
  }
}
