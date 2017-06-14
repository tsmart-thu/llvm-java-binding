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
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import java.util.ArrayList;

/**
 * Created by zhch on 2017/4/9.
 */
public class Attribute {

  private AttributeKind attributeKind;
  private UnsignedLong intParam1 = null;
  private UnsignedLong intParam2 = null;
  private String stringParam = null;

  private static ArrayList<Attribute> instances = new ArrayList<>();

  private Attribute(AttributeKind attributeKind) {
    this.attributeKind = attributeKind;
  }

  private Attribute(AttributeKind attributeKind, UnsignedLong param) {
    this.attributeKind = attributeKind;
    this.intParam1 = param;
  }

  private Attribute(AttributeKind attributeKind, UnsignedLong param1, UnsignedLong param2) {
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

  public static Attribute getIntAttribute(AttributeKind attributeKind, UnsignedLong param) {
    assert attributeKind == AttributeKind.ALIGN || attributeKind == AttributeKind.ALIGNSTACK
        || attributeKind == AttributeKind.DEREFERENCEABLE
        || attributeKind == AttributeKind.DEREFERENCEABLE_OR_NULL
        || attributeKind == AttributeKind.ALLOCSIZE : "Not int attribute kind!";
    if (attributeKind == AttributeKind.ALIGN || attributeKind == AttributeKind.ALIGNSTACK
        || attributeKind == AttributeKind.ALLOCSIZE) {
      assert param.compareTo(UnsignedLong.valueOf(4294967296L))
          < 0 : "Parameter should range in [0, 2^32 - 1]!";
    }

    for (Attribute attribute : instances) {
      if (attribute.getAttributeKind() == attributeKind
          && param.compareTo(attribute.getValueAsInt()) == 0) {
        if (attributeKind != AttributeKind.ALLOCSIZE || !attribute.getAllocSizeArgs().getSecond()
            .isPresent()) {
          return attribute;
        }
      }
    }

    Attribute attribute = new Attribute(attributeKind, param);
    instances.add(attribute);
    return attribute;
  }

  public static Attribute getAllocSizeAttribute(AttributeKind attributeKind,
      Pair<UnsignedInteger, Optional<UnsignedInteger>> params) {
    for (Attribute attribute : instances) {
      if (attribute.getAttributeKind() == attributeKind
          && attribute.getAllocSizeArgs().getFirst().compareTo(params.getFirst()) == 0) {
        if (!attribute.getAllocSizeArgs().getSecond().isPresent() && !params.getSecond()
            .isPresent()) {
          return attribute;
        } else if (attribute.getAllocSizeArgs().getSecond().isPresent() && params.getSecond()
            .isPresent()
            && attribute.getAllocSizeArgs().getSecond().get().compareTo(params.getSecond().get())
            == 0) {
          return attribute;
        }
      }
    }

    if (params.getSecond().isPresent()) {
      Attribute attribute = new Attribute(attributeKind,
          UnsignedLong.valueOf(params.getFirst().toString()),
          UnsignedLong.valueOf(params.getSecond().get().toString()));
      instances.add(attribute);
      return attribute;
    } else {
      Attribute attribute = new Attribute(attributeKind,
          UnsignedLong.valueOf(params.getFirst().toString()));
      instances.add(attribute);
      return attribute;
    }
  }

  public static Attribute getStringAttribute(AttributeKind attributeKind, String param) {
    assert attributeKind == AttributeKind.PATCHABLE_FUNCTION
        || attributeKind == AttributeKind.THUNK : "Not string attribute kind!";

    for (Attribute attribute : instances) {
      if (attribute.getAttributeKind() == attributeKind && attribute.getValueAsString()
          .equals(param)) {
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

  public UnsignedLong getValueAsInt() {
    assert isIntAttribute() : "Expected the attribute to be an integer attribute!";
    return intParam1;
  }

  public String getValueAsString() {
    assert isStringAttribute() : "Invalid attribute type to get the value as a string!";
    return stringParam;
  }

  public UnsignedInteger getAlignment() {
    assert attributeKind == AttributeKind.ALIGN : "getAlignment() only works for attribute align!";
    return UnsignedInteger.valueOf(intParam1.toString());
  }

  public UnsignedInteger getStackAlignment() {
    assert attributeKind
        == AttributeKind.ALIGNSTACK : "getStackAlignment() only works for attribute alignstack!";
    return UnsignedInteger.valueOf(intParam1.toString());
  }

  public UnsignedLong getDereferenceableBytes() {
    assert attributeKind
        == AttributeKind.DEREFERENCEABLE : "getDereferenceableBytes() only works for attribute dereferenceable!";
    return intParam1;
  }

  public UnsignedLong getDereferenceableOrNullBytes() {
    assert attributeKind
        == AttributeKind.DEREFERENCEABLE_OR_NULL : "getDereferenceableOrNullBytes() only works for attribute dereferenceable_or_null!";
    return intParam1;
  }

  public Pair<UnsignedInteger, Optional<UnsignedInteger>> getAllocSizeArgs() {
    assert attributeKind
        == AttributeKind.ALLOCSIZE : "getAllocSizeArgs() only works for attribute allocsize!";
    if (intParam2 != null) {
      return Pair.of(UnsignedInteger.valueOf(intParam1.toString()),
          Optional.of(UnsignedInteger.valueOf(intParam2.toString())));
    } else {
      return Pair
          .of(UnsignedInteger.valueOf(intParam1.toString()), Optional.<UnsignedInteger>absent());
    }
  }

  public static enum AttributeKind {
    // parameter attribute
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

    // function attribute
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
