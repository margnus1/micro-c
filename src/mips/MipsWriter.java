package mips;

import utils.CommandLine;

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
    public void emitLabel(String label) {
        emitDirective(label + ":");
    }

    @Override
    public void emitGlobal(String label, int size) {
        ensureSection(Section.DATA);
        emitDirective(".align 4");
        emitDirective(".globl " + label);
        emitLabel(label);
        emitInstruction(".space " + size);
    }

    @Override
    public void emitGlobal(String label, String stringLiteral) {
        ensureSection(Section.DATA);
        emitLabel(label);
        emitInstruction(".asciiz " + stringLiteral);
    }

    @Override
    public void emitComment(String comment) {
        for (String line : comment.split("[\n\r]+"))
            emitDirective(instrIndent + "# " + line);
    }

    private void ensureSection(Section section) {
        if (currentSection != section) {
            emitDirective("." + section.toString().toLowerCase());
            currentSection = section;
        }
    }

    @Override
    public void emitInstruction(String instruction, String... args) {
        StringBuilder line = new StringBuilder();
        line.append(instrIndent).append(instruction);

        String pad = "";
        for (int width = instruction.length(); width < 7; width++) pad += " ";

        for (int i = 0; i < args.length; i++)
            line.append(i == 0 ? pad + " " : ", ").append(args[i]);

        emitLine(line.toString());
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
