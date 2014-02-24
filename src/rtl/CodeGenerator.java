package rtl;

import org.omg.CORBA.portable.ApplicationException;
import parser.AST;
import parser.SimpleNode;
import parser.UcParseTreeConstants;
import semantic.FunctionType;
import semantic.Type;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates RTL from the AST.
 */
public class CodeGenerator {
    private CodeGenerator() {}

    public static Module compileModule(semantic.Module module) {
        Map<String, Integer> globals = new HashMap<>();
        List<Proc> procedures = new ArrayList<>();

        for (Map.Entry<String, Type> global : module.getGlobalDefinitions().entrySet()) {
            globals.put(global.getKey(), global.getValue().getByteSize());
        }

        for (FunctionType type : module.getFunctions().values()) {
            if (type.getDefinition() == null) continue;
            procedures.add(ProcedureContext.compileProcedure(module, type));
        }

        return new Module(globals, procedures);
    }

    /**
     * Generates code in the context of a procedure.
     */
    private static class ProcedureContext {
        private semantic.Module module;
        public RegisterDispenser registers = new RegisterDispenser();
        public LabelGenerator labels;
        public List<Object> instructions = new ArrayList<>();
        private Map<String, Integer> locals = new HashMap<>();

        private static class ArrayLocal {
            public int offset; public RtlType rtlType;
            private ArrayLocal(int offset, RtlType rtlType) {
                this.offset = offset;
                this.rtlType = rtlType;
            }
        }
        private Map<String, ArrayLocal> arrays = new HashMap<>();
        public int allocatedStackSpace = 0;

        private ProcedureContext(semantic.Module module, String procedureName) {
            this.module = module;
            this.labels = new LabelGenerator(procedureName);
        }

        public static Proc compileProcedure(semantic.Module module, FunctionType type) {
            SimpleNode def = type.getDefinition();
            String name = (String)def.jjtGetChild(1).jjtGetValue();
            ProcedureContext gen = new ProcedureContext(module, name);
            gen.registers.createRegister(type.getReturnType().getRtlType());

            for (int argI = 0; argI < type.getArgumentCount(); argI++) {
                String argName = AST.getVarName(def.jjtGetChild(2).jjtGetChild(argI));
                Type argType = type.getArgumentType(argI);
                int reg = gen.registers.createRegister(argType.getRtlType());
                gen.locals.put(argName, reg);
            }

            SimpleNode compoundStatement = def.jjtGetChild(3);
            for (SimpleNode local : compoundStatement.jjtGetChild(0)) {
                String localName = AST.getVarName(local);
                Type localType = new Type(local); // I was hoping I wouldn't need to do this
                if (localType.isVector()) {
                    RtlType elemType = localType.getElementType().getRtlType();
                    int vectorSize = localType.getByteSize();
                    gen.arrays.put(localName, new ArrayLocal(gen.allocatedStackSpace, elemType));
                    gen.allocatedStackSpace += vectorSize;
                } else {
                    int reg = gen.registers.createRegister(localType.getRtlType());
                    gen.locals.put(localName, reg);
                }
            }

            for (SimpleNode statement : compoundStatement.jjtGetChild(1)) {
                gen.generateStatement(statement);
            }

            // The very last instruction is the exit label
            gen.instructions.add(new Label(gen.labels.getExitPointLabel()));
            return new Proc(name,
                    type.getArgumentTypes().length,
                    gen.registers.getRegisters(),
                    gen.allocatedStackSpace,
                    gen.instructions);
        }

        /**
         * Generates code that executes a statement.
         * @param statement The statement to generate code for.
         */
        private void generateStatement(SimpleNode statement) {
            switch (statement.getId()) {
                case UcParseTreeConstants.JJTIFSTATEMENT:
                    throw new NotImplementedException();
                case UcParseTreeConstants.JJTEMPTYSTATEMENT:
                    break; // Nothing to do!
                case UcParseTreeConstants.JJTWHILESTATEMENT:
                    throw new NotImplementedException();
                case UcParseTreeConstants.JJTSIMPLECOMPOUNDSTATEMENT:
                    throw new NotImplementedException();
                case UcParseTreeConstants.JJTRETURNSTATEMENT:
                    if (statement.jjtGetNumChildren() == 1)
                        instructions.add(new Unary(Rtl.RV, UnOp.Mov,
                                generateExpression(statement.jjtGetChild(0))));
                    instructions.add(new Jump(labels.getExitPointLabel()));
                    break;
                default:
                    generateExpression(statement);
            }
        }

        /**
         * Generates code that executes an expression, returning the register
         * that will contain the result.
         * @param expression The AST expression.
         * @return The register containing the result.
         */
        private int generateExpression(SimpleNode expression) {
            int resultReg;
            switch (expression.getId()) {
                case UcParseTreeConstants.JJTINTEGERLITERAL:
                    resultReg = registers.createRegister(RtlType.INT);
                    instructions.add(new IntConst(resultReg, (Integer)expression.jjtGetValue()));
                    return resultReg;
                case UcParseTreeConstants.JJTIDENTIFIER:
                    String id = (String)expression.jjtGetValue();
                    if (locals.containsKey(id))
                        return locals.get(id);
                    else if (arrays.containsKey(id)) {
                        int addrReg = registers.createRegister(RtlType.INT);
                        instructions.add(new ArrayAddress(addrReg, arrays.get(id).offset));
                        return addrReg;
                    } else if (module.getGlobalDefinitions().containsKey(id)) {
                        Type def = module.getGlobalDefinitions().get(id);
                        resultReg = registers.createRegister(RtlType.INT);
                        instructions.add(new GlobalAddress(resultReg, id));
                        if (!def.isVector())
                            instructions.add(new Load(resultReg, def.getRtlType(), resultReg));
                        return resultReg;
                    } else throw new RuntimeException("Can't map identifier to definition.");
                // TODO: The rest of the types
                default:
                    throw new RuntimeException("Unexpected AST node in expression. This should have been caught in the semantic pass.");
            }
        }
    }
}
