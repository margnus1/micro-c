import java.util.*;

class Proc implements RtlDec {
    private String label;
    private List<Integer> formals;
    private List<Integer> locals;
    private int frame;
    private List<RtlInsn> insns;

    public Proc (String _label, List<Integer> _formals, 
                 List<Integer> _locals, int _frame, 
                 List<RtlInsn> _insns){
        label =_label;
        formals =_formals;
        locals =_locals;
        frame =_frame;
        insns =_insns;
    }

    public String getLabel (){
        return label;
    }

    public void setLabel (String _label){
        label =_label;
    }

    public List<Integer> getFormals (){
        return formals;
    }

    public void setFormals (List<Integer> _formals){
        formals =_formals;
    }

    public List<Integer> getLocals (){
        return locals;
    }

    public void setLocals (List<Integer> _locals){
        locals =_locals;
    }

    public int getFrame (){
        return frame;
    }

    public void setFrame (int _frame){
        frame =_frame;
    }

    public List<RtlInsn> getInsns (){
        return insns;
    }

    public void setInsns (List<RtlInsn> _insns){
        insns =_insns;
    }

    public String toString(){
        StringBuffer r = new StringBuffer("\nproc" + " " + label + " ");

        r.append("\n formals: ["); 

        for (int formal : formals) {
            r.append(Rtl.regToString(formal));
        }

        r.append("]\n locals: [");

        for (int local : locals) {
            r.append(Rtl.regToString(local));
        }


        r.append("]\n frame: "+frame);

        for (RtlInsn insn : insns) {
            if (insn instanceof LabDef) r.append("\n"+insn);
            else r.append("\n    "+insn);
        }
        return new String(r);
    };
}

