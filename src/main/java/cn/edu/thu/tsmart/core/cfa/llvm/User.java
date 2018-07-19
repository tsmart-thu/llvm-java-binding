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

    public List<Value> getOperands() {
        return this.operands;
    }

    public List<Use> getOperandList() {
        throw new NotImplementedException();
    }

    public Use getOperandUse(int i) {
        return uses().get(i);
    }

    public int getNumOperands() {
        return operands.size();
    }
}
