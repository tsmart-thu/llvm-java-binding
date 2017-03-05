package cn.edu.thu.tsmart.core.cfa.llvm;

import org.bytedeco.javacpp.BytePointer;

import static org.bytedeco.javacpp.LLVM.*;

/**
 * @author guangchen on 24/02/2017.
 */
public class IRReader {
    public Module parseIRFile(String fileName, Context context) {
        LLVMMemoryBufferRef bufferRef = new LLVMMemoryBufferRef();
        BytePointer outMessage = new BytePointer(new BytePointer());
        LLVMCreateMemoryBufferWithContentsOfFile(new BytePointer(fileName), bufferRef, outMessage);
        LLVMModuleRef moduleRef = new LLVMModuleRef();
        LLVMParseIRInContext(context.getContextRef(), bufferRef, moduleRef, outMessage);
        Converter converter = new Converter(context);
        return converter.convert(moduleRef);
    }
}
