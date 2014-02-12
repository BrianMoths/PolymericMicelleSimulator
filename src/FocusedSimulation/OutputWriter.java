/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import Engine.PolymerSimulator;
import Engine.PolymerState.SystemGeometry.Interfaces.ImmutableSystemGeometry;
import Engine.SystemAnalyzer;
import FocusedSimulation.surfacetension.SurfaceTensionFinder;
import FocusedSimulation.surfacetension.SurfaceTensionFinder.MeasuredSurfaceTension;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Calendar;

/**
 *
 * @author brian
 */
public class OutputWriter {

    static public String makeDatePrefix() {
        StringBuilder fileNameBuilder = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final int second = calendar.get(Calendar.SECOND);
        fileNameBuilder.append(makeDoubleDigitString(year))
                .append("_")
                .append(makeDoubleDigitString(month))
                .append("_")
                .append(makeDoubleDigitString(day))
                .append("_")
                .append(makeDoubleDigitString(hour))
                .append("_")
                .append(makeDoubleDigitString(minute))
                .append("_")
                .append(makeDoubleDigitString(second));
        //                .append("_")
        //                .append((surfaceTensionFinder.hashCode() + surfaceTensionFinder.getInputParameters().hashCode()) % 1000);
        return fileNameBuilder.toString();
    }

    static public String makeDoubleDigitString(int num) {
        num %= 100;
        StringBuilder stringBuilder = new StringBuilder();
        if (num < 10) {
            stringBuilder.append("0");
        }
        stringBuilder.append(Integer.toString(num));
        return stringBuilder.toString();
    }

    static public String getProjectPath() throws AssertionError {
        String jarPath = OutputWriter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (jarPath.contains("build/")) {
            jarPath = jarPath.substring(0, jarPath.lastIndexOf("build/"));
        } else if (jarPath.contains("dist/")) {
            jarPath = jarPath.substring(0, jarPath.lastIndexOf("dist/"));
        } else {
            throw new AssertionError("jar is neither in build/ or dist/", null);
        }
        return jarPath;
    }

    private final PrintWriter dataWriter;
    private final SurfaceTensionFinder surfaceTensionFinder;

    public OutputWriter(final SurfaceTensionFinder surfaceTensionFinder) throws FileNotFoundException {
        this.surfaceTensionFinder = surfaceTensionFinder;
        dataWriter = makeDataWriter();
    }

    private PrintWriter makeDataWriter() throws FileNotFoundException {
        String projectPath = getProjectPath();
        System.out.println(projectPath);
        final String path = projectPath + "../simulationResults/";
        int fileNameNumber = surfaceTensionFinder.getJobNumber();
        String fileName;
        fileName = makeFileName(fileNameNumber);

        return new PrintWriter(path + fileName);
    }

    private String makeFileName(int fileNameNumber) {
        StringBuilder fileNameBuilder = new StringBuilder();
        String datePrefix = makeDatePrefix();
        fileNameBuilder.append(datePrefix);
        fileNameBuilder.append("_").append(makeDoubleDigitString(fileNameNumber));
        return fileNameBuilder.toString();
    }

    public void printParameters() {
        String parametersString = makeParametersString();
        System.out.print(parametersString);
        dataWriter.print(parametersString);
        dataWriter.flush();
    }

    private String makeParametersString() {
        final int numBeadsPerChain = surfaceTensionFinder.getNumBeadsPerChain();
        final int numAnneals = surfaceTensionFinder.getNumAnneals();
        final int numSurfaceTensionTrials = surfaceTensionFinder.getNumSurfaceTensionTrials();
        final double beadSideLength = surfaceTensionFinder.getBeadSize();

        StringBuilder parametersStringBuilder = new StringBuilder();
        parametersStringBuilder
                .append("Number of Chains: ").append(Integer.toString(surfaceTensionFinder.getNumChains())).append("\n")
                .append("Number of Beads per Chain: ").append(Integer.toString(numBeadsPerChain)).append("\n")
                .append("E=(1/2)a(L-b)^2 with a: ").append(Double.toString(surfaceTensionFinder.getExternalEnergyCalculator().getxSpringConstant())).append("\n")
                .append("b: ").append(Double.toString(surfaceTensionFinder.getExternalEnergyCalculator().getxEquilibriumPosition())).append("\n")
                .append("Density: ").append(Double.toString(surfaceTensionFinder.getDensity())).append("\n")
                .append("Side length of beads: ")
                .append(Double.toString(beadSideLength)).append("\n")
                .append("number  of anneals: ").append(Integer.toString(numAnneals)).append("\n")
                .append("number of iterations finding surface tension: ").append(Integer.toString(numSurfaceTensionTrials)).append("\n")
                .append("=====================").append("\n")
                .append("\n");
        return parametersStringBuilder.toString();
    }

    public void printSurfaceTension(MeasuredSurfaceTension measuredSurfaceTension) {
        String surfaceTensionString = makeSurfaceTensionString(measuredSurfaceTension);
        dataWriter.print(surfaceTensionString);
        dataWriter.flush();
        System.out.print(surfaceTensionString);
    }

    private String makeSurfaceTensionString(MeasuredSurfaceTension measuredSurfaceTension) {
        StringBuilder parametersStringBuilder = new StringBuilder();
        parametersStringBuilder
                .append("Surface Tension found: ")
                .append(Double.toString(measuredSurfaceTension.surfaceTension))
                .append(" +/- ")
                .append(Double.toString(measuredSurfaceTension.surfaceTensionStandardError))
                .append("\n");
        return parametersStringBuilder.toString();
    }

    public void printStress(DoubleWithUncertainty stress11, DoubleWithUncertainty stress12, DoubleWithUncertainty stress22) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[").append(stress11.getValue()).append("  ").append(stress12.getValue()).append("]  +/-  [").append(stress11.getUncertainty()).append(" ").append(stress12.getUncertainty()).append("]\n");
        stringBuilder.append("[").append(stress12.getValue()).append("  ").append(stress22.getValue()).append("]  +/-  [").append(stress12.getUncertainty()).append(" ").append(stress22.getUncertainty()).append("]\n");
        stringBuilder.append("\n");
        final String outputString = stringBuilder.toString();
        dataWriter.print(outputString);
        dataWriter.flush();
        System.out.println(outputString);
    }

    public void printFinalOutput(PolymerSimulator polymerSimulator) {
        String finalOutputString = makeFinalOutputString(polymerSimulator);
        System.out.print(finalOutputString);
        dataWriter.print(finalOutputString);
        dataWriter.flush();
    }

    private String makeFinalOutputString(PolymerSimulator polymerSimulator) {
        final SystemAnalyzer systemAnalyzer = polymerSimulator.getSystemAnalyzer();
        final int numBeads = systemAnalyzer.getNumBeads();
        final double beadArea = systemAnalyzer.findArea();

        final ImmutableSystemGeometry systemGeometry = systemAnalyzer.getSystemGeometry();
        final double totalArea = systemGeometry.getVolume();
        final double width = systemGeometry.getSizeOfDimension(0);
        final double height = systemGeometry.getSizeOfDimension(1);

        StringBuilder parametersStringBuilder = new StringBuilder();
        parametersStringBuilder
                .append("\n")
                .append("fraction of area covered at end of simulation: ").append(Double.toString(beadArea / totalArea)).append("\n").append("number density of blob at end of simulation: ")
                .append(Double.toString(numBeads / beadArea)).append("\n")
                .append("horizontal size of system at end of simulation: ")
                .append(Double.toString(width)).append("\n")
                .append("vertical size of system at end of simulation: ")
                .append(Double.toString(height)).append("\n");

        return parametersStringBuilder.toString();
    }

    public void closeWriter() {
        dataWriter.close();
    }

    public void printInitializationInfo(PolymerSimulator polymerSimulator) {
        dataWriter.println("Initial Horizontal Size of System: " + polymerSimulator.getGeometry().getSizeOfDimension(0));
        dataWriter.println();
        dataWriter.flush();

        System.out.println("Initial Horizontal Size of System: " + polymerSimulator.getGeometry().getSizeOfDimension(0));
        System.out.println();
    }

}
