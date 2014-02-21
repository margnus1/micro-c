package rtl;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for generating RTL code. Creates unique and easy to read
 * labels for encoding control flow structures.
 */
public class LabelGenerator {
    // Symbol or sequence that is not allowed in C identifiers, but are allowed
    // in assembler labels.
    static final String separator = ".";
    private String methodName;
    private Map<String, Integer> symbolUse = new HashMap<>();
    private String exitPointLabel;

    public LabelGenerator(String methodName) {
        this.methodName = methodName;
        exitPointLabel = createLabel("exit");
    }

    /**
     * Creates a new, unique, label
     * @param symbol A human-readable name of the purpose of the label.
     * @return A unique label.
     */
    public String createLabel(String symbol) {
        if (symbol == "") symbol = "label";
        if (symbolUse.containsKey(symbol)) {
            int number;
            symbolUse.put(symbol, number = symbolUse.get(symbol) + 1);
            return methodName + separator + symbol + number;
        } else {
            symbolUse.put(symbol, 0);
            return methodName + separator + symbol;
        }
    }

    public String getExitPointLabel() {
        return exitPointLabel;
    }
}
