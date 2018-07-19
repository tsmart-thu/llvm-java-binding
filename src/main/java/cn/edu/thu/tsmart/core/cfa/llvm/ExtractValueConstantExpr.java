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
public class ExtractValueConstantExpr extends ConstantExpr {

  private ImmutableList<Integer> idxList;
  private static HashMap<Pair<Constant, IndexList>, ExtractValueConstantExpr> instances;

  private ExtractValueConstantExpr(String name, Type type, ImmutableList<Integer> idxList) {
    super(name, type, OpCode.EXTRACTVALUE);
    this.idxList = idxList;
  }

  public static ExtractValueConstantExpr getInstance(String name, Type type,
      ImmutableList<Integer> idxList, Constant op) {
    Pair<Constant, IndexList> key = Pair.of(op, new IndexList(idxList));
    if (instances.containsKey(key)) {
      return instances.get(key);
    } else {
      ExtractValueConstantExpr instance = new ExtractValueConstantExpr(name, type, idxList);
      instances.put(key, instance);
      return instance;
    }
  }

  @Override
  public ImmutableList<Integer> getIndices() {
    return idxList;
  }

  private static class IndexList {

    private final ImmutableList<Integer> idxList;

    public IndexList(ImmutableList<Integer> idxList) {
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
