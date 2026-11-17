# Lox Interpreter — Bytecode VM Foundations

This stage introduces the C bytecode VM and its foundational runtime work.

## Contents

- `clox/` — C source for the bytecode interpreter and virtual machine.
- `answers/chapter14/` — design notes for the Chapter 14 challenges.
- `answers/chapter15/` — design notes for the Chapter 15 challenges.
- `tests/` — Lox programs for testing the VM.

## Challenges covered

Chapter 14:

1. Run-length-style compression of line information.
2. `OP_CONSTANT_LONG` and 24-bit constant operands.
3. How open-source memory allocators work and how a simple allocator could be
   designed.

Chapter 15:

1. Bytecode sequences for expressions.
2. Removing `OP_NEGATE` or `OP_SUBTRACT` and evaluating the trade-off.
3. Dynamically growing the VM stack.
4. In-place stack operations and related optimizations.

## Compile and run

From the `clox` directory, compile the source files with a C compiler. For
example:

```sh
cc -std=c99 -Wall -Wextra -O2 *.c -lm -o clox
./clox ../tests/arithmetic.lox
```

The included source integrates compressed line information, long constants,
dynamic stack growth, and in-place stack operations. The implementation notes
explain the changes and the written answers discuss their trade-offs.
