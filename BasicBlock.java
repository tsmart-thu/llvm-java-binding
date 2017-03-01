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
public class BasicBlock extends Value {
    private List<Instruction> instList = new ArrayList<>();
    private Function parent;

    BasicBlock(LLVMBasicBlockRef basicBlockRef, Function parent) {
        super(LLVMBasicBlockAsValue(basicBlockRef));
        this.parent = parent;
        for (LLVMValueRef inst = LLVMGetFirstInstruction(basicBlockRef); inst != null; inst = LLVMGetNextInstruction(inst)) {
            int opcode = LLVMGetInstructionOpcode(inst);
            switch (opcode) {
                case LLVMRet:
                    instList.add(new ReturnInst(inst));
                    break;
                case LLVMBr:
                    instList.add(new BranchInst(inst));
                    break;
                case LLVMSwitch:
                    instList.add(new SwitchInst(inst));
                    break;
                case LLVMIndirectBr:
                    instList.add(new IndirectBrInst(inst));
                    break;
                case LLVMInvoke:
                    instList.add(new InvokeInst(inst));
                    break;
                case LLVMResume:
                    instList.add(new ResumeInst(inst));
                    break;
                case LLVMUnreachable:
                    instList.add(new UnreachableInst(inst));
                    break;
                case LLVMCleanupRet:
                    instList.add(new CleanupReturnInst(inst));
                    break;
                case LLVMCatchRet:
                    instList.add(new CatchReturnInst(inst));
                    break;
                case LLVMCatchSwitch:
                    instList.add(new CatchSwitchInst(inst));
                    break;
                case LLVMAdd:
                case LLVMFAdd:
                case LLVMSub:
                case LLVMFSub:
                case LLVMMul:
                case LLVMFMul:
                case LLVMUDiv:
                case LLVMSDiv:
                case LLVMFDiv:
                case LLVMURem:
                case LLVMSRem:
                case LLVMFRem:
                case LLVMShl:
                case LLVMLShr:
                case LLVMAShr:
                case LLVMAnd:
                case LLVMOr:
                case LLVMXor:
                    instList.add(new BinaryOperator(inst, opcode));
                    break;
                case LLVMAlloca:
                    instList.add(new AllocaInst(inst));
                    break;
                case LLVMLoad:
                    instList.add(new LoadInst(inst));
                    break;
                case LLVMStore:
                    instList.add(new StoreInst(inst));
                    break;
                case LLVMGetElementPtr:
                    instList.add(new GetElementPtrInst(inst));
                    break;
                case LLVMFence:
                    instList.add(new FenceInst(inst));
                    break;
                case LLVMAtomicCmpXchg:
                    instList.add(new AtomicCmpXchgInst(inst));
                    break;
                case LLVMAtomicRMW:
                    instList.add(new AtomicRMWInst(inst));
                    break;
                case LLVMTrunc:
                    instList.add(new TruncInst(inst));
                    break;
                case LLVMZExt:
                    instList.add(new ZExtInst(inst));
                    break;
                case LLVMSExt:
                    instList.add(new SExtInst(inst));
                    break;
                case LLVMFPToUI:
                    instList.add(new FPToUIInst(inst));
                    break;
                case LLVMFPToSI:
                    instList.add(new FPToSIInst(inst));
                    break;
                case LLVMUIToFP:
                    instList.add(new UIToFPInst(inst));
                    break;
                case LLVMSIToFP:
                    instList.add(new SIToFPInst(inst));
                    break;
                case LLVMFPTrunc:
                    instList.add(new FPTruncInst(inst));
                    break;
                case LLVMFPExt:
                    instList.add(new FPExtInst(inst));
                    break;
                case LLVMPtrToInt:
                    instList.add(new PtrToIntInst(inst));
                    break;
                case LLVMIntToPtr:
                    instList.add(new IntToPtrInst(inst));
                    break;
                case LLVMBitCast:
                    instList.add(new BitCastInst(inst));
                    break;
                case LLVMAddrSpaceCast:
                    instList.add(new AddrSpaceCastInst(inst));
                    break;
                case LLVMCleanupPad:
                    instList.add(new CleanupPadInst(inst));
                    break;
                case LLVMCatchPad:
                    instList.add(new CatchPadInst(inst));
                    break;
                case LLVMICmp:
                    instList.add(new ICmpInst(inst));
                    break;
                case LLVMFCmp:
                    instList.add(new FCmpInst(inst));
                    break;
                case LLVMPHI:
                    instList.add(new PHINode(inst));
                    break;
                case LLVMCall:
                    instList.add(new CallInst(inst));
                    break;
                case LLVMSelect:
                    instList.add(new SelectInst(inst));
                    break;
                case LLVMUserOp1:
                case LLVMUserOp2:
                    throw new IllegalArgumentException("UserOp1 / UserOp2 should not appear in ir file");
                case LLVMVAArg:
                    instList.add(new VAArgInst(inst));
                    break;
                case LLVMExtractElement:
                    instList.add(new ExtractElementInst(inst));
                    break;
                case LLVMInsertElement:
                    instList.add(new InsertElementInst(inst));
                    break;
                case LLVMShuffleVector:
                    instList.add(new ShuffleVectorInst(inst));
                    break;
                case LLVMExtractValue:
                    instList.add(new ExtractValueInst(inst));
                    break;
                case LLVMInsertValue:
                    instList.add(new InsertValueInst(inst));
                    break;
                case LLVMLandingPad:
                    instList.add(new LandingPadInst(inst));
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled instruction: " + inst.toString());
            }
            instList.add(new Instruction(inst));
        }
    }

    public List<Instruction> getInstList() {
        return instList;
    }

    public Function getParent() {
        return parent;
    }
}
