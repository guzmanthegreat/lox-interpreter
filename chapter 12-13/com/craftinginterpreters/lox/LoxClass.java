package com.craftinginterpreters.lox;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
class LoxClass extends LoxInstance implements LoxCallable {
  final String name;
  final LoxClass superclass;
  private final Map<String, LoxFunction> methods;
  LoxClass(LoxClass metaclass, String name, LoxClass superclass,
           Map<String, LoxFunction> methods) {
    super(metaclass);
    this.superclass = superclass;
    this.name = name;
    this.methods = methods;
  }
  LoxFunction findMethod(String name) {
    if (methods.containsKey(name)) {
      return methods.get(name);
    }
    if (superclass != null) {
      return superclass.findMethod(name);
    }
    return null;
  }

  LoxFunction findMethodWithInner(String name, LoxInstance instance) {
    List<LoxFunction> chain = new ArrayList<>();
    for (LoxClass klass = this; klass != null; klass = klass.superclass) {
      if (klass.methods.containsKey(name)) chain.add(klass.methods.get(name));
    }
    if (chain.isEmpty()) return null;
    return bindChain(chain, chain.size() - 1, instance);
  }

  private LoxFunction bindChain(List<LoxFunction> chain, int index,
                                LoxInstance instance) {
    LoxFunction inner = index == 0 ? null : bindChain(chain, index - 1, instance);
    return chain.get(index).bind(instance, inner);
  }

  @Override
  public String toString() {
    return name;
  }
  @Override
  public Object call(Interpreter interpreter,
                     List<Object> arguments) {
    LoxInstance instance = new LoxInstance(this);
    LoxFunction initializer = findMethodWithInner("init", instance);
    if (initializer != null) {
      initializer.call(interpreter, arguments);
    }
    return instance;
  }

  @Override
  public int arity() {
    LoxFunction initializer = null;
    for (LoxClass klass = this; klass != null; klass = klass.superclass) {
      if (klass.methods.containsKey("init")) initializer = klass.methods.get("init");
    }
    if (initializer == null) return 0;
    return initializer.arity();
  }
}
