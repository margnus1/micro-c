import java.util.*;

class GlobalEnv implements Env {
    Map<String, Type> global =  new HashMap<String, Type>();
    
    public void insert(String s, Type t, Position p) {
        System.out.println("Globally inserting "+s);
        if (global.get(s) != null)
            SemanticCheck.
            semanticError("Identifier "+s+" doubly defined", p);
        global.put(s,t);
    }
    

    public Type lookup(String s) {
        System.out.println("Global lookup: "+s);
        return global.get(s);
    }

    public void setResult(Type t) {
        throw new IllegalStateException("No result type in global environment");
    }

    public Type getResult() {
        throw new IllegalStateException("No result type in global environment");
    }

    public Env enter() {
        return new LocalEnv(this);
    }
}
