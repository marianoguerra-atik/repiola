package repiola;

/**
 *
 * @author mariano
 */
public class OpCode {

    public int type;
    public String label;
    public int opcode;

    public OpCode(int type, int opcode) {
        this(type, opcode, null);
    }

    public OpCode(int type, int opcode, String label) {
        this.type = type;
        this.opcode = opcode;
        this.label = label;
    }

    public boolean isJump() {
        return (type == OpCodeType.Jump || label != null);
    }

    public boolean isLabel() {
        return type == OpCodeType.Label;
    }
}
