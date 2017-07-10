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
