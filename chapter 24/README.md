# Lox Interpreter — Calls and Functions

This stage completes the bytecode VM work through **Calls and Functions**. The
implementation is shared with the cumulative C interpreter in
`../chapter 22-23/clox/` so that the earlier chapter features remain available.

## Features

- Function declarations compile to `ObjFunction` constants and `OP_CLOSURE`.
- Calls use `CallFrame` records with one frame per active function.
- Arguments are passed in the callee frame's slots and arity is checked before
  execution.
- `return` unwinds the current frame and leaves its result on the caller's
  stack.
- Native functions use the same call path as Lox functions; `clock()` is
  included.
- Runtime errors include a Lox stack trace.

## Build and run

From the repository root:

```sh
cc -std=c99 -Wall -Wextra -O2 "chapter 22-23/clox"/*.c -lm -o /tmp/clox-chapter24
/tmp/clox-chapter24 "chapter 24/tests/functions.lox"
```

The cumulative source also contains the Chapter 25 closure runtime; focused
closure documentation and tests live in `../chapter 25/`.
