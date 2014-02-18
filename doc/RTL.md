RTL
===

RTL operates on a register machine with an infinite amount of
registers. The registers are identified by integer numbers starting
with zero. Register zero is reserved for the return value of the
current procedure.

All registers are associated with a type, described by the *RtlType*
enumeration, having one of the following values.

    Int, Byte

The size of an `Int` is 4 bytes.

Instructions
------------
These are the available instructions in the RTL.

### Binary

    Binary(*Dest*, *BinOp*, *Lhs*, *Rhs*)

Computes a binary operation that acts on two registers *Lhs* and
*Rhs*, and puts the result in a third register *Dest*.

The *BinOp* enumeration can have the following values.

    Add, Sub, Mul, Div, Gt, Lt, Eq, Nq

The operation is sometimes pretty-printed as

    *Dest* <- *BinOp* *Lhs*, *Rhs*

### Unary
Another operation is `Unary`:

    Unary(*Dest*, *UnOp*, *Arg*)

Computes a unary operation that acts on a register *Arg* and puts the
result in a register *Dest*.

The *UnOp* enumeration can have the following values.

    Not, Neg, Mov

The operation is sometimes pretty-printed as

    *Dest* <- *UnOp* *Arg*

### Load
The `Load` operation loads a value of type *RtlType* from a memory
address stored in a register *Addr* and writes the loaded value into
register *Dest*.

    Load(*Dest*, *RtlType*, *Addr*)

The operation is sometimes pretty-printed as

    *Dest* <- Load *RtlType* *Addr*

### Store
The `Store` operation writes a value of type *RtlType* stored in a
register *Val* to a memory addres stored in register *Addr*.

    Store(*Addr*, *RtlType*, *Val*)

The operation is sometimes pretty-printed as

    *Addr* <- Store *RtlType* *Val*

### ArrayAddress
The `ArrayAddress` operation computes a memory address that is
*Offset* bytes after the memory location in the stack frame where
array locals are stored into a register *Dest*. Note that *Offset* is
an non-negative integer constant.

    ArrayAddress(*Dest*, *Offset*)

The operation is sometimes pretty-printed as

    *Dest* <- ArrayAddress *Offset*

### IntConst
The `IntConst` operation loads an integer constant *Const* into a
register *Dest*.

    IntConst(*Dest*, *Const*)

The operation is sometimes pretty-printed as

    *Dest* <- IntConst *Const*

