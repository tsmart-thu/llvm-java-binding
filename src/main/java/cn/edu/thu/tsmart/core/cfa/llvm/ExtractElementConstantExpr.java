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

import cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;

/**
 * Created by zhch on 2017/5/15.
 */
public class ExtractElementConstantExpr extends ConstantExpr {

  private ExtractElementConstantExpr(String name, Type type) {
    super(name, type, OpCode.EXTRACTELEMENT);
  }

  public static ExtractElementConstantExpr getInstance(String name, Type type, Constant op1,
      Constant op2) {
    // this expr should not be used when analysing C
    assert false : "Unhandled constant expression: extractelement";
    return null;
  }
}
