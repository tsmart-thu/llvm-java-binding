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

import cn.edu.thu.sse.common.util.Pair;
import cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import com.google.common.collect.ImmutableList;
import java.util.HashMap;

/**
 * Created by zhch on 2017/5/15.
 */
public class GetElementPtrConstantExpr extends ConstantExpr {

  private ImmutableList<Constant> idxList;
  private boolean isInBounds = false;
  private static HashMap<Pair<Pair<Constant, IndexList>, Boolean>, GetElementPtrConstantExpr> instances = new HashMap<>();

  private GetElementPtrConstantExpr(String name, Type type, ImmutableList<Constant> idxList, boolean isInBounds) {
    super(name, type, OpCode.GETELEMENTPTR);
    this.idxList = idxList;
    this.isInBounds = isInBounds;
  }

  public static GetElementPtrConstantExpr getInstance(String name, Type type,
      ImmutableList<Constant> idxList, Constant op, boolean isInBounds) {
    Pair<Pair<Constant, IndexList>, Boolean> key = Pair.of(Pair.of(op, new IndexList(idxList)), isInBounds);
    if (instances.containsKey(key)) {
      return instances.get(key);
    } else {
      GetElementPtrConstantExpr instance = new GetElementPtrConstantExpr(name, type, idxList, isInBounds);
      instances.put(key, instance);
      return instance;
    }
  }

  public boolean isInBounds() {
    return isInBounds;
  }

  @Override
  public String toString() {
    String res = "";
    res += opCode.toString();
    res += " inbounds (";
    res += getOperand(0).getType().toString();
    res = res.substring(0, res.length()-1);
    for(int i = 0; i < getNumOperands(); i++) {
      res += ", ";
      res += getOperand(i).getType().toString();
      res += " ";
      res += getOperand(i).toString();
    }
    res += ")";
    return res;
  }

  private static class IndexList {

    private final ImmutableList<Constant> idxList;

    public IndexList(ImmutableList<Constant> idxList) {
      this.idxList = idxList;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null || !obj.getClass().isInstance(IndexList.class)) {
        return false;
      }
      IndexList other = (IndexList) obj;
      if (this.idxList.size() != other.idxList.size()) {
        return false;
      }
      return this.idxList.containsAll(other.idxList);
    }

    @Override
    public int hashCode() {
      return idxList.hashCode();
    }
  }
}
