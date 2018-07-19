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
