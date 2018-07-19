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
package cn.edu.thu.tsmart.core.exceptions;

/**
 * Super class for all exceptions thrown by CPA operators.
 *
 * TODO This exception should probably be abstract, and specialized sub-classes
 * should be used for specific reasons.
 */
public abstract class CPAException extends Exception {

  private static final long serialVersionUID = 6846683924964869559L;

  public CPAException(String msg) {
    super(msg);
  }

  public CPAException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public CPAException(Throwable cause) {
    super(cause);
  }
}
