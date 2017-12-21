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
    return converter.convert(moduleRef);
  }

  public LlvmModule parseBytecodeFile(String fileName, Context context) {
    LLVMMemoryBufferRef bufferRef = new LLVMMemoryBufferRef();
    BytePointer outMessage = new BytePointer(new BytePointer());
    LLVMCreateMemoryBufferWithContentsOfFile(new BytePointer(fileName), bufferRef, outMessage);
    LLVMModuleRef moduleRef = new LLVMModuleRef();
    LLVMParseBitcodeInContext2(context.getContextRef(), bufferRef, moduleRef);
    Converter converter = new Converter(context);
    return converter.convert(moduleRef);
  }
}
