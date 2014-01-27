micro-c
=======

A compiler from a C language subset to MIPS.

CONTENTS
--------

    lexer-test/lexer-test.jj
Starting point for assignment 1.
Extend this javacc file with the tokens of uC.

    parser/parser.jj
    parser/Node.java
To be used in assignment 2.

Add the token definitions from assignment 1 to parser.jj

Extend the incomplete grammar of parser.jj to resolve ambguities and fix the
precedences of binary operators.

Add code to produce an abstract syntax tree. You may use the representation in
Node.java.

    parser/Position.java

Representation of source code positions.

    parser/Env.java

One way to represent environments in Java. May be used in assignments 3 and 4.

    rtl/Binary.java    rtl/Icon.java      rtl/Proc.java      rtl/RtlInsn.java
    rtl/Call.java      rtl/Jump.java      rtl/Rtl.java       rtl/RtlType.java
    rtl/CJump.java     rtl/LabDef.java    rtl/RtlBinop.java  rtl/Store.java
    rtl/Data.java      rtl/LabRef.java    rtl/RtlDec.java    rtl/TempExp.java
    rtl/Eval.java      rtl/Load.java      rtl/RtlExp.java

Implementation of an RTL (intermediate code) data type. These files implement
rtl instructions and other constructs.
