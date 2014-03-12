package rtl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents an RTL load module.
 */
public class Module {
    // Zero-initialised global variables of the program.
    // Name => Size
    private Map<String, Integer> globals;
    private Map<String, String> stringLiterals;

    public List<Proc> procedures;

    public Module(Map<String, Integer> globals, List<Proc> procedures, Map<String, String> stringLiterals) {
        this.globals = Collections.unmodifiableMap(globals);
        this.procedures = Collections.unmodifiableList(procedures);
        this.stringLiterals = Collections.unmodifiableMap(stringLiterals);
    }

    public Map<String, Integer> getGlobals() {
        return globals;
    }
    public List<Proc> getProcedures() {
        return procedures;
    }
    public Map<String, String> getStringLiterals() {
        return stringLiterals;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (Map.Entry<String,Integer> glob : globals.entrySet()) {
            b.append(glob.getKey()).append(": ").append(glob.getValue()).append('\n');
        }
        for (Map.Entry<String,String> strl : stringLiterals.entrySet()) {
            b.append(strl.getKey()).append(": ").append(strl.getValue()).append('\n');
        }
        for (Proc p : procedures)
            b.append(p.toString()).append("\n");

        return b.toString();
    }
}
