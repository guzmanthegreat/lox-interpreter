package com.craftinginterpreters.lox;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
class Interpreter implements Expr.Visitor<Object>,
                             Stmt.Visitor<Void> {
  private static class Break extends RuntimeException {
    Break() {
      super(null, null, false, false);
    }
  }
  final Environment globals = new Environment();
  private Environment environment = globals;
  private final Map<Expr, Integer> locals = new HashMap<>();
  Interpreter() {
    globals.define("clock", new LoxCallable() {
      @Override
      public int arity() { return 0; }

      @Override
      public Object call(Interpreter interpreter,
                         List<Object> arguments) {
        return (double)System.currentTimeMillis() / 1000.0;
      }

      @Override
      public String toString() { return "<native fn>"; }
    });
    globals.define("Array", new LoxCallable() {
      @Override
      public int arity() { return 1; }

      @Override
      public Object call(Interpreter interpreter, List<Object> arguments) {
        Object value = arguments.get(0);
        if (!(value instanceof Double)) {
          throw new RuntimeError(null, "Array size must be a number.");
        }
        double size = (double) value;
        if (!Double.isFinite(size) || size < 0 || size > Integer.MAX_VALUE || size != Math.floor(size)) {
          throw new RuntimeError(null, "Array size must be a non-negative integer.");
        }
        return new LoxArray((int) size);
      }

      @Override
      public String toString() { return "<native Array>"; }
    });
  }
  void interpret(List<Stmt> statements) {
    try {
      for (Stmt statement : statements) {
        execute(statement);
      }
    } catch (RuntimeError error) {
      Lox.runtimeError(error);
    }
  }

  void interpretExpression(Expr expression) {
    try {
      System.out.println(stringify(evaluate(expression)));
    } catch (RuntimeError error) {
      Lox.runtimeError(error);
    }
  }
  private Object evaluate(Expr expr) {
    return expr.accept(this);
  }
  private void execute(Stmt stmt) {
    stmt.accept(this);
  }
  void resolve(Expr expr, int depth) {
    locals.put(expr, depth);
  }
  void executeBlock(List<Stmt> statements,
                    Environment environment) {
    Environment previous = this.environment;
    try {
      this.environment = environment;

      for (Stmt statement : statements) {
        execute(statement);
      }
    } finally {
      this.environment = previous;
    }
  }
  @Override
  public Void visitBlockStmt(Stmt.Block stmt) {
    executeBlock(stmt.statements, new Environment(environment));
    return null;
  }
  @Override
  public Void visitBreakStmt(Stmt.Break stmt) {
    throw new Break();
  }
  @Override
  public Void visitClassStmt(Stmt.Class stmt) {
    Object superclass = null;
    if (stmt.superclass != null) {
      superclass = evaluate(stmt.superclass);
      if (!(superclass instanceof LoxClass)) {
        throw new RuntimeError(stmt.superclass.name,
            "Superclass must be a class.");
      }
    }
    environment.define(stmt.name.lexeme, null);

    if (stmt.superclass != null) {
      environment = new Environment(environment);
      environment.define("super", superclass);
    }

    Map<String, LoxFunction> classMethods = new HashMap<>();
    for (Stmt.Function method : stmt.classMethods) {
      LoxFunction function = new LoxFunction(method, environment, false);
      classMethods.put(method.name.lexeme, function);
    }
    LoxClass metaclass = new LoxClass(null,
        stmt.name.lexeme + " metaclass", null, classMethods);

    Map<String, LoxFunction> methods = new HashMap<>();
    for (Stmt.Function method : stmt.methods) {
      LoxFunction function = new LoxFunction(method, environment,
          method.name.lexeme.equals("init"));
      methods.put(method.name.lexeme, function);
    }
    LoxClass klass = new LoxClass(metaclass, stmt.name.lexeme,
        (LoxClass)superclass, methods);

    if (superclass != null) {
      environment = environment.enclosing;
    }
    environment.assign(stmt.name, klass);
    return null;
  }
  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt) {
    evaluate(stmt.expression);
    return null;
  }
  @Override
  public Void visitFunctionStmt(Stmt.Function stmt) {
    LoxFunction function = new LoxFunction(stmt, environment,
                                           false);
    environment.define(stmt.name.lexeme, function);
    return null;
  }
  @Override
  public Void visitIfStmt(Stmt.If stmt) {
    if (isTruthy(evaluate(stmt.condition))) {
      execute(stmt.thenBranch);
    } else if (stmt.elseBranch != null) {
      execute(stmt.elseBranch);
    }
    return null;
  }
  @Override
  public Void visitPrintStmt(Stmt.Print stmt) {
    Object value = evaluate(stmt.expression);
    System.out.println(stringify(value));
    return null;
  }
  @Override
  public Void visitReturnStmt(Stmt.Return stmt) {
    Object value = null;
    if (stmt.value != null) value = evaluate(stmt.value);

    throw new Return(value);
  }
  @Override
  public Void visitVarStmt(Stmt.Var stmt) {
    Object value = Environment.uninitialized();
    if (stmt.initializer != null) {
      value = evaluate(stmt.initializer);
    }

    environment.define(stmt.name.lexeme, value);
    return null;
  }
  @Override
  public Void visitWhileStmt(Stmt.While stmt) {
    try {
      while (isTruthy(evaluate(stmt.condition))) {
        execute(stmt.body);
      }
    } catch (Break breakException) {
      // Exit the nearest enclosing loop.
    }
    return null;
  }
  @Override
  public Object visitAssignExpr(Expr.Assign expr) {
    Object value = evaluate(expr.value);

    Integer distance = locals.get(expr);
    if (distance != null) {
      environment.assignAt(distance, expr.name, value);
    } else {
      globals.assign(expr.name, value);
    }
    return value;
  }
  @Override
  public Object visitBinaryExpr(Expr.Binary expr) {
    Object left = evaluate(expr.left);
    Object right = evaluate(expr.right); // [left]

    switch (expr.operator.type) {
      case COMMA: return right;
      case BANG_EQUAL: return !isEqual(left, right);
      case EQUAL_EQUAL: return isEqual(left, right);
      case GREATER:
        checkNumberOperands(expr.operator, left, right);
        return (double)left > (double)right;
      case GREATER_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        return (double)left >= (double)right;
      case LESS:
        checkNumberOperands(expr.operator, left, right);
        return (double)left < (double)right;
      case LESS_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        return (double)left <= (double)right;
      case MINUS:
        checkNumberOperands(expr.operator, left, right);
        return (double)left - (double)right;
      case PLUS:
        if (left instanceof Double && right instanceof Double) {
          return (double)left + (double)right;
        } // [plus]

        if (left instanceof String && right instanceof String) {
          return (String)left + (String)right;
        }
        throw new RuntimeError(expr.operator,
            "Operands must be two numbers or two strings.");
      case SLASH:
        checkNumberOperands(expr.operator, left, right);
        return (double)left / (double)right;
      case STAR:
        checkNumberOperands(expr.operator, left, right);
        return (double)left * (double)right;
    }

    // Unreachable.
    return null;
  }
  @Override
  public Object visitConditionalExpr(Expr.Conditional expr) {
    if (isTruthy(evaluate(expr.condition))) {
      return evaluate(expr.thenBranch);
    }

    return evaluate(expr.elseBranch);
  }
  @Override
  public Object visitCallExpr(Expr.Call expr) {
    Object callee = evaluate(expr.callee);

    List<Object> arguments = new ArrayList<>();
    for (Expr argument : expr.arguments) { // [in-order]
      arguments.add(evaluate(argument));
    }
    if (!(callee instanceof LoxCallable)) {
      throw new RuntimeError(expr.paren,
          "Can only call functions and classes.");
    }
    LoxCallable function = (LoxCallable)callee;
    if (arguments.size() != function.arity()) {
      throw new RuntimeError(expr.paren, "Expected " +
          function.arity() + " arguments but got " +
          arguments.size() + ".");
    }
    return function.call(this, arguments);
  }
  @Override
  public Object visitGetExpr(Expr.Get expr) {
    Object object = evaluate(expr.object);
    if (object instanceof LoxArray) {
      LoxArray array = (LoxArray) object;
      if (expr.name.lexeme.equals("length")) return (double) array.length();
      if (expr.name.lexeme.equals("get")) {
        return nativeFunction(1, arguments -> {
          int index = arrayIndex(arguments.get(0));
          return array.get(index);
        });
      }
      if (expr.name.lexeme.equals("set")) {
        return nativeFunction(2, arguments -> {
          int index = arrayIndex(arguments.get(0));
          array.set(index, arguments.get(1));
          return arguments.get(1);
        });
      }
      throw new RuntimeError(expr.name, "Undefined array property '" + expr.name.lexeme + "'.");
    }
    if (object instanceof LoxInstance) {
      Object result = ((LoxInstance) object).get(expr.name);
      if (result instanceof LoxFunction &&
          ((LoxFunction) result).isGetter()) {
        result = ((LoxFunction) result).call(this, null);
      }
      return result;
    }

    throw new RuntimeError(expr.name,
        "Only instances have properties.");
  }

  private int arrayIndex(Object value) {
    if (!(value instanceof Double) || !Double.isFinite((Double) value) ||
        (Double) value < 0 || (Double) value > Integer.MAX_VALUE || ((Double) value) % 1 != 0) {
      throw new RuntimeError(null, "Array index must be an integer.");
    }
    return ((Double) value).intValue();
  }

  private interface NativeBody { Object call(List<Object> arguments); }

  private LoxCallable nativeFunction(int arity, NativeBody body) {
    return new LoxCallable() {
      public int arity() { return arity; }
      public Object call(Interpreter interpreter, List<Object> arguments) {
        return body.call(arguments);
      }
      public String toString() { return "<native fn>"; }
    };
  }
  @Override
  public Object visitGroupingExpr(Expr.Grouping expr) {
    return evaluate(expr.expression);
  }
  @Override
  public Object visitLiteralExpr(Expr.Literal expr) {
    return expr.value;
  }
  @Override
  public Object visitLogicalExpr(Expr.Logical expr) {
    Object left = evaluate(expr.left);

    if (expr.operator.type == TokenType.OR) {
      if (isTruthy(left)) return left;
    } else {
      if (!isTruthy(left)) return left;
    }

    return evaluate(expr.right);
  }
  @Override
  public Object visitSetExpr(Expr.Set expr) {
    Object object = evaluate(expr.object);

    if (!(object instanceof LoxInstance)) { // [order]
      throw new RuntimeError(expr.name,
                             "Only instances have fields.");
    }

    Object value = evaluate(expr.value);
    ((LoxInstance)object).set(expr.name, value);
    return value;
  }
  @Override
  public Object visitSuperExpr(Expr.Super expr) {
    int distance = locals.get(expr);
    LoxClass superclass = (LoxClass)environment.getAt(
        distance, "super");

    LoxInstance object = (LoxInstance)environment.getAt(
        distance - 1, "this");

    LoxFunction method = superclass.findMethod(expr.method.lexeme);

    if (method == null) {
      throw new RuntimeError(expr.method,
          "Undefined property '" + expr.method.lexeme + "'.");
    }
    return method.bind(object);
  }
  @Override
  public Object visitThisExpr(Expr.This expr) {
    return lookUpVariable(expr.keyword, expr);
  }
  @Override
  public Object visitUnaryExpr(Expr.Unary expr) {
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
      case BANG:
        return !isTruthy(right);
      case MINUS:
        checkNumberOperand(expr.operator, right);
        return -(double)right;
    }

    // Unreachable.
    return null;
  }
  @Override
  public Object visitVariableExpr(Expr.Variable expr) {
    return lookUpVariable(expr.name, expr);
  }
  private Object lookUpVariable(Token name, Expr expr) {
    Integer distance = locals.get(expr);
    if (distance != null) {
      Object value = environment.getAt(distance, name.lexeme);
      if (Environment.isUninitialized(value)) {
        throw new RuntimeError(name,
            "Variable '" + name.lexeme + "' is not initialized.");
      }
      return value;
    } else {
      return globals.get(name);
    }
  }
  private void checkNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double) return;
    throw new RuntimeError(operator, "Operand must be a number.");
  }
  private void checkNumberOperands(Token operator,
                                   Object left, Object right) {
    if (left instanceof Double && right instanceof Double) return;
    // [operand]
    throw new RuntimeError(operator, "Operands must be numbers.");
  }
  private boolean isTruthy(Object object) {
    if (object == null) return false;
    if (object instanceof Boolean) return (boolean)object;
    return true;
  }
  private boolean isEqual(Object a, Object b) {
    if (a == null && b == null) return true;
    if (a == null) return false;

    return a.equals(b);
  }
  private String stringify(Object object) {
    if (object == null) return "nil";

    if (object instanceof Double) {
      String text = object.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }

    return object.toString();
  }
}
