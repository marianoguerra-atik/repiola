/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;
import javax.microedition.lcdui.*;
import repiola.Interpreter;
import repiola.Machine;

/**
 *
 * @author mariano
 */
public class MobileCanvas extends Canvas implements Drawable{
    private Image image;
    private boolean changed;
    private String program;
    private Machine machine;
    private Interpreter interpreter;

    public MobileCanvas() {
        image = Image.createImage(200, 200);
        machine = new Machine(this);
    }

    public void newImage(int width, int height) {
        image = Image.createImage(width, height);
    }


    protected void paint(Graphics g) {
        if(changed)
        {
            g.drawImage(image, 0, 0, Graphics.TOP|Graphics.LEFT);
            changed = false;
        }

    }

    public int getPixel(int x, int y) {
        return this.getPixel(x, y);
    }

    public void setPixel(int x, int y, int color) {
        changed = true;
        Graphics graphics = image.getGraphics();
        graphics.setColor(color);
        graphics.drawLine(x, y, x, y);
        this.repaint(x, y, 1, 1);
    }

    public String getProgram() {
        return program;
    }

    public void clear() {
        image.getGraphics().fillRect(0, 0, this.getWidth(), this.getHeight());
    }

    public void setProgram(String program) {
        this.program = program;
        String line = null;
        int opcode;
        clear();

        try
        {
            interpreter = new Interpreter(program, machine);
            line = interpreter.step();
        }
        catch(Exception ex)
        {
            System.err.println("\nError0 line: " + line + "\n\t" + ex.getMessage());
            return;
        }

        machine.clear();

        while(line != null)
        {
            opcode = interpreter.getCurrentOpcode();

            try
            {
                if(opcode == -1) {
                    opcode = interpreter.parseLine(line);
                    interpreter.setCurrentOpcode(opcode);
                }
            }
            catch(Exception ex)
            {
                System.err.println("\nError1 line: " + line + "\n\t" + ex.getMessage());
                break;
            }

            try
            {
                machine.execute(opcode);
            }
            catch(Exception ex)
            {
                System.err.println("\nError executing code: " + line + "\n\t" + ex.getMessage());
                break;
            }

            try
            {
                line = interpreter.step();
            }
            catch(Exception ex)
            {
                System.err.println("\nError2 line: " + line + "\n\t" + ex.getMessage());
                break;
            }
        }
    }
}
