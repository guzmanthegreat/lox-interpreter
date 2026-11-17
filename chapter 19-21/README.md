# Lox Interpreter — Strings, Tables, and Globals

Status: this folder contains a copy of the full C interpreter. The core chapter
features run, but the chapter challenges are not completed. See `../REVIEW.md`.

This folder continues the cumulative C `clox` implementation from Chapters
16–18.

## Chapters covered

- Chapter 19: Strings
- Chapter 20: Hash Tables
- Chapter 21: Global Variables

The source in `clox/` includes string objects and interning, hash-table
support, and global variable declaration, lookup, and assignment.

## Compile and run

```sh
cd clox
gcc -std=c99 -Wall -Wextra -O2 *.c -lm -o clox
./clox ../tests/strings_and_globals.lox
```
