# Crafting Interpreters Chapters 22–24

This folder contains the cumulative C `clox` implementation through local
variables, control flow, calls, and functions. The Chapter 24 documentation and
focused tests live in `../chapter 24/` and use this same source tree.

## Implemented features

- Local variables stored in compiler-assigned stack slots.
- Local-variable resolution and upvalues for closures.
- `if`, `while`, and `for` bytecode control flow.
- `switch` with `case` and `default` clauses.
- `continue` for `while` and `for` loops, including correct cleanup of locals.
- Function declarations, calls, parameters, returns, recursion, call frames,
  arity checks, and the native `clock()` function.

Compile and run:

```sh
cd clox
cc -std=c99 -O2 *.c -lm -o clox
./clox ../tests/switch_and_continue.lox
```

The `switch` implementation intentionally has no fallthrough, matching the
challenge requirement. Each matching case exits the switch automatically.
