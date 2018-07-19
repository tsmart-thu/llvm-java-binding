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
    if (this instanceof ConstantDataSequential) {
      ConstantDataSequential that = Casting.cast(this, ConstantDataSequential.class);
      return index < that.getNumElements() ? that.getElementAsConstant(index) : null;
    }
    assert false : "not implemented aggregate type";
    return null;
  }

  public static Constant getNullValue(Type type) {
    switch (type.getTypeID()) {
      case IntegerTyID:
        return ConstantInt
            .get((IntegerType) type, new APInt(((IntegerType) type).getBitWidth(), "0"));
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
