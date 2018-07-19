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
package cn.edu.thu.tsmart.core.cfa.util;

import javax.annotation.Nullable;

/**
 * @author guangchen on 01/03/2017.
 */
public class Casting {

  public static <T1, T2> T2 cast(T1 obj, Class<T2> t2Class) {
    assert t2Class.isInstance(obj) : "cast<Ty>() argument of incompatible type!";
    return t2Class.cast(obj);
  }

  @Nullable
  public static <T1, T2> T2 castOrNull(T1 obj, @Nullable Class<T2> t2Class) {
    if (t2Class == null || obj == null) {
      return null;
    }
    assert t2Class.isInstance(obj) : "cast_or_null<Ty>() argument of incompatible type!";
    return t2Class.cast(obj);
  }

  @Nullable
  public static <T1, T2> T2 dyncast(T1 obj, Class<T2> t2Class) {
    if (t2Class.isInstance(obj)) {
      return t2Class.cast(obj);
    }
    return null;
  }
}
