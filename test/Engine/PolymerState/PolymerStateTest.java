/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState;

import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.PolymerState.SystemGeometry.Implementations.PeriodicGeometry;
import Engine.PolymerState.SystemGeometry.Interfaces.ImmutableSystemGeometry;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
import Engine.SystemAnalyzer.AnalyzerListener;
import Engine.SystemAnalyzer.BeadPositionsGetter;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author bmoths
 */
public class PolymerStateTest {

    public PolymerStateTest() {
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
     * Test of scaleSystemAlongDimension method, of class PolymerState.
     */
    @Test
    public void testScaleSystemAlongDimension() {
        System.out.println("scaleSystemAlongDimension");
        PolymerState instance;
        double[][] actualPositions;
        double[][] expectedPositions;
        double sizeChange;
        int dimension;




        sizeChange = 20.0;
        dimension = 0;
        instance = makePolymerState(3, 1);
        instance.getPolymerPosition().setBeadPositions(new double[][]{new double[]{0, 0}, new double[]{1, 0}, new double[]{0, 1}});
        instance.scaleSystemAlongDimension(sizeChange, dimension);
        actualPositions = instance.getPolymerPosition().getBeadPositions();
        expectedPositions = new double[][]{new double[]{0, 0}, new double[]{2, 0}, new double[]{0, 1}};
        for (int i = 0; i < actualPositions.length; i++) {
            assertArrayEquals(actualPositions[i], expectedPositions[i], .00001);
        }

        sizeChange = 10.0;
        dimension = 0;
        instance = makePolymerState(3, 1);
        instance.getPolymerPosition().setBeadPositions(new double[][]{new double[]{0, 0}, new double[]{1, 0}, new double[]{0, 1}});
        instance.scaleSystemAlongDimension(sizeChange, dimension);
        actualPositions = instance.getPolymerPosition().getBeadPositions();
        expectedPositions = new double[][]{new double[]{0, 0}, new double[]{1.5, 0}, new double[]{0, 1}};
        for (int i = 0; i < actualPositions.length; i++) {
            assertArrayEquals(actualPositions[i], expectedPositions[i], .00001);
        }


        sizeChange = 20.0;
        dimension = 1;
        instance = makePolymerState(3, 1);
        instance.getPolymerPosition().setBeadPositions(new double[][]{new double[]{0, 0}, new double[]{1, 0}, new double[]{0, 1}});
        instance.scaleSystemAlongDimension(sizeChange, dimension);
        actualPositions = instance.getPolymerPosition().getBeadPositions();
        expectedPositions = new double[][]{new double[]{0, 0}, new double[]{1, 0}, new double[]{0, 2}};
        for (int i = 0; i < actualPositions.length; i++) {
            assertArrayEquals(actualPositions[i], expectedPositions[i], .00001);
        }
    }

    /**
     * Test of reptate method, of class PolymerState.
     */
    @Ignore
    @Test
    public void testReptate() {
        System.out.println("reptate");
        int bead = 0;
        boolean isGoingRight = false;
        PolymerState instance = null;
        boolean expResult = false;
        boolean result = instance.reptate(bead, isGoingRight);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getImmutableSystemGeometry method, of class PolymerState.
     */
    @Ignore
    @Test
    public void testGetImmutableSystemGeometry() {
        System.out.println("getImmutableSystemGeometry");
        PolymerState instance = null;
        ImmutableSystemGeometry expResult = null;
        ImmutableSystemGeometry result = instance.getImmutableSystemGeometry();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getImmutablePolymerPosition method, of class PolymerState.
     */
    @Ignore
    @Test
    public void testGetImmutablePolymerPosition() {
        System.out.println("getImmutablePolymerPosition");
        PolymerState instance = null;
        ImmutablePolymerPosition expResult = null;
        ImmutablePolymerPosition result = instance.getImmutablePolymerPosition();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getImmutableDiscretePolymerState method, of class PolymerState.
     */
    @Ignore
    @Test
    public void testGetImmutableDiscretePolymerState() {
        System.out.println("getImmutableDiscretePolymerState");
        PolymerState instance = null;
        ImmutableDiscretePolymerState expResult = null;
        ImmutableDiscretePolymerState result = instance.getImmutableDiscretePolymerState();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of acceptBeadPositionGetter method, of class PolymerState.
     */
    @Ignore
    @Test
    public void testAcceptBeadPositionGetter() {
        System.out.println("acceptBeadPositionGetter");
        BeadPositionsGetter beadPositionsGetter = null;
        PolymerState instance = null;
        instance.acceptBeadPositionGetter(beadPositionsGetter);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of acceptAnalyzerListener method, of class PolymerState.
     */
    @Ignore
    @Test
    public void testAcceptAnalyzerListener() {
        System.out.println("acceptAnalyzerListener");
        AnalyzerListener analyzerListener = null;
        PolymerState instance = null;
        instance.acceptAnalyzerListener(analyzerListener);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of randomize method, of class PolymerState.
     */
    @Ignore
    @Test
    public void testRandomize() {
        System.out.println("randomize");
        PolymerState instance = null;
        instance.randomize();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of columnRandomize method, of class PolymerState.
     */
    @Ignore
    @Test
    public void testColumnRandomize() {
        System.out.println("columnRandomize");
        PolymerState instance = null;
        instance.columnRandomize();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of reasonableColumnRandomize method, of class PolymerState.
     */
    @Ignore
    @Test
    public void testReasonableColumnRandomize() {
        System.out.println("reasonableColumnRandomize");
        PolymerState instance = null;
        instance.reasonableColumnRandomize();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of anneal method, of class PolymerState.
     */
    @Ignore
    @Test
    public void testAnneal() {
        System.out.println("anneal");
        PolymerState instance = null;
        instance.anneal();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of moveBead method, of class PolymerState.
     */
    @Ignore
    @Test
    public void testMoveBead() {
        System.out.println("moveBead");
        int stepBead = 0;
        double[] stepVector = null;
        PolymerState instance = null;
        boolean expResult = false;
        boolean result = instance.moveBead(stepBead, stepVector);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of undoStep method, of class PolymerState.
     */
    @Ignore
    @Test
    public void testUndoStep() {
        System.out.println("undoStep");
        int stepBead = 0;
        double[] stepVector = null;
        PolymerState instance = null;
        instance.undoStep(stepBead, stepVector);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEndToEndDisplacements method, of class PolymerState.
     */
    @Test
    public void testGetEndToEndDisplacements() {
        System.out.println("getEndToEndDisplacements");
        PolymerState instance;
        List<double[]> expResult;
        List<double[]> result;

        instance = makePolymerState(3, 1);
        instance.getPolymerPosition().setBeadPositions(new double[][]{new double[]{0, 0}, new double[]{1, 0}, new double[]{0, 1}});
        expResult = new ArrayList<>();
        expResult.add(new double[]{0, 1});
        result = instance.getEndToEndDisplacements();
        assertDisplacementsEqual(result, expResult);

        instance = makePolymerState(3, 1);
        instance.getPolymerPosition().setBeadPositions(new double[][]{new double[]{0, 0}, new double[]{1, 0}, new double[]{1, 1}});
        expResult = new ArrayList<>();
        expResult.add(new double[]{1, 1});
        result = instance.getEndToEndDisplacements();
        assertDisplacementsEqual(result, expResult);

        instance = makePolymerState(3, 1);
        instance.getPolymerPosition().setBeadPositions(new double[][]{new double[]{0, 0}, new double[]{9, 0}, new double[]{18, 1}});
        expResult = new ArrayList<>();
        expResult.add(new double[]{18, 1});
        result = instance.getEndToEndDisplacements();
        assertDisplacementsEqual(result, expResult);

        instance = makePolymerState(3, 1);
        instance.getPolymerPosition().setBeadPositions(new double[][]{new double[]{0, 0}, new double[]{19, 0}, new double[]{19, 1}});
        expResult = new ArrayList<>();
        expResult.add(new double[]{-1, 1});
        result = instance.getEndToEndDisplacements();
        assertDisplacementsEqual(result, expResult);

        instance = makePolymerState(3, 2);
        instance.getPolymerPosition().setBeadPositions(new double[][]{new double[]{0, 0}, new double[]{19, 0}, new double[]{19, 1}, new double[]{0, 0}, new double[]{19, 0}, new double[]{19, 1}});
        expResult = new ArrayList<>();
        expResult.add(new double[]{-1, 1});
        expResult.add(new double[]{-1, 1});
        result = instance.getEndToEndDisplacements();
        assertDisplacementsEqual(result, expResult);

        instance = makePolymerState(3, 3);
        instance.getPolymerPosition().setBeadPositions(new double[][]{new double[]{0, 0}, new double[]{1, 3}, new double[]{5, 12}, new double[]{10, 10}, new double[]{19, 5}, new double[]{0, 0}, new double[]{0, 0}, new double[]{1, 2}, new double[]{3, 6}});
        expResult = new ArrayList<>();
        expResult.add(new double[]{5, 12});
        expResult.add(new double[]{10, -10});
        expResult.add(new double[]{3, 6});
        result = instance.getEndToEndDisplacements();
        assertDisplacementsEqual(result, expResult);


    }

    private void assertDisplacementsEqual(List<double[]> result, List<double[]> expResult) {

        assertEquals(expResult.size(), result.size());
        for (int i = 0; i < result.size(); i++) {
            assertArrayEquals(expResult.get(i), result.get(i), .00001);
        }
    }

    private PolymerState makePolymerState(final int beadsPerChain, final int numChains) {
        PolymerState instance;
        final int numBeads = beadsPerChain * numChains;
        final int numDimensions = 2;
        SystemGeometry systemGeometry = new PeriodicGeometry(numDimensions, new double[]{20, 20}, new GeometricalParameters());
        PolymerPosition polymerPosition = new PolymerPosition(numBeads, systemGeometry);
        DiscretePolymerState discretePolymerState = new DiscretePolymerState(PolymerCluster.makeRepeatedChainCluster(PolymerChain.makeChainStartingWithA(beadsPerChain), numChains));
        instance = new PolymerState(discretePolymerState, polymerPosition, systemGeometry);
        return instance;
    }

}
