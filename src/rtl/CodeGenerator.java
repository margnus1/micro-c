package rtl;

import org.omg.CORBA.portable.ApplicationException;
import parser.AST;
import parser.Binop;
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
                    return generateIdentifier(expression);
                case UcParseTreeConstants.JJTBINARY:
                    resultReg = registers.createRegister(RtlType.INT);
                    int lhs = generateExpression(expression.jjtGetChild(0));
                    int rhs = generateExpression(expression.jjtGetChild(1));
                    instructions.add(new Binary(resultReg,((Binop)expression.jjtGetValue()).getRTLBinop(),lhs,rhs));
                   return resultReg;
                case UcParseTreeConstants.JJTASSIGNMENT:
                    return generateAssignment(expression);

                case UcParseTreeConstants.JJTARRAYLOOKUP:
                    return generateArrayLookup(expression);

                case UcParseTreeConstants.JJTFUNCTIONCALL:
                    break;

                case UcParseTreeConstants.JJTUNARYEXPR:
                    break;


                default:
                    throw new RuntimeException("Unexpected AST node in expression. This should have been caught in the semantic pass.");
            }
        }

        private int generateArrayLookup(SimpleNode expression) {
            int baseReg = generateExpression(expression.jjtGetChild(0));
            int indexReg = generateExpression(expression.jjtGetChild(1));

            RtlType elemType;
            int size  = (elemType =(RtlType)expression.jjtGetValue()).size();
            int sizeReg = registers.createRegister(RtlType.INT);
            instructions.add(new IntConst(sizeReg,size));

            instructions.add(new Binary(sizeReg, BinOp.MUL,indexReg,sizeReg));

            instructions.add(new Binary(sizeReg,BinOp.ADD,sizeReg,baseReg));

            //sizeReg contains the address now

            instructions.add(new Load(sizeReg,elemType,sizeReg));

            return sizeReg;
        }

        private int generateAssignment(SimpleNode expression) {

            int rhsReg = generateExpression(expression.jjtGetChild(1));

            SimpleNode lhs = expression.jjtGetChild(0);
            RtlType elemType;
            int dest;

            switch (lhs.getId()){
                case UcParseTreeConstants.JJTIDENTIFIER:
                    //we should distinguish the locals and globals

                    String name = (String) expression.jjtGetValue();

                    if(locals.containsKey(name)){
                        dest = locals.get(name);

                        instructions.add(new Unary(dest,UnOp.Mov,rhsReg));

                        return rhsReg;

                    }else{
                        dest = registers.createRegister(RtlType.INT);
                        instructions.add(new GlobalAddress(dest,name));

                        elemType = module.getGlobalDefinitions().get(name).getRtlType();

                    }
                case UcParseTreeConstants.JJTARRAYLOOKUP:
                    int baseReg = generateExpression(lhs.jjtGetChild(0));
                    int indexReg = generateExpression(lhs.jjtGetChild(1));

                    int size  = (elemType = (RtlType)lhs.jjtGetValue()).size();
                    int sizeReg = registers.createRegister(RtlType.INT);
                    instructions.add(new IntConst(sizeReg,size));

                    instructions.add(new Binary(sizeReg,BinOp.MUL,indexReg,sizeReg));

                    instructions.add(new Binary(sizeReg,BinOp.ADD,sizeReg,baseReg));

                    //sizeReg now contains the address to be assigned
                    //address in the register

                    dest = sizeReg;
                    break;
                default: throw new RuntimeException("Bad assignee");
            }
            // in this case, dest contains the address to be assigned

            instructions.add(new Store(dest,elemType,rhsReg));

            return rhsReg;


        }

        private int generateIdentifier(SimpleNode expression) {
            int resultReg;
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
        }
    }
}
