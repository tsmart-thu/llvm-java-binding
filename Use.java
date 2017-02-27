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

import com.sun.istack.internal.Nullable;

import java.util.List;

/**
 * @author guangchen on 26/02/2017.
 */
public class Use {
    Value value;
    User user;
    int index;
    Use next = null;

    public Use(Value value, User user, int index) {
        this.value = value;
        this.user = user;
        this.index = index;
    }

    public Value get() {
        return value;
    }

    public @Nullable Use getNext() {
        return next;
    }

    public User getUser() {
        return user;
    }

    public int getOperandNo() {
        return index;
    }
}
