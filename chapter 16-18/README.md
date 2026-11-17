# Lox Interpreter — Compiler and Runtime Values

This stage continues the cumulative C `clox` implementation with the compiler
pipeline and runtime value system.

## Chapters covered

- Chapter 16: Scanning on Demand
- Chapter 17: Compiling Expressions
- Chapter 18: Types of Values

The source in `clox/` includes the cumulative implementation needed to scan
Lox source, compile expressions to bytecode, and evaluate numbers, booleans,
and `nil`.

## Compile and run

```sh
cd clox
cc -std=c99 -Wall -Wextra -O2 *.c -lm -o clox
./clox ../tests/expressions.lox
```
