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
    public int hashCode() {
        int result = moduleIdentifier.hashCode();
        result = 31 * result + functionMap.hashCode();
        result = 31 * result + globalList.hashCode();
        return result;
    }
}
