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
    //get filename
    File file = new File(fileName);
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(file));
      String line = null;
      while((line = reader.readLine()) != null) {
        if(line.length() < 2)
          continue;
        if(line.charAt(0) == '!' && line.charAt(1) >= '0' && line.charAt(1) <= '9') {
          String[] s = line.split(" = |\\(|\\)");
          if(s[1].equals("!DIFile")) {
            String[] ss = (s[2]).split(", |: ");
            context.putFilename(1, ss[1].replace("\"", ""));
          } else if(s[1].contains("!DIGlobalVariable")) {
            //TODO： get GlobalVariable Metadata
            System.out.println('c');
            String[] ss = (s[2]).split(", |: ");
            Metadata m = new Metadata();
            m.setFile(context.getFilename(Integer.valueOf(ss[5].replace("!", ""))));
            m.setLine(Integer.valueOf(ss[7]));
            context.putGlobalVariableMetadata(ss[1].replace("\"", ""), m);
          }
        }
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

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
