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
import cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OperatorFlags;
import java.util.HashMap;

/**
 * Created by zhch on 2017/5/15.
 */
public class BinaryConstantExpr extends ConstantExpr {

  private OperatorFlags flags;
  private static HashMap<Pair<Pair<OpCode, OperatorFlags>, Pair<Constant, Constant>>, BinaryConstantExpr> instances = new HashMap<>();

  private BinaryConstantExpr(String name, Type type, OpCode opCode, OperatorFlags flags) {
    super(name, type, opCode);
    this.flags = flags;
  }

  public static BinaryConstantExpr getInstance(String name, Type type, OpCode opCode,
      OperatorFlags flags, Constant op1, Constant op2) {
    Pair<Pair<OpCode, OperatorFlags>, Pair<Constant, Constant>> key = Pair
        .of(Pair.of(opCode, flags), Pair.of(op1, op2));
    if (instances.containsKey(key)) {
      return instances.get(key);
    } else {
      BinaryConstantExpr instance = new BinaryConstantExpr(name, type, opCode, flags);
      instances.put(key, instance);
      return instance;
    }
  }
}
