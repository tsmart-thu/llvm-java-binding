# llvm-java-binding

There are already java bindings for LLVM C API, like [javacpp-presets](https://github.com/bytedeco/javacpp-presets/tree/master/llvm).
However, using the C API in java is painful and most of the LLVM examples/documents/stackoverflows are about using LLVM in C++ way.
To ease the usage of LLVM in java project, we built this library for writting our own software in java upon analysis LLVM IR.

The goal of this project is finally mocking the full LLVM C++ Class in java.
Currently not all llvm staffs are mapped, but most of common use should be usable. (We used this library in our real world project)
The LLVM and Clang version we use on master branch is **3.9.0**
