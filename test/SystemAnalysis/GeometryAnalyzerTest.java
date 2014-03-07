/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import SystemAnalysis.AreaPerimeter.BeadRectangle;
import SystemAnalysis.GeometryAnalyzer.AreaPerimeter;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
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
        AreaPerimeter expResult = null;
        AreaPerimeter result = GeometryAnalyzer.findAreaAndPerimeterOfRectangles(beadRectangles);
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
        radius = 1;
        result = GeometryAnalyzer.findAreaOfCirclesWithRadius(centers, radius, boundaryRectangle);
        expResult = 0;
        assertEquals(expResult, result, .0000001);

        final double maxRadius = 6;

        Random random = new Random(42);
        for (int i = 0; i < 100; i++) {
            final double x1 = maxRadius + random.nextDouble() * (xMax - 2 * maxRadius);
            final double y1 = maxRadius + random.nextDouble() * (yMax - 2 * maxRadius);
            final double x2 = maxRadius + random.nextDouble() * (xMax - 2 * maxRadius);
            final double y2 = maxRadius + random.nextDouble() * (yMax - 2 * maxRadius);
            radius = random.nextDouble() * maxRadius;
            System.out.println("radius: " + radius);
            centers = new ArrayList<>();
            centers.add(new Point2D.Double(x1, y1));
            centers.add(new Point2D.Double(x2, y2));
            result = GeometryAnalyzer.findAreaOfCirclesWithRadius(centers, radius, boundaryRectangle);
            expResult = calculateTwoCircleOverlap(radius, (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
            assertEquals(expResult, result, .0000001);
        }

        final int numCellsInEachDirection = 100;
        final double xWidth = xMax / numCellsInEachDirection;
        final double yWidth = yMax / numCellsInEachDirection;
        for (int i = 0; i < 100; i++) {
            centers = new double[numCellsInEachDirection * numCellsInEachDirection][2];
            for (int xIndex = 0; xIndex < numCellsInEachDirection; xIndex++) {
                final double x0 = xIndex * xWidth;
                for (int yIndex = 0; yIndex < numCellsInEachDirection; yIndex++) {
                    final double y0 = yIndex * yWidth;
                    centers[xIndex * numCellsInEachDirection + yIndex][0] = x0 + Math.random() * xWidth;
                    centers[xIndex * numCellsInEachDirection + yIndex][1] = y0 + Math.random() * yWidth;
                }
            }
        }
    }

    private double calculateTwoCircleOverlap(double radius, double squareDistance) {
        System.out.println("Square distance: " + squareDistance);
        final double squareRadius = radius * radius;
        if (4 * squareRadius < squareDistance) {
            return 2 * Math.PI * squareRadius;
        }
        double x = Math.sqrt(squareDistance) / (2 * radius);
        return 2 * squareRadius * (Math.PI - (Math.acos(x) - x * Math.sqrt(1 - x * x)));
    }

    /**
     * Test of findAreaOfCircles method, of class GeometryAnalyzer.
     */
    @Test
    public void testFindAreaOfCircles() {
        System.out.println("findAreaOfCircles");
        Iterable<Point2D> centers = null;
        Iterable<Double> radii = null;
        Rectangle2D boundaryRectangle = null;
        double expResult = 0.0;
        double result = GeometryAnalyzer.findAreaOfCircles(centers, radii, boundaryRectangle);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
