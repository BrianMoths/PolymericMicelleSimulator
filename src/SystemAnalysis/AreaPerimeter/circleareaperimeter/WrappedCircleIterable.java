/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.circleareaperimeter;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

/**
 *
 * @author bmoths
 */
public class WrappedCircleIterable implements Iterable<Circle> {

    private final Iterable<Circle> circles;
    private final Rectangle2D boundaryRectangle;

    public WrappedCircleIterable(Iterable<Circle> circles, Rectangle2D boundaryRectangle) {
        this.circles = circles;
        this.boundaryRectangle = boundaryRectangle;
    }

    public WrappedCircleIterable(Iterable<Point2D> centerIterable, Iterable<Double> radiusIterable, Rectangle2D boundaryRectangle) {
        this(new CircleIterable(centerIterable, radiusIterable), boundaryRectangle);
    }

    public Iterator<Circle> iterator() {
        return new WrappedCircleIterator(circles.iterator(), boundaryRectangle);
    }

}
