/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;
import java.awt.*;
import java.awt.image.*;

/**
 *
 * @author mariano
 */
public class SwingCanvas extends Canvas implements Drawable{
    private BufferedImage image;
    
    public SwingCanvas() {
        super();
        image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
    }
    
    public int getPixel(int x, int y) {
        return image.getRGB(x, y);
    }

    @Override
    public void paint(Graphics g)
    {
        this.getGraphics().drawImage(image, 0, 0, this);
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
        image.setRGB(x, y, color);
        this.repaint(x, y, 1, 1);
    }

    public void clear()
    {
        Graphics graph = image.getGraphics();
        graph.setColor(Color.BLACK);
        graph.fillRect(0, 0, this.getWidth(), this.getHeight());
        this.repaint();
    }

}