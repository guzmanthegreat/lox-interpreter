# Lox Interpreter — Strings, Tables, and Globals

This stage continues the cumulative C `clox` implementation with strings,
hash tables, and global variables.

## Chapters covered

- Chapter 19: Strings
- Chapter 20: Hash Tables
- Chapter 21: Global Variables

The source in `clox/` includes string objects and interning, hash-table
support, and global variable declaration, lookup, and assignment.

## Compile and run

```sh
cd clox
cc -std=c99 -Wall -Wextra -O2 *.c -lm -o clox
./clox ../tests/strings_and_globals.lox
```
