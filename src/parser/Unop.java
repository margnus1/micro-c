package parser;

import rtl.UnOp;

public enum Unop { NOT, MINUS;
    public UnOp getRtlUnop(){
        switch (this){
            case NOT: return rtl.UnOp.Not;
            case MINUS: return rtl.UnOp.Neg;
            default: throw new RuntimeException("Unsupported Unary Operation");
        }
    }
}
