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

import cn.edu.thu.sse.common.util.Pair;
import com.google.common.base.Optional;
import java.util.ArrayList;

/**
 * Created by zhch on 2017/4/9.
 */
public class Attribute {

  private AttributeKind attributeKind;
  // NOTICE that type of an integer parameter of attribute in LLVM IR should be UNSIGNED or UINT_64
  // Unsigned parameter is zero-extended to 64 bits and then stored in a long variable
  // Uint_64 parameter is stored in a long variable with bits unchanged
  private long intParam1;
  private long intParam2 = -1;
  private String stringParam = "";

  private static ArrayList<Attribute> instances = new ArrayList<>();

  private Attribute(AttributeKind attributeKind) {
    this.attributeKind = attributeKind;
  }

  private Attribute(AttributeKind attributeKind, long param) {
    this.attributeKind = attributeKind;
    this.intParam1 = param;
  }

  private Attribute(AttributeKind attributeKind, long param1, long param2) {
    this.attributeKind = attributeKind;
    this.intParam1 = param1;
    this.intParam2 = param2;
  }

  private Attribute(AttributeKind attributeKind, String param) {
    this.attributeKind = attributeKind;
    this.stringParam = param;
  }

  public static Attribute getEnumAttribute(AttributeKind attributeKind) {
    assert attributeKind != AttributeKind.ALIGN && attributeKind != AttributeKind.ALIGNSTACK
        && attributeKind != AttributeKind.DEREFERENCEABLE
        && attributeKind != AttributeKind.DEREFERENCEABLE_OR_NULL
        && attributeKind != AttributeKind.ALLOCSIZE
        && attributeKind != AttributeKind.PATCHABLE_FUNCTION
        && attributeKind != AttributeKind.THUNK : "Not enum attribute kind!";

    for (Attribute attribute : instances) {
      if (attribute.getAttributeKind() == attributeKind) {
        return attribute;
      }
    }

    Attribute attribute = new Attribute(attributeKind);
    instances.add(attribute);
    return attribute;
  }

  // NOTICE the 2nd parameter type uses long to store unsigned/uint_64 (regarding to attribute kind)
  public static Attribute getIntAttribute(AttributeKind attributeKind, long param) {
    assert attributeKind == AttributeKind.ALIGN || attributeKind == AttributeKind.ALIGNSTACK
        || attributeKind == AttributeKind.DEREFERENCEABLE
        || attributeKind == AttributeKind.DEREFERENCEABLE_OR_NULL
        || attributeKind == AttributeKind.ALLOCSIZE : "Not int attribute kind!";
    if (attributeKind == AttributeKind.ALIGN || attributeKind == AttributeKind.ALIGNSTACK
        || attributeKind == AttributeKind.ALLOCSIZE) {
      assert param >= 0 && param < 4294967296L : "Parameter should range in [0, 2^32 - 1]!";
    }

    for (Attribute attribute : instances) {
      if (attribute.getAttributeKind() == attributeKind && attribute.getValueAsInt() == param) {
        return attribute;
      }
    }

    Attribute attribute = new Attribute(attributeKind, param);
    instances.add(attribute);
    return attribute;
  }

  // NOTICE both the 2nd & 3rd parameter type uses long to store unsigned
  public static Attribute getAllocSizeAttribute(AttributeKind attributeKind, long param1,
      long param2) {
    assert attributeKind == AttributeKind.ALLOCSIZE : "Not allocsize attribute kind!";
    assert param1 >= 0 && param1 < 4294967296L && param2 >= 0
        && param2 < 4294967296L : "Parameter should range in [0, 2^32 - 1]!";

    for (Attribute attribute : instances) {
      if (attribute.getAttributeKind() == attributeKind
          && attribute.getAllocSizeArgs().getFirst().longValue() == param1 && attribute
          .getAllocSizeArgs().getSecond().isPresent()
          && attribute.getAllocSizeArgs().getSecond().get().longValue() == param2) {
        return attribute;
      }
    }

    Attribute attribute = new Attribute(attributeKind, param1, param2);
    instances.add(attribute);
    return attribute;
  }

  public static Attribute getStringAttribute(AttributeKind attributeKind, String param) {
    assert attributeKind == AttributeKind.PATCHABLE_FUNCTION
        || attributeKind == AttributeKind.THUNK : "Not string attribute kind!";

    for (Attribute attribute : instances) {
      if (attribute.getAttributeKind() == attributeKind && attribute.getValueAsString().equals(param)) {
        return attribute;
      }
    }

    Attribute attribute = new Attribute(attributeKind, param);
    instances.add(attribute);
    return attribute;
  }

  public AttributeKind getAttributeKind() {
    return attributeKind;
  }

  public boolean isEnumAttribute() {
    return !isIntAttribute() && !isStringAttribute();
  }

  public boolean isIntAttribute() {
    return attributeKind == AttributeKind.ALIGN || attributeKind == AttributeKind.ALIGNSTACK
        || attributeKind == AttributeKind.DEREFERENCEABLE
        || attributeKind == AttributeKind.DEREFERENCEABLE_OR_NULL
        || attributeKind == AttributeKind.ALLOCSIZE;
  }

  public boolean isStringAttribute() {
    return attributeKind == AttributeKind.PATCHABLE_FUNCTION
        || attributeKind == AttributeKind.THUNK;
  }

  // NOTICE the return type uses long to store unsigned/uint_64 (regarding to attribute kind)
  public long getValueAsInt() {
    assert isIntAttribute() : "Expected the attribute to be an integer attribute!";
    return intParam1;
  }

  public String getValueAsString() {
    assert isStringAttribute() : "Invalid attribute type to get the value as a string!";
    return stringParam;
  }

  // NOTICE the return type uses long to store unsigned
  public long getAlignment() {
    assert attributeKind == AttributeKind.ALIGN : "getAlignment() only works for attribute align!";
    return intParam1;
  }

  // NOTICE the return type uses long to store unsigned
  public long getStackAlignment() {
    assert attributeKind
        == AttributeKind.ALIGNSTACK : "getStackAlignment() only works for attribute alignstack!";
    return intParam1;
  }

  // NOTICE the return type uses long to store u_int64
  public long getDereferenceableBytes() {
    assert attributeKind
        == AttributeKind.DEREFERENCEABLE : "getDereferenceableBytes() only works for attribute dereferenceable!";
    return intParam1;
  }

  // NOTICE the return type uses long to store u_int64
  public long getDereferenceableOrNullBytes() {
    assert attributeKind
        == AttributeKind.DEREFERENCEABLE_OR_NULL : "getDereferenceableOrNullBytes() only works for attribute dereferenceable_or_null!";
    return intParam1;
  }

  // NOTICE the return type uses long to store unsigned
  public Pair<Long, Optional<Long>> getAllocSizeArgs() {
    assert attributeKind
        == AttributeKind.ALLOCSIZE : "getAllocSizeArgs() only works for attribute allocsize!";
    if (intParam2 >= 0) {
      return Pair.of(new Long(intParam1), Optional.of(new Long(intParam2)));
    } else {
      return Pair.of(new Long(intParam1), Optional.<Long>absent());
    }
  }

  public static enum AttributeKind {
    ZEROEXT,
    SIGNEXT,
    INREG,
    BYVAL,
    INALLOCA,
    SRET,
    ALIGN,
    NOALIAS,
    NOCAPTURE,
    NEST,
    RETURNED,
    NONNULL,
    DEREFERENCEABLE,
    DEREFERENCEABLE_OR_NULL,
    SWIFTSELF,
    SWIFTERROR,

    ALIGNSTACK,
    ALLOCSIZE,
    ALWAYSINLINE,
    BUILTIN,
    COLD,
    CONVERGENT,
    INACCESSIBLEMEMONLY,
    INACCESSIBLEMEMONLY_OR_ARGMEMONLY,
    INLINEHINT,
    JUMPTABLE,
    MINSIZE,
    NAKED,
    NOBUILTIN,
    NODUPLICATE,
    NOIMPLICITFLOAT,
    NOINLINE,
    NONLAZYBIND,
    NOREDZONE,
    NORETURN,
    NORECURSE,
    NOUNWIND,
    OPTNONE,
    OPTSIZE,
    PATCHABLE_FUNCTION,
    READNONE,
    READONLY,
    WRITEONLY,
    ARGMEMONLY,
    RETURNS_TWICE,
    SAFESTACK,
    SANITIZE_ADDRESS,
    SANITIZE_MEMORY,
    SANITIZE_THREAD,
    SSP,
    SSPREQ,
    SSPSTRONG,
    THUNK,
    UWTABLE;
  }
}
