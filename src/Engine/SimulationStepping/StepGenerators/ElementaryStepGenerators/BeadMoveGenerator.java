/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators;

import Engine.PolymerState.SystemGeometry.Interfaces.ImmutableSystemGeometry;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.SimulationStep;
import Engine.SimulationStepping.StepTypes.SingleBeadStep;
import Engine.SimulationStepping.StepTypes.ZeroStep;
import Engine.SystemAnalyzer;
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
        if (systemAnalyzer.getNumBeads() == 0) {
            return ZeroStep.getZeroStep();
        }
        final int bead = getRandomBeadNumber(systemAnalyzer);
        final double[] stepVector = getStepVector(systemAnalyzer);

        return new SingleBeadStep(bead, stepVector);
    }

    static private int getRandomBeadNumber(SystemAnalyzer systemAnalyzer) {
        return random.nextInt(systemAnalyzer.getNumBeads());
    }

    static private double[] getStepVector(SystemAnalyzer systemAnalyzer) {
        ImmutableSystemGeometry systemGeometry = systemAnalyzer.getSystemGeometry();
        return systemGeometry.randomGaussian(.4);
    }

    public BeadMoveGenerator() {
    }

    @Override
    public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
        return getBeadMove(systemAnalyzer);
    }

}
