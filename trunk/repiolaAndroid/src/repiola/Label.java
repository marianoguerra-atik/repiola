/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package repiola;

/**
 *
 * @author mariano
 */
public class Label {
    private String name;
    private int line;

    public Label() {
    }

    public Label(String name, int line) {
        this.name = name;
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
}
