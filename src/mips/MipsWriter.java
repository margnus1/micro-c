package mips;

import java.io.IOException;
import java.io.Writer;

/**
 * Writes MIPS assembly to a java.io.Writer, Wraps IOExceptions in
 * RuntimeExceptions to prevent befouling the code that uses MipsOutputStreams
 * with throws-declarations, breaking the abstraction.
 */
public class MipsWriter extends MipsOutputStream {
    private enum Section {DATA, TEXT, UNKNOWN}
    private Section currentSection = Section.UNKNOWN;
    Writer writer;
    static final String instrIndent = "    ";

    public MipsWriter(Writer writer) {
        this.writer = writer;
        // The assembler is responsible for filling the branch delay slots.
        emitDirective(".set reorder");
    }

    @Override
    public void emitProcedure(String label) {
        ensureSection(Section.TEXT);
        emitDirective(".globl " + label);
        emitDirective(label + ":");
    }

    @Override
    public void emitRType(MipsROp op, MipsRegister rs, MipsRegister rt, MipsRegister rd) {
        emitInstruction(op.toString().toLowerCase() + " " +
                rs + ", " + rt + ", " + rd);
    }

    @Override
    public void emitIType(MipsIOp op, MipsRegister rs, MipsRegister rt, int immediate) {
        emitInstruction(op.toString().toLowerCase() + " " +
                rs + ", " + rt + ", " + immediate);
    }

    @Override
    public void emitIType(MipsIOp op, MipsRegister rs, MipsRegister rt, String label) {
        emitInstruction(op.toString().toLowerCase() + " " +
                rs + ", " + rt + ", " + label);
    }

    @Override
    public void emitJType(MipsJOp op, String label) {
        emitInstruction(op.toString().toLowerCase() + " " + label);
    }

    @Override
    public void emitLabel(String label) {
        emitDirective(label + ":");
    }

    @Override
    public void emitGlobal(String label, int size) {
        ensureSection(Section.DATA);
        emitLabel(label);
        emitInstruction(".space " + size);
    }

    private void ensureSection(Section section) {
        if (currentSection != section) {
            emitDirective("." + section.toString().toLowerCase() + ":");
            currentSection = section;
        }
    }

    private void emitInstruction(String instruction) {
        emitLine(instrIndent + instruction);
    }
    private void emitDirective(String directive) {
        emitLine(directive);
    }
    private void emitLine(String line) {
        try {
            writer.write(line);
            writer.write("\n");
            writer.flush();

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
