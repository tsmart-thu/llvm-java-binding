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
