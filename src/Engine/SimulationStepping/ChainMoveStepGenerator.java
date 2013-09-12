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
public class ChainMoveStepGenerator implements StepGenerator {

    static private Random random;

    static {
        random = new Random();
    }

    private static SimulationStep getChainMove(SystemAnalyzer systemAnalyzer) {
        List<Integer> chain = getChain(systemAnalyzer);
        final double[] stepVector = getStepVector(systemAnalyzer); //need to make this bigger

        stepVector[0] *= 1;
        stepVector[1] *= 1;

//        for (int i = 0; i < stepVector.length; i++) {
//            stepVector[i] *= 10;
//        }

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

    static private double[] getStepVector(SystemAnalyzer systemAnalyzer) {
        SystemGeometry systemGeometry = systemAnalyzer.getSystemGeometry();
        return systemGeometry.randomGaussian();
    }

    final private double chainMoveChance;

    public ChainMoveStepGenerator() {
        chainMoveChance = 0;
    }

    public ChainMoveStepGenerator(double chainMoveChance) {
        this.chainMoveChance = chainMoveChance;
    }

    @Override
    public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
        if (random.nextDouble() < chainMoveChance) {
            return getChainMove(systemAnalyzer);
        } else {
            return getBeadMove(systemAnalyzer);
        }
    }

}
