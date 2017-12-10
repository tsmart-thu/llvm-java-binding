package cn.edu.thu.tsmart.core.cfa.llvm;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author guangchen on 24/02/2017.
 */
public class LlvmModule {
    private final Context context;
    private final String moduleIdentifier;
    private final Map<String, LlvmFunction> functionMap;
    private final List<GlobalVariable> globalList;

    public LlvmModule(Context context, String moduleIdentifier, Map<String, LlvmFunction> functionMap, List<GlobalVariable> globalList) {
        this.context = context;
        this.moduleIdentifier = moduleIdentifier;
        this.functionMap = functionMap;
        this.globalList = globalList;
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

    public Context getContext() {
        return context;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LlvmModule that = (LlvmModule) o;

        if (!moduleIdentifier.equals(that.moduleIdentifier)) {
            return false;
        }
        if (Sets.difference(functionMap.keySet(), that.functionMap.keySet()).size() != 0 ||
            Sets.difference(that.functionMap.keySet(), functionMap.keySet()).size() != 0) {
            return false;
        }
        for (Entry<String, LlvmFunction> p : functionMap.entrySet()) {
            LlvmFunction f = p.getValue();
            LlvmFunction of = that.getFunction(p.getKey());
            if (! f.equals(of)) {
                return false;
            }
        }
        if (globalList.size() != that.globalList.size()) {
            return false;
        }
        for (int i = 0; i < globalList.size(); ++i) {
            if (! globalList.get(i).toString().equals(that.globalList.get(i).toString())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = moduleIdentifier.hashCode();
        result = 31 * result + functionMap.hashCode();
        result = 31 * result + globalList.hashCode();
        return result;
    }
}
