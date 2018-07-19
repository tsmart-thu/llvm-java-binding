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
package cn.edu.thu.tsmart.core.cfa.util.visitor;

import cn.edu.thu.tsmart.core.cfa.llvm.AllocaInst;
import cn.edu.thu.tsmart.core.cfa.llvm.AtomicCmpXchgInst;
import cn.edu.thu.tsmart.core.cfa.llvm.AtomicRMWInst;
import cn.edu.thu.tsmart.core.cfa.llvm.BinaryOperator;
import cn.edu.thu.tsmart.core.cfa.llvm.BranchInst;
import cn.edu.thu.tsmart.core.cfa.llvm.CallInst;
import cn.edu.thu.tsmart.core.cfa.llvm.CastInst;
import cn.edu.thu.tsmart.core.cfa.llvm.CatchPadInst;
import cn.edu.thu.tsmart.core.cfa.llvm.CatchReturnInst;
import cn.edu.thu.tsmart.core.cfa.llvm.CatchSwitchInst;
import cn.edu.thu.tsmart.core.cfa.llvm.CleanupPadInst;
import cn.edu.thu.tsmart.core.cfa.llvm.CleanupReturnInst;
import cn.edu.thu.tsmart.core.cfa.llvm.ExtractElementInst;
import cn.edu.thu.tsmart.core.cfa.llvm.ExtractValueInst;
import cn.edu.thu.tsmart.core.cfa.llvm.FCmpInst;
import cn.edu.thu.tsmart.core.cfa.llvm.FenceInst;
import cn.edu.thu.tsmart.core.cfa.llvm.GetElementPtrInst;
import cn.edu.thu.tsmart.core.cfa.llvm.ICmpInst;
import cn.edu.thu.tsmart.core.cfa.llvm.IndirectBrInst;
import cn.edu.thu.tsmart.core.cfa.llvm.InsertElementInst;
import cn.edu.thu.tsmart.core.cfa.llvm.InsertValueInst;
import cn.edu.thu.tsmart.core.cfa.llvm.InvokeInst;
import cn.edu.thu.tsmart.core.cfa.llvm.LandingPadInst;
import cn.edu.thu.tsmart.core.cfa.llvm.LoadInst;
import cn.edu.thu.tsmart.core.cfa.llvm.PhiNode;
import cn.edu.thu.tsmart.core.cfa.llvm.ResumeInst;
import cn.edu.thu.tsmart.core.cfa.llvm.ReturnInst;
import cn.edu.thu.tsmart.core.cfa.llvm.SelectInst;
import cn.edu.thu.tsmart.core.cfa.llvm.ShuffleVectorInst;
import cn.edu.thu.tsmart.core.cfa.llvm.StoreInst;
import cn.edu.thu.tsmart.core.cfa.llvm.SwitchInst;
import cn.edu.thu.tsmart.core.cfa.llvm.UnreachableInst;
import cn.edu.thu.tsmart.core.cfa.llvm.VAArgInst;
import cn.edu.thu.tsmart.core.exceptions.CPAException;

/**
 * Created by tomgu on 4/21/17.
 * InstructionVisitor: visitor to visit each instruction.
 * We will organize it by llvm instruction reference.
 * For each instruction, we have a visit method to visit this instruction.
 */
public interface InstructionVisitor<R, E extends CPAException> {

  // Terminator instructions

  R visit(ReturnInst returnInst) throws E;

  R visit(BranchInst branchInst) throws E;

  R visit(SwitchInst switchInst) throws E;

  R visit(IndirectBrInst indirectBrInst) throws E;

  R visit(InvokeInst invokeInst) throws E;

  R visit(ResumeInst resumeInst) throws E;

  R visit(CatchSwitchInst catchSwitchInst) throws E;

  R visit(CatchReturnInst catchReturnInst) throws E;

  R visit(CleanupReturnInst cleanupReturnInst) throws E;

  R visit(UnreachableInst unreachableInst) throws E;

  // Binary Operations

  R visit(BinaryOperator binaryOperator) throws E;

  // Vector Operations

  R visit(ExtractElementInst extractElementInst) throws E;

  R visit(InsertElementInst insertElementInst) throws E;

  R visit(ShuffleVectorInst shuffleVectorInst) throws E;

  // Aggregate Operations

  R visit(ExtractValueInst extractValueInst) throws E;

  R visit(InsertValueInst insertValueInst) throws E;

  // Memory Access

  R visit(AllocaInst allocaInst) throws E;

  R visit(LoadInst loadInst) throws E;

  R visit(StoreInst storeInst) throws E;

  R visit(FenceInst fenceInst) throws E;

  R visit(AtomicCmpXchgInst atomicCmpXchgInst) throws E;

  R visit(AtomicRMWInst atomicCmpXchgInst) throws E;

  R visit(GetElementPtrInst getElementPtrInst) throws E;

  // Conversion Operations

  R visit(CastInst castInst) throws E;


  // other instructions
  R visit(ICmpInst iCmpInst) throws E;

  R visit(FCmpInst fCmpInst) throws E;

  R visit(PhiNode phiNode) throws E;

  R visit(SelectInst selectInst) throws E;

  R visit(CallInst callInst) throws E;

  R visit(VAArgInst vaArgInst) throws E;

  R visit(LandingPadInst landingPadInst) throws E;

  R visit(CatchPadInst catchPadInst) throws E;

  R visit(CleanupPadInst cleanupPadInst) throws E;

}
