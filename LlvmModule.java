package cn.edu.thu.tsmart.core.cfa.llvm;

import java.util.Map;

/**
 * @author guangchen on 24/02/2017.
 */
public class LlvmModule {
    private final String moduleIdentifier;
    private final Map<String, LlvmFunction> functionMap;

    public LlvmModule(String moduleIdentifier, Map<String, LlvmFunction> functionMap) {
        this.moduleIdentifier = moduleIdentifier;
        this.functionMap = functionMap;
    }

    public String getModuleIdentifier() {
        return this.moduleIdentifier;
    }

    public LlvmFunction getFunction(String name) {
        return functionMap.get(name);
    }

    public Iterable<Map.Entry<String, LlvmFunction>> functionEntries() {
        return functionMap.entrySet();
    }

    public Iterable<LlvmFunction> functions() {
        return functionMap.values();
    }
}
