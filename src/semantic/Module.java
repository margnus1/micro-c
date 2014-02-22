package semantic;

import java.util.Collections;
import java.util.Map;

/**
 * AST organised by the load-module definitions.
 */
public class Module {
    private Map<String, Type> globalDefinitions;
    private Map<String, FunctionType> functions;

    public Module(Map<String, Type> globalDefinitions, Map<String, FunctionType> functions) {
        this.globalDefinitions = Collections.unmodifiableMap(globalDefinitions);
        this.functions         = Collections.unmodifiableMap(functions);
    }

    public Map<String, Type> getGlobalDefinitions() {
        return globalDefinitions;
    }
    public Map<String, FunctionType> getFunctions() {
        return functions;
    }
}
