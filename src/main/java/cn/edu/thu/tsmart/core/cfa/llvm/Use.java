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

import java.util.Iterator;

/**
 * @author guangchen on 26/02/2017.
 */
public class Use {

  private final Value value;
  private final User user;
  private final int index;
  private Use next = null;

  public Use(Value value, User user, int index) {
    this.value = value;
    this.user = user;
    this.index = index;
  }

  public Value get() {
    return value;
  }

  public Use getNext() {
    return next;
  }

  public User getUser() {
    return user;
  }

  public int getOperandNo() {
    return index;
  }

  public void setNext(Use next) {
    this.next = next;
  }

  public static class LazyIterable implements Iterable<Use> {
    private Iterator<Use> iter;

    public LazyIterable(Use use) {
      iter = new LazyIterator(use);
    }

    @Override
    public Iterator<Use> iterator() {
      return iter;
    }
  }

  public static class LazyIterator implements Iterator<Use> {
    private Use use;

    public LazyIterator(Use use) {
      this.use = use;
    }

    @Override
    public boolean hasNext() {
      return use != null && use.getNext() != null;
    }

    @Override
    public Use next() {
      return use = use.getNext();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("LazyIterator.remove not supported");
    }
  }

  public static Iterable<Use> makeIterable(Use use) {
    return new LazyIterable(use);
  }
}
