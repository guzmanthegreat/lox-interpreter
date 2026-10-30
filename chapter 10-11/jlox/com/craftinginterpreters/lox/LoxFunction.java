package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
  private final Stmt.Function declaration;
  private final Expr.Function expression;
  private final Environment closure;
  private final boolean isInitializer;

  LoxFunction(Stmt.Function declaration, Environment closure,
              boolean isInitializer) {
    this.isInitializer = isInitializer;
    this.closure = closure;
    this.declaration = declaration;
    this.expression = null;
  }

  LoxFunction(Expr.Function expression, Environment closure) {
    this.declaration = null;
    this.expression = expression;
    this.closure = closure;
    this.isInitializer = false;
  }
  LoxFunction bind(LoxInstance instance) {
    Environment environment = new Environment(closure);
    environment.define("this", instance);
    return new LoxFunction(declaration, environment,
                           isInitializer);
  }
  @Override
  public String toString() {
    return declaration == null ? "<fn>" : "<fn " + declaration.name.lexeme + ">";
  }
  @Override
  public int arity() {
    return declaration == null ? expression.params.size() : declaration.params.size();
  }
  @Override
  public Object call(Interpreter interpreter,
                     List<Object> arguments) {
    Environment environment = new Environment(closure);
    List<Token> params = declaration == null ? expression.params : declaration.params;
    List<Stmt> body = declaration == null ? expression.body : declaration.body;
    for (int i = 0; i < params.size(); i++) {
      environment.define(params.get(i).lexeme,
          arguments.get(i));
    }
    try {
      interpreter.executeBlock(body, environment);
    } catch (Return returnValue) {
      if (isInitializer) return closure.getAt(0, "this");
      return returnValue.value;
    }

    if (isInitializer) return closure.getAt(0, "this");
    return null;
  }
}
