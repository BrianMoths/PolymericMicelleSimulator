/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter;

/**
 *
 * @author bmoths
 */
public class BeadRectangle {

    public double left, right, top, bottom;

    public BeadRectangle() {
    }

    public BeadRectangle(BeadRectangle beadRectangle) {
        left = beadRectangle.left;
        right = beadRectangle.right;
        top = beadRectangle.top;
        bottom = beadRectangle.bottom;
    }

    public BeadRectangle(double left, double right, double top, double bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }
}
