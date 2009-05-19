/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package repiola;
import gui.Drawable;
import java.util.Random;
/**
 *
 * @author mariano
 */
public class Machine {
    public static final int REGISTER_NUMBER = 8;
    public static final int REGISTER_MAX_NUMBER = 256;
    public static final int MASK_INSTRUCTION = 0xFF000000;
    public static final int MASK_PIXEL_COLOR = 0x00FFFFFF00;
    public static final int MASK_SOURCE_REGISTER = 0x00FF0000;
    public static final int MASK_NUMBER = 0x0000FFFF;
    public static final int MASK_DESTINATION_REGISTER = 0x0000FF00;

    public static final byte I_PUT = 112;
    public static final byte I_RPUT = 113;
    public static final byte I_GET = 115;
    public static final byte I_RANDOM = 63;
    public static final byte I_ADD = 43;
    public static final byte I_RADD = 1;
    public static final byte I_SUB = 45;
    public static final byte I_RSUB = 2;
    public static final byte I_MUL = 42;
    public static final byte I_RMUL = 3;
    public static final byte I_DIV = 47;
    public static final byte I_RDIV = 4;
    public static final byte I_MOD = 37;
    public static final byte I_RMOD = 5;
    public static final byte I_AND = 38;
    public static final byte I_RAND = 6;
    public static final byte I_OR = 124;
    public static final byte I_ROR = 7;
    public static final byte I_XOR = 94;
    public static final byte I_RXOR = 8;
    public static final byte I_NOT = 33;
    public static final byte I_SET = 61;
    public static final byte I_RSET = 9;
    public static final byte I_EQ = 101;
    public static final byte I_REQ = 10;
    public static final byte I_NE = 110;
    public static final byte I_RNE = 11;
    public static final byte I_GT = 62;
    public static final byte I_RGT = 12;
    public static final byte I_GE = 13;
    public static final byte I_RGE = 14;
    public static final byte I_LT = 60;
    public static final byte I_RLT = 15;
    public static final byte I_LE = 16;
    public static final byte I_RLE = 17;
    public static final byte I_JUMP = 106;
    public static final byte I_NOP = 0;

    // instructions that contain register as second byte
    public static final int[] I_SOURCE_REGISTER = {I_RANDOM, I_GET, I_ADD, I_SUB, I_MUL, I_DIV, I_MOD, I_AND, I_OR, I_XOR, I_NOT, I_SET, I_EQ, I_NE, I_GT, I_GE, I_LT, I_LE};
    // instructions that contain a pixel color xppx
    public static final int[] I_PIXEL = {I_PUT, I_RPUT};
    // instructions that contain a number xxnn
    public static final int[] I_NUMBER = {I_ADD, I_SUB, I_MUL, I_DIV, I_MOD, I_AND, I_OR, I_XOR, I_SET, I_EQ, I_NE, I_GT, I_GE, I_LT, I_LE};
    // instructions that contain a number xxnn
    public static final int[] I_DESTINATION_REGISTER = {I_RADD, I_RSUB, I_RMUL, I_RDIV, I_RMOD, I_RAND, I_ROR, I_RXOR, I_RSET, I_REQ, I_RNE, I_RGT, I_RGE, I_RLT, I_RLE};
    // instructions that are followed by a jump address
    public static final int[] I_LABEL = {I_EQ, I_NE, I_GT, I_GE, I_LT, I_LE, I_JUMP};

    private int instructionPointer;
    private boolean inJump;
    private boolean jump;
    private int []registers;
    private Random generator;
    private Drawable screen;

    public Machine(Drawable screen) {
        registers = new int[REGISTER_NUMBER];
        generator = new Random();
        this.screen = screen;
    }

    public int[] getRegisters() {
        return registers;
    }

    public void setRegisters(int[] registers) {
        this.registers = registers;
    }

    public int getX() {
        return registers[0];
    }

    public void setX(int x) {
        registers[0] = x;
    }

    public int getY() {
        return registers[1];
    }

    public void setY(int y) {
        registers[1] = y;
    }

    public boolean isInJump() {
        return inJump;
    }

    public void setInJump(boolean inJump) {
        this.inJump = inJump;
    }

    public int getInstructionPointer() {
        return instructionPointer;
    }

    public void setInstructionPointer(int instructionPointer) {
        this.instructionPointer = instructionPointer;
    }

    public boolean isJump() {
        return jump;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void execute(int instruction) throws Exception
    {
        byte instr, register=0, dest_register=0;
        short pixel=0, number=0;

        if(inJump)
        {
            if(jump)
            {
                instructionPointer = instruction;
            }

            inJump = false;
            return;
        }

        instr = (byte)((instruction & MASK_INSTRUCTION) >> 24);
        if(isSourceRegisterInstruction(instr))
        {
            register = (byte)((instruction & MASK_SOURCE_REGISTER) >> 16);

            if(register >= Machine.REGISTER_NUMBER)
            {
                throw new Exception("Invalid register number");
            }
        }

        if(isDestinationRegisterInstruction(instr))
        {
            dest_register = (byte)((instruction & MASK_DESTINATION_REGISTER) >> 8);

            if(dest_register >= Machine.REGISTER_NUMBER)
            {
                throw new Exception("Invalid register number");
            }
        }

        if(isPixelInstruction(instr))
        {
            if(instr == I_PUT)
            {
                pixel = (short)((instruction & MASK_PIXEL_COLOR) >> 8);
            }
            else if(instr == I_RPUT)
            {
                register = (byte)((instruction & MASK_SOURCE_REGISTER) >> 16);
            }
        }

        if(isNumberInstruction(instr))
        {
            number = (short)(instruction & MASK_NUMBER);
        }

        if(isJumpInstruction(instr))
        {
            inJump = true;
        }

        switch(instr)
        {
            case I_PUT: setPixel(pixel);break;
            case I_RPUT: setPixel(registers[register]);break;
            case I_GET: getPixel(register);break; // s
            case I_RANDOM: registers[register] = Math.abs((short)generator.nextInt(0xFFFF));break;
            case I_ADD: registers[register] += number;break;
            case I_RADD: registers[register] += registers[dest_register];break;
            case I_SUB: registers[register] -= number;break;
            case I_RSUB: registers[register] -= registers[dest_register];break;
            case I_MUL: registers[register] *= number;break;
            case I_RMUL: registers[register] *= registers[dest_register];break;
            case I_DIV: registers[register] /= number;break;
            case I_RDIV: registers[register] /= registers[dest_register];break;
            case I_MOD: registers[register] %= number;break;
            case I_RMOD: registers[register] %= registers[dest_register];break;
            case I_AND: registers[register] &= number;break;
            case I_RAND: registers[register] &= registers[dest_register];break;
            case I_OR: registers[register] |= number;break;
            case I_ROR: registers[register] |= registers[dest_register];break;
            case I_XOR: registers[register] ^= number;break;
            case I_RXOR: registers[register] ^= registers[dest_register];break;
            case I_NOT: registers[register] = ~registers[register];break;
            case I_SET: registers[register] = number;break; // =
            case I_RSET: registers[register] = registers[dest_register];break; // =
            case I_EQ: setJump(registers[register] == number);break;
            case I_REQ: setJump(registers[register] == registers[dest_register]);break;
            case I_NE: setJump(registers[register] != number);break;
            case I_RNE: setJump(registers[register] != registers[dest_register]);break;
            case I_GT: setJump(registers[register] > number);break;
            case I_RGT: setJump(registers[register] > registers[dest_register]);break;
            case I_GE: setJump(registers[register] >= number);break;
            case I_RGE: setJump(registers[register] >= registers[dest_register]);break;
            case I_LT: setJump(registers[register] < number);break;
            case I_RLT: setJump(registers[register] < registers[dest_register]);break;
            case I_LE: setJump(registers[register] <= number);break;
            case I_RLE: setJump(registers[register] <= registers[dest_register]);break;
            case I_JUMP: setJump(true);break;
            case I_NOP: ;break;

            default: throw new Exception("Invalid instruction " + instr);
        }
    }

    public boolean isSourceRegisterInstruction(int instruction)
    {
        return inArray(I_SOURCE_REGISTER, instruction);
    }

    public boolean isDestinationRegisterInstruction(int instruction)
    {
        return inArray(I_DESTINATION_REGISTER, instruction);
    }

    public boolean isNumberInstruction(int instruction)
    {
        return inArray(I_NUMBER, instruction);
    }

    public boolean isPixelInstruction(int instruction)
    {
        return inArray(I_PIXEL, instruction);
    }

    public boolean isJumpInstruction(int instruction)
    {
        return inArray(I_LABEL, instruction);
    }

    private boolean inArray(int[] array, int number)
    {
        int value;
        for(int i = 0; i < array.length; i++)
        {
            value = array[i];
            if(value == number)
            {
                return true;
            }
        }

        return false;
    }

    private int getPixel(short register)
    {
        int color = screen.getPixel(getX(), getY());

        registers[register] = color;
        return color;
    }

    private void setPixel(int color)
    {
        screen.setPixel(getX(), getY(), color);
    }

    //@Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("x:" + getX() + " y:" + getY() + " \n");

        for(int i = 0; i < Machine.REGISTER_NUMBER; i++)
        {
            sb.append("r" + i + ": " + registers[i] + " ");
        }

        return sb.toString();
    }

    public String explainInstruction(int instruction)
    {
        byte instr = (byte)((instruction & Machine.MASK_INSTRUCTION) >> 24);

        return "Instruction: " + instr;
    }

    public void clear()
    {
        for(int i = 0; i < Machine.REGISTER_NUMBER; i++)
        {
            registers[i] = 0;
        }
    }

}
