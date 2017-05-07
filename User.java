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

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * @author guangchen on 26/02/2017.
 */
public class User extends Value {
    //
//    User(LLVMValueRef valueRef) {
//        super(valueRef);
//        for (int i = 0, end = LLVMGetNumOperands(valueRef); i < end; i++) {
//            Value value = new Value(LLVMGetOperand(valueRef, i));
//            use.add(new Use(value, this, i));
//        }
//        for (int i = 0; i < use.size() - 1; i ++) {
//            use.get(i).setNext(use.get(i + 1));
//        }
//    }

    private List<Value> operands;

    public User() {}

    public User(String name, Type type) {
        super(name, type);
    }

    public Value getOperand(int i) {
        return operands.get(i);
    }

    public void setOperands(List<Value> operands) {
        this.operands = operands;
    }

    public List<Use> getOperandList() {
        throw new NotImplementedException();
    }

    public Use getOperandUse(int i) {
        throw new NotImplementedException();
    }

    public int getNumOperands() {
        return operands.size();
    }
}
