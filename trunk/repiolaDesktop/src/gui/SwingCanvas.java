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
        image.setRGB(x, y, color);
        this.repaint(x, y, 1, 1);
    }

    public void clear()
    {

        getGraphics().fillRect(0, 0, this.getWidth(), this.getHeight());
    }

}
