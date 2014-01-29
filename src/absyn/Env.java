package absyn;
import parser.*;

interface Env {
    void insert(String s, Type t, Position p);
    Type lookup(String s);
    void setResult(Type t);
    Type getResult();
    Env enter();
}
