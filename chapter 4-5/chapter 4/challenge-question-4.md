Add support to Lox’s scanner for C-style /_ ... _/ block comments. Make sure to handle newlines in them. Consider allowing them to nest. Is adding support for nesting more work than you expected? Why?

The scanner is in `chapter 8-9/jlox/com/craftinginterpreters/lox/Scanner.java`.
It counts nested opening and closing block comments and updates the line
number for each newline. Reaching the end of the file with an open comment
reports an error. Nesting only needs a depth counter instead of a yes/no flag.
