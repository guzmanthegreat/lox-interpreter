# Crafting Interpreters Chapters 10–11 Challenges

This submission contains:

- `jlox/`: the Java tree-walk interpreter source used for Chapters 10–11.
- `chapter_10-11_challenges.md`: written answers and implementation notes
  for all seven challenges.
- `tests/`: Lox programs that exercise functions, closures, recursion, and
  resolver behavior.
- `chapter11_indexed_locals_reference/`: the original reference implementation
  for comparison; the main `jlox/` source now includes indexed locals directly.

The Java source is the working jlox implementation from the project. It
includes anonymous functions, unused-local diagnostics, and indexed local
resolution. Compile
it from the `jlox` directory with:

```sh
javac com/craftinginterpreters/lox/*.java
java com.craftinginterpreters.lox.Lox ../tests/chapter10_functions.lox
```
