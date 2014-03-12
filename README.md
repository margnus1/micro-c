micro-c
=======

A compiler from a C language subset to MIPS.

To Compile
----------

To compile, use apache ant:

    ant

To Use Compiler
---------------

If you've compiled to `build` (which is the default), you can run it like this

    java -ea -cp build Main

You can try compiling the sample files in the `testcases` directory.

To Run Assembly In Simulator
----------------------------

The output files can be run in Mips simulators as-is. If you have made use of
the micro-c standard library, you will need to include the assembly
implementations in the `priv` directory. In MARS, you will need to concatenate
the files, because it does not link assembly files together. It is preferable to
put `uc-mars.s` first, because the entry point will be correct by default.
