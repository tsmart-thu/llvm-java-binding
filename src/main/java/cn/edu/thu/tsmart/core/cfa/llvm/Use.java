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
