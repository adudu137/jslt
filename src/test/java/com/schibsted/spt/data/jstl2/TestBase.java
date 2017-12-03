
package com.schibsted.spt.data.jstl2;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utilities for test cases.
 */
public class TestBase {
  private static ObjectMapper mapper = new ObjectMapper();

  Map<String, JsonNode> makeVars(String var, String val) {
    try {
      Map<String, JsonNode> map = new HashMap();
      map.put(var, mapper.readTree(val));
      return map;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  void check(String input, String query, String result) {
    check(input, query, result, Collections.EMPTY_MAP);
  }

  void check(String input, String query, String result,
             Map<String, JsonNode> variables) {
    try {
      JsonNode context = mapper.readTree(input);

      Expression expr = Parser.compile(query);
      JsonNode actual = expr.apply(variables, context);

      JsonNode expected = mapper.readTree(result);

      assertEquals(expected, actual, "actual class " + actual.getClass() + ", expected class " + expected.getClass());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // result must be contained in the error message
  void error(String query, String result) {
    error("{}", query, result);
  }

  // result must be contained in the error message
  void error(String input, String query, String result) {
    try {
      JsonNode context = mapper.readTree(input);

      Expression expr = Parser.compile(query);
      JsonNode actual = expr.apply(context);
      fail("JSTL did not detect error");
    } catch (JstlException e) {
      assertTrue(e.getMessage().indexOf(result) != -1);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}