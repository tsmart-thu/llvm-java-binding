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
import cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.Predicate;
import java.util.HashMap;
import javax.annotation.Nullable;
import org.bytedeco.javacpp.annotation.Virtual;

/**
 * Created by zhch on 2017/5/15.
 */
public class CompareConstantExpr extends ConstantExpr {

  private Predicate pred;
  private static HashMap<Pair<Pair<OpCode, Predicate>, Pair<Constant, Constant>>, CompareConstantExpr> instances = new HashMap<>();

  private CompareConstantExpr(String name, Type type, OpCode opCode, Predicate pred) {
    super(name, type, opCode);
    this.pred = pred;
  }

  public static CompareConstantExpr getInstance(String name, Type type, OpCode opCode,
      Predicate pred, Constant op1, Constant op2) {
    Pair<Pair<OpCode, Predicate>, Pair<Constant, Constant>> key = Pair
        .of(Pair.of(opCode, pred), Pair.of(op1, op2));
    if (instances.containsKey(key)) {
      return instances.get(key);
    } else {
      CompareConstantExpr instance = new CompareConstantExpr(name, type, opCode, pred);
      instances.put(key, instance);
      return instance;
    }
  }

  @Override
  public Predicate getPredicate() {
    return pred;
  }
}
