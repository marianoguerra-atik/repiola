package repiola;

import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author mariano
 */
public abstract class Parser {

    protected Hashtable labels;
    protected String[] lines;
    protected boolean inJump;
    protected String lastLabel;
    protected static final String[] commands = {"put", "#", ":", "jmp",
        "add", "sub", "mul", "div", "mod", "set", "or", "and", "xor", "not", "eq", "ne", "gt", "lt", "ge", "le", "get", ".", "rnd", "clr", "push", "pop", "call", "ret"};
    protected static final String[] jumpCommands = {"jmp", ":", "call", "ret"};
    protected static final String[] singleCommands = {"put", "get", "rnd", "not", "clr", "push", "pop"};
    protected static final String[] registerCommands = {"add", "sub", "mul", "div", "mod", "set", "or", "and", "xor"};
    protected static final String[] compareCommands = {"eq", "ne", "gt", "lt", "ge", "le"};

    public OpCode parseLine(String line) throws Exception {
        String[] tokens = split(line.trim(), " ");

        if (tokens.length == 0) {
            return null;
        }

        String command = tokens[0];

        if (command.startsWith("#") || command.equals(".") || command.equals("")) {
            return null;
        }

        if (!this.isStringInArrayBecauseJavaDoesNotHaveInLikePython(commands, command)) {
            throw new Exception("Invalid command '" + command + "'");
        }

        if (this.isStringInArrayBecauseJavaDoesNotHaveInLikePython(registerCommands, command)) {
            return parseRegisterCommand(tokens);
        } else if (this.isStringInArrayBecauseJavaDoesNotHaveInLikePython(compareCommands, command)) {
            return parseCompareCommand(tokens);
        } else if (this.isStringInArrayBecauseJavaDoesNotHaveInLikePython(singleCommands, command)) {
            return parseSingleCommand(tokens);
        } else if (this.isStringInArrayBecauseJavaDoesNotHaveInLikePython(jumpCommands, command)) {
            return parseJumpCommand(tokens);
        } else {
            throw new Exception("Invalid command " + command);
        }
    }

    protected OpCode parseJumpCommand(String[] tokens) throws Exception {
        String command, label = null;
        OpCode opcode;
        short registerNumber;

        command = tokens[0].toLowerCase();

        if (!command.equals("ret")) {
            if (tokens.length != 2) {
                throw new Exception("Invalid number of arguments for jump command " + tokens.toString());
            }

            label = tokens[1].toLowerCase();
        }


        if (command.equals("jmp")) {

            registerNumber = getRegisterNumber(label);

            if (isRegisterIdentifier(label) && registerNumber != -1) {
                opcode = new OpCode(OpCodeType.Opcode, ((Machine.I_RJUMP << 24) | (registerNumber << 16)));
            } else {
                opcode = new OpCode(OpCodeType.Jump, (Machine.I_JUMP << 24), label);
            }
        } else if (command.equals("call")) {
            opcode = new OpCode(OpCodeType.Jump, (Machine.I_CALL << 24), label);
        } else if (command.equals("ret")) {
            opcode = new OpCode(OpCodeType.Opcode, (Machine.I_RET << 24));
        } else if (command.equals(":")) {
            opcode = new OpCode(OpCodeType.Label, -1, label);
        } else {
            throw new Exception("Invalid jmp/label command");
        }

        return opcode;
    }

    public boolean isStringInArrayBecauseJavaDoesNotHaveInLikePython(String[] array, String item) {
        String value = null;
        for (int i = 0; i < array.length; i++) {
            value = array[i];
            if (value.equals(item)) {
                return true;
            }
        }

        return false;
    }

    protected OpCode parseSingleCommand(String[] tokens) throws Exception {
        String dest, command;
        short number = 0;
        byte register = 0;
        int instruction;
        boolean isNumber = false;

        if (tokens.length != 2) {
            throw new Exception("Invalid number of arguments for single command " + tokens.toString());
        }

        command = tokens[0].toLowerCase();
        dest = tokens[1].toLowerCase();

        if (isRegisterIdentifier(dest)) {
            register = (byte) getNumber(dest);
        } else {
            isNumber = true;
            number = getNumber(dest);
        }

        if (command.equals("put") && isNumber) {
            instruction = buildSingleInstruction(isNumber, Machine.I_PUT, number, (byte) 0);
        } else if (command.equals("put") && !isNumber) {
            instruction = buildSingleInstruction(isNumber, Machine.I_RPUT, (byte) 0, register);
        } else if (command.equals("get") && !isNumber) {
            instruction = buildSingleInstruction(isNumber, Machine.I_GET, (byte) 0, register);
        } else if (command.equals("not") && !isNumber) {
            instruction = buildSingleInstruction(isNumber, Machine.I_NOT, (byte) 0, register);
        } else if (command.equals("rnd") && !isNumber) {
            instruction = buildSingleInstruction(isNumber, Machine.I_RANDOM, (byte) 0, register);
        } else if (command.equals("clr") && isNumber) {
            instruction = buildSingleInstruction(isNumber, Machine.I_CLR, number, (byte) 0);
        } else if (command.equals("clr") && !isNumber) {
            instruction = buildSingleInstruction(isNumber, Machine.I_RCLR, (byte) 0, register);
        } else if (command.equals("push") && isNumber) {
            instruction = buildSingleInstruction(isNumber, Machine.I_PUSH, number, (byte) 0);
        } else if (command.equals("push") && !isNumber) {
            instruction = buildSingleInstruction(isNumber, Machine.I_RPUSH, (byte) 0, register);
        } else if (command.equals("pop") && !isNumber) {
            instruction = buildSingleInstruction(isNumber, Machine.I_RPOP, (byte) 0, register);
        } else {
            throw new Exception("Invalid single command");
        }

        return new OpCode(OpCodeType.Opcode, instruction);
    }

    protected OpCode parseCompareCommand(String[] tokens) throws Exception {
        String dest, register, command, label;
        short number = 0;
        byte destinationRegister = 0, sourceRegister = 0;
        int instruction;
        boolean isNumber = false;

        if (tokens.length != 4) {
            throw new Exception("Invalid number of arguments for compare command " + tokens.toString());
        }

        command = tokens[0].toLowerCase();
        register = tokens[1].toLowerCase();
        dest = tokens[2].toLowerCase();
        label = tokens[3].toLowerCase();
        //addJumpLabel(label);
        sourceRegister = (byte) getNumber(register);
        if (isRegisterIdentifier(dest)) {
            destinationRegister = (byte) getNumber(dest);
        } else {
            isNumber = true;
            number = getNumber(dest);
        }

        if (command.equals("eq")) {
            instruction = buildCompareInstruction(isNumber, Machine.I_EQ, Machine.I_REQ, number, sourceRegister, destinationRegister);
        } else if (command.equals("ne")) {
            instruction = buildCompareInstruction(isNumber, Machine.I_NE, Machine.I_RNE, number, sourceRegister, destinationRegister);
        } else if (command.equals("gt")) {
            instruction = buildCompareInstruction(isNumber, Machine.I_GT, Machine.I_RGT, number, sourceRegister, destinationRegister);
        } else if (command.equals("lt")) {
            instruction = buildCompareInstruction(isNumber, Machine.I_LT, Machine.I_RLT, number, sourceRegister, destinationRegister);
        } else if (command.equals("ge")) {
            instruction = buildCompareInstruction(isNumber, Machine.I_GE, Machine.I_RGE, number, sourceRegister, destinationRegister);
        } else if (command.equals("le")) {
            instruction = buildCompareInstruction(isNumber, Machine.I_LE, Machine.I_RLE, number, sourceRegister, destinationRegister);
        } else {
            throw new Exception("Invalid compare command");
        }

        return new OpCode(OpCodeType.Opcode, instruction, label);
    }

    protected OpCode parseRegisterCommand(String[] tokens) throws Exception {
        String dest, register, command;
        short number = 0;
        byte destinationRegister = 0, sourceRegister = 0;
        int instruction;
        boolean isNumber = false;

        if (tokens.length != 3) {
            throw new Exception("Invalid number of arguments for register command " + tokens.toString());
        }

        command = tokens[0].toLowerCase();
        register = tokens[1].toLowerCase();
        dest = tokens[2].toLowerCase();

        sourceRegister = (byte) getNumber(register);
        if (isRegisterIdentifier(dest)) {
            destinationRegister = (byte) getNumber(dest);
        } else {
            isNumber = true;
            number = getNumber(dest);
        }


        if (command.equals("add")) {
            instruction = buildRegisterInstruction(isNumber, Machine.I_ADD, Machine.I_RADD, number, sourceRegister, destinationRegister);
        } else if (command.equals("sub")) {
            instruction = buildRegisterInstruction(isNumber, Machine.I_SUB, Machine.I_RSUB, number, sourceRegister, destinationRegister);
        } else if (command.equals("mul")) {
            instruction = buildRegisterInstruction(isNumber, Machine.I_MUL, Machine.I_RMUL, number, sourceRegister, destinationRegister);
        } else if (command.equals("div")) {
            instruction = buildRegisterInstruction(isNumber, Machine.I_DIV, Machine.I_RDIV, number, sourceRegister, destinationRegister);
        } else if (command.equals("mod")) {
            instruction = buildRegisterInstruction(isNumber, Machine.I_MOD, Machine.I_RMOD, number, sourceRegister, destinationRegister);
        } else if (command.equals("and")) {
            instruction = buildRegisterInstruction(isNumber, Machine.I_AND, Machine.I_RAND, number, sourceRegister, destinationRegister);
        } else if (command.equals("or")) {
            instruction = buildRegisterInstruction(isNumber, Machine.I_OR, Machine.I_ROR, number, sourceRegister, destinationRegister);
        } else if (command.equals("xor")) {
            instruction = buildRegisterInstruction(isNumber, Machine.I_XOR, Machine.I_RXOR, number, sourceRegister, destinationRegister);
        } else if (command.equals("set")) {
            instruction = buildRegisterInstruction(isNumber, Machine.I_SET, Machine.I_RSET, number, sourceRegister, destinationRegister);
        } else {
            throw new Exception("Invalid register command");
        }

        return new OpCode(OpCodeType.Opcode, instruction);
    }

    protected boolean isRegisterIdentifier(String identifier) {
        if (identifier.startsWith("r")) {
            return true;
        }

        return false;
    }

    protected short getRegisterNumber(String str) {
        String register = str.substring(1);

        try {
            return (short) Integer.parseInt(register);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    protected short getNumber(String number) throws Exception {
        short value;
        int base;
        String register, numberValue;

        // if it's a register
        if (number.startsWith("r")) {
            register = number.substring(1);

            try {
                value = (short) Integer.parseInt(register);
            } catch (NumberFormatException ex) {
                throw new Exception("Invalid register identifier " + register);
            }

            if (value >= Machine.REGISTER_NUMBER) {
                throw new Exception("Register max identifier exceeded" + register);
            }

            return value;
        } else if (number.startsWith("0x")) {
            base = 16;
            numberValue = number.substring(2);
        } else if (number.startsWith("0b")) {
            base = 2;
            numberValue = number.substring(2);
        } else if (number.startsWith("0o")) {
            base = 8;
            numberValue = number.substring(2);
        } else {
            base = 10;
            numberValue = number;
        }

        try {
            value = Short.parseShort(numberValue, base);
        } catch (NumberFormatException ex) {
            throw new Exception("Invalid number format " + number);
        }


        return value;
    }

    protected int buildCompareInstruction(boolean isNumber, byte instNumber, byte instRegister, short number, byte sourceRegister, byte destinationRegister) {
        if (isNumber) {
            // isnn
            return (instNumber << 24) | (sourceRegister << 16) | number;
        } else {
            // isdx
            return (instRegister << 24) | (sourceRegister << 16) | (destinationRegister << 8);
        }
    }

    protected int buildRegisterInstruction(boolean isNumber, byte instNumber, byte instRegister, short number, byte sourceRegister, byte destinationRegister) {
        if (isNumber) {
            // isnn
            return (instNumber << 24) | (sourceRegister << 16) | number;
        } else {
            // isdx
            return (instRegister << 24) | (sourceRegister << 16) | (destinationRegister << 8);
        }
    }

    protected int buildSingleInstruction(boolean isNumber, byte instruction, short number, byte register) {
        if (isNumber) {
            // ixnn
            // this one is for I_PUT, where number is a pixel color
            return (instruction << 24) | (number << 8);
        } else {
            // irxx
            return (instruction << 24) | (register << 16);
        }
    }

    /**
     * Split string into multiple strings
     * @param original      Original string
     * @param separator     Separator string in original string
     * @return              Splitted string array
     */
    protected String[] split(String original, String separator) {
        Vector nodes = new Vector();

        // Parse nodes into vector
        int index = original.indexOf(separator);
        while (index >= 0) {
            nodes.addElement(original.substring(0, index));
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }
        // Get the last node
        nodes.addElement(original);

        // Create splitted string array
        String[] result = new String[nodes.size()];
        if (nodes.size() > 0) {
            for (int loop = 0; loop < nodes.size(); loop++) {
                result[loop] = (String) nodes.elementAt(loop);
            }
        }
        return result;
    }
}
