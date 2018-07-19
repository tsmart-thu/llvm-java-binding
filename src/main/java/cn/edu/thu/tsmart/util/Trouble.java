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
