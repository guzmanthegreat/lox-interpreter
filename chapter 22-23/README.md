# Crafting Interpreters Chapters 22–23

This folder contains the cumulative C `clox` implementation through local
variables and control flow.

## Implemented features

- Local variables stored in compiler-assigned stack slots.
- Local-variable resolution and upvalues for closures.
- `if`, `while`, and `for` bytecode control flow.
- `switch` with `case` and `default` clauses.
- `continue` for `while` and `for` loops, including correct cleanup of locals.

Compile and run:

```sh
cd clox
cc -std=c99 -O2 *.c -lm -o clox
./clox ../tests/switch_and_continue.lox
```

The `switch` implementation intentionally has no fallthrough, matching the
challenge requirement. Each matching case exits the switch automatically.
