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

  @Override
  public Type getElementType() {
    return this.elementType;
  }

  @Override
  public String toString() {
    return elementType.toString() + "*";
  }

  @Override
  public long sizeInBytes() {
    return getContext().getPointerSize();
  }
}
