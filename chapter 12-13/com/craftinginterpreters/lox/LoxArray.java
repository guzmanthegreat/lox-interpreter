package com.craftinginterpreters.lox;

import java.util.Arrays;

class LoxArray {
  private final Object[] values;

  LoxArray(int size) {
    values = new Object[size];
  }

  int length() { return values.length; }

  Object get(int index) {
    check(index);
    return values[index];
  }

  void set(int index, Object value) {
    check(index);
    values[index] = value;
  }

  private void check(int index) {
    if (index < 0 || index >= values.length) {
      throw new RuntimeError(null, "Array index out of bounds.");
    }
  }

  @Override
  public String toString() {
    return Arrays.toString(values);
  }
}
