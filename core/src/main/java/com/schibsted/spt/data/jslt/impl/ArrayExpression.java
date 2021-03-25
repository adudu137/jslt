
// Copyright 2018 Schibsted Marketplaces Products & Technology As
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.schibsted.spt.data.jslt.impl;

import java.util.List;
import java.util.Arrays;
import com.schibsted.spt.data.json.*;

public class ArrayExpression extends AbstractNode {
  private ExpressionNode[] children;

  public ArrayExpression(ExpressionNode[] children, Location location) {
    super(location);
    this.children = children;
  }

  public JsonValue apply(Scope scope, JsonValue input) {
    JsonValue[] buffer = new JsonValue[children.length];
    for (int ix = 0; ix < children.length; ix++)
      buffer[ix] = children[ix].apply(scope, input);
    return input.makeArray(buffer);
  }

  public void computeMatchContexts(DotExpression parent) {
    FailDotExpression fail = new FailDotExpression(location, "array");
    for (int ix = 0; ix < children.length; ix++)
      children[ix].computeMatchContexts(fail);
  }

  public List<ExpressionNode> getChildren() {
    return Arrays.asList(children);
  }

  public ExpressionNode optimize() {
    boolean allLiterals = true;
    for (int ix = 0; ix < children.length; ix++) {
      children[ix] = children[ix].optimize();
      allLiterals = allLiterals && (children[ix] instanceof LiteralExpression);
    }
    if (!allLiterals)
      return this;

    // we're a static array expression. we can just make the array and
    // turn that into a literal, instead of creating it over and over
    JsonValue array = apply(null, NullJValue.instance);
    return new LiteralExpression(array, location);
  }

  public void dump(int level) {
    System.out.println(NodeUtils.indent(level) + '[');
    for (int ix = 0; ix < children.length; ix++)
      children[ix].dump(level + 1);
    System.out.println(NodeUtils.indent(level) + ']');
  }
}
