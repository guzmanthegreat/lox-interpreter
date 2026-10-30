package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
  private final Stmt.Function declaration;
  private final Environment closure;
  private final boolean isInitializer;

  LoxFunction(Stmt.Function declaration, Environment closure,
              boolean isInitializer) {
    this.isInitializer = isInitializer;
    this.closure = closure;
    this.declaration = declaration;
  }
  LoxFunction bind(LoxInstance instance) {
    return bind(instance, null);
  }

  LoxFunction bind(LoxInstance instance, LoxFunction inner) {
    Environment environment = new Environment(closure);
    environment.define("this", instance);
    // At the end of the method chain, inner() does nothing.
    environment.define("inner", inner != null ? inner : new LoxCallable() {
      public int arity() { return LoxFunction.this.arity(); }
      public Object call(Interpreter interpreter, List<Object> arguments) {
        return null;
      }
    });
    return new LoxFunction(declaration, environment, isInitializer);
  }
  @Override
  public String toString() {
    return "<fn " + declaration.name.lexeme + ">";
  }
  @Override
  public int arity() {
    if (declaration.params == null) return 0;
    return declaration.params.size();
  }
  @Override
  public Object call(Interpreter interpreter,
                     List<Object> arguments) {
    Environment environment = new Environment(closure);
    if (declaration.params != null) {
      for (int i = 0; i < declaration.params.size(); i++) {
        environment.define(declaration.params.get(i).lexeme,
            arguments.get(i));
      }
    }
    try {
      interpreter.executeBlock(declaration.body, environment);
    } catch (Return returnValue) {
      if (isInitializer) return closure.getAt(0, "this");
      return returnValue.value;
    }

    if (isInitializer) return closure.getAt(0, "this");
    return null;
  }

  boolean isGetter() {
    return declaration.params == null;
  }
}
