All AST Node Types
==================

    FunctionDefinition (tick)
        BaseType
        Identifier
        FunctionParameters
        CompoundStatement

    FunctionDeclaration (unknown)
        BaseType
        Identifier
        FunctionParameters

    VariableDeclaration (tick)
        BaseType
        Identifier | ArrayDeclarator

    ArrayDeclarator (tick, processed within varDec)
        Identifier
        [] | [IntegerLiteral]

    FunctionParameters (tick)
        VariableDeclaration
        ...

    CompoundStatement
        VariableDeclarations
        Statements

    VariableDeclarations
        VariableDeclaration
        ...

    Statements
        #Statement
        ...

    #Statement
        IfStatement
        EmptyStatement
        WhileStatement
        SimpleCompoundStatement
        ReturnStatement
        #Expression

    #Expression
        Binary
        Assignment
        Unary
        IntegerLiteral
        Identifier
        ArrayLookup
        FunctionCall

    IfStatement
        #Expression
        #Statement
        #Statement (?)

    WhileStatement
        #Expression
        #Statement

    EmptyStatement

    ReturnStatement
        #Expression (?)

    Binary (value is parser.Binop)
        #Expression
        #Expression

    Unary (value is parser.Unop)
        #Expression

    Assignment
        #Expression  (Identifier & ArrayLookup are allowed semantically)
        #Expression

    ArrayLookup
        Identifier
        #Expression

    Identifier (value is String)

    BaseType (value is parser.Type)

    IntegerLiteral (value is Integer)

    StringLiteral (value is String (the whole literal, incl. escape codes))

--------
    sample for precedence

----------
    for left-associative operators, we use EBNF expressions
    for right-associative operators, we use right recursion to build corresponding nodes.

