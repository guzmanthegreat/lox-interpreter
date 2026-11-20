# Lox Interpreter — Closures

This stage documents and tests lexical closures in the cumulative C bytecode
VM from `../chapter 22-23/clox/`. Chapter 24 adds calls and functions; Chapter
25 completes the function runtime by allowing a function to retain access to
locals from the scope where it was declared.

## Features

- The compiler resolves references to enclosing locals as upvalues.
- `OP_CLOSURE` records whether each captured variable is local to the enclosing
  function or is itself an upvalue.
- Open upvalues point into the VM stack while a function is active.
- Upvalues are closed over a heap-owned `Value` when their defining scope ends,
  so returned functions keep working after their creator returns.
- Multiple closures share the same captured variable, preserving mutation and
  lexical scope semantics.
- The garbage collector marks closures, their functions, and their upvalues.

## Build and run

From the repository root:

```sh
cc -std=c99 -Wall -Wextra -O2 "chapter 22-23/clox"/*.c -lm -o /tmp/clox-chapter25
/tmp/clox-chapter25 "chapter 25/tests/closures.lox"
```

Expected output:

```text
11
12
1
13
outer middle
```

The source tree is shared with the cumulative implementation so later fixes to
the VM remain available to every documented stage.
