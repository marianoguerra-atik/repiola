/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package repiola;

/**
 *
 * @author mariano
 */
public class OpCode {
    public OpCodeType type;
    public String label;
    public int opcode;

    public OpCode(OpCodeType type, int opcode) {
        this(type, opcode, null);
    }

    public OpCode(OpCodeType type, int opcode, String label) {
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
