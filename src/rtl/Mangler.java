package rtl;

import java.util.*;

/**
 * Mangles all global symbols of an RTL module so that programs using
 * instruction names as symbols can be used in simulators that have so bad
 * parsers that they choke if fed such perfectly unambiguous assembly.
 */
public final class Mangler {
    private Mangler() {}
    public static Module mangle(Module input) {
        HashMap<String,Integer> mangledGlobals = new HashMap<>();
        for (Map.Entry<String, Integer> global : input.getGlobals().entrySet())
            mangledGlobals.put(mangleSymbol(global.getKey()), global.getValue());

        List<Proc> mangledProcedures = new ArrayList<>();
        for (Proc procedure : input.getProcedures())
            mangledProcedures.add(mangle(procedure));

        return new Module(mangledGlobals, mangledProcedures);
    }

    private static Proc mangle(Proc input) {
        String mangledName = mangleSymbol(input.getName());
        List<Object> mangledInstructions = new ArrayList<Object>();
        for (Object instruction : input.getInstructions())
            mangledInstructions.add(mangleInstruction(instruction));

        return new Proc(mangledName, input.getArgumentCount(),
                input.getRegisterTypes(), input.getStackFrameSize(),
                mangledInstructions);
    }

    private static Object mangleInstruction(Object instruction) {
        if (instruction instanceof GlobalAddress)
            return new GlobalAddress(((GlobalAddress) instruction).getDest(),
                    mangleSymbol(((GlobalAddress) instruction).getName()));
        else if (instruction instanceof Call)
            return new Call(((Call) instruction).getDest(),
                    mangleSymbol(((Call) instruction).getProcName()),
                    ((Call) instruction).getArgs());
        else return instruction;
    }

    private static String mangleSymbol(String symbol) {
        return "$$MANGLED_RTL_SYMBOL_" + symbol + "$$";
    }
}
