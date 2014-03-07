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

        public AreaPerimeter(double area, double perimeter) {
            this.area = area;
            this.perimeter = perimeter;
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

    static public AreaPerimeter findAreaAndPerimeterOfRectangles(List<BeadRectangle> beadRectangles) {
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
        PowerDiagram powerDiagram = new PowerDiagram();
        OpenList sites = new OpenList();
        final Iterator<Double> radiusIterator = radii.iterator();
        for (Point2D center : centers) {
            final double radius = radiusIterator.next();
            final Site site = new Site(center.getX(), center.getY(), radius * radius);
            sites.add(site);
        }
        powerDiagram.setSites(sites);
        final PolygonSimple boundaryPolygon = getPolygonFromRectangle(boundaryRectangle);
        powerDiagram.setClipPoly(boundaryPolygon);
        powerDiagram.computeDiagram();
        return computeAreaOfSites(sites);
    }

    static private PolygonSimple getPolygonFromRectangle(Rectangle2D boundaryRectangle) {
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

    private static double computeAreaOfSite(Site site) {
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

    private static double computeAreaForSegment(Site site, double[] initialVertex, double[] finalVertex) {
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

    private static double[] displacment2D(final double[] initialPoint, final double[] finalPoint) {
        return new double[]{finalPoint[0] - initialPoint[0], finalPoint[1] - initialPoint[1]};
    }

    private static double wedgeProduct2D(final double[] firstFactor, final double[] secondFactor) {
        return firstFactor[0] * secondFactor[1] - firstFactor[1] * secondFactor[0];
    }

    private static double dotProduct2D(final double[] firstFactor, final double[] secondFactor) {
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

}
