package parser;


import rtl.BinOp;

public enum Binop { ANDAND, NE,
        LT, GT, LTEQ, GTEQ,
        PLUS, MINUS,
        MUL, DIV, EQEQ, NOTEQ;

    public BinOp getRTLBinop(){
        switch(this){
            case NE:    return rtl.BinOp.NE;
            case LT:    return rtl.BinOp.LT;
            case GT:    return rtl.BinOp.GT;
            case LTEQ:  return rtl.BinOp.LTEQ;
            case GTEQ:  return rtl.BinOp.GTEQ;
            case PLUS:  return rtl.BinOp.ADD;
            case MINUS: return rtl.BinOp.SUB;
            case MUL:   return rtl.BinOp.MUL;
            case DIV:   return rtl.BinOp.DIV;
            case EQEQ:  return rtl.BinOp.EQ;
            case NOTEQ: return rtl.BinOp.NE;
            default:
                throw new RuntimeException("Unsupported Binop");
        }
    }
}
