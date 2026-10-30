package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Environment {
  private static final Object UNINITIALIZED = new Object();
  final Environment enclosing;
  private final Map<String, Integer> names = new HashMap<>();
  private final List<Object> values = new ArrayList<>();

  Environment() { this(null); }
  Environment(Environment enclosing) { this.enclosing = enclosing; }
  static Object uninitialized() { return UNINITIALIZED; }
  static boolean isUninitialized(Object value) { return value == UNINITIALIZED; }

  void define(String name, Object value) {
    Integer slot = names.get(name);
    if (slot == null) {
      names.put(name, values.size());
      values.add(value);
    } else {
      values.set(slot, value);
    }
  }

  Object get(Token name) {
    Integer slot = names.get(name.lexeme);
    if (slot != null) {
      Object value = values.get(slot);
      if (isUninitialized(value)) {
        throw new RuntimeError(name, "Variable '" + name.lexeme + "' is not initialized.");
      }
      return value;
    }
    if (enclosing != null) return enclosing.get(name);
    throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
  }

  void assign(Token name, Object value) {
    Integer slot = names.get(name.lexeme);
    if (slot != null) {
      values.set(slot, value);
    } else if (enclosing != null) {
      enclosing.assign(name, value);
    } else {
      throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }
  }

  Environment ancestor(int distance) {
    Environment result = this;
    for (int i = 0; i < distance; i++) result = result.enclosing;
    return result;
  }

  Object getAt(int distance, int slot) {
    return ancestor(distance).values.get(slot);
  }

  Object getAt(int distance, String name) {
    Environment target = ancestor(distance);
    return target.values.get(target.names.get(name));
  }

  void assignAt(int distance, int slot, Object value) {
    ancestor(distance).values.set(slot, value);
  }

  void assignAt(int distance, Token name, Object value) {
    Environment target = ancestor(distance);
    target.values.set(target.names.get(name.lexeme), value);
  }
}
