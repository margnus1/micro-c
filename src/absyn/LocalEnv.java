package absyn;
import java.util.*;
import parser.*;

class LocalEnv implements Env {

    Map<String, Type> local =  new HashMap<String, Type>();
    
    Env globalEnv;

    Type result;

    LocalEnv(Env _globalEnv) {
        globalEnv = _globalEnv;
    }
    
    public void insert(String s, Type t, Position p) {
        System.out.println("Locally inserting "+s);
        if (local.get(s) != null)
            SemanticCheck.
                semanticError("Identifier "+s+" doubly defined", p);
        local.put(s,t);
    }
    

    public Type lookup(String s) {
        System.out.println("Local lookup: "+s);
        Type r = local.get(s);
        if (r!=null) return r;
        else return globalEnv.lookup(s);
    }

    public void setResult(Type t) {
        result = t;
    }

    public Type getResult() {
        return result;
    }

    public Env enter() {
        throw new IllegalStateException("Already in local environment");
    }
}
