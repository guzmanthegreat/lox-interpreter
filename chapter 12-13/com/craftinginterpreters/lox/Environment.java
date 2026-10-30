package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
  private static final Object UNINITIALIZED = new Object();

  static Object uninitialized() {
    return UNINITIALIZED;
  }

  static boolean isUninitialized(Object value) {
    return value == UNINITIALIZED;
  }
  final Environment enclosing;
  private final Map<String, Object> values = new HashMap<>();
  Environment() {
    enclosing = null;
  }

  Environment(Environment enclosing) {
    this.enclosing = enclosing;
  }

  Object get(Token name) {
    if (values.containsKey(name.lexeme)) {
      Object value = values.get(name.lexeme);
      if (isUninitialized(value)) {
        throw new RuntimeError(name,
            "Variable '" + name.lexeme + "' is not initialized.");
      }
      return value;
    }

    if (enclosing != null) return enclosing.get(name);

    throw new RuntimeError(name,
        "Undefined variable '" + name.lexeme + "'.");
  }
  void assign(Token name, Object value) {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value);
      return;
    }
    if (enclosing != null) {
      enclosing.assign(name, value);
      return;
    }
    throw new RuntimeError(name,
        "Undefined variable '" + name.lexeme + "'.");
  }
  void define(String name, Object value) {
    values.put(name, value);
  }
  Environment ancestor(int distance) {
    Environment environment = this;
    for (int i = 0; i < distance; i++) {
      environment = environment.enclosing; // [coupled]
    }

    return environment;
  }
  Object getAt(int distance, String name) {
    return ancestor(distance).values.get(name);
  }
  void assignAt(int distance, Token name, Object value) {
    ancestor(distance).values.put(name.lexeme, value);
  }
  @Override
  public String toString() {
    String result = values.toString();
    if (enclosing != null) {
      result += " -> " + enclosing.toString();
    }

    return result;
  }
}
