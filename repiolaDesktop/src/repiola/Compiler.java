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
public class Compiler extends Parser {
    protected Hashtable toReplace;

    public int[] compile (String program) throws Exception {
        Vector instructions = new Vector();
        labels = new Hashtable();
        toReplace = new Hashtable();
        lines = split(program, "\n");
        OpCode opcode;
        int address, offset = 0;

        int[] result;

        for (String line: lines) {
            opcode = this.parseLine(line);
            
            if (opcode == null) {
                continue;
            }

            if (opcode.isLabel()) {
                addLabel(opcode.label, offset);
            }
            else if (opcode.isJump()) {
                instructions.add(new Integer(opcode.opcode));
                offset++;
                
                address = getLabelAddress(opcode.label);
                instructions.add(new Integer(address));

                if (address == -1) {
                    addLabelToReplace(opcode.label, offset);
                }
                
                offset++;
            }
            else {
                instructions.add(new Integer(opcode.opcode));
                offset++;
            }
        }

        String key;
        Vector values;
        Integer labelAddress;
        Object[] keys = toReplace.keySet().toArray();

        // replace the addresses that where set to -1 because the label address
        // wasn't available yet
        for (int i = 0; i < keys.length; i++) {
            key = (String)keys[i];
            labelAddress = (Integer)labels.get(key);
            values = (Vector)toReplace.get(key);

            for (int j = 0; j < values.size(); j++) {
                address = ((Integer)values.get(j)).intValue();
                instructions.set(address, labelAddress);
            }
        }

        result = new int[instructions.size()];

        for (int i = 0; i < instructions.size(); i++) {
            result[i] = ((Integer)instructions.get(i)).intValue();
            System.out.println("" + result[i]);
        }

        return result;
    }

    protected int getLabelAddress(String label) {
        if (labels.containsKey(label)) {
            return ((Integer)labels.get(label)).intValue();
        }
        
        return  -1;
    }
    
    protected void addLabelToReplace(String label, int offset) {
        if (!toReplace.containsKey(label)) {
            toReplace.put(label, new Vector());
        }

        ((Vector)toReplace.get(label)).add(new Integer(offset));
    }

    protected void addLabel(String label, int offset) throws Exception{
        if (labels.containsKey(label)) {
            throw new Exception("label already defined in line" + ((Integer)labels.get(label)).toString());
        }

        labels.put(label, new Integer(offset));
    }


}
