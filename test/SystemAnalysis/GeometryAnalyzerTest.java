/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import SystemAnalysis.AreaPerimeter.AreaPerimeter;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.BeadRectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author bmoths
 */
public class GeometryAnalyzerTest {

    public GeometryAnalyzerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of findAreaOfRectangles method, of class GeometryAnalyzer.
     */
    @Ignore
    @Test
    public void testFindAreaOfRectangles() {
        System.out.println("findAreaOfRectangles");
        List<BeadRectangle> beadRectangles = null;
        double expResult = 0.0;
        double result = GeometryAnalyzer.findAreaOfRectangles(beadRectangles);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAreaAndPerimeterOfRectangles method, of class
     * GeometryAnalyzer.
     */
    @Ignore
    @Test
    public void testFindAreaAndPerimeterOfRectangles() {
        System.out.println("findAreaAndPerimeterOfRectangles");
        List<BeadRectangle> beadRectangles = null;
        SystemAnalysis.AreaPerimeter.AreaPerimeter expResult = null;
        SystemAnalysis.AreaPerimeter.AreaPerimeter result = GeometryAnalyzer.findAreaAndPerimeterOfRectangles(beadRectangles);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAreaOfCirclesWithRadius method, of class GeometryAnalyzer.
     */
    @Test
    public void testFindAreaOfCirclesWithRadius() {
        System.out.println("findAreaOfCirclesWithRadius");
        List<Point2D> centers;
        double radius;
        final double xMax = 30;
        final double yMax = 20;
        Rectangle2D boundaryRectangle = new Rectangle2D.Double(0, 0, xMax, yMax);
        double expResult;
        double result;




        centers = new ArrayList<>();
        centers.add(new Point2D.Double(5, 5));
        radius = 1;
        result = GeometryAnalyzer.findAreaOfCirclesWithRadius(centers, radius, boundaryRectangle);
        expResult = Math.PI;
        assertEquals(expResult, result, .0000001);


        centers = new ArrayList<>();
        radius = 1;
        result = GeometryAnalyzer.findAreaOfCirclesWithRadius(centers, radius, boundaryRectangle);
        expResult = 0;
        assertEquals(expResult, result, .0000001);

        centers = new ArrayList<>();
        centers.add(new Point2D.Double(0, 0));
        radius = 1;
        result = GeometryAnalyzer.findAreaOfCirclesWithRadius(centers, radius, boundaryRectangle);
        expResult = Math.PI / 4;
        assertEquals(expResult, result, .0000001);

        centers = new ArrayList<>();
        centers.add(new Point2D.Double(0, 0));
        centers.add(new Point2D.Double(xMax, 0));
        centers.add(new Point2D.Double(0, yMax));
        centers.add(new Point2D.Double(xMax, yMax));
        centers.add(new Point2D.Double(5, 5));
        centers.add(new Point2D.Double(13, 7));
        radius = 1;
        result = GeometryAnalyzer.findAreaOfCirclesWithRadius(centers, radius, boundaryRectangle);
        expResult = 3 * Math.PI;
        assertEquals(expResult, result, .001);

        centers = new ArrayList<>();
        radius = 1;
        result = GeometryAnalyzer.findAreaOfCirclesWithRadius(centers, radius, boundaryRectangle);
        expResult = 0;
        assertEquals(expResult, result, .0000001);

        final double maxRadius = 6;

        Random random = new Random(42);
        for (int i = 0; i < 1000; i++) {
            final double x1 = maxRadius + random.nextDouble() * (xMax - 2 * maxRadius);
            final double y1 = maxRadius + random.nextDouble() * (yMax - 2 * maxRadius);
            final double x2 = maxRadius + random.nextDouble() * (xMax - 2 * maxRadius);
            final double y2 = maxRadius + random.nextDouble() * (yMax - 2 * maxRadius);
            radius = random.nextDouble() * maxRadius;
//            System.out.println("radius: " + radius);
            centers = new ArrayList<>();
            centers.add(new Point2D.Double(x1, y1));
            centers.add(new Point2D.Double(x2, y2));
            result = GeometryAnalyzer.findAreaOfCirclesWithRadius(centers, radius, boundaryRectangle);
            expResult = calculateTwoCircleOverlap(radius, (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
            assertEquals(expResult, result, .0000001);
        }


    }

    private double calculateTwoCircleOverlap(double radius, double squareDistance) {
//        System.out.println("Square distance: " + squareDistance);
        final double squareRadius = radius * radius;
        if (4 * squareRadius < squareDistance) {
            return 2 * Math.PI * squareRadius;
        }
        double x = Math.sqrt(squareDistance) / (2 * radius);
        return 2 * squareRadius * (Math.PI - (Math.acos(x) - x * Math.sqrt(1 - x * x)));
    }

    private AreaPerimeter calculateTwoCircleAreaPerimeter(double radius, double squareDistance) {
        final double squareRadius = radius * radius;
        if (4 * squareRadius < squareDistance) {
            return new AreaPerimeter(2 * Math.PI * squareRadius, 4 * Math.PI * radius);
        }
        final double x = Math.sqrt(squareDistance) / (2 * radius);
        final double nonOverlappingHalfAngle = Math.PI - Math.acos(x);
        final double area = 2 * squareRadius * (nonOverlappingHalfAngle + x * Math.sqrt(1 - x * x));
        final double perimeter = 4 * radius * nonOverlappingHalfAngle;
        return new AreaPerimeter(area, perimeter);
    }

    /**
     * Test of findAreaOfCircles method, of class GeometryAnalyzer.
     */
    @Test
    public void testFindAreaOfCircles() {
        System.out.println("findAreaOfCircles");
        final Random random = new Random(42);
        final double xMax = 30;
        final double yMax = 20;
        final double expResult = xMax * yMax;
        final Rectangle2D boundaryRectangle = new Rectangle2D.Double(0, 0, xMax, yMax);
        Collection<Point2D> centers;
        Collection<Double> radii;

        final int numCellsInEachDirection = 20;
        final double xWidth = xMax / numCellsInEachDirection;
        final double yWidth = yMax / numCellsInEachDirection;
        final double minRadius = Math.sqrt(xWidth * xWidth + yWidth * yWidth);
        final int numTrials = 100;
        for (int i = 0; i < numTrials; i++) {
            centers = new ArrayList<>(numCellsInEachDirection * numCellsInEachDirection);
            radii = new ArrayList<>(numCellsInEachDirection * numCellsInEachDirection);
            for (int xIndex = 0; xIndex < numCellsInEachDirection; xIndex++) {
                final double x0 = xIndex * xWidth;
                for (int yIndex = 0; yIndex < numCellsInEachDirection; yIndex++) {
                    final double y0 = yIndex * yWidth;
                    final double x = x0 + random.nextDouble() * xWidth;
                    final double y = y0 + random.nextDouble() * yWidth;
                    Point2D center = new Point2D.Double(x, y);
                    centers.add(center);
                    final double radius = minRadius + random.nextDouble() * Math.min(xMax, yMax) / 4;
                    radii.add(radius);
                }
            }
            double result = GeometryAnalyzer.findAreaOfCircles(centers, radii, boundaryRectangle);
            assertEquals(expResult, result, .0000001);
        }
    }

    /**
     * Test of findAreaOfCirclesPeriodic method, of class GeometryAnalyzer.
     */
    @Test
    public void testFindAreaOfCirclesPeriodic() {
        System.out.println("findAreaOfCirclesPeriodic");
        final Random random = new Random(42);
        final double xMax = 30;
        final double yMax = 20;
        final double expResult = xMax * yMax;
        final Rectangle2D boundaryRectangle = new Rectangle2D.Double(0, 0, xMax, yMax);
        Collection<Point2D> centers;
        Collection<Double> radii;

        final int numCellsInEachDirection = 20;
        final double xWidth = xMax / numCellsInEachDirection;
        final double yWidth = yMax / numCellsInEachDirection;
        final double minRadius = Math.sqrt(xWidth * xWidth + yWidth * yWidth);
        final int numTrials = 100;
        for (int i = 0; i < numTrials; i++) {
            centers = new ArrayList<>(numCellsInEachDirection * numCellsInEachDirection);
            radii = new ArrayList<>(numCellsInEachDirection * numCellsInEachDirection);
            for (int xIndex = 0; xIndex < numCellsInEachDirection; xIndex++) {
                final double x0 = xIndex * xWidth;
                for (int yIndex = 0; yIndex < numCellsInEachDirection; yIndex++) {
                    final double y0 = yIndex * yWidth;
                    final double x = x0 + random.nextDouble() * xWidth;
                    final double y = y0 + random.nextDouble() * yWidth;
                    Point2D center = new Point2D.Double(x, y);
                    centers.add(center);
                    final double radius = minRadius + random.nextDouble() * Math.min(xMax, yMax) / 4;
                    radii.add(radius);
                }
            }
            double result = GeometryAnalyzer.findAreaOfCirclesPeriodic(centers, radii, boundaryRectangle);
            assertEquals(expResult, result, .0000001);
        }
    }

    /**
     * Test of findAreaOfCirclesWithRadiusPeriodic method, of class
     * GeometryAnalyzer.
     */
    @Test
    public void testFindAreaOfCirclesWithRadiusPeriodic() {
        System.out.println("findAreaOfCirclesWithRadiusPeriodic");
        List<Point2D> centers;
        double radius;
        final double xMax = 30;
        final double yMax = 20;
        final Rectangle2D boundaryRectangle = new Rectangle2D.Double(0, 0, xMax, yMax);
        double expResult;
        double result;




        centers = new ArrayList<>();
        centers.add(new Point2D.Double(5, 5));
        radius = 1;
        result = GeometryAnalyzer.findAreaOfCirclesWithRadiusPeriodic(centers, radius, boundaryRectangle);
        expResult = Math.PI;
        assertEquals(expResult, result, .0000001);


        centers = new ArrayList<>();
        radius = 1;
        result = GeometryAnalyzer.findAreaOfCirclesWithRadiusPeriodic(centers, radius, boundaryRectangle);
        expResult = 0;
        assertEquals(expResult, result, .0000001);

        centers = new ArrayList<>();
        centers.add(new Point2D.Double(0, 0));
        centers.add(new Point2D.Double(10, 13));
        centers.add(new Point2D.Double(16, 8));
        radius = 1;
        result = GeometryAnalyzer.findAreaOfCirclesWithRadiusPeriodic(centers, radius, boundaryRectangle);
        expResult = 3 * Math.PI;
        assertEquals(expResult, result, .0000001);

        final double maxRadius = 4;

        Random random = new Random(42);
        for (int i = 0; i < 50; i++) { //fails (actually power diagram algorithm hangs) for seed = 42 and 170 or more iterations
            final double x1 = random.nextDouble() * xMax;
            final double y1 = random.nextDouble() * yMax;
            final double x2 = random.nextDouble() * xMax;
            final double y2 = random.nextDouble() * yMax;
            radius = random.nextDouble() * maxRadius;
            centers = new ArrayList<>();
            centers.add(new Point2D.Double(x1, y1));
            centers.add(new Point2D.Double(x2, y2));
            result = GeometryAnalyzer.findAreaOfCirclesWithRadiusPeriodic(centers, radius, boundaryRectangle);
            expResult = calculateTwoCircleOverlapPeriodic(radius, x1 - x2, y1 - y2, boundaryRectangle);
            assertEquals(expResult, result, .0000001);
        }

    }

    private double calculateTwoCircleOverlapPeriodic(double radius, double deltaX, double deltaY, Rectangle2D boundaryRectangle) {
        deltaX = Math.abs(deltaX);
        deltaY = Math.abs(deltaY);
        if (deltaX > boundaryRectangle.getWidth() / 2) {
            deltaX = boundaryRectangle.getWidth() - deltaX;
        }
        if (deltaY > boundaryRectangle.getHeight() / 2) {
            deltaY = boundaryRectangle.getHeight() - deltaY;
        }
        return calculateTwoCircleOverlap(radius, deltaX * deltaX + deltaY * deltaY);
    }

    /**
     * Test of findAreaPerimeterOfCircles method, of class GeometryAnalyzer.
     */
    @Test
    public void testFindAreaPerimeterOfCircles() {
        System.out.println("findAreaPerimeterOfCircles");
        final Random random = new Random(42);
        final double xMax = 30;
        final double yMax = 20;
        final AreaPerimeter expResult = new AreaPerimeter(xMax * yMax, 2 * (xMax + yMax));
        final Rectangle2D boundaryRectangle = new Rectangle2D.Double(0, 0, xMax, yMax);
        Collection<Point2D> centers;
        Collection<Double> radii;

        final int numCellsInEachDirection = 20;
        final double xWidth = xMax / numCellsInEachDirection;
        final double yWidth = yMax / numCellsInEachDirection;
        final double minRadius = Math.sqrt(xWidth * xWidth + yWidth * yWidth);
        final int numTrials = 100;
        for (int i = 0; i < numTrials; i++) {
            centers = new ArrayList<>(numCellsInEachDirection * numCellsInEachDirection);
            radii = new ArrayList<>(numCellsInEachDirection * numCellsInEachDirection);
            for (int xIndex = 0; xIndex < numCellsInEachDirection; xIndex++) {
                final double x0 = xIndex * xWidth;
                for (int yIndex = 0; yIndex < numCellsInEachDirection; yIndex++) {
                    final double y0 = yIndex * yWidth;
                    final double x = x0 + random.nextDouble() * xWidth;
                    final double y = y0 + random.nextDouble() * yWidth;
                    Point2D center = new Point2D.Double(x, y);
                    centers.add(center);
                    final double radius = minRadius + random.nextDouble() * Math.min(xMax, yMax) / 4;
                    radii.add(radius);
                }
            }
            AreaPerimeter result = GeometryAnalyzer.findAreaPerimeterOfCircles(centers, radii, boundaryRectangle);
            assertAreaPerimeterEquals(expResult, result, .0000001);
        }
    }

    /**
     * Test of findAreaPerimeterOfCriclesWithRadius method, of class
     * GeometryAnalyzer.
     */
    @Test
    public void testFindAreaPerimeterOfCriclesWithRadius() {
        System.out.println("findAreaPerimeterOfCriclesWithRadius");
        List<Point2D> centers;
        double radius;
        final double xMax = 30;
        final double yMax = 20;
        Rectangle2D boundaryRectangle = new Rectangle2D.Double(0, 0, xMax, yMax);
        AreaPerimeter expResult;
        AreaPerimeter result;




        centers = new ArrayList<>();
        centers.add(new Point2D.Double(5, 5));
        radius = 1;
        result = GeometryAnalyzer.findAreaPerimeterOfCirclesWithRadius(centers, radius, boundaryRectangle);
        expResult = new AreaPerimeter(Math.PI, 2 * Math.PI);
        assertAreaPerimeterEquals(expResult, result, radius);


        centers = new ArrayList<>();
        radius = 1;
        result = GeometryAnalyzer.findAreaPerimeterOfCirclesWithRadius(centers, radius, boundaryRectangle);
        expResult = new AreaPerimeter(0, 0);
        assertAreaPerimeterEquals(expResult, result, .0000001);

        centers = new ArrayList<>();
        centers.add(new Point2D.Double(0, 0));
        centers.add(new Point2D.Double(10, 13));
        centers.add(new Point2D.Double(16, 8));
        radius = 1;
        result = GeometryAnalyzer.findAreaPerimeterOfCirclesWithRadius(centers, radius, boundaryRectangle);
        expResult = new AreaPerimeter((2 + 1. / 4) * Math.PI, 4.5 * Math.PI + 2);
        assertAreaPerimeterEquals(expResult, result, .0000001);

        centers = new ArrayList<>();
        centers.add(new Point2D.Double(0, 0));
        centers.add(new Point2D.Double(xMax, 0));
        centers.add(new Point2D.Double(0, yMax));
        centers.add(new Point2D.Double(xMax, yMax));
        centers.add(new Point2D.Double(5, 5));
        centers.add(new Point2D.Double(13, 7));
        radius = 1;
        result = GeometryAnalyzer.findAreaPerimeterOfCirclesWithRadius(centers, radius, boundaryRectangle);
        expResult = new AreaPerimeter(3 * Math.PI, 8 + 2 * Math.PI + 4 * Math.PI);
        assertAreaPerimeterEquals(expResult, result, .001);

        final double maxRadius = 6;

        Random random = new Random(42);
        for (int i = 0; i < 1000; i++) {
            final double x1 = maxRadius + random.nextDouble() * (xMax - 2 * maxRadius);
            final double y1 = maxRadius + random.nextDouble() * (yMax - 2 * maxRadius);
            final double x2 = maxRadius + random.nextDouble() * (xMax - 2 * maxRadius);
            final double y2 = maxRadius + random.nextDouble() * (yMax - 2 * maxRadius);
            radius = random.nextDouble() * maxRadius;
//            System.out.println("radius: " + radius);
            centers = new ArrayList<>();
            centers.add(new Point2D.Double(x1, y1));
            centers.add(new Point2D.Double(x2, y2));
            result = GeometryAnalyzer.findAreaPerimeterOfCirclesWithRadius(centers, radius, boundaryRectangle);
            expResult = calculateTwoCircleAreaPerimeter(radius, (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
            assertAreaPerimeterEquals(expResult, result, .0000001);
        }
    }

    static private void assertAreaPerimeterEquals(SystemAnalysis.AreaPerimeter.AreaPerimeter expected, SystemAnalysis.AreaPerimeter.AreaPerimeter actual, double numericalError) {
        assertEquals(expected.area, actual.area, numericalError);
        assertEquals(expected.perimeter, actual.perimeter, numericalError);
    }

    /**
     * Test of findAreaPerimeterOfCirclesPeriodic method, of class
     * GeometryAnalyzer.
     */
    @Test
    public void testFindAreaPerimeterOfCirclesPeriodic() {
        System.out.println("findAreaPerimeterOfCirclesPeriodic");
        final Random random = new Random(42);
        final double xMax = 30;
        final double yMax = 20;
        final AreaPerimeter expResult = new AreaPerimeter(xMax * yMax, 0);
        final Rectangle2D boundaryRectangle = new Rectangle2D.Double(0, 0, xMax, yMax);
        Collection<Point2D> centers;
        Collection<Double> radii;

        final int numCellsInEachDirection = 20;
        final double xWidth = xMax / numCellsInEachDirection;
        final double yWidth = yMax / numCellsInEachDirection;
        final double minRadius = Math.sqrt(xWidth * xWidth + yWidth * yWidth);
        final int numTrials = 100;
        for (int i = 0; i < numTrials; i++) {
            centers = new ArrayList<>(numCellsInEachDirection * numCellsInEachDirection);
            radii = new ArrayList<>(numCellsInEachDirection * numCellsInEachDirection);
            for (int xIndex = 0; xIndex < numCellsInEachDirection; xIndex++) {
                final double x0 = xIndex * xWidth;
                for (int yIndex = 0; yIndex < numCellsInEachDirection; yIndex++) {
                    final double y0 = yIndex * yWidth;
                    final double x = x0 + random.nextDouble() * xWidth;
                    final double y = y0 + random.nextDouble() * yWidth;
                    Point2D center = new Point2D.Double(x, y);
                    centers.add(center);
                    final double radius = minRadius + random.nextDouble() * Math.min(xMax, yMax) / 4;
                    radii.add(radius);
                }
            }
            AreaPerimeter result = GeometryAnalyzer.findAreaPerimeterOfCirclesPeriodic(centers, radii, boundaryRectangle);
            assertAreaPerimeterEquals(expResult, result, .0000001);
        }
    }

    /**
     * Test of findAreaPerimeterOfCirclesWithRadiusPeriodic method, of class
     * GeometryAnalyzer.
     */
    @Test
    public void testFindAreaPerimeterOfCirclesWithRadiusPeriodic() {
        System.out.println("findAreaPerimeterOfCirclesWithRadiusPeriodic");
        List<Point2D> centers;
        double radius;
        final double xMax = 30;
        final double yMax = 20;
        Rectangle2D boundaryRectangle = new Rectangle2D.Double(0, 0, xMax, yMax);
        AreaPerimeter expResult;
        AreaPerimeter result;




        centers = new ArrayList<>();
        centers.add(new Point2D.Double(5, 5));
        radius = 1;
        result = GeometryAnalyzer.findAreaPerimeterOfCirclesWithRadiusPeriodic(centers, radius, boundaryRectangle);
        expResult = new AreaPerimeter(Math.PI, 2 * Math.PI);
        assertAreaPerimeterEquals(expResult, result, radius);


        centers = new ArrayList<>();
        radius = 1;
        result = GeometryAnalyzer.findAreaPerimeterOfCirclesWithRadiusPeriodic(centers, radius, boundaryRectangle);
        expResult = new AreaPerimeter(0, 0);
        assertAreaPerimeterEquals(expResult, result, .0000001);

        centers = new ArrayList<>();
        centers.add(new Point2D.Double(0, 0));
        centers.add(new Point2D.Double(5, 5));
        centers.add(new Point2D.Double(13, 7));
        radius = 1;
        result = GeometryAnalyzer.findAreaPerimeterOfCirclesWithRadiusPeriodic(centers, radius, boundaryRectangle);
        expResult = new AreaPerimeter(3 * Math.PI, 6 * Math.PI);
        assertAreaPerimeterEquals(expResult, result, .001);

        final double maxRadius = 6;

        Random random = new Random(42);
        for (int i = 0; i < 1000; i++) {
            final double x1 = maxRadius + random.nextDouble() * (xMax - 2 * maxRadius);
            final double y1 = maxRadius + random.nextDouble() * (yMax - 2 * maxRadius);
            final double x2 = maxRadius + random.nextDouble() * (xMax - 2 * maxRadius);
            final double y2 = maxRadius + random.nextDouble() * (yMax - 2 * maxRadius);
            radius = random.nextDouble() * maxRadius;
//            System.out.println("radius: " + radius);
            centers = new ArrayList<>();
            centers.add(new Point2D.Double(x1, y1));
            centers.add(new Point2D.Double(x2, y2));
            result = GeometryAnalyzer.findAreaPerimeterOfCirclesWithRadiusPeriodic(centers, radius, boundaryRectangle);
            expResult = calculateTwoCircleAreaPerimeterPeriodic(radius, x1 - x2, y1 - y2, boundaryRectangle);
            assertAreaPerimeterEquals(expResult, result, .0000001);
        }

    }

    private AreaPerimeter calculateTwoCircleAreaPerimeterPeriodic(double radius, double deltaX, double deltaY, Rectangle2D boundaryRectangle) {
        deltaX = Math.abs(deltaX);
        deltaY = Math.abs(deltaY);
        if (deltaX > boundaryRectangle.getWidth() / 2) {
            deltaX = boundaryRectangle.getWidth() - deltaX;
        }
        if (deltaY > boundaryRectangle.getHeight() / 2) {
            deltaY = boundaryRectangle.getHeight() - deltaY;
        }
        return calculateTwoCircleAreaPerimeter(radius, deltaX * deltaX + deltaY * deltaY);
    }

}
