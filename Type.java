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

import cn.edu.thu.tsmart.core.cfa.util.Casting;
import com.google.common.base.Preconditions;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/** @author guangchen on 01/03/2017. */
public class Type {
  public enum TypeID {
    VoidTyID,
    HalfTyID,
    FloatTyID,
    DoubleTyID,
    X86_FP80TyID,
    FP128TyID,
    PPC_FP128TyID,
    LabelTyID,
    MetadataTyID,
    X86_MMXTyID,
    TokenTyID,
    IntegerTyID,
    FunctionTyID,
    StructTyID,
    ArrayTyID,
    PointerTyID,
    VectorTyID
  }

  private final TypeID typeID;
  private final Context context;

  protected Type(Context context, TypeID id) {
    this.context = context;
    this.typeID = id;
  }

  public TypeID getTypeID() {
    return typeID;
  }

  public Context getContext() {
    return context;
  }

  public boolean isVoidTy() {
    return this.typeID == TypeID.VoidTyID;
  }

  public boolean isHalfTy() {
    return this.typeID == TypeID.HalfTyID;
  }

  public boolean isFloatTy() {
    return this.typeID == TypeID.FloatTyID;
  }

  public boolean isDoubleTy() {
    return this.typeID == TypeID.DoubleTyID;
  }

  public boolean isX86_FP80T() {
    return this.typeID == TypeID.X86_FP80TyID;
  }

  public boolean isFP128Ty() {
    return this.typeID == TypeID.FP128TyID;
  }

  public boolean isPPC_FP128Ty() {
    return this.typeID == TypeID.PPC_FP128TyID;
  }

  public boolean isFloatingPointTy() {
    return getTypeID() == TypeID.HalfTyID ||
        getTypeID() == TypeID.FloatTyID ||
        getTypeID() == TypeID.DoubleTyID ||
        getTypeID() == TypeID.X86_FP80TyID ||
        getTypeID() == TypeID.FP128TyID ||
        getTypeID() == TypeID.PPC_FP128TyID;
  }

  public boolean isX86_MMXTy() {
    return this.typeID == TypeID.X86_MMXTyID;
  }

  public boolean isFPOrFPVectorTy() {
    return getScalarType().isFloatingPointTy();
  }

  public boolean isLabelTy() {
    return this.typeID == TypeID.LabelTyID;
  }

  public boolean isMetadataTy() {
    return this.typeID == TypeID.MetadataTyID;
  }

  public boolean isTokenTy() {
    return this.typeID == TypeID.TokenTyID;
  }

  public boolean isIntegerTy() {
    return this.typeID == TypeID.IntegerTyID;
  }

  public boolean isIntegerTy(int bitWidth) {
    if(isIntegerTy()) {
      IntegerType integerType = Casting.dyncast(this, IntegerType.class);
      return integerType.getBitWidth() == bitWidth;
    }
    return false;
  }

  public boolean isIntOrIntVectorTy() {
    return getScalarType().isIntegerTy();
  }

  public boolean isFunctionTy() {
    return this.typeID == TypeID.FunctionTyID;
  }

  public boolean isStructTy() {
    return this.typeID == TypeID.StructTyID;
  }

  public boolean isArrayTy() {
    return this.typeID == TypeID.ArrayTyID;
  }

  public boolean isPointerTy() {
    return this.typeID == TypeID.PointerTyID;
  }

  public boolean isPtrOrPtrVectorTy() {
    return getScalarType().isPointerTy();
  }

  public boolean isVectorTy() {
    return this.typeID == TypeID.VectorTyID;
  }

  public boolean canLosslesslyBitCastTo(Type type) {
    if(this == type)
      return true;

    if(!this.isFirstClassType() || !type.isFirstClassType())
      return false;

    if(isVectorTy()) {
      VectorType thisType = Casting.dyncast(this, VectorType.class);
      if(type.isVectorTy()) {
        VectorType thatType = Casting.dyncast(type, VectorType.class);
        return thisType.getBitWidth() == thatType.getBitWidth();
      }
      if(this.isX86_MMXTy() && thisType.getBitWidth() == 64)
        return true;
    }
    if(this.isX86_MMXTy() && type.isVectorTy()) {
      VectorType thatType = Casting.dyncast(type, VectorType.class);
      if(thatType.getBitWidth() == 64)
        return true;
    }

    if(isPointerTy() && type.isPointerTy()) {
      PointerType PTy = Casting.dyncast(this, PointerType.class);
      PointerType OtherPTy = Casting.dyncast(type, PointerType.class);
      return PTy.getAddressSpace() == OtherPTy.getAddressSpace();
    }

    return false;
  }

  public boolean isEmptyTy() {
    if(isArrayTy()) {
      ArrayType ATy = Casting.dyncast(this, ArrayType.class);
      long numElements = ATy.getNumElements();
      return numElements == 0 || ATy.getElementType().isEmptyTy();
    }

    if(isStructTy()) {
      StructType STy = Casting.dyncast(this, StructType.class);
      long numElements = STy.getNumElements();
      for(int i = 0; i < numElements; i++) {
        if(!STy.getElementType(i).isEmptyTy())
          return false;
      }
      return true;
    }

    return false;
  }

  public boolean isFirstClassType() {
    return this.typeID != TypeID.FunctionTyID && this.typeID != TypeID.VoidTyID;
  }

  public boolean isSingleValueType() {
    return isFloatingPointTy() || isX86_MMXTy() || isIntegerTy() ||
        isPointerTy() || isVectorTy();
  }

  public boolean isAggregateType() {
    return this.typeID == TypeID.StructTyID || this.typeID == TypeID.ArrayTyID;
  }

  public boolean isSized() {
    if(isIntegerTy() || isFloatingPointTy() || isPointerTy() || isX86_MMXTy())
      return true;

    if(!isStructTy() && !isArrayTy() && !isVectorTy())
      return false;

    return isSizedDerivedType();
  }

  public boolean isSizedDerivedType() {
    if(isArrayTy()) {
      ArrayType arrayType = Casting.dyncast(this, ArrayType.class);
      return arrayType.getElementType().isSized();
    } else if(isVectorTy()) {
      VectorType vectorType = Casting.dyncast(this, VectorType.class);
      return vectorType.getElementType().isSized();
    } else {
      StructType structType = Casting.dyncast(this, StructType.class);
      for(int i = 0; i < structType.getNumElements(); i++) {
        if(!structType.getElementType(i).isSized())
          return false;
      }
      return true;
    }
  }

  public int getPrimitiveSizeInBits() {
    if(isHalfTy()) return 16;
    else if(isFloatTy()) return 32;
    else if(isDoubleTy() || isX86_MMXTy()) return 64;
    else if(isX86_FP80T()) return 80;
    else if(isFP128Ty() || isPPC_FP128Ty()) return 128;
    else if(isIntegerTy()){
      IntegerType integerType = Casting.dyncast(this, IntegerType.class);
      return integerType.getBitWidth();
    } else if(isVectorTy()) {
      VectorType vectorType = Casting.dyncast(this, VectorType.class);
      return vectorType.getBitWidth();
    }
    return 0;
  }

  public int getScalarSizeInBits() {
    return getScalarType().getPrimitiveSizeInBits();
  }

  public int getFPMantissaWidth() {
    if(isVectorTy()) {
      VectorType vectorType = Casting.dyncast(this, VectorType.class);
      return vectorType.getElementType().getFPMantissaWidth();
    }
    if (isHalfTy()) return 11;
    if (isFloatTy()) return 24;
    if (isDoubleTy()) return 53;
    if (isX86_FP80T()) return 64;
    if (isFP128Ty()) return 113;
    return -1;
  }

  public Type getScalarType() {
    VectorType vectorType = Casting.dyncast(this, VectorType.class);
    if (vectorType != null) {
      return vectorType.getElementType();
    }
    return this;
  }

  public Type getContainedType() {
    throw new NotImplementedException();
  }

  public int getNumContainedTypes() {
    throw new NotImplementedException();
  }

  public int getIntegerBitWidth() {
    if(isIntegerTy()){
      IntegerType integerType = Casting.dyncast(this, IntegerType.class);
      return integerType.getBitWidth();
    }
    return 0;
  }

  public Type[] getFunctionParamType() {
    if(isFunctionTy()){
      FunctionType functionType = Casting.dyncast(this, FunctionType.class);
      return functionType.getFunctionParamType();
    }
    return null;
  }

  public int getFunctionNumParams() {
    if(isFunctionTy()){
      FunctionType functionType = Casting.dyncast(this, FunctionType.class);
      return functionType.getFunctionNumParams();
    }
    return 0;
  }

  public boolean isFunctionVarArg() {
    if(isFunctionTy()){
      FunctionType functionType = Casting.dyncast(this, FunctionType.class);
      return functionType.isFunctionVarArg();
    }
    return false;
  }

  public String getStructName() {
    if(isStructTy()){
      StructType structType = Casting.dyncast(this, StructType.class);
      return structType.getStructName();
    }
    return "";
  }

  public int getStructNumElements() {
    if(isStructTy()){
      StructType structType = Casting.dyncast(this, StructType.class);
      return structType.getNumElements();
    }
    return 0;
  }

  public Type getStructElementType(int index) {
    if(isStructTy()){
      StructType structType = Casting.dyncast(this, StructType.class);
      return structType.getElementType(index);
    }
    return null;
  }

  public Type getSequentialElementType() {
    Preconditions.checkArgument(isSequentialType(getTypeID()), "Not a equential type!");
    return ((SequentialType)this).getElementType();
  }

  public boolean isSequentialType(TypeID typeID) {
    return typeID == TypeID.ArrayTyID || typeID == TypeID.VectorTyID || typeID == TypeID.PointerTyID;
  }

  public long getArrayNumElements() {
    if(isArrayTy()){
      ArrayType arrayType = Casting.dyncast(this, ArrayType.class);
      return arrayType.getNumElements();
    }
    return 0;
  }

  public Type getArrayElementType() {
    if(isArrayTy()){
      ArrayType arrayType = Casting.dyncast(this, ArrayType.class);
      return arrayType.getElementType();
    }
    return null;
  }

  public int getVectorNumElements() {
    if(isVectorTy()){
      VectorType vectorType = Casting.dyncast(this, VectorType.class);
      return vectorType.getNumElements();
    }
    return 0;
  }

  public Type getVectorElementType() {
    if(isVectorTy()){
      VectorType vectorType = Casting.dyncast(this, VectorType.class);
      return vectorType.getElementType();
    }
    return null;
  }

  public Type getPointerElementType() {
    if(isPointerTy()){
      PointerType pointerType = Casting.dyncast(this, PointerType.class);
      return pointerType.getElementType();
    }
    return null;
  }

  public int getPointerAddressSpace() {
    if(isPointerTy()){
      PointerType pointerType = Casting.dyncast(this, PointerType.class);
      return pointerType.getAddressSpace();
    }
    return 0;
  }

  public PointerType getPointerTo(int addrSpace) {
    return new PointerType(context, this, addrSpace);
  }

  public static Type getPrimitiveType(Context context, TypeID typeID) {
    switch (typeID) {
      case VoidTyID      : return getVoidTy(context);
      case HalfTyID      : return getHalfTy(context);
      case FloatTyID     : return getFloatTy(context);
      case DoubleTyID    : return getDoubleTy(context);
      case X86_FP80TyID  : return getX86_FP80Ty(context);
      case FP128TyID     : return getFP128Ty(context);
      case PPC_FP128TyID : return getPPC_FP128Ty(context);
      case LabelTyID     : return getLabelTy(context);
      case MetadataTyID  : return getMetadataTy(context);
      case X86_MMXTyID   : return getX86_MMXTy(context);
      case TokenTyID     : return getTokenTy(context);
      default:
        return null;
    }
  }

  public static Type getVoidTy(Context context) {
    return new Type(context, TypeID.VoidTyID);
  }

  public static Type getLabelTy(Context context) {
    return new Type(context, TypeID.LabelTyID);
  }

  public static Type getHalfTy(Context context) {
    return new Type(context, TypeID.HalfTyID);
  }

  public static Type getFloatTy(Context context) {
    return new Type(context, TypeID.FloatTyID);
  }

  public static Type getDoubleTy(Context context) {
    return new Type(context, TypeID.DoubleTyID);
  }

  public static Type getMetadataTy(Context context) {
    return new Type(context, TypeID.MetadataTyID);
  }

  public static Type getX86_FP80Ty(Context context) {
    return new Type(context, TypeID.X86_FP80TyID);
  }

  public static Type getFP128Ty(Context context) {
    return new Type(context, TypeID.FP128TyID);
  }

  public static Type getPPC_FP128Ty(Context context) {
    return new Type(context, TypeID.PPC_FP128TyID);
  }

  public static Type getX86_MMXTy(Context context) {
    return new Type(context, TypeID.X86_MMXTyID);
  }

  public static Type getTokenTy(Context context) {
    return new Type(context, TypeID.TokenTyID);
  }

  public static IntegerType getIntNTy(Context context, int N) {
    return new IntegerType(context, N);
  }

  public static IntegerType getInt1Ty(Context context) {
    return getIntNTy(context, 1);
  }

  public static IntegerType getInt8Ty(Context context) {
    return getIntNTy(context, 8);
  }

  public static IntegerType getInt16Ty(Context context) {
    return getIntNTy(context, 16);
  }

  public static IntegerType getInt32Ty(Context context) {
    return getIntNTy(context, 32);
  }

  public static IntegerType getInt64Ty(Context context) {
    return getIntNTy(context, 64);
  }

  public static IntegerType getInt128Ty(Context context) {
    return getIntNTy(context, 128);
  }

  public static PointerType getHalfPtrTy(Context context, int as) {
    return new PointerType(context, getHalfTy(context), as);
  }

  public static PointerType getFloatPtrTy(Context context, int as) {
    return new PointerType(context, getFloatTy(context), as);
  }

  public static PointerType getDoublePtrTy(Context context, int as) {
    return new PointerType(context, getDoubleTy(context), as);
  }

  public static PointerType getX86_FP80PtrTy(Context context, int as) {
    return new PointerType(context, getX86_FP80Ty(context), as);
  }

  public static PointerType getFP128PtrTy(Context context, int as) {
    return new PointerType(context, getFP128Ty(context), as);
  }

  public static PointerType getPPC_FP128PtrTy(Context context, int as) {
    return new PointerType(context, getPPC_FP128Ty(context), as);
  }

  public static PointerType getX86_MMXPtrTy(Context context, int as) {
    return new PointerType(context, getX86_MMXTy(context), as);
  }

  public static PointerType getIntNPtrTy(Context context, int N, int as) {
    return new PointerType(context, getIntNTy(context, N), as);
  }

  public static PointerType getInt1PtrTy(Context context, int as) {
    return getIntNPtrTy(context, 1, as);
  }

  public static PointerType getInt8PtrTy(Context context, int as) {
    return getIntNPtrTy(context, 8, as);
  }

  public static PointerType getInt16PtrTy(Context context, int as) {
    return getIntNPtrTy(context, 16, as);
  }

  public static PointerType getInt32PtrTy(Context context, int as) {
    return getIntNPtrTy(context, 32, as);
  }

  public static PointerType getInt64PtrTy(Context context, int as) {
    return getIntNPtrTy(context, 64, as);
  }

  @Override
  public String toString() {
    if (isVoidTy()) {
      return "void";
    } else if(isFloatTy()) {
      return "float";
    } else if(isDoubleTy()) {
      return "double";
    } else if(isX86_FP80T()) {
      return "x86_fp80";
    } else if(isLabelTy()) {
      return "label";
    }
    return "TYPE";
  }

  public long sizeInBytes() {
    return context.getTypeStoreSize(this);
  }
}
