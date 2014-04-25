/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import Engine.PolymerSimulator;
import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
import Engine.SimulatorParameters.SystemParametersBuilder;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author bmoths
 */
public class StressFinderTest {

    private SystemParametersBuilder systemParametersBuilder;

    public StressFinderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        systemParametersBuilder = SystemParametersBuilder.getDefaultSystemParametersBuilder();
        PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(PolymerChain.makeChainOfType(false, 2), 1);
        polymerCluster.setConcentrationInWater(.01);
        systemParametersBuilder.setPolymerCluster(polymerCluster);
        systemParametersBuilder.getEnergeticsConstantsBuilder().setBBOverlapCoefficient(.01);
        systemParametersBuilder.setInteractionLength(1.);
        systemParametersBuilder.setAspectRatio(1);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of calculateSpringStress method, of class StressFinder.
     */
    @Test
    public void testCalculateSpringStress() {
        System.out.println("calculateSpringStress");
        PolymerSimulator polymerSimulator = systemParametersBuilder.buildSystemParametersWithAutomaticHardOverlap().makePolymerSimulator();
        final double systemSize = polymerSimulator.getGeometry().getSizeOfDimension(0);
        final double displacement = .2;
        double[][] beadPositions = {{systemSize / 2 + 0, systemSize / 2 + 0}, {systemSize / 2 + displacement, systemSize / 2 + 0}};
        polymerSimulator.setBeadPositions(beadPositions);
        double[][] expResult = {{-polymerSimulator.getEnergeticsConstants().getSpringConstant() * displacement * 9 / polymerSimulator.getGeometry().getVolume() * displacement * 2, 0}, {0, 0}};
        System.out.println(expResult[0][0]);
        double[][] result = StressFinder.calculateSpringStress(polymerSimulator);
        double fractionalError = getFractionalError(expResult, result);

        assertEquals(0, fractionalError, .00001);
    }

    /**
     * Test of calculateOverlapStress method, of class StressFinder.
     */
    @Test
    public void testCalculateOverlapStress() {
        System.out.println("calculateOverlapStress");
        PolymerSimulator polymerSimulator = systemParametersBuilder.buildSystemParametersWithAutomaticHardOverlap().makePolymerSimulator();
        final double systemSize = polymerSimulator.getGeometry().getSizeOfDimension(0);
        final double displacement = .2;
        double[][] beadPositions = {{systemSize / 2 + 0, systemSize / 2 + 0}, {systemSize / 2 + displacement, systemSize / 2 + 0}};
        polymerSimulator.setBeadPositions(beadPositions);
        double[][] expResult = {{polymerSimulator.getEnergeticsConstants().getBBOverlapCoefficient() * Math.sqrt(polymerSimulator.getGeometricalParameters().getInteractionLength() - displacement * displacement) * 9 / polymerSimulator.getGeometry().getVolume() * displacement * 2, 0}, {0, 0}};
        System.out.println(expResult[0][0]);
        double[][] result = StressFinder.calculateOverlapStress(polymerSimulator);

        double fractionalError = getFractionalError(expResult, result);
        assertEquals(0, fractionalError, .00001);
    }

    private double getFractionalError(double[][] expResult, double[][] result) {
        double error = 0;
        double sum = 0;
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result.length; j++) {
                error += Math.abs(expResult[i][j] - result[i][j]);
                sum += Math.abs(expResult[i][j]);
            }
        }
        return error / sum;
    }

    /**
     * Test of calculateTotalStress method, of class StressFinder.
     */
    @Test
    public void testCalculateTotalStress() {
        System.out.println("calculateTotalStress");

        PolymerSimulator polymerSimulator = systemParametersBuilder.buildSystemParametersWithAutomaticHardOverlap().makePolymerSimulator();
        final double systemSize = polymerSimulator.getGeometry().getSizeOfDimension(0);
        final double displacement = .2;
        double[][] beadPositions = {{systemSize / 2 + 0, systemSize / 2 + 0}, {systemSize / 2 + displacement, systemSize / 2 + 0}};
        polymerSimulator.setBeadPositions(beadPositions);
        double[][] expResult = {{(-polymerSimulator.getEnergeticsConstants().getSpringConstant() * displacement + polymerSimulator.getEnergeticsConstants().getBBOverlapCoefficient() * Math.sqrt(polymerSimulator.getGeometricalParameters().getInteractionLength() - displacement * displacement)) * 9 / polymerSimulator.getGeometry().getVolume() * displacement * 2, 0}, {0, 0}};
        System.out.println(expResult[0][0]);
        double[][] result = StressFinder.calculateTotalStress(polymerSimulator);
        double fractionalError = getFractionalError(expResult, result);
        assertEquals(0, fractionalError, .00001);
    }

}
