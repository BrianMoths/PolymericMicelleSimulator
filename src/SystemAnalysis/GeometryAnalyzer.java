/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import SystemAnalysis.AreaPerimeter.BeadRectangle;
import SystemAnalysis.AreaPerimeter.IntervalListEndpoints;
import SystemAnalysis.AreaPerimeter.LengthAndEdgeFinder;
import SystemAnalysis.AreaPerimeter.OverlappingIntervalLengthFinder;
import java.awt.Rectangle;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
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

    static public class AreaPerimeter {

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

    private static class Circle {

        private final Point2D center;
        private final Double radius;

        public Circle(Point2D center, Double radius) {
            this.center = center;
            this.radius = radius;
        }

        public Point2D getCenter() {
            return center;
        }

        public Double getRadius() {
            return radius;
        }

        public double getCenterX() {
            return center.getX();
        }

        public double getCenterY() {
            return center.getY();
        }

    }

    private static class CircleIterator implements Iterator<Circle> {

        private final Iterator<Point2D> centerIterator;
        private final Iterator<Double> radiusIterator;

        public CircleIterator(Iterator<Point2D> centerIterator, Iterator<Double> radiusIterator) {
            this.centerIterator = centerIterator;
            this.radiusIterator = radiusIterator;
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

    static private class WrappedCircleIterator implements Iterator<Circle> {

        private final Iterator<Circle> circleIterator;
        private final Rectangle2D boundaryRectangle;
        private boolean hasNext;
        private Circle lastCircle;
        private boolean needsReflectionOverVertical;
        private boolean needsReflectionOverHorizontal;
        private boolean needsReflectionOverBoth;

        public WrappedCircleIterator(Iterator<Circle> circleIterator, Rectangle2D boundaryRectangle) {
            this.circleIterator = circleIterator;
            this.boundaryRectangle = boundaryRectangle;
            needsReflectionOverHorizontal = false;
            needsReflectionOverVertical = false;
            needsReflectionOverBoth = false;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public Circle next() {
            if (!(needsReflectionOverHorizontal && needsReflectionOverBoth && needsReflectionOverVertical)) {
                lastCircle = circleIterator.next();
                computeNeedsReflection();
                return lastCircle;
            } else if (needsReflectionOverVertical) {
                needsReflectionOverVertical = false;
                return reflectOverVertical(lastCircle);
            } else if (needsReflectionOverHorizontal) {
                needsReflectionOverHorizontal = false;
                return reflectOverHorizontal(lastCircle);
            } else if (needsReflectionOverBoth) {
                needsReflectionOverBoth = false;
                return reflectOverHorizontal(reflectOverVertical(lastCircle));
            }
            throw new AssertionError();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private void computeNeedsReflection() {
            needsReflectionOverHorizontal = (lastCircle.getCenterY() - lastCircle.getRadius() < boundaryRectangle.getMinY()) || (lastCircle.getCenterY() + lastCircle.getRadius() > boundaryRectangle.getMaxY());
            needsReflectionOverVertical = (lastCircle.getCenterX() - lastCircle.getRadius() < boundaryRectangle.getMinX()) || (lastCircle.getCenterX() + lastCircle.getRadius() > boundaryRectangle.getMaxX());
            needsReflectionOverBoth = needsReflectionOverHorizontal && needsReflectionOverVertical;
        }

        private Circle reflectOverVertical(Circle lastCircle) {
            if (lastCircle.getCenterX() - lastCircle.getRadius() < boundaryRectangle.getMinX()) {
                return new Circle(new Point2D.Double(lastCircle.getCenterX() + boundaryRectangle.getWidth(), lastCircle.getCenterY()), lastCircle.radius);
            } else if (lastCircle.getCenterX() + lastCircle.getRadius() > boundaryRectangle.getMaxX()) {
                return new Circle(new Point2D.Double(lastCircle.getCenterX() - boundaryRectangle.getWidth(), lastCircle.getCenterY()), lastCircle.radius);
            }
            throw new AssertionError("reflect over verticle true, but reflection over vertical not needed");
        }

        private Circle reflectOverHorizontal(Circle lastCircle) {
            if (lastCircle.getCenterY() - lastCircle.getRadius() < boundaryRectangle.getMinY()) {
                return new Circle(new Point2D.Double(lastCircle.getCenterX(), lastCircle.getCenterY() + boundaryRectangle.getHeight()), lastCircle.radius);
            } else if (lastCircle.getCenterY() + lastCircle.getRadius() > boundaryRectangle.getMaxY()) {
                return new Circle(new Point2D.Double(lastCircle.getCenterX(), lastCircle.getCenterY() - boundaryRectangle.getHeight()), lastCircle.radius);
            }
            throw new AssertionError("reflect over horizontal true, but reflection over horizontal not needed");
        }

    }

    static public double findAreaOfRectangles(List<BeadRectangle> beadRectangles) {

        final IntervalListEndpoints horizontalEndpoints = IntervalListEndpoints.endpointsOfHorizontalRectangleEdges(beadRectangles);
        List<Integer> xPermutation = horizontalEndpoints.getPermutation();
        OverlappingIntervalLengthFinder overlappingIntervalLengthFinder = OverlappingIntervalLengthFinder.makeFromBeadRectangles(beadRectangles, xPermutation);

        double area = 0;
        double oldX = 0;

        for (int linearIndex : xPermutation) {
            final double newX = getNewX(beadRectangles, linearIndex);
            final double deltaX = newX - oldX;

            final double coveredVerticalLength = overlappingIntervalLengthFinder.getLength();
            area += deltaX * coveredVerticalLength;

            overlappingIntervalLengthFinder.doNextStep();
            oldX = newX;
        }
        return area;
    }

    public static AreaPerimeter findAreaAndPerimeterOfRectangles(List<BeadRectangle> beadRectangles) {
        final IntervalListEndpoints horizontalEndpoints = IntervalListEndpoints.endpointsOfHorizontalRectangleEdges(beadRectangles);
        List<Integer> xPermutation = horizontalEndpoints.getPermutation();
        LengthAndEdgeFinder lengthAndEdgeFinder = LengthAndEdgeFinder.makeForBeadRectangles(beadRectangles, xPermutation);

        double area = 0;
        double oldCoveredLength = 0;
        double perimeter = 0;
        double oldx = 0;

        for (int linearIndex : xPermutation) {
            final double newX = getNewX(beadRectangles, linearIndex);
            final double deltaX = newX - oldx;

            final double coveredVerticalLength = lengthAndEdgeFinder.getLength();
            area += deltaX * coveredVerticalLength;

            final int numEdges = lengthAndEdgeFinder.getNumEdges();
            perimeter += numEdges * deltaX;
            perimeter += Math.abs(coveredVerticalLength - oldCoveredLength);

            lengthAndEdgeFinder.doNextStep();
            oldCoveredLength = coveredVerticalLength;
            oldx = newX;
        }
        perimeter += oldCoveredLength; //add on perimeter from rightmost vertical edge
        return new AreaPerimeter(area, perimeter);
    }

    private static double getNewX(List<BeadRectangle> beadRectangles, int linearIndex) {
        final int beadIndex = IntervalListEndpoints.getIntervalIndexFromLinearIndex(linearIndex);
        final BeadRectangle beadRectangle = beadRectangles.get(beadIndex);
        return IntervalListEndpoints.getIsStartFromLinearIndex(linearIndex) ? beadRectangle.getLeft() : beadRectangle.getRight();
    }

    public static double findAreaOfCirclesWithRadius(Iterable<Point2D> centers, final double radius, Rectangle2D boundaryRectangle) {
        final Iterator<Double> radiusIterator = new Iterator<Double>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Double next() {
                return radius;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        };
        final Iterable<Double> radii = new Iterable<Double>() {
            @Override
            public Iterator<Double> iterator() {
                return radiusIterator;
            }

        };
        return findAreaOfCircles(centers, radii, boundaryRectangle);
    }

    public static double findAreaOfCircles(Iterable<Point2D> centers, Iterable<Double> radii, Rectangle2D boundaryRectangle) {
        final Iterator<Circle> circleIterator = new CircleIterator(centers.iterator(), radii.iterator());
        return findAreaOfCircles(circleIterator, boundaryRectangle);
    }

    private static double findAreaOfCircles(final Iterator<Circle> circleIterator, Rectangle2D boundaryRectangle) {
        PowerDiagram powerDiagram = new PowerDiagram();
        OpenList sites = new OpenList();
        while (circleIterator.hasNext()) {
            final Circle circle = circleIterator.next();
            final Site site = new Site(circle.getCenterX(), circle.getCenterY(), circle.getRadius() * circle.getRadius());
            sites.add(site);
        }
        powerDiagram.setSites(sites);
        final PolygonSimple boundaryPolygon = getPolygonFromRectangle(boundaryRectangle);
        powerDiagram.setClipPoly(boundaryPolygon);
        powerDiagram.computeDiagram();
        return computeAreaOfSites(sites);
    }

    public static double findAreaOfCirclesPeriodic(Iterable<Point2D> centers, Iterable<Double> radii, Rectangle2D boundaryRectangle) {
        final Iterator<Circle> circleIterator = new CircleIterator(centers.iterator(), radii.iterator());
        final Iterator<Circle> wrappedCircleIterator = new WrappedCircleIterator(circleIterator, boundaryRectangle);
        return findAreaOfCircles(wrappedCircleIterator, boundaryRectangle);
    }

    private static PolygonSimple getPolygonFromRectangle(Rectangle2D boundaryRectangle) {
        PolygonSimple boundaryPolygon = new PolygonSimple(4);
        boundaryPolygon.add(boundaryRectangle.getMinX(), boundaryRectangle.getMinY());
        boundaryPolygon.add(boundaryRectangle.getMaxX(), boundaryRectangle.getMinY());
        boundaryPolygon.add(boundaryRectangle.getMaxX(), boundaryRectangle.getMaxY());
        boundaryPolygon.add(boundaryRectangle.getMinX(), boundaryRectangle.getMaxY());
        return boundaryPolygon;
    }

    private static double computeAreaOfSites(OpenList sites) {
        double area = 0;
        final int numSites = sites.size;
        for (int siteIndex = 0; siteIndex < numSites; siteIndex++) {
            final Site site = sites.get(siteIndex);
            area += computeAreaOfSite(site);
        }
        return area;
    }

    static private AreaPerimeter computeAreaPerimeterOfSites(OpenList sites) {
        AreaPerimeter areaPerimeter = new AreaPerimeter();
        final int numSites = sites.size;
        for (int siteIndex = 0; siteIndex < numSites; siteIndex++) {
            final Site site = sites.get(siteIndex);
            areaPerimeter.incrementBy(computeAreaPerimeterOfSite(site));
        }
        return areaPerimeter;
    }

    static private double computeAreaOfSite(Site site) {
        double area = 0;
        final PolygonSimple cellBoundaryPolygon = site.getPolygon();
        if (cellBoundaryPolygon == null) {
            return 0;
        }
        PathIterator boundaryPathIterator = cellBoundaryPolygon.getPathIterator(null);
        double[] firstVertex = new double[2];
        double[] oldVertex = new double[2];
        double[] newVertex = new double[2];
        int segmentType = boundaryPathIterator.currentSegment(firstVertex);
        if (segmentType != PathIterator.SEG_MOVETO) {
            throw new AssertionError();
        }
        System.arraycopy(firstVertex, 0, newVertex, 0, 2);
        boundaryPathIterator.next();
        while (segmentType != PathIterator.SEG_CLOSE) {
            System.arraycopy(newVertex, 0, oldVertex, 0, 2);
            segmentType = boundaryPathIterator.currentSegment(newVertex);//I am computing zero on the last iteration
            area += computeAreaForSegment(site, oldVertex, newVertex);
            boundaryPathIterator.next();
        }
        area += computeAreaForSegment(site, newVertex, firstVertex);
        return area;
    }

    static private AreaPerimeter computeAreaPerimeterOfSite(Site site) {
        AreaPerimeter areaPerimeter = new AreaPerimeter();
        final PolygonSimple cellBoundaryPolygon = site.getPolygon();
        if (cellBoundaryPolygon == null) {
            return areaPerimeter;
        }
        PathIterator boundaryPathIterator = cellBoundaryPolygon.getPathIterator(null);
        double[] firstVertex = new double[2];
        double[] oldVertex = new double[2];
        double[] newVertex = new double[2];
        int segmentType = boundaryPathIterator.currentSegment(firstVertex);
        if (segmentType != PathIterator.SEG_MOVETO) {
            throw new AssertionError();
        }
        System.arraycopy(firstVertex, 0, newVertex, 0, 2);
        boundaryPathIterator.next();
        segmentType = boundaryPathIterator.currentSegment(newVertex);//I am computing zero on the last iteration

        while (segmentType != PathIterator.SEG_CLOSE) {
            System.arraycopy(newVertex, 0, oldVertex, 0, 2);
            areaPerimeter.incrementBy(computeAreaPerimeterForSegment(site, oldVertex, newVertex));
            boundaryPathIterator.next();
            segmentType = boundaryPathIterator.currentSegment(newVertex);//I am computing zero on the last iteration
        }
        areaPerimeter.incrementBy(computeAreaPerimeterForSegment(site, newVertex, firstVertex));
        return areaPerimeter;
    }

    static private double computeAreaForSegment(Site site, double[] initialVertex, double[] finalVertex) {
        double[] segmentDisplacement = displacment2D(initialVertex, finalVertex);
        if (segmentDisplacement[0] == 0 && segmentDisplacement[1] == 0) {
            return 0;
        }
        double segmentLength = Math.sqrt(dotProduct2D(segmentDisplacement, segmentDisplacement));
        double[] centerToInitialDisplacement = new double[]{initialVertex[0] - site.x, initialVertex[1] - site.y};
        final double leftX = dotProduct2D(centerToInitialDisplacement, segmentDisplacement) / segmentLength;
        final double rightX = leftX + segmentLength;
        final double y = wedgeProduct2D(segmentDisplacement, centerToInitialDisplacement) / segmentLength;
        return computeCircleHorizontalLineOverlap(site.getWeight(), leftX, rightX, y);
    }

    static private AreaPerimeter computeAreaPerimeterForSegment(Site site, double[] initialVertex, double[] finalVertex) {
        double[] segmentDisplacement = displacment2D(initialVertex, finalVertex);
        if (segmentDisplacement[0] == 0 && segmentDisplacement[1] == 0) {
            return new AreaPerimeter();
        }
        double segmentLength = Math.sqrt(dotProduct2D(segmentDisplacement, segmentDisplacement));
        double[] centerToInitialDisplacement = new double[]{initialVertex[0] - site.x, initialVertex[1] - site.y};
        final double leftX = dotProduct2D(centerToInitialDisplacement, segmentDisplacement) / segmentLength;
        final double rightX = leftX + segmentLength;
        final double y = wedgeProduct2D(segmentDisplacement, centerToInitialDisplacement) / segmentLength;
        return computeCircleHorizontalLineAreaPerimeter(site.getWeight(), leftX, rightX, y);
    }

    private static double[] displacment2D(final double[] initialPoint, final double[] finalPoint) {
        return new double[]{finalPoint[0] - initialPoint[0], finalPoint[1] - initialPoint[1]};
    }

    private static double wedgeProduct2D(final double[] firstFactor, final double[] secondFactor) {
        return firstFactor[0] * secondFactor[1] - firstFactor[1] * secondFactor[0];
    }

    static private double dotProduct2D(final double[] firstFactor, final double[] secondFactor) {
        return firstFactor[0] * secondFactor[0] + firstFactor[1] * secondFactor[1];
    }

    static private double computeCircleHorizontalLineOverlap(double squareRadius, double leftX, double rightX, double y) {
        double area = 0;
        if (y * y > squareRadius) {
            area += fullCircleHorizontalLineArea(squareRadius, leftX, rightX, y);
        } else {
            final double intersectionX = Math.sqrt(squareRadius - y * y);
            if (leftX < -intersectionX) {
                area += fullCircleHorizontalLineArea(squareRadius, leftX, -intersectionX, y);
            }
            if (intersectionX < rightX) {
                area += fullCircleHorizontalLineArea(squareRadius, intersectionX, rightX, y);
            }
            area += Math.min(Math.max(-intersectionX, leftX) - Math.min(intersectionX, rightX), 0) * y / 2;
        }
        return area;
    }

    static private AreaPerimeter computeCircleHorizontalLineAreaPerimeter(double squareRadius, double leftX, double rightX, double y) {
        AreaPerimeter areaPerimeter = new AreaPerimeter();
        if (y * y > squareRadius) {
            areaPerimeter.incrementBy(fullCircleHorizontalLineAreaPerimeter(squareRadius, leftX, rightX, y));
        } else {
            final double intersectionX = Math.sqrt(squareRadius - y * y);
            if (leftX < -intersectionX) {
                areaPerimeter.incrementBy(fullCircleHorizontalLineAreaPerimeter(squareRadius, leftX, -intersectionX, y));
            }
            if (intersectionX < rightX) {
                areaPerimeter.incrementBy(fullCircleHorizontalLineAreaPerimeter(squareRadius, intersectionX, rightX, y));
            }
            areaPerimeter.area += Math.min(Math.max(-intersectionX, leftX) - Math.min(intersectionX, rightX), 0) * y / 2;
        }
        return areaPerimeter;
    }

    static private double fullCircleHorizontalLineArea(double squareRadius, double leftX, double rightX, double y) {
        final double initialTheta = Math.atan2(y, leftX);
        final double finalTheta = Math.atan2(y, rightX);
        double deltaTheta = finalTheta - initialTheta;
        if (deltaTheta < -Math.PI) {
            deltaTheta += 2 * Math.PI;
        } else if (deltaTheta > Math.PI) {
            deltaTheta -= 2 * Math.PI;
        }
        return squareRadius * deltaTheta / 2;
    }

    private static AreaPerimeter fullCircleHorizontalLineAreaPerimeter(double squareRadius, double leftX, double rightX, double y) {
        final double initialTheta = Math.atan2(y, leftX);
        final double finalTheta = Math.atan2(y, rightX);
        double deltaTheta = finalTheta - initialTheta;
        if (deltaTheta < -Math.PI) {
            deltaTheta += 2 * Math.PI;
        } else if (deltaTheta > Math.PI) {
            deltaTheta -= 2 * Math.PI;
        }
        return new AreaPerimeter(squareRadius * deltaTheta / 2, Math.sqrt(squareRadius) * deltaTheta);
    }

}
