/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping;

import Engine.SystemAnalyzer;
import Engine.SystemGeometry.SystemGeometry;
import java.util.List;
import java.util.Random;

/**
 *
 * @author bmoths
 */
public class StepGenerator {

    static private Random random;
    static final private double chainMoveChance = .1;

    static {
        random = new Random();
    }

    static public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
        if (random.nextDouble() < chainMoveChance) {
            return getChainMove(systemAnalyzer);
        } else {
            return getBeadMove(systemAnalyzer);
        }
    }

    static private SimulationStep getChainMove(SystemAnalyzer systemAnalyzer) {
        List<Integer> chain = getChain(systemAnalyzer);
        final double[] stepVector = getStepVector(systemAnalyzer); //need to make this bigger

        return new SingleChainStep(chain, stepVector);
    }

    static private SimulationStep getBeadMove(SystemAnalyzer systemAnalyzer) {
        final int bead = getBeadNumber(systemAnalyzer);
        final double[] stepVector = getStepVector(systemAnalyzer);

        return new SingleBeadStep(bead, stepVector);
    }

    static private int getBeadNumber(SystemAnalyzer systemAnalyzer) {
        return random.nextInt(systemAnalyzer.getNumBeads());
    }

    static private List<Integer> getChain(SystemAnalyzer systemAnalyzer) {
        final int bead = getBeadNumber(systemAnalyzer);
        return systemAnalyzer.getChainOfBead(bead);
    }

    private static double[] getStepVector(SystemAnalyzer systemAnalyzer) {
        SystemGeometry systemGeometry = systemAnalyzer.getSystemGeometry();
        return systemGeometry.randomGaussian();
    }
}
