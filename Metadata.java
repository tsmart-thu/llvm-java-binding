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

/**
 * @author guangchen on 16/04/2017.
 */
public class Metadata extends Value {
  private String file;
  private int line;
  private int column;
  public String getFile() {
    return file;
  }
  public int getLine() {
    return line;
  }
  public int getColumn() {
    return column;
  }
  public void setFile(String f) {
    file = f;
  }
  public void setLine(int l) {
    line = l;
  }
  public void setColumn(int c) {
    column = c;
  }
}
