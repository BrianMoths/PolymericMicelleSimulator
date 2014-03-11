/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.circleareaperimeter;

import SystemAnalysis.AreaPerimeter.circleareaperimeter.Circle;
import SystemAnalysis.GeometryAnalyzer;
import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 *
 * @author bmoths
 */
class CircleIterator implements Iterator<Circle> {

    private final Iterator<Point2D> centerIterator;
    private final Iterator<Double> radiusIterator;

    public CircleIterator(Iterator<Point2D> centerIterator, Iterator<Double> radiusIterator) {
        this.centerIterator = centerIterator;
        this.radiusIterator = radiusIterator;
    }

    public CircleIterator(Iterator<Point2D> centerIterator, double radius) {
        this.centerIterator = centerIterator;
        this.radiusIterator = GeometryAnalyzer.getConstantIterator(radius);
    }

    @Override
    public boolean hasNext() {
        return centerIterator.hasNext() && radiusIterator.hasNext();
    }

    @Override
    public Circle next() {
        return new Circle(centerIterator.next(), radiusIterator.next());
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
