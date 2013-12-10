/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SystemGeometry;

import Engine.PolymerState.SystemGeometry.Implementations.AbstractGeometry;
import Engine.PolymerState.SystemGeometry.Implementations.PeriodicGeometry;
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
public class AbstractGeometryTest {

    public AbstractGeometryTest() {
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
     * Test of toString method, of class AbstractGeometry.
     */
    @Ignore
    @Test
    public void testToString() {
        System.out.println("toString");
        AbstractGeometry instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of randomPosition method, of class AbstractGeometry.
     */
    @Test
    public void testRandomPosition() {
        System.out.println("randomPosition");
        AbstractGeometry instance = PeriodicGeometry.defaultGeometry();

        for (int i = 0; i < 100; i++) {
            double[] result = instance.randomPosition();
            assertTrue(isPositionValid(result, instance.getRMax()));
        }
    }

    private boolean arePositionsValid(double[][] positions, double[] rMax) {
        for (int i = 0; i < positions.length; i++) {
            double[] position = positions[i];
            if (!isPositionValid(position, rMax)) {
                return false;
            }
        }
        return true;
    }

    private boolean isPositionValid(double[] position, double[] rMax) {
        for (int i = 0; i < position.length; i++) {
            double component = position[i];
            if (component > rMax[i] || component < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Test of randomPositions method, of class AbstractGeometry.
     */
    @Test
    public void testRandomPositions() {
        System.out.println("randomPositions");
        int numPositions = 100;
        AbstractGeometry instance = PeriodicGeometry.defaultGeometry();
        double[][] result = instance.randomPositions(numPositions);
        assertTrue(arePositionsValid(result, instance.getRMax()));
    }

//    /**
//     * Test of randomColumnPositions method, of class AbstractGeometry.
//     */
//    @Test
//    public void testRandomColumnPositions() {
//        System.out.println("randomColumnPositions");
//        int numPositions = 0;
//        AbstractGeometry instance = null;
//        double[][] expResult = null;
//        double[][] result = instance.randomColumnPositions(numPositions);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of randomColumnPosition method, of class AbstractGeometry.
//     */
//    @Test
//    public void testRandomColumnPosition() {
//        System.out.println("randomColumnPosition");
//        AbstractGeometry instance = null;
//        double[] expResult = null;
//        double[] result = instance.randomColumnPosition();
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of randomGaussian method, of class AbstractGeometry.
//     */
//    @Test
//    public void testRandomGaussian_0args() {
//        System.out.println("randomGaussian");
//        AbstractGeometry instance = null;
//        double[] expResult = null;
//        double[] result = instance.randomGaussian();
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of randomGaussian method, of class AbstractGeometry.
//     */
//    @Test
//    public void testRandomGaussian_double() {
//        System.out.println("randomGaussian");
//        double scaleFactor = 0.0;
//        AbstractGeometry instance = null;
//        double[] expResult = null;
//        double[] result = instance.randomGaussian(scaleFactor);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getVolume method, of class AbstractGeometry.
//     */
//    @Test
//    public void testGetVolume() {
//        System.out.println("getVolume");
//        AbstractGeometry instance = null;
//        double expResult = 0.0;
//        double result = instance.getVolume();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRectanglesFromPosition method, of class AbstractGeometry.
//     */
//    @Test
//    public void testGetRectangleFromPosition() {
//        System.out.println("getRectanglesFromPosition");
//        double[] beadPosition = null;
//        AbstractGeometry instance = null;
//        BeadRectangle expResult = null;
//        BeadRectangle result = instance.getRectanglesFromPosition(beadPosition);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRectanglesFromPositions method, of class AbstractGeometry.
//     */
//    @Test
//    public void testGetRectanglesFromPositions() {
//        System.out.println("getRectanglesFromPositions");
//        double[][] beadPositions = null;
//        AbstractGeometry instance = null;
//        List expResult = null;
//        List result = instance.getRectanglesFromPositions(beadPositions);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of checkedCopyPositions method, of class AbstractGeometry.
//     */
//    @Test
//    public void testCheckedCopyPositions() {
//        System.out.println("checkedCopyPositions");
//        double[][] src = null;
//        double[][] dest = null;
//        AbstractGeometry instance = null;
//        instance.checkedCopyPositions(src, dest);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
    /**
     * Test of setRMax method, of class AbstractGeometry.
     */
    @Test
    public void testSetRMax() {
        System.out.println("setRMax");
        AbstractGeometry instance = PeriodicGeometry.defaultGeometry();
        int index = 0;
        double rMax;
        double[] oldRMax = new double[2];

        System.arraycopy(instance.getRMax(), 0, oldRMax, 0, instance.getNumDimensions());
        rMax = 10.0;
        instance.setRMax(0, rMax);
        oldRMax[0] = 10.;
        assertArrayEquals(oldRMax, instance.getRMax(), 0.);
    }
//

    /**
     * Test of getNumDimensions method, of class AbstractGeometry.
     */
    @Test
    public void testGetDimension() {
        System.out.println("getDimension");
        AbstractGeometry instance = PeriodicGeometry.defaultGeometry();
        int expResult = instance.getRMax().length;
        int result = instance.getNumDimensions();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRMax method, of class AbstractGeometry.
     */
    @Test
    public void testGetRMax() {
        System.out.println("getRMax");
        AbstractGeometry instance = PeriodicGeometry.defaultGeometry();
        double[] result = instance.getRMax();
        double[] expResult = new double[result.length];
        System.arraycopy(result, 0, expResult, 0, result.length);
        instance.setRMax(0, 2);
        assertArrayEquals(expResult, result, 0.);
    }
//
//    /**
//     * Test of getParameters method, of class AbstractGeometry.
//     */
//    @Test
//    public void testGetParameters() {
//        System.out.println("getParameters");
//        AbstractGeometry instance = null;
//        GeometricalParameters expResult = null;
//        GeometricalParameters result = instance.getParameters();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    public class AbstractGeometryImpl extends AbstractGeometry {
//
//        public AbstractGeometryImpl() {
//            super(0, null, null);
//        }
//
//    }
}
