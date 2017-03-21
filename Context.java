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

import static org.bytedeco.javacpp.LLVM.*;

/**
 * @author guangchen on 26/02/2017.
 */
public class Context {
    private final LLVMContextRef contextRef;

    public Context(LLVMContextRef contextRef) {
        this.contextRef = contextRef;
    }

    LLVMContextRef getContextRef() {
        return this.contextRef;
    }

    @Override
    protected void finalize() throws Throwable {
        LLVMContextDispose(contextRef);
        super.finalize();
    }
}
