package cn.edu.thu.tsmart.core.cfa.llvm;

import org.bytedeco.javacpp.SizeTPointer;

import java.util.Map;

import static org.bytedeco.javacpp.LLVM.*;

/**
 * @author guangchen on 24/02/2017.
 */
public class Module {
    private String moduleIdentifier;
    private Map<String, Function> functionMap;

    Module(LLVMModuleRef moduleRef) {
        SizeTPointer sizeTPointer = new SizeTPointer(64);
        this.moduleIdentifier = LLVMGetModuleIdentifier(moduleRef, sizeTPointer).getString();
        initFunctionMap(moduleRef);
    }

    public String getModuleIdentifier() {
        return this.moduleIdentifier;
    }

    public Function getFunction(String name) {
        return functionMap.get(name);
    }

    private void initFunctionMap(LLVMModuleRef moduleRef) {
        for (LLVMValueRef f = LLVMGetFirstFunction(moduleRef); f != null; f = LLVMGetNextFunction(f)) {
            Function func = new Function(f);
            String name = func.getName();
            functionMap.put(name, func);
        }
    }
}
