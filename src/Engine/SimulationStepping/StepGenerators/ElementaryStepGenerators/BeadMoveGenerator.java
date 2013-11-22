/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators;

import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.SimulationStep;
import Engine.SimulationStepping.StepTypes.SingleBeadStep;
import Engine.SystemAnalyzer;
import Engine.SystemGeometry.SystemGeometry;
import java.util.Random;

/**
 *
 * @author bmoths
 */
public class BeadMoveGenerator implements StepGenerator {

    static private Random random;

    static {
        random = new Random();
    }

    static public SimulationStep getBeadMove(SystemAnalyzer systemAnalyzer) {
        final int bead = getBeadNumber(systemAnalyzer);
        final double[] stepVector = getStepVector(systemAnalyzer);

        return new SingleBeadStep(bead, stepVector);
    }

    static private int getBeadNumber(SystemAnalyzer systemAnalyzer) {
        return random.nextInt(systemAnalyzer.getNumBeads());
    }

    static private double[] getStepVector(SystemAnalyzer systemAnalyzer) {
        SystemGeometry systemGeometry = systemAnalyzer.getSystemGeometry();
        return systemGeometry.randomGaussian();
    }

    public BeadMoveGenerator() {
    }

    @Override
    public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
        return getBeadMove(systemAnalyzer);
    }

}
