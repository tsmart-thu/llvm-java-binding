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
