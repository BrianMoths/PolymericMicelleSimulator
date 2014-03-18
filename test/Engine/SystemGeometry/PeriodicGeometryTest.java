/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SystemGeometry;

import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.PolymerState.SystemGeometry.Interfaces.GeometryBuilder;
import Engine.PolymerState.SystemGeometry.Implementations.PeriodicGeometry;
import Engine.Energetics.TwoBeadOverlap;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.BeadRectangle;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.RectangleSplitting.RectanglesAndGluedPerimeter;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bmoths
 */
public class PeriodicGeometryTest {

    public PeriodicGeometryTest() {
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
     * Test of defaultGeometry method, of class PeriodicGeometry. Makes sure
     * default object has legal state
     */
    @Test
    public void testDefaultGeometry() {
        System.out.println("defaultGeometry");
        PeriodicGeometry defaultGeometry = PeriodicGeometry.defaultGeometry();
        assertTrue(defaultGeometry.getNumDimensions() > 0);
        assertTrue(defaultGeometry.getRMax().length == defaultGeometry.getNumDimensions());
        GeometricalParameters geometricalParameters = defaultGeometry.getParameters();
        assertTrue(geometricalParameters.getCoreLength() >= 0);
        assertTrue(geometricalParameters.getInteractionLength() >= 0);
        assertTrue(geometricalParameters.getStepLength() >= 0);
    }

    /**
     * Test of toBuilder method, of class PeriodicGeometry.
     */
    @Test
    public void testToBuilder() {
        System.out.println("toBuilder");
        PeriodicGeometry instance = PeriodicGeometry.defaultGeometry();
        GeometryBuilder result = instance.toBuilder();
        checkConsistency(instance, result);
    }

    private void checkConsistency(PeriodicGeometry geometry, GeometryBuilder builder) {
        assertArrayEquals(geometry.getRMax(), builder.getFullRMaxCopy(), 0);
        assertEquals(geometry.getNumDimensions(), builder.getDimension());
        assertEquals(geometry.getParameters(), builder.getParameters());
    }

    /**
     * Test of getRectanglesAndPerimeterFromPositions method, of class
     * PeriodicGeometry.
     */
    @Test
    public void testGetRectanglesFromPositions() {
        double[][] beadPositions;
        final double xMax = 100;
        final double yMax = 100;
        PeriodicGeometry periodicGeometry = makeGeometryForRectangles(xMax, yMax);

//        System.out.println("periodic rmax" + periodicGeometry.getRMax()[0]);
//        System.out.println(periodicGeometry.getRMax()[1]); ok
        List<BeadRectangle> result;
        BeadRectangle expectRectangle;
        List<BeadRectangle> expectedResult;




        beadPositions = new double[][]{new double[]{50, 50}};
        result = periodicGeometry.getRectanglesFromPositions(beadPositions);

        expectRectangle = new BeadRectangle(48, 52, 52, 48);
        expectedResult = new ArrayList<>();
        expectedResult.add(expectRectangle);

        assertRectangleListConsistent(result, expectedResult);
//        assertEquals(GeometryAnalyzer.findArea(result), 16, 0);



        beadPositions = new double[][]{new double[]{0, 50}};
        result = periodicGeometry.getRectanglesFromPositions(beadPositions);

        expectedResult = new ArrayList<>();
        expectRectangle = new BeadRectangle(98, 100, 52, 48);
        expectedResult.add(expectRectangle);
        expectRectangle = new BeadRectangle(0, 2, 52, 48);
        expectedResult.add(expectRectangle);

        assertRectangleListConsistent(result, expectedResult);
//        assertEquals(GeometryAnalyzer.findArea(result), 16, 0);


        beadPositions = new double[][]{new double[]{0, 0}};
        result = periodicGeometry.getRectanglesFromPositions(beadPositions);
        System.out.println(result.size());

        expectedResult = new ArrayList<>();
        expectRectangle = new BeadRectangle(98, 100, 100, 98);
        expectedResult.add(expectRectangle);
        expectRectangle = new BeadRectangle(98, 100, 2, 0);
        expectedResult.add(expectRectangle);
        expectRectangle = new BeadRectangle(0, 2, 100, 98);
        expectedResult.add(expectRectangle);
        expectRectangle = new BeadRectangle(0, 2, 2, 0);
        expectedResult.add(expectRectangle);

        assertRectangleListConsistent(result, expectedResult);
//        assertEquals(GeometryAnalyzer.findArea(result), 16, 0);




        beadPositions = new double[][]{new double[]{50, 50}, new double[]{51, 51}};
        result = periodicGeometry.getRectanglesFromPositions(beadPositions);

//        System.out.println("Split size: " + expectRectangle.splitOverPeriodicBoundary(boundaryRectangle).size());
        expectedResult = new ArrayList<>();
        expectRectangle = new BeadRectangle(48, 52, 52, 48);
        expectedResult.add(expectRectangle);
        expectRectangle = new BeadRectangle(49, 53, 53, 49);
        expectedResult.add(expectRectangle);

        assertRectangleListConsistent(result, expectedResult);
//        assertEquals(GeometryAnalyzer.findArea(result), 23, 0);



        beadPositions = new double[][]{new double[]{0, 50}, new double[]{1, 51},};
        result = periodicGeometry.getRectanglesFromPositions(beadPositions);

        expectedResult = new ArrayList<>();
        expectRectangle = new BeadRectangle(98, 100, 52, 48);
        expectedResult.add(expectRectangle);
        expectRectangle = new BeadRectangle(0, 2, 52, 48);
        expectedResult.add(expectRectangle);
        expectRectangle = new BeadRectangle(99, 100, 53, 49);
        expectedResult.add(expectRectangle);
        expectRectangle = new BeadRectangle(0, 3, 53, 49);
        expectedResult.add(expectRectangle);

        assertRectangleListConsistent(result, expectedResult);
//        assertEquals(GeometryAnalyzer.findArea(result), 23, 0);


        beadPositions = new double[][]{new double[]{0, 0}, new double[]{1, 1}};
        result = periodicGeometry.getRectanglesFromPositions(beadPositions);
        System.out.println(result.size());

        expectedResult = new ArrayList<>();
        expectRectangle = new BeadRectangle(98, 100, 100, 98);
        expectedResult.add(expectRectangle);
        expectRectangle = new BeadRectangle(98, 100, 2, 0);
        expectedResult.add(expectRectangle);
        expectRectangle = new BeadRectangle(0, 2, 100, 98);
        expectedResult.add(expectRectangle);
        expectRectangle = new BeadRectangle(0, 2, 2, 0);
        expectedResult.add(expectRectangle);
        expectRectangle = new BeadRectangle(99, 100, 100, 99);
        expectedResult.add(expectRectangle);
        expectRectangle = new BeadRectangle(99, 100, 3, 0);
        expectedResult.add(expectRectangle);
        expectRectangle = new BeadRectangle(0, 3, 100, 99);
        expectedResult.add(expectRectangle);
        expectRectangle = new BeadRectangle(0, 3, 3, 0);
        expectedResult.add(expectRectangle);

        assertRectangleListConsistent(result, expectedResult);
//        assertEquals(GeometryAnalyzer.findArea(result), 23, 0);


    }

    private PeriodicGeometry makeGeometryForRectangles(double xMax, double yMax) {
        PeriodicGeometry.PeriodicGeometryBuilder builder = new PeriodicGeometry.PeriodicGeometryBuilder();
        builder.setDimension(2);
        builder.setDimensionSize(0, xMax);
        builder.setDimensionSize(1, yMax);
        builder.setParameters(makeParametersForRectangles());
        return builder.buildGeometry();
    }

    private GeometricalParameters makeParametersForRectangles() {
        GeometricalParameters geometricalParameters;

        geometricalParameters = new GeometricalParameters(1.0, 4.);

        return geometricalParameters;
    }

    private void assertRectangleListConsistent(List<BeadRectangle> actual, List<BeadRectangle> expected) {
        assertEquals(expected.size(), actual.size());
        for (BeadRectangle actualBeadRectangle : actual) {
            boolean isConsistentWithOne = false;
            for (BeadRectangle expectedBeadRectangle : expected) {
                isConsistentWithOne |= isRectanlgesConsistent(actualBeadRectangle, expectedBeadRectangle);
            }
            assertTrue(isConsistentWithOne);
        }
    }

    private boolean isRectanlgesConsistent(BeadRectangle actual, BeadRectangle expected) {
        boolean isConsistent = true;

        isConsistent &= expected.getLeft() == actual.getLeft();
        isConsistent &= expected.getRight() == actual.getRight();
        isConsistent &= expected.getTop() == actual.getTop();
        isConsistent &= expected.getBottom() == actual.getBottom();

        return isConsistent;
    }

    /**
     * Test of getRectanglesAndPerimeterFromPositions method, of class
     * PeriodicGeometry.
     */
    @Test
    public void testGetRectanglesAndPerimeterFromPositions() {
        System.out.println("getRectanglesAndPerimeterFromPositions");
        double[][] beadPositions;
        final double xMax = 100;
        final double yMax = 100;
        PeriodicGeometry periodicGeometry = makeGeometryForRectangles(xMax, yMax);

//        System.out.println("periodic rmax" + periodicGeometry.getRMax()[0]);
//        System.out.println(periodicGeometry.getRMax()[1]); ok
        RectanglesAndGluedPerimeter result;
        BeadRectangle expectRectangle;
        RectanglesAndGluedPerimeter expectedResult;
        List<BeadRectangle> expectedBeadRectangles;

        beadPositions = new double[][]{new double[]{50, 50}};
        result = periodicGeometry.getRectanglesAndPerimeterFromPositions(beadPositions);

        expectRectangle = new BeadRectangle(48, 52, 52, 48);
//        System.out.println("Split size: " + expectRectangle.splitOverPeriodicBoundary(boundaryRectangle).size());
        expectedBeadRectangles = new ArrayList<>();
        expectedBeadRectangles.add(expectRectangle);
        expectedResult = new RectanglesAndGluedPerimeter(expectedBeadRectangles, 0.);

        assertRectanglesAndGluedPerimeterConsisent(result, expectedResult);

        beadPositions = new double[][]{new double[]{0, 50}};
        result = periodicGeometry.getRectanglesAndPerimeterFromPositions(beadPositions);

        expectedBeadRectangles = new ArrayList<>();
        expectRectangle = new BeadRectangle(98, 100, 52, 48);
        expectedBeadRectangles.add(expectRectangle);
        expectRectangle = new BeadRectangle(0, 2, 52, 48);
        expectedBeadRectangles.add(expectRectangle);
        expectedResult = new RectanglesAndGluedPerimeter(expectedBeadRectangles, 4.);

        assertRectanglesAndGluedPerimeterConsisent(result, expectedResult);
//        assertEquals(GeometryAnalyzer.findArea(result), 16, 0);


        beadPositions = new double[][]{new double[]{0, 0}};
        result = periodicGeometry.getRectanglesAndPerimeterFromPositions(beadPositions);

        expectedBeadRectangles = new ArrayList<>();
        expectRectangle = new BeadRectangle(98, 100, 100, 98);
        expectedBeadRectangles.add(expectRectangle);
        expectRectangle = new BeadRectangle(98, 100, 2, 0);
        expectedBeadRectangles.add(expectRectangle);
        expectRectangle = new BeadRectangle(0, 2, 100, 98);
        expectedBeadRectangles.add(expectRectangle);
        expectRectangle = new BeadRectangle(0, 2, 2, 0);
        expectedBeadRectangles.add(expectRectangle);
        expectedResult = new RectanglesAndGluedPerimeter(expectedBeadRectangles, 8.);

        assertRectanglesAndGluedPerimeterConsisent(result, expectedResult);
//        assertEquals(GeometryAnalyzer.findArea(result), 16, 0);




        beadPositions = new double[][]{new double[]{50, 50}, new double[]{51, 51}};
        result = periodicGeometry.getRectanglesAndPerimeterFromPositions(beadPositions);

//        System.out.println("Split size: " + expectRectangle.splitOverPeriodicBoundary(boundaryRectangle).size());
        expectedBeadRectangles = new ArrayList<>();
        expectRectangle = new BeadRectangle(48, 52, 52, 48);
        expectedBeadRectangles.add(expectRectangle);
        expectRectangle = new BeadRectangle(49, 53, 53, 49);
        expectedBeadRectangles.add(expectRectangle);
        expectedResult = new RectanglesAndGluedPerimeter(expectedBeadRectangles, 0.);

        assertRectanglesAndGluedPerimeterConsisent(result, expectedResult);
//        assertEquals(GeometryAnalyzer.findArea(result), 23, 0);



        beadPositions = new double[][]{new double[]{0, 50}, new double[]{1, 51},};
        result = periodicGeometry.getRectanglesAndPerimeterFromPositions(beadPositions);

        expectedBeadRectangles = new ArrayList<>();
        expectRectangle = new BeadRectangle(98, 100, 52, 48);
        expectedBeadRectangles.add(expectRectangle);
        expectRectangle = new BeadRectangle(0, 2, 52, 48);
        expectedBeadRectangles.add(expectRectangle);
        expectRectangle = new BeadRectangle(99, 100, 53, 49);
        expectedBeadRectangles.add(expectRectangle);
        expectRectangle = new BeadRectangle(0, 3, 53, 49);
        expectedBeadRectangles.add(expectRectangle);
        expectedResult = new RectanglesAndGluedPerimeter(expectedBeadRectangles, 5.);

        assertRectanglesAndGluedPerimeterConsisent(result, expectedResult);
//        assertEquals(GeometryAnalyzer.findArea(result), 23, 0);


        beadPositions = new double[][]{new double[]{0, 0}, new double[]{1, 1}};
        result = periodicGeometry.getRectanglesAndPerimeterFromPositions(beadPositions);

        expectedBeadRectangles = new ArrayList<>();
        expectRectangle = new BeadRectangle(98, 100, 100, 98);
        expectedBeadRectangles.add(expectRectangle);
        expectRectangle = new BeadRectangle(98, 100, 2, 0);
        expectedBeadRectangles.add(expectRectangle);
        expectRectangle = new BeadRectangle(0, 2, 100, 98);
        expectedBeadRectangles.add(expectRectangle);
        expectRectangle = new BeadRectangle(0, 2, 2, 0);
        expectedBeadRectangles.add(expectRectangle);
        expectRectangle = new BeadRectangle(99, 100, 100, 99);
        expectedBeadRectangles.add(expectRectangle);
        expectRectangle = new BeadRectangle(99, 100, 3, 0);
        expectedBeadRectangles.add(expectRectangle);
        expectRectangle = new BeadRectangle(0, 3, 100, 99);
        expectedBeadRectangles.add(expectRectangle);
        expectRectangle = new BeadRectangle(0, 3, 3, 0);
        expectedBeadRectangles.add(expectRectangle);
        expectedResult = new RectanglesAndGluedPerimeter(expectedBeadRectangles, 10.);

        assertRectanglesAndGluedPerimeterConsisent(result, expectedResult);
//        assertEquals(GeometryAnalyzer.findArea(result), 23, 0);

        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    private void assertRectanglesAndGluedPerimeterConsisent(RectanglesAndGluedPerimeter actual, RectanglesAndGluedPerimeter expected) {
        assertRectangleListConsistent(actual.beadRectangles, expected.beadRectangles);
        assertEquals(actual.gluedPerimeter, expected.gluedPerimeter, 0);
    }

    /**
     * Test of sqDist method, of class PeriodicGeometry.
     */
    @Test
    public void testSqDist() {
        System.out.println("sqDist");
        PeriodicGeometry instance = PeriodicGeometry.defaultGeometry();

        //test basic functionality
        double[] position1 = new double[]{0, 0};
        double[] position2 = new double[]{0, 0};
        double expResult = 0.0;
        double result = instance.sqDist(position1, position2);
        assertEquals(expResult, result, 0.0);

        position1 = new double[]{0, 0};
        position2 = new double[]{1, 0};
        expResult = 1.0;
        result = instance.sqDist(position1, position2);
        assertEquals(expResult, result, 0.0);

        position1 = new double[]{0, 1};
        position2 = new double[]{-1, 0};
        expResult = 2.0;
        result = instance.sqDist(position1, position2);
        assertEquals(expResult, result, 0.0);

        position1 = new double[]{.5, 0};
        position2 = new double[]{0, 2.5};
        expResult = 6.5;
        result = instance.sqDist(position1, position2);
        assertEquals(expResult, result, 0.0);

        //test bc wrapping
        double xMax = instance.getRMax()[0];
        double yMax = instance.getRMax()[1];

        position1 = new double[]{xMax - .5, 0};
        position2 = new double[]{0, yMax - 2.5};
        expResult = 6.5;
        result = instance.sqDist(position1, position2);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of twoBeadOverlap method, of class PeriodicGeometry.
     */
    @Test
    public void testTwoBeadOverlap() {
        System.out.println("twoBeadOverlap");
        PeriodicGeometry instance = PeriodicGeometry.defaultGeometry();
        double interactionLength = instance.getParameters().getInteractionLength();
        double coreLength = instance.getParameters().getCoreLength();
        double xMax = instance.getRMax()[0];
        double yMax = instance.getRMax()[1];
        double[] position1;
        double[] position2;

        //test simplest case
        double smallPos = .5;
        position1 = new double[]{0, 0};
        position2 = new double[]{smallPos, smallPos};
        double squareLength = interactionLength - smallPos > 0 ? interactionLength - smallPos : 0;
        double expSoftResult = squareLength * squareLength;
        double coreOverlapSide = coreLength - smallPos > 0 ? coreLength - smallPos : 0;
        double expHardResult = coreOverlapSide * coreOverlapSide;
        TwoBeadOverlap result = instance.twoBeadRectangularOverlap(position1, position2);
        assertEquals(result.softOverlap, expSoftResult, 0);
        assertEquals(result.hardOverlap, expHardResult, 0);

        //test case with rectangle
        double mediumPos = 3;
        position1 = new double[]{0, 0};
        position2 = new double[]{smallPos, mediumPos};
        double xSideLength = interactionLength - smallPos > 0 ? interactionLength - smallPos : 0;
        double ySideLength = interactionLength - mediumPos > 0 ? interactionLength - mediumPos : 0;
        expSoftResult = xSideLength * ySideLength;
        double xCoreSideLength = coreLength - smallPos > 0 ? coreLength - smallPos : 0;
        double yCoreSideLength = coreLength - mediumPos > 0 ? coreLength - mediumPos : 0;
        expHardResult = xCoreSideLength * yCoreSideLength;
        result = instance.twoBeadRectangularOverlap(position1, position2);
        assertEquals(result.softOverlap, expSoftResult, 0);
        assertEquals(result.hardOverlap, expHardResult, 0);

        //test case with wrapping
        position1 = new double[]{0, 0};
        position2 = new double[]{xMax - smallPos, yMax - mediumPos};
        xSideLength = interactionLength - smallPos > 0 ? interactionLength - smallPos : 0;
        ySideLength = interactionLength - mediumPos > 0 ? interactionLength - mediumPos : 0;
        expSoftResult = xSideLength * ySideLength;
        xCoreSideLength = coreLength - smallPos > 0 ? coreLength - smallPos : 0;
        yCoreSideLength = coreLength - mediumPos > 0 ? coreLength - mediumPos : 0;
        expHardResult = xCoreSideLength * yCoreSideLength;
        result = instance.twoBeadRectangularOverlap(position1, position2);
        assertEquals(result.softOverlap, expSoftResult, 0);
        assertEquals(result.hardOverlap, expHardResult, 0);
    }

    /**
     * Test of incrementFirstVector method, of class PeriodicGeometry.
     */
    @Test
    public void testIncrementFirstVector() {
        System.out.println("incrementFirstVector");
        PeriodicGeometry instance = PeriodicGeometry.defaultGeometry();
        double[] oldToStep;
        double[] newToStep;
        double[] oldStepVector;
        double[] newStepVector;
        boolean expResult;
        boolean result;

        //simplest case
        oldToStep = new double[]{0, 0};
        oldStepVector = new double[]{1, 1};
        newToStep = new double[]{1, 1};
        newStepVector = new double[]{1, 1};
        expResult = true;

        result = instance.incrementFirstVector(oldToStep, oldStepVector);
        assertEquals(expResult, result);
        assertArrayEquals(oldToStep, newToStep, 0);
        assertArrayEquals(oldStepVector, newStepVector, 0);

        //slightly more complicated
        oldToStep = new double[]{3, 2};
        oldStepVector = new double[]{1.5, -1};
        newToStep = new double[]{4.5, 1};
        newStepVector = new double[]{1.5, -1};
        expResult = true;

        result = instance.incrementFirstVector(oldToStep, oldStepVector);
        assertEquals(expResult, result);
        assertArrayEquals(oldToStep, newToStep, 0);
        assertArrayEquals(oldStepVector, newStepVector, 0);

        //involves wrapping
        double xMax = instance.getRMax()[0];
        double yMax = instance.getRMax()[1];
        oldToStep = new double[]{3, 2};
        oldStepVector = new double[]{xMax + 1.5, -1 - yMax};
        newToStep = new double[]{4.5, 1};
        newStepVector = new double[]{xMax + 1.5, -1 - yMax};
        expResult = true;

        result = instance.incrementFirstVector(oldToStep, oldStepVector);
        assertEquals(expResult, result);
        assertArrayEquals(oldToStep, newToStep, 0);
        assertArrayEquals(oldStepVector, newStepVector, 0);
    }

    /**
     * Test of decrementFirstVector method, of class PeriodicGeometry.
     */
    @Test
    public void testDecrementFirstVector() {
        System.out.println("decrementFirstVector");
        PeriodicGeometry instance = PeriodicGeometry.defaultGeometry();
        double xMax = instance.getRMax()[0];
        double yMax = instance.getRMax()[1];
        double[] oldToStep;
        double[] newToStep;
        double[] oldStepVector;
        double[] newStepVector;

        //simplest case
        oldToStep = new double[]{0, 0};
        oldStepVector = new double[]{1, 1};
        newToStep = new double[]{xMax - 1, yMax - 1};
        newStepVector = new double[]{1, 1};

        instance.decrementFirstVector(oldToStep, oldStepVector);
        assertArrayEquals(oldToStep, newToStep, 0);
        assertArrayEquals(oldStepVector, newStepVector, 0);

        //slightly more complicated
        oldToStep = new double[]{3, 2};
        oldStepVector = new double[]{1.5, -1};
        newToStep = new double[]{1.5, 3};
        newStepVector = new double[]{1.5, -1};

        instance.decrementFirstVector(oldToStep, oldStepVector);
        assertArrayEquals(oldToStep, newToStep, 0);
        assertArrayEquals(oldStepVector, newStepVector, 0);

        //involves wrapping
        oldToStep = new double[]{3, 2};
        oldStepVector = new double[]{xMax + 1.5, -1 - yMax};
        newToStep = new double[]{1.5, 3};
        newStepVector = new double[]{xMax + 1.5, -1 - yMax};

        instance.decrementFirstVector(oldToStep, oldStepVector);
        assertArrayEquals(oldToStep, newToStep, 0);
        assertArrayEquals(oldStepVector, newStepVector, 0);
    }

    /**
     * Test of checkedCopyPosition method, of class PeriodicGeometry.
     */
    @Test
    public void testCheckedCopyPosition() {
        System.out.println("checkedCopyPosition");
        PeriodicGeometry instance = PeriodicGeometry.defaultGeometry();
        double xMax = instance.getRMax()[0];
        double yMax = instance.getRMax()[1];
        double[] oldSrc;
        double[] oldDest;
        double[] newSrc;
        double[] newDest;

        //simple case
        oldSrc = new double[]{0, 0};
        oldDest = new double[]{1, 1};
        newSrc = new double[]{0, 0};
        instance.checkedCopyPosition(oldSrc, oldDest);
        assertArrayEquals(oldSrc, newSrc, 0.);
        assertArrayEquals(oldDest, oldSrc, 0.);

        //slightly more complicated
        oldSrc = new double[]{1, 2};
        oldDest = new double[]{3, 1};
        newSrc = new double[]{1, 2};
        instance.checkedCopyPosition(oldSrc, oldDest);
        assertArrayEquals(oldSrc, newSrc, 0.);
        assertArrayEquals(oldDest, oldSrc, 0.);

        //involving wrapping
        oldSrc = new double[]{1, 2};
        oldDest = new double[]{3, 1};
        newSrc = new double[]{1, 2};
        instance.checkedCopyPosition(oldSrc, oldDest);
        assertArrayEquals(oldSrc, newSrc, 0.);
        assertArrayEquals(oldDest, oldSrc, 0.);

        //involving wrapping
        oldSrc = new double[]{xMax + 1, yMax + 2};
        oldDest = new double[]{3, 1};
        newSrc = new double[]{xMax + 1, yMax + 2};
        newDest = new double[]{1, 2};
        instance.checkedCopyPosition(oldSrc, oldDest);
        assertArrayEquals(oldSrc, newSrc, 0.);
        assertArrayEquals(oldDest, newDest, 0.);
    }

    /**
     * Test of isPositionValid method, of class PeriodicGeometry.
     */
    @Test
    public void testIsPositionValid() {
        System.out.println("isPositionValid");
        double[] position;
        PeriodicGeometry instance = PeriodicGeometry.defaultGeometry();
        final boolean expResult = true;
        boolean result;

        position = new double[]{0, 0};
        result = instance.isPositionValid(position);
        assertEquals(expResult, result);

        position = new double[]{1, 3};
        result = instance.isPositionValid(position);
        assertEquals(expResult, result);

        position = new double[]{6443.63684, 35451.566};
        result = instance.isPositionValid(position);
        assertEquals(expResult, result);
    }

}
