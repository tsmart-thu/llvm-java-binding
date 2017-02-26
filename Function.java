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

import static org.bytedeco.javacpp.LLVM.*;

/**
 * @author guangchen on 26/02/2017.
 */
public class Function extends GlobalObject {
    private List<BasicBlock> basicBlockList = new ArrayList<>();

    Function(LLVMValueRef valueRef) {
        super(valueRef);
        for (LLVMBasicBlockRef bb = LLVMGetFirstBasicBlock(valueRef); bb != null; bb = LLVMGetNextBasicBlock(bb)) {
            basicBlockList.add(new BasicBlock(bb));
        }
    }

    public List<BasicBlock> getBasicBlockList() {
        return basicBlockList;
    }
}
