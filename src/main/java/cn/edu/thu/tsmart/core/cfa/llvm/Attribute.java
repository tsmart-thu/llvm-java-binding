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

import cn.edu.thu.sse.common.util.Pair;
import com.google.common.base.Optional;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import java.util.ArrayList;

import static org.bytedeco.javacpp.LLVM.*;

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
    ZEROEXT(LLVMZExtAttribute),
    SIGNEXT(LLVMSExtAttribute),
    INREG(LLVMInRegAttribute),
    BYVAL(LLVMByValAttribute),
    INALLOCA(0),
    SRET(LLVMStructRetAttribute),
    ALIGN(LLVMAlignment),
    NOALIAS(LLVMNoAliasAttribute),
    NOCAPTURE(LLVMNoCaptureAttribute),
    NEST(LLVMNestAttribute),
    RETURNED(0),
    NONNULL(0),
    DEREFERENCEABLE(0),
    DEREFERENCEABLE_OR_NULL(0),
    SWIFTSELF(0),
    SWIFTERROR(0),

    // function attribute
    ALIGNSTACK(LLVMStackAlignment),
    ALLOCSIZE(0),
    ALWAYSINLINE(LLVMAlwaysInlineAttribute),
    BUILTIN(0),
    COLD(0),
    CONVERGENT(0),
    INACCESSIBLEMEMONLY(0),
    INACCESSIBLEMEMONLY_OR_ARGMEMONLY(0),
    INLINEHINT(LLVMInlineHintAttribute),
    JUMPTABLE(0),
    MINSIZE(0),
    NAKED(LLVMNakedAttribute),
    NOBUILTIN(0),
    NODUPLICATE(0),
    NOIMPLICITFLOAT(LLVMNoImplicitFloatAttribute),
    NOINLINE(LLVMNoInlineAttribute),
    NONLAZYBIND(LLVMNonLazyBind),
    NOREDZONE(LLVMNoRedZoneAttribute),
    NORETURN(LLVMNoReturnAttribute),
    NORECURSE(0),
    NOUNWIND(LLVMNoUnwindAttribute),
    OPTNONE(0),
    OPTSIZE(LLVMOptimizeForSizeAttribute),
    PATCHABLE_FUNCTION(0),
    READNONE(LLVMReadNoneAttribute),
    READONLY(LLVMReadOnlyAttribute),
    WRITEONLY(0),
    ARGMEMONLY(0),
    RETURNS_TWICE(LLVMReturnsTwice),
    SAFESTACK(0),
    SANITIZE_ADDRESS(0),
    SANITIZE_MEMORY(0),
    SANITIZE_THREAD(0),
    SSP(LLVMStackProtectAttribute),
    SSPREQ(LLVMStackProtectReqAttribute),
    SSPSTRONG(0),
    THUNK(0),
    UWTABLE(LLVMUWTable);

    private final int value;

    AttributeKind(int llvmKindValue) {
      this.value = llvmKindValue;
    }

    public int getValue() {
      return value;
    }
  }
}
