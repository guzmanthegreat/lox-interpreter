"""Build the interpreters and check expected output. Run with python3 check.py."""
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parent
count = 0

def run_case(command, source, output="", code=0, error=""):
    global count
    with tempfile.NamedTemporaryFile(mode="w", suffix=".lox") as test:
        test.write(source)
        test.flush()
        result = subprocess.run(command + [test.name], capture_output=True, text=True)
    assert result.returncode == code, (source, result.returncode, result.stderr)
    assert result.stdout == output, (source, result.stdout, output)
    assert error in result.stderr, (source, result.stderr, error)
    count += 1

with tempfile.TemporaryDirectory() as temp:
    for chapter, source_dir in [("8-9", "jlox/com"), ("10-11", "jlox/com"), ("12-13", "com")]:
        folder = ROOT / ("chapter " + chapter)
        build = Path(temp) / chapter
        build.mkdir()
        sources = sorted((folder / source_dir / "craftinginterpreters/lox").glob("*.java"))
        subprocess.run(["java", "--module", "jdk.compiler/com.sun.tools.javac.Main",
                        "-d", str(build), *map(str, sources)], check=True)
        cmd = ["java", "-cp", str(build), "com.craftinginterpreters.lox.Lox"]
        run_case(cmd, "print 1 + 2 * 3;", "7\n")
        run_case(cmd, "var x; print x;", code=70, error="not initialized")
        run_case(cmd, "/* first\n /* nested */ last */ print 8;", "8\n")
        run_case(cmd, "/* open", code=65, error="Unterminated block comment")
        run_case(cmd, "var i=0; while(true) { i=i+1; if(i==3) break; } print i;", "3\n")
        if chapter == "10-11":
            run_case(cmd, "var f = fun(x) { return x + 1; }; print f(4);", "5\n")
            run_case(cmd, "{ var x = 1; x = 2; }", code=65, error="not used")
            run_case(cmd, "while(false) { var f = fun() { break; }; print f; }", code=65, error="loop")
            run_case(cmd, "{ class A {} print A(); }", "A instance\n")
            run_case(cmd, "fun make() { var x = 0; fun f() { x = x + 1; return x; } return f; } var f = make(); print f(); print f();", "1\n2\n")
        elif chapter == "12-13":
            run_case(cmd, 'class A { m() { print "A"; inner(); } } A().m();', "A\n")
            run_case(cmd, 'class A { m() { print "A"; inner(); } } class B < A { m() { print "B"; inner(); } } class C < B { m() { print "C"; } } C().m();', "A\nB\nC\n")
            run_case(cmd, 'class A { init(x) { this.x = x; inner(x); } } class B < A { init(x) { this.x = x + 1; } } print B(4).x;', "5\n")
            run_case(cmd, '{ var x = "scope"; class A { class m() { print x; print this; } } A.m(); }', "scope\nA\n")
            run_case(cmd, 'class A { value { return 7; } } print A().value;', "7\n")
            run_case(cmd, 'var a = Array(2); a.set(1, "ok"); print a.get(0); print a.get(1);', "nil\nok\n")
            run_case(cmd, 'Array("bad");', code=70, error="size must be a number")
            run_case(cmd, 'Array(2).get(3);', code=70, error="bounds")
            run_case(cmd, 'Array(2).get(0.5);', code=70, error="integer")
        print("Java", chapter, "passed")
        generator = folder / source_dir / "craftinginterpreters/tool/GenerateAst.java"
        subprocess.run(["java", "--module", "jdk.compiler/com.sun.tools.javac.Main", "-d", str(build), str(generator)], check=True)
        generated = Path(temp) / ("ast-" + chapter)
        generated.mkdir()
        subprocess.run(["java", "-cp", str(build), "com.craftinginterpreters.tool.GenerateAst", str(generated)], check=True)
        sources = [s for s in sources if s.name not in {"Expr.java", "Stmt.java"}]
        subprocess.run(["java", "--module", "jdk.compiler/com.sun.tools.javac.Main", "-d", str(build), *map(str, sources), *map(str, generated.glob("*.java"))], check=True)
        count += 1

    for chapter in ["14-15", "16-18", "19-21"]:
        folder = ROOT / ("chapter " + chapter)
        binary = Path(temp) / ("clox-" + chapter)
        subprocess.run(["cc", "-std=c99", "-O2", *map(str, sorted((folder / "clox").glob("*.c"))), "-lm", "-o", str(binary)], check=True)
        cmd = [str(binary)]
        run_case(cmd, "print (1 + 2) * 3; print !nil;", "9\ntrue\n")
        run_case(cmd, 'var x = "a"; x = x + "b"; print x;', "ab\n")
        run_case(cmd, "print missing;", code=70, error="Undefined variable")
        run_case(cmd, "\n".join("print " + str(i) + ";" for i in range(300)),
                 "".join(str(i) + "\n" for i in range(300)))
        run_case(cmd, 'fun outer(n) { var x = n; fun get() { return x; } if (n > 0) outer(n-1); return get; } print outer(30)();', "30\n")
        print("C", chapter, "passed")
print(count, "checks passed")
