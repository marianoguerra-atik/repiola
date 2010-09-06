/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;
import repiola.Interpreter;
import repiola.Machine;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 * @author Mateo
 */
public class MobileCanvas extends View implements Drawable {
    private Bitmap image;
    private String program;
    private Machine machine;
    private Interpreter interpreter;
	int xWidth = 0;
	int yHeight = 0;
	
    public MobileCanvas(Context context) {
    	super(context);
        newImage(getWidth(), getHeight());
    	machine = new Machine(this);
    }
    
    public MobileCanvas(Context context, AttributeSet attr) {
    	super(context, attr);
    	newImage(getWidth(), getHeight());
    	machine = new Machine(this);
    }

    public void newImage(int width, int height) {
    	if(xWidth == yHeight && yHeight == 0) {
    		xWidth = width;
    		yHeight = height;
    	}
    	image = Bitmap.createBitmap(xWidth, yHeight, Config.RGB_565);
    	for(int i = 0; i < xWidth; i++) {
    		for(int j = 0; j < yHeight; j++) {
    			image.setPixel(i, j, Color.MAGENTA);
    		}
    	}
    }

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.setBitmap(image);
		super.onDraw(canvas);
	}

    public int getPixel(int x, int y) {
        int red, green, blue, color, val;

        color = image.getPixel(x, y);

        color = color & 0x00FFFFFF;
        blue = (color & 0xFF) >> 3;
        green = ((color >> 8) & 0xFF) >> 3;
        red = ((color >> 16) & 0xFF) >> 3;

        val = (red << 10) | (green << 5) | blue;
        return val;
    }

    public void setPixel(int x, int y, int color) {
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
        
        image.setPixel(x, y, color);
        this.invalidate();
        //this.invalidate(new Rect(x, y, 1, 1));
    }

    public String getProgram() {
        return program;
    }

    public void clear() {
    	ViewGroup layout = ((ViewGroup)this.getParent());
    	newImage(layout.getWidth(), layout.getHeight());
    }

    public void setProgram(String program) {
        this.program = program;
        String line = null;
        int opcode;

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
        clear();

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
