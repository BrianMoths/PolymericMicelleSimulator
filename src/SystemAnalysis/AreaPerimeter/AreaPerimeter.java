/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter;

/**
 *
 * @author bmoths
 */
public class AreaPerimeter {

    public double area;
    public double perimeter;

    public AreaPerimeter() {
        this(0, 0);
    }

    public AreaPerimeter(double area, double perimeter) {
        this.area = area;
        this.perimeter = perimeter;
    }

    public void incrementBy(AreaPerimeter areaPerimeter) {
        area += areaPerimeter.area;
        perimeter += areaPerimeter.perimeter;
    }

}
