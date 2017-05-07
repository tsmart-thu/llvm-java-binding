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

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author guangchen on 26/02/2017.
 */
public class Value {
    private String name;
    private Type type;
    private List<Use> uses;

    public Value() {}

    public Value(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public boolean hasName() {
        return name != null && !name.equals("");
    }

    public List<Use> uses() {
        return this.uses;
    }

    public void setUses(final List<Use> uses) {
        this.uses = uses;
    }

    public List<User> users() {
        return Lists.transform(this.uses, new Function<Use, User>() {
            @Nullable
            @Override
            public User apply(@Nullable Use input) {
                return Optional.fromNullable(input).transform(new Function<Use, User>() {
                    @Override
                    public User apply(Use input) {
                        return input.getUser();
                    }
                }).orNull();
            }
        });
    }

}
