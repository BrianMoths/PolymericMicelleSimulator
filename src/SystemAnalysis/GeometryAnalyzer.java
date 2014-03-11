/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import SystemAnalysis.AreaPerimeter.circleareaperimeter.CircleIterable;
import SystemAnalysis.AreaPerimeter.circleareaperimeter.Circle;
import SystemAnalysis.AreaPerimeter.AreaPerimeter;
import SystemAnalysis.AreaPerimeter.circleareaperimeter.CircleAreaFinder;
import SystemAnalysis.AreaPerimeter.circleareaperimeter.CircleAreaPerimeterFinder;
import SystemAnalysis.AreaPerimeter.circleareaperimeter.PowerDiagramEvaluator;
import SystemAnalysis.AreaPerimeter.circleareaperimeter.WrappedCircleIterable;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.BeadRectangle;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.Interval;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.IntervalListEndpoints;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.OverlappingIntervalLengthFinder;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.OverlappingRectangleAreaFinder;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.OverlappingRectangleAreaPerimeterFinder;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.diagram.PowerDiagram;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;

/**
 *
 * @author bmoths
 */
public class GeometryAnalyzer {

    static public double findAreaOfRectangles(List<BeadRectangle> beadRectangles) {
        OverlappingRectangleAreaFinder overlappingRectangleAreaFinder = new OverlappingRectangleAreaFinder();
        return overlappingRectangleAreaFinder.compute(beadRectangles);
    }

    public static AreaPerimeter findAreaAndPerimeterOfRectangles(List<BeadRectangle> beadRectangles) {
        OverlappingRectangleAreaPerimeterFinder overlappingRectangleAreaPerimeterFinder = new OverlappingRectangleAreaPerimeterFinder();
        return overlappingRectangleAreaPerimeterFinder.compute(beadRectangles);
    }

    private static <T> Iterable<T> getConstantIterable(final T t) {
        return new Iterable<T>() {
            private Iterator<T> constantIterator = getConstantIterator(t);

            @Override
            public Iterator<T> iterator() {
                return constantIterator;
            }

        };
    }

    public static <T> Iterator<T> getConstantIterator(final T t) {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public T next() {
                return t;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        };
    }

    public static double findAreaOfCircles(Iterable<Point2D> centers, Iterable<Double> radii, Rectangle2D boundaryRectangle) {
        final Iterable<Circle> circleIterator = new CircleIterable(centers, radii);
        return CircleAreaFinder.findAreaOfCircles(circleIterator, boundaryRectangle);
    }

    public static double findAreaOfCirclesWithRadius(Iterable<Point2D> centers, final double radius, Rectangle2D boundaryRectangle) {
        final Iterable<Double> radii = getConstantIterable(radius);
        return findAreaOfCircles(centers, radii, boundaryRectangle);
    }

    public static double findAreaOfCirclesWithRadiusPeriodic(Iterable<Point2D> centers, double radius, Rectangle2D boundaryRectangle) {
        final Iterable<Double> radii = getConstantIterable(radius);
        return findAreaOfCirclesPeriodic(centers, radii, boundaryRectangle);
    }

    public static double findAreaOfCirclesPeriodic(Iterable<Point2D> centers, Iterable<Double> radii, Rectangle2D boundaryRectangle) {
        final Iterable<Circle> circles = new CircleIterable(centers, radii);
        return CircleAreaFinder.findAreaOfCirclesPeriodic(circles, boundaryRectangle);
    }

    static public AreaPerimeter findAreaPerimeterOfCircles(Iterable<Point2D> centers, Iterable<Double> radii, Rectangle2D boundaryRectangle) {
        final Iterable<Circle> circles = new CircleIterable(centers, radii);
        return CircleAreaPerimeterFinder.findAreaPerimeterOfCircles(circles, boundaryRectangle);
    }

    static public AreaPerimeter findAreaPerimeterOfCirclesWithRadius(Iterable<Point2D> centers, double radius, Rectangle2D boundaryRectangle) {
        final Iterable<Double> radii = getConstantIterable(radius);
        return findAreaPerimeterOfCircles(centers, radii, boundaryRectangle);
    }

    static public AreaPerimeter findAreaPerimeterOfCirclesPeriodic(Iterable<Point2D> centers, Iterable<Double> radii, Rectangle2D boundaryRectangle) {
        final Iterable<Circle> circles = new CircleIterable(centers, radii);
        return CircleAreaPerimeterFinder.findAreaPerimeterOfCirclesPeriodic(circles, boundaryRectangle);
    }

    static public AreaPerimeter findAreaPerimeterOfCirclesWithRadiusPeriodic(Iterable<Point2D> centers, double radius, Rectangle2D boundaryRectangle) {
        final Iterable<Double> radii = getConstantIterable(radius);
        return findAreaPerimeterOfCirclesPeriodic(centers, radii, boundaryRectangle);
    }

}
