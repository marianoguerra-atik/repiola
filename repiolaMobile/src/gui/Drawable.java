/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

/**
 *
 * @author mariano
 */
public interface Drawable {
    int getPixel(int x, int y);
    void setPixel(int x, int y, int color);
}
