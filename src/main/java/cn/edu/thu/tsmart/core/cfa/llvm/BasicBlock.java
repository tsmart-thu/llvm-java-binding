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
    public int hashCode() {
        return instList.hashCode();
    }
}
