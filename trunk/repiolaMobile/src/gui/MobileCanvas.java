/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;
import javax.microedition.lcdui.*;
import repiola.Compiler;
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
    private Compiler compiler;
    private int rgbData[] = new int[1];
    private Graphics graphics;

    public MobileCanvas() {
        image = Image.createImage(200, 200);
        graphics = image.getGraphics();
        machine = new Machine(this);
    }

    public void newImage(int width, int height) {
        image = Image.createImage(width, height);
        graphics = image.getGraphics();
    }

    public void clear() {
        clear(0);
    }


    protected void paint(Graphics g) {
        if(changed)
        {
            g.drawImage(image, 0, 0, Graphics.TOP|Graphics.LEFT);
            changed = false;
        }

    }

    public int getPixel(int x, int y) {
        int red, green, blue, color, val;

        image.getRGB(rgbData, 0, 1, x, y, 1, 1);
        color = rgbData[0];

        color = color & 0x00FFFFFF;
        blue = (color & 0xFF) >> 3;
        green = ((color >> 8) & 0xFF) >> 3;
        red = ((color >> 16) & 0xFF) >> 3;

        val = (red << 10) | (green << 5) | blue;
        return val;
    }

    public void setPixel(int x, int y, int color) {
        changed = true;

        int red, green, blue;
        red = ((color & 31744) >> 10) << 3;
        green = ((color & 992) >> 5) << 3;
        blue = (color & 31) << 3;

        if(red != 0) {
            red |= 7;
        }

        if(green != 0) {
            green |= 7;
        }

        if(blue != 0) {
            blue |= 7;
        }

        color = red << 16;
        color |= green << 8;
        color |= blue;

        graphics.setColor(color);
        graphics.drawLine(x, y, x, y);
        this.repaint(x, y, 1, 1);
        this.serviceRepaints();
    }

     public int getColor(int color) {
        int red, green, blue;
        red = ((color & 31744) >> 10) << 3;
        green = ((color & 992) >> 5) << 3;
        blue = (color & 31) << 3;

        if(red != 0) {
            red |= 7;
        }

        if(green != 0) {
            green |= 7;
        }

        if(blue != 0) {
            blue |= 7;
        }

        color = red << 16;
        color |= green << 8;
        color |= blue;

        return color;
    }

    public String getProgram() {
        return program;
    }

    public void clear(int color) {
        changed = true;
        
        graphics.setColor(getColor(color));
        graphics.fillRect(0, 0, image.getWidth() - 1, image.getHeight() - 1);
        this.repaint();
        this.serviceRepaints();
    }

    public void setProgram(String program) throws Exception{
        compiler = new Compiler();
        int[] instructions;
        boolean keepRunning = true;
        this.program = program;

        machine.clear();
        clear();


        instructions = compiler.compile(program);

        machine.clear();

        while (keepRunning) {
            keepRunning = machine.step(instructions);
        }
    }


}
