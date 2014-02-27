package rtl;

public enum RtlType { BYTE, INT ;

    public int size(){
        switch (this){
        case BYTE: return 1;
        case INT: return 4;
        }

        throw new RuntimeException("Bad RTL Type");
    }
}
