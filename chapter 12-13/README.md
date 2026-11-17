# Lox Interpreter — Classes and Inheritance

This stage adds object-oriented features to the Java interpreter, including
classes, inheritance, methods, getters, and static methods.

## Included files

- `answers/Chapter12_Classes_Challenges.md`
- `answers/Chapter13_Inheritance_Challenges.md`
- `tests/Chapter12/static_methods.lox`
- `tests/Chapter12/getters.lox`
- `tests/Chapter13/beta_inner.lox`
- `tests/Chapter13/arrays_design.lox`
- `com/`, the Java jlox source with the Chapter 12–13 challenge features

The answer files explain the design and implementation changes required by each challenge. The test files use the exact Lox examples from the chapter challenges where the book provides them.

## Implemented features

The Java source includes class methods using metaclasses, getter methods, the
native `Array(size)` feature, and BETA-style `inner()` method dispatch. The
tests in `tests/Chapter12/` and `tests/Chapter13/` exercise these features.

The source snapshot contains additional features from earlier implementation stages. The answer files focus on the classes and inheritance work added here.

## Sources

- https://craftinginterpreters.com/classes.html
- https://craftinginterpreters.com/inheritance.html
