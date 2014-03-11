/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis.AreaPerimeter.circleareaperimeter;

import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.diagram.PowerDiagram;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;

/**
 *
 * @author bmoths
 */
public abstract class PowerDiagramEvaluator<T> {

    static public OpenList makeSites(final Iterable<Circle> circles, Rectangle2D boundaryRectangle) {
        PowerDiagram powerDiagram = new PowerDiagram();

        OpenList sites = new OpenList();
        for (Circle circle : circles) {
            final Site site = new Site(circle.getCenterX(), circle.getCenterY(), circle.getRadius() * circle.getRadius());
            sites.add(site);
        }
        powerDiagram.setSites(sites);
        final PolygonSimple boundaryPolygon = getPolygonFromRectangle(boundaryRectangle);
        powerDiagram.setClipPoly(boundaryPolygon);
        powerDiagram.computeDiagram();
        return sites;
    }

    private static PolygonSimple getPolygonFromRectangle(Rectangle2D boundaryRectangle) {
        PolygonSimple boundaryPolygon = new PolygonSimple(4);
        boundaryPolygon.add(boundaryRectangle.getMinX(), boundaryRectangle.getMinY());
        boundaryPolygon.add(boundaryRectangle.getMaxX(), boundaryRectangle.getMinY());
        boundaryPolygon.add(boundaryRectangle.getMaxX(), boundaryRectangle.getMaxY());
        boundaryPolygon.add(boundaryRectangle.getMinX(), boundaryRectangle.getMaxY());
        return boundaryPolygon;
    }

    public final T computeValue(final Iterable<Circle> circles, Rectangle2D boundaryRectangle) {
        OpenList sites = makeSites(circles, boundaryRectangle);
        processSites(sites);
        return getValue();
    }

    private void processSites(OpenList sites) {
        initialize();
        final int numSites = sites.size;
        for (int siteIndex = 0; siteIndex < numSites; siteIndex++) {
            final Site site = sites.get(siteIndex);
            processSite(site);
        }
    }

    private void processSite(Site site) {
        initializeForSite(site);
        final PolygonSimple cellBoundaryPolygon = site.getPolygon();
        if (cellBoundaryPolygon == null) {
            return;
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
        System.arraycopy(newVertex, 0, oldVertex, 0, 2);
        segmentType = boundaryPathIterator.currentSegment(newVertex);
        while (segmentType != PathIterator.SEG_CLOSE) {
            processSegment(site, oldVertex, newVertex);
            boundaryPathIterator.next();
            System.arraycopy(newVertex, 0, oldVertex, 0, 2);
            segmentType = boundaryPathIterator.currentSegment(newVertex);
        }
        processSegment(site, newVertex, firstVertex);
    }

    private void processSegment(Site site, double[] initialVertex, double[] finalVertex) {
        double[] segmentDisplacement = displacment2D(initialVertex, finalVertex);
        if (segmentDisplacement[0] == 0 && segmentDisplacement[1] == 0) {
            return;
        }
        double segmentLength = Math.sqrt(dotProduct2D(segmentDisplacement, segmentDisplacement));
        double[] centerToInitialDisplacement = new double[]{initialVertex[0] - site.x, initialVertex[1] - site.y};
        final double leftX = dotProduct2D(centerToInitialDisplacement, segmentDisplacement) / segmentLength;
        final double rightX = leftX + segmentLength;
        final double y = wedgeProduct2D(segmentDisplacement, centerToInitialDisplacement) / segmentLength;
        processSegmentStandardGeometry(site.getWeight(), leftX, rightX, y);
    }

    private void processSegmentStandardGeometry(double squareRadius, double leftX, double rightX, double y) {
        if (y * y > squareRadius) {
            processNonIntersectingRegion(leftX, rightX, y);
        } else {
            final double intersectionX = Math.sqrt(squareRadius - y * y);
            if (leftX < -intersectionX) {
                final double leftRegionRightEndpoint = Math.min(-intersectionX, rightX);
                processNonIntersectingRegion(leftX, leftRegionRightEndpoint, y);
            }
            if (intersectionX < rightX) {
                final double rightRegionLeftEndpoint = Math.max(intersectionX, leftX);
                processNonIntersectingRegion(rightRegionLeftEndpoint, rightX, y);
            }
            final double middleRegionLeftEndpoint = Math.max(-intersectionX, leftX);
            final double middleRegionRightEndpoint = Math.min(intersectionX, rightX);
            final double middleRegionLength = Math.max(middleRegionRightEndpoint - middleRegionLeftEndpoint, 0);
            processIntersectingRegion(middleRegionLength, y);
        }
    }

    private void processNonIntersectingRegion(double leftX, double rightX, double y) {
        final double initialTheta = Math.atan2(y, leftX);
        final double finalTheta = Math.atan2(y, rightX);
        double deltaTheta = finalTheta - initialTheta;
        if (deltaTheta < -Math.PI) {
            deltaTheta += 2 * Math.PI;
        } else if (deltaTheta > Math.PI) {
            deltaTheta -= 2 * Math.PI;
        }
        processNonIntersectingRegion(deltaTheta);
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

    protected abstract void initialize();

    protected abstract void initializeForSite(Site site);

    protected abstract void processNonIntersectingRegion(double deltaTheta);

    protected abstract void processIntersectingRegion(double length, double y);

    protected abstract T getValue();

}
