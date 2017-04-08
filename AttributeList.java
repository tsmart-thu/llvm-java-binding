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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import javax.annotation.Nullable;

/**
 * Created by zhch on 2017/4/1.
 */
public class AttributeList {

  public final ImmutableList<ImmutableSet<Attribute>> paramAttributes;
  public final ImmutableSet<Attribute> retAttributes;
  public final ImmutableSet<Attribute> fnAttributes;

  public AttributeList(ImmutableList<ImmutableSet<Attribute>> paramAttributes,
      ImmutableSet<Attribute> retAttributes, ImmutableSet<Attribute> fnAttributes) {
    this.paramAttributes = paramAttributes;
    this.retAttributes = retAttributes;
    this.fnAttributes = fnAttributes;
  }

  @Nullable
  public Attribute getParamAttribute(int index, AttrKind attrKind) {
    if (index >= 0 && index < paramAttributes.size()) {
      for (Attribute attr : paramAttributes.get(index)) {
        if (attr.getAttrKind() == attrKind) {
          return attr;
        }
      }
    }
    return null;
  }

  @Nullable
  public Attribute getRetAttribute(AttrKind attrKind) {
    for (Attribute attr : retAttributes) {
      if (attr.getAttrKind() == attrKind) {
        return attr;
      }
    }
    return null;
  }

  @Nullable
  public Attribute getFnAttribute(AttrKind attrKind) {
    for (Attribute attr : fnAttributes) {
      if (attr.getAttrKind() == attrKind) {
        return attr;
      }
    }
    return null;
  }

  public boolean hasParamAttribute(int index, AttrKind attrKind) {
    if (index >= 0 && index < paramAttributes.size()) {
      for (Attribute attr : paramAttributes.get(index)) {
        if (attr.getAttrKind() == attrKind) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean hasRetAttribute(AttrKind attrKind) {
    for (Attribute attr : retAttributes) {
      if (attr.getAttrKind() == attrKind) {
        return true;
      }
    }
    return false;
  }

  public boolean hasFnAttribute(AttrKind attrKind) {
    for (Attribute attr : fnAttributes) {
      if (attr.getAttrKind() == attrKind) {
        return true;
      }
    }
    return false;
  }

  public boolean isEmpty() {
    if (!retAttributes.isEmpty() || !fnAttributes.isEmpty()) {
      return false;
    }
    for (ImmutableSet<Attribute> attrs : paramAttributes) {
      if (!attrs.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  public static enum AttrKind {
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

  public class Attribute {

    private AttrKind attrKind;
    private long intParam1 = -1;
    private long intParam2 = -1;
    private String stringParam = "";

    public Attribute(AttrKind attrKind) {
      switch (attrKind) {
        case ALIGN:
        case DEREFERENCEABLE:
        case DEREFERENCEABLE_OR_NULL:
        case ALIGNSTACK:
        case ALLOCSIZE:
        case PATCHABLE_FUNCTION:
        case THUNK:
          assert false : "Attribute requires extra parameter!";
        default:
          this.attrKind = attrKind;
          break;
      }
    }

    public Attribute(AttrKind attrKind, long param) {
      switch (attrKind) {
        case ALIGN:
        case DEREFERENCEABLE:
        case DEREFERENCEABLE_OR_NULL:
        case ALIGNSTACK:
        case ALLOCSIZE:
          this.attrKind = attrKind;
          this.intParam1 = param;
          break;
        default:
          assert false : "Attribute cannot take an integer parameter!";
      }
    }

    public Attribute(AttrKind attrKind, long param1, long param2) {
      switch (attrKind) {
        case ALLOCSIZE:
          this.attrKind = attrKind;
          this.intParam1 = param1;
          this.intParam2 = param2;
          break;
        default:
          assert false : "Attribute cannot take two integer parameters!";
      }
    }

    public Attribute(AttrKind attrKind, String param) {
      switch (attrKind) {
        case PATCHABLE_FUNCTION:
        case THUNK:
          this.attrKind = attrKind;
          this.stringParam = param;
          break;
        default:
          assert false : "Attribute cannot take a string parameter!";
      }
    }

    public boolean isEnumAttribute() {
      return !isIntAttribute() && !isStringAttribute();
    }

    public boolean isIntAttribute() {
      return attrKind == AttrKind.ALIGN || attrKind == AttrKind.ALIGNSTACK
          || attrKind == AttrKind.DEREFERENCEABLE
          || attrKind == AttrKind.DEREFERENCEABLE_OR_NULL
          || attrKind == AttrKind.ALLOCSIZE;
    }

    public boolean isStringAttribute() {
      return attrKind == AttrKind.PATCHABLE_FUNCTION || attrKind == AttrKind.THUNK;
    }

    public boolean hasAttribute(AttrKind attrKind) {
      return this.attrKind == attrKind;
    }

    public AttrKind getAttrKind() {
      return attrKind;
    }

    public long getValueAsInt() {
      assert isIntAttribute() : "Expected the attribute to be an integer attribute!";
      assert intParam1 >= 0 : "Invalid parameter for an integer attribute!";
      return intParam1;
    }

    public String getValueAsString() {
      assert isStringAttribute() : "Invalid attribute type to get the value as a string!";
      return stringParam;
    }

    public long getAlignment() {
      assert attrKind == AttrKind.ALIGN : "getAlignment() only works for attribute align!";
      assert intParam1 >= 0 : "Invalid parameter for attribute align!";
      return intParam1;
    }

    public long getStackAlignment() {
      assert attrKind
          == AttrKind.ALIGNSTACK : "getStackAlignment() only works for attribute alignstack!";
      assert intParam1 >= 0 : "Invalid parameter for attribute alignstack!";
      return intParam1;
    }

    public long getDereferenceableBytes() {
      assert attrKind
          == AttrKind.DEREFERENCEABLE : "getDereferenceableBytes() only works for attribute dereferenceable!";
      assert intParam1 >= 0 : "Invalid parameter for attribute dereferenceable!";
      return intParam1;
    }

    public long getDereferenceableOrNullBytes() {
      assert attrKind
          == AttrKind.DEREFERENCEABLE_OR_NULL : "getDereferenceableOrNullBytes() only works for attribute dereferenceable_or_null!";
      assert intParam1 >= 0 : "Invalid parameter for attribute dereferenceable_or_null!";
      return intParam1;
    }

    public Pair<Long, Optional<Long>> getAllocSizeArgs() {
      assert attrKind
          == AttrKind.ALLOCSIZE : "getAllocSizeArgs() only works for attribute allocsize!";
      assert intParam1 >= 0 : "Invalid parameter for attribute allocsize!";
      if (intParam2 >= 0) {
        return Pair.of(new Long(intParam1), Optional.of(new Long(intParam2)));
      } else {
        return Pair.of(new Long(intParam1), Optional.<Long>absent());
      }
    }
  }
}
