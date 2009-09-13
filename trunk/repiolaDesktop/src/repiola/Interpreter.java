/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package repiola;
import java.util.Hashtable;
import java.util.Vector;
/**
 *
 * @author mariano
 */
public class Interpreter {
    private String[] lines;
    // line: label
    private Hashtable labels;
    // line where a jump names a label: label
    private Hashtable jumpLabels;
    private Hashtable opcodes;
    private int currentLine;
    private Machine machine;
    private boolean inJump;
    private String lastLabel;

    private static final String[] commands =  {"put", "#", ":", "jmp",
     "add", "sub", "mul", "div", "mod", "set", "or", "and", "xor", "not", "eq", "ne", "gt", "lt", "ge", "le", "get", ".", "rnd"};
    private static final String[] jumpCommands =  {"jmp", ":"};
    private static final String[] singleCommands =  {"put", "get", "rnd", "not"};
    private static final String[] registerCommands =  {"add", "sub", "mul", "div", "mod", "set", "or", "and", "xor"};
    private static final String[] compareCommands =  {"eq", "ne", "gt", "lt", "ge", "le"};

    public Interpreter(String program, Machine machine){
        lines = split(program, "\n");
        labels = new Hashtable();
        jumpLabels = new Hashtable();
        opcodes = new Hashtable();
        parseLabels();
        this.machine = machine;
    }

    private void parseLabels()
    {
        String label, line;
        for(int i = 0; i < lines.length; i++)
        {
            line = lines[i];

            if(line.startsWith(":"))
            {
                label = line.substring(2);
                labels.put(label, new Label(label, i + 1));
            }
        }
    }

    public String step() throws Exception
    {
        String line;
        if(inJump)
        {
            if(machine.isJump())
            {
                jumpToLabel(lastLabel);
                machine.setInJump(false);
                machine.setJump(false);
                inJump = false;
                line = lines[currentLine];
                currentLine++;
                return line;
            }
            else {
                machine.setInJump(false);
                machine.setJump(false);
                inJump = false;
                line = lines[currentLine];
                currentLine++;
                return line;
            }
        }

        if(currentLine < lines.length)
        {
            line = lines[currentLine];
            currentLine++;
            return line;
        }

        return null;
    }

    public int parseLine(String line) throws Exception
    {
        String []tokens = split(line.trim(), " ");
        int nop = (int)Machine.I_NOP << 24;

        if(tokens.length == 0)
        {
            return nop;
        }

        String command = tokens[0];

        if(command.startsWith("#") || command.equals(".") || command.equals(""))
        {
            return nop;
        }

        if(!this.isStringInArrayBecauseJavaDoesNotHaveInLikePython(Interpreter.commands, command))
        {
            throw new Exception("Invalid command '" + command + "'");
        }

        if(this.isStringInArrayBecauseJavaDoesNotHaveInLikePython(Interpreter.registerCommands, command))
        {
            return parseRegisterCommand(tokens);
        }
        else if(this.isStringInArrayBecauseJavaDoesNotHaveInLikePython(Interpreter.compareCommands, command))
        {
            return parseCompareCommand(tokens);
        }
        else if(this.isStringInArrayBecauseJavaDoesNotHaveInLikePython(Interpreter.singleCommands, command))
        {
            return parseSingleCommand(tokens);
        }
        else if(this.isStringInArrayBecauseJavaDoesNotHaveInLikePython(Interpreter.jumpCommands, command))
        {
            return parseJumpCommand(tokens);
        }
        else
        {
            throw new Exception("Invalid command " + command);
        }
    }

    public boolean isStringInArrayBecauseJavaDoesNotHaveInLikePython(String[] array, String item)
    {
        String value = null;
        for(int i = 0; i < array.length; i++)
        {
            value = array[i];
            if(value.equals(item))
            {
                return true;
            }
        }

        return false;
    }

    private int parseJumpCommand(String[] tokens) throws Exception
    {
        String command, label;

        if(tokens.length != 2)
        {
            throw new Exception("Invalid number of arguments for jump command " + tokens.toString());
        }

        command = tokens[0].toLowerCase();
        label = tokens[1].toLowerCase();

        if(command.equals("jmp"))
        {
            addJumpLabel(label);
            jumpToLabel(label);
        }
        else if(command.equals(":"))
        {
            addLabel(label);
        }
        else throw new Exception("Invalid compare command");

        return (int)(Machine.I_NOP << 24);
    }

    private int parseSingleCommand(String[] tokens) throws Exception
    {
        String dest, command;
        short number=0;
        byte register=0;
        int instruction;
        boolean isNumber =  false;

        if(tokens.length != 2)
        {
            throw new Exception("Invalid number of arguments for single command " + tokens.toString());
        }

        command = tokens[0].toLowerCase();
        dest = tokens[1].toLowerCase();

        if(isRegisterIdentifier(dest))
        {
            register = (byte)getNumber(dest);
        }
        else
        {
            isNumber = true;
            number = getNumber(dest);
        }

        if(command.equals("put") && isNumber)instruction = buildSingleInstruction(isNumber, Machine.I_PUT, number, (byte)0);
        else if(command.equals("put") && !isNumber)instruction = buildSingleInstruction(isNumber, Machine.I_RPUT, (byte)0, register);
        else if(command.equals("get") && !isNumber)instruction = buildSingleInstruction(isNumber, Machine.I_GET, (byte)0, register);
        else if(command.equals("not") && !isNumber)instruction = buildSingleInstruction(isNumber, Machine.I_NOT, (byte)0, register);
        else if(command.equals("rnd") && !isNumber)instruction = buildSingleInstruction(isNumber, Machine.I_RANDOM, (byte)0, register);
        else throw new Exception("Invalid compare command");

        return instruction;
    }

    private int parseCompareCommand(String[] tokens) throws Exception
    {
        String dest, register, command, label;
        short number=0;
        byte destinationRegister=0, sourceRegister=0;
        int instruction;
        boolean isNumber =  false;

        if(tokens.length != 4)
        {
            throw new Exception("Invalid number of arguments for compare command " + tokens.toString());
        }

        command = tokens[0].toLowerCase();
        register = tokens[1].toLowerCase();
        dest = tokens[2].toLowerCase();
        label = tokens[3].toLowerCase();
        addJumpLabel(label);
        sourceRegister = (byte)getNumber(register);
        if(isRegisterIdentifier(dest))
        {
            destinationRegister = (byte)getNumber(dest);
        }
        else
        {
            isNumber = true;
            number = getNumber(dest);
        }

        if(command.equals("eq"))instruction = buildCompareInstruction(isNumber, Machine.I_EQ, Machine.I_REQ, number, sourceRegister, destinationRegister);
        else if(command.equals("ne"))instruction = buildCompareInstruction(isNumber, Machine.I_NE, Machine.I_RNE, number, sourceRegister, destinationRegister);
        else if(command.equals("gt"))instruction = buildCompareInstruction(isNumber, Machine.I_GT, Machine.I_RGT, number, sourceRegister, destinationRegister);
        else if(command.equals("lt"))instruction = buildCompareInstruction(isNumber, Machine.I_LT, Machine.I_RLT, number, sourceRegister, destinationRegister);
        else if(command.equals("ge"))instruction = buildCompareInstruction(isNumber, Machine.I_GE, Machine.I_RGE, number, sourceRegister, destinationRegister);
        else if(command.equals("le"))instruction = buildCompareInstruction(isNumber, Machine.I_LE, Machine.I_RLE, number, sourceRegister, destinationRegister);
        else throw new Exception("Invalid compare command");

        inJump = true;
        lastLabel = label;
        return instruction;
    }

    private int parseRegisterCommand(String[] tokens) throws Exception
    {
        String dest, register, command;
        short number=0;
        byte destinationRegister=0, sourceRegister=0;
        int instruction;
        boolean isNumber =  false;

        if(tokens.length != 3)
        {
            throw new Exception("Invalid number of arguments for register command " + tokens.toString());
        }

        command = tokens[0].toLowerCase();
        register = tokens[1].toLowerCase();
        dest = tokens[2].toLowerCase();

        sourceRegister = (byte)getNumber(register);
        if(isRegisterIdentifier(dest))
        {
            destinationRegister = (byte)getNumber(dest);
        }
        else
        {
            isNumber = true;
            number = getNumber(dest);
        }


        if(command.equals("add"))instruction = buildRegisterInstruction(isNumber, Machine.I_ADD, Machine.I_RADD, number, sourceRegister, destinationRegister);
        else if(command.equals("sub"))instruction = buildRegisterInstruction(isNumber, Machine.I_SUB, Machine.I_RSUB, number, sourceRegister, destinationRegister);
        else if(command.equals("mul"))instruction = buildRegisterInstruction(isNumber, Machine.I_MUL, Machine.I_RMUL, number, sourceRegister, destinationRegister);
        else if(command.equals("div"))instruction = buildRegisterInstruction(isNumber, Machine.I_DIV, Machine.I_RDIV, number, sourceRegister, destinationRegister);
        else if(command.equals("mod"))instruction = buildRegisterInstruction(isNumber, Machine.I_MOD, Machine.I_RMOD, number, sourceRegister, destinationRegister);
        else if(command.equals("and"))instruction = buildRegisterInstruction(isNumber, Machine.I_AND, Machine.I_RAND, number, sourceRegister, destinationRegister);
        else if(command.equals("or"))instruction = buildRegisterInstruction(isNumber, Machine.I_OR, Machine.I_ROR, number, sourceRegister, destinationRegister);
        else if(command.equals("xor"))instruction = buildRegisterInstruction(isNumber, Machine.I_XOR, Machine.I_RXOR, number, sourceRegister, destinationRegister);
        else if(command.equals("set"))instruction = buildRegisterInstruction(isNumber, Machine.I_SET, Machine.I_RSET, number, sourceRegister, destinationRegister);
        else throw new Exception("Invalid compare command");

        return instruction;
    }

    private boolean isRegisterIdentifier(String identifier)
    {
        if(identifier.startsWith("r"))
        {
            return true;
        }

        return false;
    }

    private short getNumber(String number) throws Exception
    {
        short value;
        int base;
        String register, numberValue;

        // if it's a register
        if(number.startsWith("r"))
        {
            register = number.substring(1);
            try
            {
                value = (short)Integer.parseInt(register);
            }
            catch(NumberFormatException ex)
            {
                throw new Exception("Invalid register identifier " + register);
            }

            if(value >= Machine.REGISTER_NUMBER)
            {
                throw new Exception("Register max identifier exceeded" + register);
            }

            return value;
        }
        else if(number.startsWith("0x"))
        {
            base = 16;
            numberValue = number.substring(2);
        }
        else if(number.startsWith("0b"))
        {
            base = 2;
            numberValue = number.substring(2);
        }
        else if(number.startsWith("0o"))
        {
            base = 8;
            numberValue = number.substring(2);
        }
        else
        {
            base = 10;
            numberValue = number;
        }

        try
        {
            value = Short.parseShort(numberValue, base);
        }
        catch(NumberFormatException ex)
        {
            throw new Exception("Invalid number format " + number);
        }
        

        return value;
    }

    private int buildCompareInstruction(boolean isNumber, byte instNumber, byte instRegister, short number, byte sourceRegister, byte destinationRegister)
    {
        if(isNumber)
        {
            // isnn
            return (instNumber << 24) | (sourceRegister << 16) | number;
        }
        else
        {
            // isdx
            return (instRegister << 24) | (sourceRegister << 16) | (destinationRegister << 8);
        }
    }

    private int buildRegisterInstruction(boolean isNumber, byte instNumber, byte instRegister, short number, byte sourceRegister, byte destinationRegister)
    {
        if(isNumber)
        {
            // isnn
            return (instNumber << 24) | (sourceRegister << 16) | number;
        }
        else
        {
            // isdx
            return (instRegister << 24) | (sourceRegister << 16) | (destinationRegister << 8);
        }
    }

    private int buildSingleInstruction(boolean isNumber, byte instruction, short number, byte register)
    {
        if(isNumber)
        {
            // ixnn
            // this one is for I_PUT, where number is a pixel color
            return (instruction << 24) | (number << 8);
        }
        else
        {
            // irxx
            return (instruction << 24) | (register << 16);
        }
    }

    private void addLabel(String label) throws Exception
    {
        Label l;
        if(labels.containsKey(label))
        {
            l = ((Label)labels.get(label));
            if(l.getLine() != currentLine)
            {
                throw new Exception("Duplacated label " + label + " " + currentLine + " and " + l.getLine());
            }
        }
        else
        {
            labels.put(label, new Label(label, currentLine));
        }
    }

    private void jumpToLabel(String label) throws Exception
    {
        if(labels.containsKey(label))
        {
            currentLine = ((Label)labels.get(label)).getLine();
        }
        else
        {
            throw new Exception("Label not found" + label + "\n");
        }
    }

    /**
     * Split string into multiple strings
     * @param original      Original string
     * @param separator     Separator string in original string
     * @return              Splitted string array
     */
    private String[] split(String original, String separator) {
        Vector nodes = new Vector();

        // Parse nodes into vector
        int index = original.indexOf(separator);
        while(index>=0) {
            nodes.addElement( original.substring(0, index) );
            original = original.substring(index+separator.length());
            index = original.indexOf(separator);
        }
        // Get the last node
        nodes.addElement( original );

        // Create splitted string array
        String[] result = new String[ nodes.size() ];
        if( nodes.size()>0 ) {
            for(int loop=0; loop<nodes.size(); loop++)
            result[loop] = (String)nodes.elementAt(loop);
        }
        return result;
    }

    public int getOpcode(int line) {
        Integer iline = new Integer(line);
        if(opcodes.containsKey(iline)) {
            if(jumpLabels.containsKey(iline)) {
                lastLabel = (String)jumpLabels.get(iline);
                inJump = true;
            }
            return ((Integer)opcodes.get(iline)).intValue();
        }

        return  -1;
    }

    public int getCurrentOpcode() {
        return getOpcode(getCurrentLine());
    }

    public void setOpcode(int line, int opcode) {
        opcodes.put(new Integer(line), new Integer(opcode));
    }

    public void setCurrentOpcode(int opcode) {
        opcodes.put(new Integer(getCurrentLine()), new Integer(opcode));
    }

    private void addJumpLabel(String label) {
        Integer cl = new Integer(getCurrentLine());
        if(!jumpLabels.containsKey(cl)) {
            jumpLabels.put(cl, label);
        }
    }

    public int getCurrentLine() {
        return currentLine - 1;
    }

}
