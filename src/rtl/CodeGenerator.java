package rtl;

import parser.*;
import semantic.FunctionType;
import semantic.Type;

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
                    generateIfStatement(statement);
                    break;
                case UcParseTreeConstants.JJTEMPTYSTATEMENT:
                    break; // Nothing to do!
                case UcParseTreeConstants.JJTWHILESTATEMENT:
                    generateWhileStatement(statement);
                    break;
                case UcParseTreeConstants.JJTSIMPLECOMPOUNDSTATEMENT:
                    for(SimpleNode stmt : statement)
                        generateStatement(stmt);
                    break;
                case UcParseTreeConstants.JJTRETURNSTATEMENT:
                    if (statement.jjtGetNumChildren() == 1)
                        instructions.add(new Unary(Rtl.RV, UnOp.Mov,
                                generateExpression(statement.jjtGetChild(0))));
                    instructions.add(new Jump(labels.getExitPointLabel()));
                    break;
                default:
                    generateExpression(statement);
                    break;
            }
        }

        private void generateWhileStatement(SimpleNode statement) {
            String condLabel = labels.createLabel("while_cond");
            String loopLabel = labels.createLabel("while_loop");

            instructions.add(new Jump(condLabel));
            instructions.add(new Label(loopLabel));
            generateStatement(statement.jjtGetChild(1));
            instructions.add(new Label(condLabel));
            int condReg = generateExpression(statement.jjtGetChild(0));
            instructions.add(new Branch(loopLabel,BranchMode.NonZero,condReg));
        }

        private void generateIfStatement(SimpleNode statement) {
            int numOfChild = statement.jjtGetNumChildren();
            int condReg = generateExpression(statement.jjtGetChild(0));
            String endIfLabel = labels.createLabel("end_if");

            if(numOfChild == 2){
                instructions.add(new Branch(endIfLabel, BranchMode.Zero,condReg));
                generateStatement(statement.jjtGetChild(1));

            }else{
                String elseLabel = labels.createLabel("else");
                instructions.add(new Branch(elseLabel,BranchMode.Zero,condReg));
                generateStatement(statement.jjtGetChild(1));
                instructions.add(new Jump(endIfLabel));

                instructions.add(new Label(elseLabel));
                generateStatement(statement.jjtGetChild(2));
            }
            instructions.add(new Label(endIfLabel));
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

                case UcParseTreeConstants.JJTUNARYEXPR:
                    //not or minus
                    resultReg = generateExpression(expression.jjtGetChild(0));
                    UnOp unOp = ((Unop)expression.jjtGetValue()).getRtlUnop();
                    instructions.add(new Unary(resultReg,unOp,resultReg));
                    return resultReg;

                case UcParseTreeConstants.JJTIDENTIFIER:
                    return generateIdentifier(expression);
                case UcParseTreeConstants.JJTBINARY:
                    return generateBinary(expression);
                case UcParseTreeConstants.JJTASSIGNMENT:
                    return generateAssignment(expression);
                case UcParseTreeConstants.JJTARRAYLOOKUP:
                    return generateArrayLookup(expression);
                case UcParseTreeConstants.JJTFUNCTIONCALL:
                    return generateFunctionCall(expression);

                default:
                    throw new RuntimeException("Unexpected AST node in expression. This should have been caught in the semantic pass.");
            }
        }

        private int generateFunctionCall(SimpleNode expression) {
            String name = (String) expression.jjtGetChild(0).jjtGetValue();
            int numOfChild = expression.jjtGetChild(1).jjtGetNumChildren();
            int[] argRegArr = new int[numOfChild];

            for(int i=0; i <numOfChild; i++) {
                argRegArr[i] = generateExpression(expression.jjtGetChild(1).jjtGetChild(i));
            }

            Type retT = module.getFunctions().get(name).getReturnType();
            if(!retT.isVoid()){
                int rvReg = registers.createRegister(retT.getRtlType());
                instructions.add(new Call(rvReg,name,argRegArr));
                return rvReg;
            }else{
                instructions.add(new Call(name,argRegArr));
                return -1;
            }
        }

        private int generateBinary(SimpleNode expression) {
            int resultReg;
            resultReg = registers.createRegister(RtlType.INT);
            int lhs = generateExpression(expression.jjtGetChild(0));

            Binop binOp = (Binop)expression.jjtGetValue();
            if(binOp == Binop.ANDAND){
                instructions.add(new Unary(resultReg, UnOp.Mov, lhs));
                String bypassLabel = labels.createLabel("andshortcircut");
                instructions.add(new Branch(bypassLabel, BranchMode.NonZero, resultReg));
                int rhs = generateExpression(expression.jjtGetChild(1));
                instructions.add(new Unary(resultReg, UnOp.Mov, rhs));
                instructions.add(new Label(bypassLabel));

            }else{
                int rhs = generateExpression(expression.jjtGetChild(1));
                instructions.add(new Binary(resultReg,binOp.getRTLBinop(),lhs,rhs));
            }
            return resultReg;
        }

        private int generateArrayLookup(SimpleNode expression) {
            RtlType elemType = (RtlType)expression.jjtGetValue();
            int tempReg = generateArrayLookupAddress(expression);

            instructions.add(new Load(tempReg,elemType,tempReg));
            return tempReg;
        }

        /**
         * Compute the address of an ArrayLookup node
         * @param arrayLookup An ArrayLookup AST node.
         * @return A newly allocated temporary register containing the address
         *         of the looked-up element.
         */
        private int generateArrayLookupAddress(SimpleNode arrayLookup) {
            RtlType elemType = (RtlType)arrayLookup.jjtGetValue();
            int baseReg = generateExpression(arrayLookup.jjtGetChild(0));
            int indexReg = generateExpression(arrayLookup.jjtGetChild(1));

            // Compute the address of the element in tempReg
            int tempReg = registers.createRegister(RtlType.INT);
            instructions.add(new IntConst(tempReg,elemType.size()));
            instructions.add(new Binary(tempReg,BinOp.MUL,tempReg,indexReg));
            instructions.add(new Binary(tempReg,BinOp.ADD,tempReg,baseReg));
            return tempReg;
        }

        private int generateAssignment(SimpleNode expression) {
            int rhsReg = generateExpression(expression.jjtGetChild(1));

            SimpleNode lhs = expression.jjtGetChild(0);
            RtlType elemType;
            int dest;

            /* This method has two code paths, corresponding to two different
             * types of left-hand sides.
             * One is a local stored in a register, which causes the code to
             * return inside the switch statement.
             * The other one is anything we need to compute the address of,
             * which falls out of the switch statement, with the dest register
             * set to some temporary register containing the address of the lhs.
             */
            switch (lhs.getId()){
                case UcParseTreeConstants.JJTIDENTIFIER:
                    //we should distinguish the locals and globals
                    String name = (String) lhs.jjtGetValue();

                    if(locals.containsKey(name)){
                        dest = locals.get(name);
                        instructions.add(new Unary(dest,UnOp.Mov,rhsReg));
                        return rhsReg;

                    }else{
                        dest = registers.createRegister(RtlType.INT);
                        instructions.add(new GlobalAddress(dest,name));

                        elemType = module.getGlobalDefinitions().get(name).getRtlType();
                    }
                    break;
                case UcParseTreeConstants.JJTARRAYLOOKUP:
                    elemType = (RtlType)lhs.jjtGetValue();
                    dest = generateArrayLookupAddress(lhs);
                    break;
                default:
                    throw new RuntimeException("Bad assignee");
            }

            // in this case, dest contains the address to be assigned and
            // elemType its type
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
