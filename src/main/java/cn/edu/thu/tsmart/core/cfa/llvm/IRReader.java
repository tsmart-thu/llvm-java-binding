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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.bytedeco.javacpp.BytePointer;

import static org.bytedeco.javacpp.LLVM.*;

/** @author guangchen on 24/02/2017. */
public class IRReader {
  public LlvmModule parseIRFile(String fileName, Context context) {
    LLVMMemoryBufferRef bufferRef = new LLVMMemoryBufferRef();
    BytePointer outMessage = new BytePointer(new BytePointer());
    LLVMCreateMemoryBufferWithContentsOfFile(new BytePointer(fileName), bufferRef, outMessage);
    LLVMModuleRef moduleRef = new LLVMModuleRef();
    LLVMParseIRInContext(context.getContextRef(), bufferRef, moduleRef, outMessage);

    Converter converter = new Converter(context);
    return converter.convert(moduleRef, fileName);
  }

  public LlvmModule parseBytecodeFile(String fileName, Context context) {
    LLVMMemoryBufferRef bufferRef = new LLVMMemoryBufferRef();
    BytePointer outMessage = new BytePointer(new BytePointer());
    LLVMCreateMemoryBufferWithContentsOfFile(new BytePointer(fileName), bufferRef, outMessage);
    LLVMModuleRef moduleRef = new LLVMModuleRef();
    LLVMParseBitcodeInContext2(context.getContextRef(), bufferRef, moduleRef);
    Converter converter = new Converter(context);
    return converter.convert(moduleRef, fileName);
  }
}