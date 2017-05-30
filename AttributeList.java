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

import cn.edu.thu.tsmart.core.cfa.llvm.Attribute.AttributeKind;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import javax.annotation.Nullable;

/**
 * Created by zhch on 2017/4/1.
 */
public class AttributeList {

  private final ImmutableList<ImmutableSet<Attribute>> paramAttributes;
  private final ImmutableSet<Attribute> retAttributes;
  private final ImmutableSet<Attribute> fnAttributes;

  private static ArrayList<AttributeList> instances = new ArrayList<>();

  private AttributeList(ImmutableList<ImmutableSet<Attribute>> paramAttributes,
      ImmutableSet<Attribute> retAttributes, ImmutableSet<Attribute> fnAttributes) {
    this.paramAttributes = paramAttributes;
    this.retAttributes = retAttributes;
    this.fnAttributes = fnAttributes;
  }

  public static AttributeList getAttributeList(
      ImmutableList<ImmutableSet<Attribute>> paramAttributes,
      ImmutableSet<Attribute> retAttributes, ImmutableSet<Attribute> fnAttributes) {
    for (AttributeList attributeList : instances) {
      if (attributeList.getParamAttributes().size() != paramAttributes.size()
          || attributeList.getRetAttributes().size() != retAttributes.size()
          || attributeList.getFnAttributes().size() != fnAttributes.size()) {
        continue;
      }
      if (!attributeList.getRetAttributes().containsAll(retAttributes) || !attributeList
          .getFnAttributes().containsAll(fnAttributes)) {
        continue;
      }
      int i = 0;
      for (; i < attributeList.getParamAttributes().size(); ++ i) {
        if (attributeList.getParamAttributes().get(i).size() == paramAttributes.get(i).size()) {
          if (attributeList.getParamAttributes().get(i).containsAll(paramAttributes.get(i))) {
            continue;
          }
        }
        break;
      }
      if (i < attributeList.getParamAttributes().size()) {
        continue;
      }
      return attributeList;
    }

    AttributeList attributeList = new AttributeList(paramAttributes, retAttributes, fnAttributes);
    instances.add(attributeList);
    return attributeList;
  }

  public ImmutableList<ImmutableSet<Attribute>> getParamAttributes() {
    return paramAttributes;
  }

  public ImmutableSet<Attribute> getRetAttributes() {
    return retAttributes;
  }

  public ImmutableSet<Attribute> getFnAttributes() {
    return fnAttributes;
  }

  public boolean hasParamAttribute(int paramIndex, AttributeKind attributeKind) {
    if (paramIndex >= 0 && paramIndex < paramAttributes.size()) {
      for (Attribute attribute : paramAttributes.get(paramIndex)) {
        if (attribute.getAttributeKind() == attributeKind) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean hasRetAttribute(AttributeKind attributeKind) {
    for (Attribute attribute : retAttributes) {
      if (attribute.getAttributeKind() == attributeKind) {
        return true;
      }
    }
    return false;
  }

  public boolean hasFnAttribute(AttributeKind attributeKind) {
    for (Attribute attribute : fnAttributes) {
      if (attribute.getAttributeKind() == attributeKind) {
        return true;
      }
    }
    return false;
  }

  @Nullable
  public Attribute getParamAttribute(int index, AttributeKind attributeKind) {
    if (index >= 0 && index < paramAttributes.size()) {
      for (Attribute attr : paramAttributes.get(index)) {
        if (attr.getAttributeKind() == attributeKind) {
          return attr;
        }
      }
    }
    return null;
  }

  @Nullable
  public Attribute getRetAttribute(AttributeKind attributeKind) {
    for (Attribute attr : retAttributes) {
      if (attr.getAttributeKind() == attributeKind) {
        return attr;
      }
    }
    return null;
  }

  @Nullable
  public Attribute getFnAttribute(AttributeKind attributeKind) {
    for (Attribute attr : fnAttributes) {
      if (attr.getAttributeKind() == attributeKind) {
        return attr;
      }
    }
    return null;
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
}
