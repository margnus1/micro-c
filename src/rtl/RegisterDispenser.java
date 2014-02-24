package rtl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for generating RTL code. Keeps track of type information and
 * creates new, unused, registers.
 */
public class RegisterDispenser {
    private List<RtlType> registers;

    public RegisterDispenser(List<RtlType> registers) {
        this.registers = new ArrayList<>(registers);
    }
    public RegisterDispenser(){
        this.registers = new ArrayList<>();
    }

    public List<RtlType> getRegisters() {
        return Collections.unmodifiableList(registers);
    }

    public int createRegister(RtlType type) {
        registers.add(type);
        return registers.size()-1;
    }
}
