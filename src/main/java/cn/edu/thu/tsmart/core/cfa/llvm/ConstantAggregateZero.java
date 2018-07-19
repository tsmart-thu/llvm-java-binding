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

import cn.edu.thu.tsmart.core.cfa.util.Casting;
import com.google.common.base.Preconditions;
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

  public Constant getElementValue(int index) {
    if (getType() instanceof SequentialType) {
      return getSequentialElement();
    }
    return getStructElement(index);
  }

  public Constant getStructElement(int index) {
    return Constant.getNullValue(getType().getStructElementType(index));
  }

  public Constant getSequentialElement() {
    return Constant.getNullValue(getType().getSequentialElementType());
  }

  public static Constant get(Type type) {
    Preconditions.checkArgument(type.isStructTy() || type.isArrayTy() || type.isVectorTy(), "Cannot create an aggregate zero of non-aggregate type!");
    ConstantAggregateZero entry = type.getContext().getCAZConstants(type);
    if (entry != null) {
      return entry;
    }
    entry = new ConstantAggregateZero(type);
    type.getContext().putCAZConstants(type, entry);
    return entry;
  }
}
