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

import java.util.ArrayList;
import java.util.List;

/**
 * @author guangchen on 26/02/2017.
 */
public class BasicBlock extends Value {
    private List<Instruction> instList = new ArrayList<>();
    private LlvmFunction parent;

    public BasicBlock() {}

    public BasicBlock(String name, Type type, LlvmFunction parent, List<Instruction> instList) {
        super(name, type);
        this.parent = parent;
        this.instList = instList;
    }

    public BasicBlock(String name, Type type, LlvmFunction parent) {
        super(name, type);
        this.parent = parent;
    }

    public List<Instruction> getInstList() {
        return instList;
    }

    public void setInstList(List<Instruction> instList) {
        this.instList = instList;
    }

    public LlvmFunction getParent() {
        return parent;
    }

    public void setParent(LlvmFunction parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BasicBlock that = (BasicBlock) o;

        if (instList.size() != that.instList.size()) {
            return false;
        }

        for (int i = 0; i < instList.size(); ++i) {
            if (! instList.get(i).toString().equals(that.instList.get(i).toString())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return instList.hashCode();
    }
}
