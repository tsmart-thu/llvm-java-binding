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

import cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.OpCode;
import cn.edu.thu.tsmart.core.cfa.llvm.InstructionProperties.Predicate;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by zhch on 2017/5/15.
 */
public abstract class ConstantExpr extends Constant {

  protected final OpCode opCode;

  protected ConstantExpr(String name, Type type, OpCode opCode) {
    super(name, type);
    this.opCode = opCode;
  }

  public boolean isCast() {
    if (opCode.ordinal() >= OpCode.TRUNC.ordinal()
        && opCode.ordinal() <= OpCode.ADDRSPACECAST
        .ordinal()) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isCompare() {
    return opCode == OpCode.ICMP || opCode == OpCode.FCMP;
  }

  public boolean hasIndices() {
    return opCode == OpCode.EXTRACTVALUE || opCode == OpCode.EXTRACTELEMENT;
  }

  // TODO require generic_gep_type_iterator
  // isGEPWithNoNotionalOverIndexing()

  public OpCode getOpcode() {
    return opCode;
  }

  // override in CompareConstantExpr
  public Predicate getPredicate() {
    assert this.getClass().isInstance(CompareConstantExpr.class) : "getPredicate from non-CompareConstantExpr";
    return null;
  }

  // override in ExtractValueConstantExpr and InsertValueConstantExpr
  public ImmutableList<Integer> getIndices() {
    assert this.getClass().isInstance(ExtractValueConstantExpr.class) || this.getClass().isInstance(InsertValueConstantExpr.class) : "getIndices from non-ExtractValueConstantExpr-nor-InsertValueConstantExpr";
    return null;
  }

  public Instruction getAsInstruction() {
    switch (getOpcode()) {
      case TRUNC:
      case ZEXT:
      case SEXT:
      case FPTRUNC:
      case FPEXT:
      case UITOFP:
      case SITOFP:
      case FPTOSI:
      case FPTOUI:
      case PTRTOINT:
      case INTTOPTR:
      case BITCAST:
      case ADDRSPACECAST:
        return CastInst.create(getOpcode(), getOperand(0), getType());
      case SELECT:
        return SelectInst.create(getOperand(0), getOperand(1), getOperand(2));
      case INSERTELEMENT:
        return InsertElementInst.create(getOperand(0) ,getOperand(1) ,getOperand(2));
      case EXTRACTELEMENT:
        return ExtractElementInst.create(getOperand(0), getOperand(1));
      case SHUFFLEVECTOR: {
        ShuffleVectorInst shuffleVectorInst = new ShuffleVectorInst("", getType());
        shuffleVectorInst.setOperands(getOperands());
        return shuffleVectorInst;
      }
      case GETELEMENTPTR: {
        GetElementPtrConstantExpr gep = (GetElementPtrConstantExpr) this;
        GetElementPtrInst inst = new GetElementPtrInst("", getType());
        inst.setIsInBounds(gep.isInBounds());
        inst.setOperands(getOperands());
        return inst;
      }
      case ADD:
      case FADD:
      case SUB:
      case FSUB:
      case MUL:
      case FMUL:
      case UDIV:
      case SDIV:
      case UREM:
      case SREM:
      case FREM:
      case SHL:
      case LSHR:
      case ASHR:
      case AND:
      case OR:
      case XOR: {
        return BinaryOperator.create(getOpcode(), getOperand(0), getOperand(1));
      }
      case FCMP:
      case ICMP: {
        return CmpInst.create(getOpcode(), getPredicate(), getOperand(0), getOperand(1));
      }
      case EXTRACTVALUE: {
        return ExtractValueInst.create(getOperand(0), getIndices());
      }
      case INSERTVALUE: {
        return InsertValueInst.create(getOperand(0), getOperand(1), getIndices());
      }
      // TODO UnaryConstantExpr
      case ALLOCA:
      case LOAD:
      case STORE:
      case VA_ARG:
        throw new NotImplementedException();
      default:
        Preconditions.checkArgument(false, "Unhandled getAsInstruction Opcode %s", opCode.toString());
        return null;
    }
  }
}
