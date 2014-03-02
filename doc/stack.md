MIPS Calling Convention (O32)
=============================

Register use
-----------

Registers 16-23 (s0-s7), 28-31 (gp, sp, fp, ra) are callee save.
Registers 1, 25, 25 (at, k0, k1) are unused.
The rest are caller save.

Stack layout
------------

When control is transfered from caller to callee. Argument fields are at least 4
bytes large each (note that this might mean the endianess will matter with byte
arguments).

           +----------------------+  ^ Higher
           |                      |  | addresses
           |  Caller stackframe   |
           |                      |
           +----------------------+
           | Argument n           |
           +----------------------+
           | ...                  |
           +----------------------+
           | Argument 4           |
           +----------------------+
           | Space for Argument 3 |
           | (value in $a3)       |
           +----------------------+
           | ...                  |
           +----------------------+
           | Space for Argument 0 |
           | (value in $a0)       |
    $sp -> +----------------------+

When control is transfered from callee back to caller, `$v0` contains the return
value.
