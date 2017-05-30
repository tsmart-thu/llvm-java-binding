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
public class SelectConstantExpr extends ConstantExpr {

  private static HashMap<Pair<Pair<Constant, Constant>, Constant>, SelectConstantExpr> instances;

  private SelectConstantExpr(String name, Type type) {
    super(name, type, OpCode.SELECT);
  }

  public static SelectConstantExpr getInstance(String name, Type type, Constant opCond,
      Constant op1, Constant op2) {
    Pair<Pair<Constant, Constant>, Constant> key = Pair.of(Pair.of(op1, op2), opCond);
    if (instances.containsKey(key)) {
      return instances.get(key);
    } else {
      SelectConstantExpr instance = new SelectConstantExpr(name, type);
      instances.put(key, instance);
      return instance;
    }
  }
}
