/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators;

import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.SimulationStep;
import Engine.SimulationStepping.StepTypes.SingleChainStep;
import Engine.SystemAnalyzer;
import Engine.SystemGeometry.Interfaces.SystemGeometry;
import java.util.List;
import java.util.Random;

/**
 *
 * @author bmoths
 */
public class ChainMoveStepGenerator implements StepGenerator {

    static private Random random;

    static {
        random = new Random();
    }

    public static SimulationStep getChainMove(SystemAnalyzer systemAnalyzer) {
        List<Integer> chain = getChain(systemAnalyzer);
        final double[] stepVector = getStepVector(systemAnalyzer); //need to make this bigger

//        stepVector[0] *= 1;
//        stepVector[1] *= 1;

        return new SingleChainStep(chain, stepVector);
    }

    static private int getBeadNumber(SystemAnalyzer systemAnalyzer) {
        return random.nextInt(systemAnalyzer.getNumBeads());
    }

    static private List<Integer> getChain(SystemAnalyzer systemAnalyzer) {
        final int bead = getBeadNumber(systemAnalyzer);
        return systemAnalyzer.getChainOfBead(bead);
    }

    static private double[] getStepVector(SystemAnalyzer systemAnalyzer) {
        SystemGeometry systemGeometry = systemAnalyzer.getSystemGeometry();
        return systemGeometry.randomGaussian();
    }

    @Override
    public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
        return getChainMove(systemAnalyzer);
    }

}
