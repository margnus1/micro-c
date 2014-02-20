RTL
===

RTL operates on a register machine with an infinite amount of
registers. The registers are identified by integer numbers starting
with zero. Register zero is reserved for the return value of the
current procedure. Registers 1 through *n* are the parameters to the
current *n*-ary fuction.

All registers are associated with a type, described by the *RtlType*
enumeration, having one of the following values.

    Int, Byte

The size of an `Int` is 4 bytes.

In addition to the registers, each procedure has a certain, specified
per procedure, number of bytes of stack space available for use. The
code aquires the address to this stack space using the `ArrayAddress`
operation.

Instructions
------------
These are the available instructions in the RTL.

### Binary

    Binary(Dest, BinOp, Lhs, Rhs)

Computes a binary operation that acts on two registers *Lhs* and
*Rhs*, and puts the result in a third register *Dest*.

The *BinOp* enumeration can have the following values.

    Add, Sub, Mul, Div, Gt, Lt, GtEq, LtEq, Eq, Ne

The operation is sometimes pretty-printed as

    Dest <- BinOp Lhs, Rhs

### Unary
Another operation is `Unary`:

    Unary(Dest, UnOp, Arg)

Computes a unary operation that acts on a register *Arg* and puts the
result in a register *Dest*.

The *UnOp* enumeration can have the following values.

    Not, Neg, Mov

The operation is sometimes pretty-printed as

    Dest <- UnOp Arg

### Load
The `Load` operation loads a value of type *RtlType* from a memory
address stored in a register *Addr* and writes the loaded value into
register *Dest*.

    Load(Dest, RtlType, Addr)

The operation is sometimes pretty-printed as

    Dest <- Load RtlType Addr

### Store
The `Store` operation writes a value of type *RtlType* stored in a
register *Val* to a memory addres stored in register *Addr*.

    Store(Addr, RtlType, Val)

The operation is sometimes pretty-printed as

    Addr <- Store RtlType Val

### ArrayAddress
The `ArrayAddress` operation computes a memory address that is
*Offset* bytes after the memory location in the stack frame where
array locals are stored into a register *Dest*. Note that *Offset* is
an non-negative integer constant.

    ArrayAddress(Dest, Offset)

The operation is sometimes pretty-printed as

    Dest <- ArrayAddress Offset

### IntConst
The `IntConst` operation loads an integer constant *Const* into a
register *Dest*.

    IntConst(Dest, Const)

The operation is sometimes pretty-printed as

    Dest <- IntConst Const

### Label
The `Label` operation has no side effects, but defines a location in
the program code that is identified by a string *name*, and may be the
target of control flow instructions. The name must be unique in the
entire program, and may not clash with the names of functions or
global variables.

    Label(Name)

The operation is sometimes pretty-printed as

    Name:

### Branch
The `Branch` operation diverts control flow to after a `Label`
instruction with name *Name* in the same procedure based on the value
of the register *Cond*.

    Branch(Name, Mode, Cond)

The *Mode* enumeration defines what values of the register *Cond* that
causes the control flow to be diverted.

    Zero, NonZero

The operation is sometimes prettyprinted as

    Branch Name, Mode, Cond

### Jump
The `Jump` operation unconditinally transfers control flow to after a
`Label` instructiom with name *Name* in the same procedure.

    Jump(Name)

The operation is sometimes prettyprinted as

    Jump Name

### Call
The `Call` operation calls another procedure with the name *Name*,
sending the values of the registers *Args* as arguments, placing the
return value in register *Dest*.

    Call(Dest, Name, Args)

The *Dest* parameter may be -1, signifying that the return value (if
any) should be discarded. In this form, the operation may be
constructed as

    Call(Name, Args)

The operation is sometimes prettyprinted as

    Dest <- Call Name [Arg [, Arg...]]

    Call Name [Arg [, Arg...]]
