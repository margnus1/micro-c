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
    public List<Proc> procedures;

    public Module(Map<String, Integer> globals, List<Proc> procedures) {
        this.globals = Collections.unmodifiableMap(globals);
        this.procedures = Collections.unmodifiableList(procedures);
    }

    public Map<String, Integer> getGlobals() {
        return globals;
    }
    public List<Proc> getProcedures() {
        return procedures;
    }
}
