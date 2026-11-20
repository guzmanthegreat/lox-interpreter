# Lox Interpreter

An interpreter project built in two implementations: a Java tree-walk
interpreter (`jlox`) and a C bytecode virtual machine (`clox`). The project
starts with a hand-written scanner and parser, then grows into a complete
language runtime with functions, closures, classes, inheritance, arrays, local
variables, control flow, and garbage collection.

The language is [Lox](https://craftinginterpreters.com/), based on Robert
Nystrom's *Crafting Interpreters*. The implementation is intentionally built
from first principles so the compiler, VM, object model, and runtime behavior
are easy to inspect.

## Highlights

- Hand-written scanners and recursive-descent / Pratt parsers
- Java tree-walk interpreter for clear language semantics
- C bytecode compiler and stack-based virtual machine
- Lexical scope resolution, closures, upvalues, and recursion
- Classes, instances, methods, initializers, inheritance, and `super`
- Arrays, getters, static methods, `switch`, and `continue`
- Interned strings, hash tables, globals, native functions, and mark-sweep GC
- Runtime diagnostics with source locations and Lox stack traces
- Automated Java and C verification through `check.py`

## Quick start

Run the project checks from the repository root:

```sh
python3 check.py
```

The checker builds the Java interpreters and the cumulative C VM in temporary
directories, then runs language and runtime checks. A successful run reports
the number of checks passed.

Build and run the C VM directly:

```sh
cc -std=c99 -Wall -Wextra -O2 "chapter 22-23/clox"/*.c -lm -o /tmp/clox
/tmp/clox "chapter 24/tests/functions.lox"
```

Expected output:

```text
5
120
true
```

Run the Java tree-walk interpreter:

```sh
mkdir -p /tmp/lox-classes
javac -d /tmp/lox-classes \
  "chapter 12-13/com/craftinginterpreters/lox"/*.java
java -cp /tmp/lox-classes com.craftinginterpreters.lox.Lox
```

Both interpreters accept a `.lox` file path as an argument or start a REPL
when no path is supplied.

## Architecture

The Java implementation makes the language pipeline easy to follow:

```text
source → scanner → tokens → parser → AST → resolver → interpreter
```

The C implementation adds compilation and execution stages:

```text
source → scanner → Pratt compiler → bytecode chunks → stack VM
```

The C VM uses call frames for function execution, stack slots for locals,
upvalues for captured variables, object tables for fields and methods, and a
mark-sweep collector for managed objects.

## Project layout

The numbered directories are implementation stages of the same project:

| Directory | Focus |
| --- | --- |
| `chapter 1-3/` | Language design notes and foundational exercises |
| `chapter 4-5/` | Scanning and expression parsing |
| `chapter 6-7/` | Statements, environments, and control flow |
| `chapter 8-9/` | Java interpreter foundations |
| `chapter 10-11/` | Functions, closures, and lexical resolution |
| `chapter 12-13/` | Classes, inheritance, getters, and arrays |
| `chapter 14-15/` | C VM and bytecode foundations |
| `chapter 16-18/` | Compiler pipeline and runtime values |
| `chapter 19-21/` | Strings, tables, globals, and object management |
| `chapter 22-23/` | Locals, control flow, and closures |
| [`chapter 24/`](chapter%2024/README.md) | Calls, functions, recursion, and native functions |
| [`chapter 25/`](chapter%2025/README.md) | Closures, captured locals, and upvalues |

The latest cumulative C implementation is in `chapter 22-23/clox/`; the
[Chapter 24 stage](chapter%2024/README.md) and
[Chapter 25 stage](chapter%2025/README.md) contain focused documentation and
tests against that shared source.

## Testing

`check.py` compiles each supported Java snapshot, regenerates AST sources, builds
the C snapshots, and exercises arithmetic, variables, closures, classes,
inheritance, arrays, strings, globals, and control flow. Individual `.lox`
programs in the stage `tests/` directories are also useful for exploring the
language interactively.

## Roadmap

- Expand the standalone language test suite with parser, compiler, and VM edge
  cases
- Add a small standard library and file/module loading
- Improve bytecode and VM performance with profiling-guided optimizations
- Add CI so every push builds both implementations and runs the checks

## License

The interpreter is an educational implementation based on the source and
license included in `UPSTREAM_LICENSE`. See that file for the upstream terms.
