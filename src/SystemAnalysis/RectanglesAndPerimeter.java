/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import java.util.List;

/**
 *
 * @author bmoths
 */
public class RectanglesAndPerimeter {

    public List<BeadRectangle> beadRectangles;
    public double gluedPerimeter;

    public RectanglesAndPerimeter(List<BeadRectangle> beadRectangles, double gluedPerimeter) {
        this.beadRectangles = beadRectangles;
        this.gluedPerimeter = gluedPerimeter;
    }
}
