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

import cn.edu.thu.sse.common.util.Pair;
import cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import com.google.common.collect.ImmutableList;
import java.util.HashMap;

/**
 * Created by zhch on 2017/5/15.
 */
public class GetElementPtrConstantExpr extends ConstantExpr {

  private ImmutableList<Constant> idxList;
  private static HashMap<Pair<Constant, IndexList>, GetElementPtrConstantExpr> instances = new HashMap<>();

  private GetElementPtrConstantExpr(String name, Type type, ImmutableList<Constant> idxList) {
    super(name, type, OpCode.GETELEMENTPTR);
    this.idxList = idxList;
  }

  public static GetElementPtrConstantExpr getInstance(String name, Type type,
      ImmutableList<Constant> idxList, Constant op) {
    Pair<Constant, IndexList> key = Pair.of(op, new IndexList(idxList));
    if (instances.containsKey(key)) {
      return instances.get(key);
    } else {
      GetElementPtrConstantExpr instance = new GetElementPtrConstantExpr(name, type, idxList);
      instances.put(key, instance);
      return instance;
    }
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
