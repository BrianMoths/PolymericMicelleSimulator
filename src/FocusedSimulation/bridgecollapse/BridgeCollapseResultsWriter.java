/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.bridgecollapse;

import Engine.PolymerSimulator;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.output.AbstractResultsWriter;
import SGEManagement.Input;
import java.io.FileNotFoundException;

/**
 *
 * @author bmoths
 */
public class BridgeCollapseResultsWriter extends AbstractResultsWriter {

    public BridgeCollapseResultsWriter(Input input) throws FileNotFoundException {
        super(input);
    }

    public void printInitializationInfo(PolymerSimulator polymerSimulator) {
        final String initializationString = "Initial Horizontal Size of System: " + polymerSimulator.getGeometry().getSizeOfDimension(0) + "\n";
        printAndSoutString(initializationString);
    }

    public void printHorizontalSystemSize(DoubleWithUncertainty horizontalSystemSize) {
        final CharSequence horizontalSizeString = makeHorizontalSizeCharSequence(horizontalSystemSize);
        printAndSoutString(horizontalSizeString);
    }

    static private CharSequence makeHorizontalSizeCharSequence(DoubleWithUncertainty horizontalSystemSize) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("horizontal size of system: ")
                .append(Double.toString(horizontalSystemSize.getValue()))
                .append(" +/- ")
                .append(Double.toString(horizontalSystemSize.getUncertainty()))
                .append("\n");
        return stringBuilder;
    }

    public void printFractionOfAreaCovered(PolymerSimulator polymerSimulator) {
        final String fractionOfAreaCovered = makeFractionOfAreaCoveredString(polymerSimulator);
        printAndSoutString(fractionOfAreaCovered);
    }

    private String makeFractionOfAreaCoveredString(PolymerSimulator polymerSimulator) {
        final double totalArea = polymerSimulator.getGeometry().getVolume();
        final double beadArea = polymerSimulator.getSystemAnalyzer().findArea();
        return "fraction of area covered: " + Double.toString(beadArea / totalArea) + "\n";
    }

}
