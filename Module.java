package cn.edu.thu.tsmart.core.cfa.llvm;

import java.util.Map;

/**
 * @author guangchen on 24/02/2017.
 */
public class Module {
    private final String moduleIdentifier;
    private final Map<String, Function> functionMap;

    public Module(String moduleIdentifier, Map<String, Function> functionMap) {
        this.moduleIdentifier = moduleIdentifier;
        this.functionMap = functionMap;
    }

    public String getModuleIdentifier() {
        return this.moduleIdentifier;
    }

    public Function getFunction(String name) {
        return functionMap.get(name);
    }
}
