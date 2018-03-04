/*
 * Copyright (c) 2018
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

import cn.edu.thu.tsmart.core.cfa.util.Casting;

/**
 * Created by fzp on 18-3-4.
 */
public class ConstantDataVector extends ConstantDataSequential {

  public ConstantDataVector(String name, Type type) {
    super(name, type);
  }

  public VectorType getType() {
    return Casting.cast(super.getType(), VectorType.class);
  }
}
