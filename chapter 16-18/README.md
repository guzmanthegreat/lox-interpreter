# Crafting Interpreters Chapters 16–18

Status: this folder contains a copy of the full C interpreter. The core chapter
features run, but the chapter challenges are not completed. See `../REVIEW.md`.

This folder continues the cumulative C `clox` implementation from Chapters
14–15.

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
gcc -std=c99 -Wall -Wextra -O2 *.c -lm -o clox
./clox ../tests/expressions.lox
```
