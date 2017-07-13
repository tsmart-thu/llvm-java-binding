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
import java.util.HashMap;

/**
 * Created by zhch on 2017/5/15.
 */
public class UnaryConstantExpr extends ConstantExpr {

  private Type destTy;
  private static HashMap<Pair<OpCode, Pair<Constant, Type>>, UnaryConstantExpr> instances = new HashMap<>();

  private UnaryConstantExpr(String name, Type type, OpCode opCode, Type destTy) {
    super(name, type, opCode);
    this.destTy = destTy;
  }

  public static UnaryConstantExpr getInstance(String name, Type type, OpCode opCode, Constant op,
      Type destTy) {
    Pair<OpCode, Pair<Constant, Type>> key = Pair.of(opCode, Pair.of(op, destTy));
    if (instances.containsKey(key)) {
      return instances.get(key);
    } else {
      UnaryConstantExpr instance = new UnaryConstantExpr(name, type, opCode, destTy);
      instances.put(key, instance);
      return instance;
    }
  }

  @Override
  public String toString() {
    String res = opCode.toString();
    res += " (";
    res += getOperand(0).getType().toString();
    res += " ";
    res += getOperand(0).toString();
    res += " to ";
    res += destTy.toString();
    res += ")";
    return res;
  }
}
