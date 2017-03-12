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
    return this.typeID == TypeID.FloatTyID;
  }

  public boolean isX86_MMXTy() {
    return this.typeID == TypeID.X86_MMXTyID;
  }

  public boolean isFPOrFPVectorTy() {
    throw new NotImplementedException();
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
    throw new NotImplementedException();
  }

  public boolean isIntOrIntVectorTy() {
    throw new NotImplementedException();
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
    throw new NotImplementedException();
  }

  public boolean isVectorTy() {
    return this.typeID == TypeID.VectorTyID;
  }

  public boolean canLosslesslyBitCastTo(Type type) {
    throw new NotImplementedException();
  }

  public boolean isEmptyTy() {
    throw new NotImplementedException();
  }

  public boolean isFirstClassType() {
    throw new NotImplementedException();
  }

  public boolean isSingleValueType() {
    throw new NotImplementedException();
  }

  public boolean isAggregateType() {
    throw new NotImplementedException();
  }

  public boolean isSized() {
    throw new NotImplementedException();
  }

  public int getPrimitiveSizeInBits() {
    throw new NotImplementedException();
  }

  public int getScalarSizeInBits() {
    throw new NotImplementedException();
  }

  public int getFPMantissaWidth() {
    throw new NotImplementedException();
  }

  public Type getScalarType() {
    throw new NotImplementedException();
  }

  public Type getContainedType() {
    throw new NotImplementedException();
  }

  public int getNumContainedTypes() {
    throw new NotImplementedException();
  }

  public int getIntegerBitWidth() {
    throw new NotImplementedException();
  }

  public Type getFunctionParamType() {
    throw new NotImplementedException();
  }

  public int getFunctionNumParams() {
    throw new NotImplementedException();
  }

  public boolean isFunctionVarArg() {
    throw new NotImplementedException();
  }

  public String getStructName() {
    throw new NotImplementedException();
  }

  public int getStructNumElements() {
    throw new NotImplementedException();
  }

  public Type getStructElementType() {
    throw new NotImplementedException();
  }

  public Type getSequentialElementType() {
    throw new NotImplementedException();
  }

  public long getArrayNumElements() {
    throw new NotImplementedException();
  }

  public Type getArrayElementType() {
    throw new NotImplementedException();
  }

  public int getVectorNumElements() {
    throw new NotImplementedException();
  }

  public Type getVectorElementType() {
    throw new NotImplementedException();
  }

  public Type getPointerElementType() {
    throw new NotImplementedException();
  }

  public int getPointerAddressSpace() {
    throw new NotImplementedException();
  }

  public PointerType getPointerTo(int addrSpace) {
    return new PointerType(context, this, addrSpace);
  }

  public static Type getPrimitiveType(Context context, TypeID typeID) {
    throw new NotImplementedException();
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
    throw new NotImplementedException();
  }

  public static PointerType getFloatPtrTy(Context context, int as) {
    throw new NotImplementedException();
  }

  public static PointerType getDoublePtrTy(Context context, int as) {
    throw new NotImplementedException();
  }

  public static PointerType getX86_FP80PtrTy(Context context, int as) {
    throw new NotImplementedException();
  }

  public static PointerType getFP128PtrTy(Context context, int as) {
    throw new NotImplementedException();
  }

  public static PointerType getPPC_FP128PtrTy(Context context, int as) {
    throw new NotImplementedException();
  }

  public static PointerType getX86_MMXPtrTy(Context context, int as) {
    throw new NotImplementedException();
  }

  public static PointerType getIntNPtrTy(Context context, int N, int as) {
    throw new NotImplementedException();
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
}
