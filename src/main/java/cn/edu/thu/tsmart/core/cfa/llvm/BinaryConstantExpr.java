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

  @Override
  public String toString() {
    String res = getOpcode().toString() + " ";
    if(flags.hasAnyFlag())
      res += flags.toString() + " ";
    res += "(";
    for(int i = 0; i < getNumOperands(); i++) {
      res += getOperand(i).getType().toString() + " ";
      res += getOperand(i).toString();
      if(i != getNumOperands() - 1)
        res += ", ";
    }
    res += ")";
    return res;
  }
}
