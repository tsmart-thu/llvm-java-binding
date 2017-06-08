package cn.edu.thu.tsmart.core.cfa.llvm;

import java.util.List;
import java.util.Map;

/**
 * @author guangchen on 24/02/2017.
 */
public class LlvmModule {
    private final Context context;
    private final String moduleIdentifier;
    private final Map<String, LlvmFunction> functionMap;
    private final List<GlobalVariable> globalList;
    private final DataLayout dataLayout;

    public LlvmModule(Context context, String moduleIdentifier, Map<String, LlvmFunction> functionMap, List<GlobalVariable> globalList, DataLayout dataLayout) {
        this.context = context;
        this.moduleIdentifier = moduleIdentifier;
        this.functionMap = functionMap;
        this.globalList = globalList;
        this.dataLayout = dataLayout;
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

    public List<GlobalVariable> getGlobalList() {
        return globalList;
    }

    public DataLayout getDataLayout() {
        return dataLayout;
    }

    public Context getContext() {
        return context;
    }
}
