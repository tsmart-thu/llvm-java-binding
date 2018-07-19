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
public class StructType extends CompositeType {
  private Type[] elements;
  private String name;
  private boolean isPacked;

  protected StructType(Context context, Type[] elements, String name, boolean isPacked) {
    super(context, TypeID.StructTyID);
    this.elements = elements;
    this.name = name;
    this.isPacked = isPacked;
  }

  public boolean isPacked() {
    return this.isPacked;
  }

  public boolean isLiteral() {
    throw new NotImplementedException();
  }

  public boolean isOpaque() {
    throw new NotImplementedException();
  }

  public boolean hasName() {
    return name != null && !name.equals("");
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setBody(Type[] elements, boolean isPacked) {
    this.elements = elements;
    this.isPacked = isPacked;
  }

  public void setBody(Type[] elements) {
    setBody(elements, false);
  }

  public Type[] elements() {
    return this.elements;
  }

  public boolean isLayoutIdentical(StructType other) {
    throw new NotImplementedException();
  }

  public int getNumElements() {
    return this.elements.length;
  }

  public Type getElementType(int n) {
    return this.elements[n];
  }

  public static StructType create(Context context, String name) {
    return create(context, new Type[] {}, name, false);
  }

  public static StructType create(Context context) {
    return create(context, "");
  }

  public static StructType create(Type[] elements, String name, boolean isPacked) {
    return create(elements[0].getContext(), elements, name, isPacked);
  }

  public static StructType create(Type[] elements) {
    return create(elements, "", false);
  }

  public static StructType create(Context context, Type[] elements, String name, boolean isPacked) {
    return new StructType(context, elements, name, isPacked);
  }

  public static StructType create(Context context, Type[] elements) {
    return create(context, elements, "", false);
  }

  public static StructType create(String name, Type... type) {
    return create(type[0].getContext(), type, name, false);
  }

  public static StructType get(Context context, Type[] elements, boolean isPacked) {
    throw new NotImplementedException();
  }

  public static StructType get(Context context, Type[] elements) {
    return get(context, elements, false);
  }

  public static StructType get(Context context, boolean isPacked) {
    throw new NotImplementedException();
  }

  public static StructType get(Context context) {
    return get(context, false);
  }

  public static StructType get(Type... type) {
    throw new NotImplementedException();
  }

  @Override
  public String toString() {
    if(this.name == null) {
      String res = "{";
      for(int i = 0; i < elements.length; i++) {
        res += " ";
        res += elements[i].toString();
        if(i != elements.length-1)
          res += ",";
        else
          res += " ";
      }
      res += "}";
      return res;
    }
    return "%"+this.name;
  }
}
