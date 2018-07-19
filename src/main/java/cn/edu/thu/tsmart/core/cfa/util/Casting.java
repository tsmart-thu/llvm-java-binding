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
