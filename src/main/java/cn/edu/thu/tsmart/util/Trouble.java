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
package cn.edu.thu.tsmart.util;

/**
 * Dummy handler for various troubles
 *
 * Always use this class to temporarily eliminate troubles, because:
 * 1. it has generic result which can be used as return value for any function
 * 2. it terminates the program by throwing the function
 * 3. it throws runtime exception which does not affect the function signature
 * 4. we can search reference of this class and fix them later
 *
 * Created by zhoumin on 6/26/17.
 */
public class Trouble {

  private static<T> T error(String msg) {
    try {
      throw new RuntimeException(msg);
    } catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    }
  }

  public static<T> T futureWorkSymbolicPointer() {
    return error("reading or writing of symbolic pointer is not supported.");
  }

  public static<T> T futureWorkBitPrecise() {
    return error("bit-precise operation analysis is not supported now");
  }

  public static<T> T futureWorkMultipleValues() {
    return error("multiple values not handled here.");
  }

  public static<T> T futureWorkPathCondition() {
    return error("multiple values not handled here.");
  }

  public static<T> T futureWorkReport(String errorType) {
    return error("should report " + errorType + " here.");
  }

  public static<T> T futureWork(String msg) {
    return error(msg);
  }

  public static<T> T unexpected(String msg) {
    return error(msg);
  }

  public static<T> T unexpected() {
    return error("something is wrong. it is not possible to reach this line.");
  }

  public static <T> T notImplementedFor(Object witness) {
    return error("not implemented for " + witness.getClass().getCanonicalName());
  }

  public static <T> T notImplemented() {
    return error("not implemented!");
  }
}
