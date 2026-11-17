# Crafting Interpreters Coursework

This repository contains my coursework for **CS 4080.02: Concepts of Programming Languages**.

This follows [*Crafting Interpreters*](https://craftinginterpreters.com/) by Robert Nystrom. The folders group the assignments by their submission dates. See `REVIEW.md` for what works and what still needs work.

## Repository Structure

```text
chapter 1-3/                         Chapters 1 through 3 coursework
chapter 4-5/                         Chapters 4 and 5 coursework
chapter 6-7/                         Chapters 6 and 7 coursework
chapter 8-9/                         Java interpreter and Chapter 8–9 answers
chapter 10-11/                       Java jlox and challenge work
chapter 12-13/                       Java classes/inheritance work
chapter 14-15/                       C clox and VM challenge work
chapter 16-18/                       C clox compiler/value work
chapter 19-21/                       C clox strings/tables/globals work
chapter 22-23/                       C clox locals and control-flow work
chapter 24/                          Chapter 24 calls/functions coverage
```

## Languages

- Java (jlox)
- C (clox)

## Checking the code

Run `python3 check.py` from this folder. It needs Python 3, Java 17 with the
compiler module, and a C compiler. It builds into temporary folders and checks
the output and errors from small Lox programs.

The Java folders contain separate snapshots. Changes in one are not
automatically included in the others. The three C folders currently contain
the same full interpreter, including features from later chapters. Chapter 24
documents and tests the calls/functions portion of the cumulative
implementation.

The interpreter starts from the book's source. `UPSTREAM_LICENSE` keeps its
original license. Book publishing markers and old commented-out versions were
removed to make the source easier to read.

## Course Information

- **Course:** CS 4080.02 – Concepts of Programming Languages
- **Student:** Josh Guzman

This repository is intended for educational and coursework purposes. Generated
class files, executables, IDE metadata, and copied book/site files are not part
of the coursework submission.
