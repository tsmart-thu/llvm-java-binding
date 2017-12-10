/*
 * Copyright (c) 2017
 * ------------------
 * Institute on Software System and Engineering
 * School of Software, Tsinghua University
 *
 * All Rights Reserved.
 *
 * NOTICE:
 * All information contained herein is, and remains the property of Tsinghua University.
 *
 * The intellectual and technical concepts contained herein are proprietary to
 * Tsinghua University and may be covered by China and Foreign Patents, patents in process,
 * and are protected by copyright law.
 *
 * Dissemination of this information or reproduction of this material is strictly forbidden
 * unless prior written permission is obtained from Tsinghua University.
 *
 */
package cn.edu.thu.tsmart.core.cfa.llvm;

/**
 * @author guangchen on 27/05/2017.
 */
public class GlobalVariable extends GlobalObject {
    private Constant initializer;
    private Metadata metadata;

    public GlobalVariable(String name, Type type, Constant init, Metadata pMetadata) {
        super(name, type);
        this.initializer = init;
        this.metadata = pMetadata;
    }

    public Constant getInitializer() {
        return initializer;
    }

    public Metadata getMetadata() { return metadata; }

    @Override
    public String toString() {
        String res = "@";
        res += getName().toString();
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GlobalVariable that = (GlobalVariable) o;

        if (!initializer.equals(that.initializer)) {
            return false;
        }
        return metadata.equals(that.metadata);
    }

    @Override
    public int hashCode() {
        int result = toString().hashCode();
        return result;
    }
}
