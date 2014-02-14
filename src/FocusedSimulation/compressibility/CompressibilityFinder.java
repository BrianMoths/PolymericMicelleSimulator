/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.compressibility;

import Engine.PolymerSimulator;
import Engine.SimulatorParameters;
import FocusedSimulation.JobParameters;
import FocusedSimulation.SimulationRunner;
import FocusedSimulation.surfacetension.SurfaceTensionJobMaker;
import FocusedSimulation.surfacetension.SurfaceTensionOutputWriter;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;

/**
 *
 * @author bmoths
 */
public class CompressibilityFinder {

    public static void main(String[] args) {
    }

    private static Input readInput(String[] args) {
        if (args.length == 0) {
            final double verticalScaleFactor = .25;
            final double horizontalScaleFactor = 10;

            InputBuilder inputBuilder = SurfaceTensionJobMaker.makeRescaleInputBuilderWithHorizontalRescaling(verticalScaleFactor, horizontalScaleFactor, 0);
            inputBuilder.getJobParametersBuilder().setNumAnneals(5);
            return inputBuilder.buildInput();
        } else if (args.length == 1) {
            final String fileName = args[0];
            return Input.readInputFromFile(fileName);
        } else {
            throw new IllegalArgumentException("At most one input allowed");
        }
    }

    private final JobParameters jobParameters;
    private final SimulatorParameters systemParameters;
    private final SurfaceTensionOutputWriter outputWriter;
    private final PolymerSimulator polymerSimulator;
    private final SimulationRunner simulationRunner;

    public CompressibilityFinder(Input input) {
    }

}
