/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import Engine.ExternalEnergyCalculator;
import Engine.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PhysicalConstants;
import Engine.PolymerChain;
import Engine.PolymerCluster;
import Engine.PolymerSimulator;
import Engine.SimulationParameters;
import Engine.SystemAnalyzer;
import Engine.SystemGeometry.AbstractGeometry.AbstractGeometryBuilder;
import Engine.SystemGeometry.PeriodicGeometry;
import Engine.SystemGeometry.SystemGeometry;
import Gui.SystemViewer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author bmoths
 */
public class SurfaceTensionFinder {

    static public final class MeasuredSurfaceTension {

        public final double surfaceTension;
        public final double surfaceTensionStandardError;

        public MeasuredSurfaceTension(double surfaceTension, double surfaceTensionStandardError) {
            this.surfaceTension = surfaceTension;
            this.surfaceTensionStandardError = surfaceTensionStandardError;
        }

    }

    static private final int numBeadsPerChain = 15;

    public static void main(String[] args) {

        final int numChains;
        final ExternalEnergyCalculator externalEnergyCalculator;
        final double density;

        if (args.length == 4) {
            numChains = Integer.parseInt(args[0]);
            final double xQuadratic = Double.parseDouble(args[1]);
            final double xTension = Double.parseDouble(args[2]);
            ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
            externalEnergyCalculatorBuilder.setxQuadratic(xQuadratic);
            externalEnergyCalculatorBuilder.setxTension(xTension);
            externalEnergyCalculator = externalEnergyCalculatorBuilder.build();
            density = Double.parseDouble(args[3]);
        } else {
            numChains = 100;//100
            final ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
            externalEnergyCalculatorBuilder.setxTension(-50.); //was -50
            externalEnergyCalculatorBuilder.setxQuadratic(.15); //was .2
            externalEnergyCalculator = externalEnergyCalculatorBuilder.build();
            density = .15; //.15
        }


        final SurfaceTensionFinder surfaceTensionFinder;
        try {
            surfaceTensionFinder = new SurfaceTensionFinder(externalEnergyCalculator, density, numChains);
            surfaceTensionFinder.findSurfaceTension();
            surfaceTensionFinder.closeWriter();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SurfaceTensionFinder.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("File not able to be opened");
        }
    }

    //<editor-fold defaultstate="collapsed" desc="makePolymerSimulator">
    static private SystemGeometry makeSystemGeometry(PolymerCluster polymerCluster, SimulationParameters simulationParameters) {
        AbstractGeometryBuilder systemGeometryBuilder = new PeriodicGeometry.PeriodicGeometryBuilder();

        systemGeometryBuilder.setDimension(2);
        systemGeometryBuilder.makeConsistentWith(polymerCluster, simulationParameters);
        return systemGeometryBuilder.buildGeometry();
    }
//</editor-fold>

    static public DescriptiveStatistics generateLengthStatistics(int numSamples, PolymerSimulator polymerSimulator) {
        final int iterationsPerSample = 100000;
        int numSamplesTaken = 0;
        SystemAnalyzer systemAnalyzer = polymerSimulator.getSystemAnalyzer();

        DescriptiveStatistics lengthStatistics = new DescriptiveStatistics(numSamples);
        while (numSamplesTaken < numSamples) {
//            System.out.println(Integer.toString(100 * numSamplesTaken / numSamples) + "% done collecting statisitcs.");
            polymerSimulator.doIterations(iterationsPerSample);
            lengthStatistics.addValue(systemAnalyzer.getSystemGeometry().getRMax()[0]);
            numSamplesTaken++;
        }

        return lengthStatistics;
    }

    static public void outputSurfaceTension(MeasuredSurfaceTension measuredSurfaceTension) {
        System.out.println("Surface Tension found: " + Double.toString(measuredSurfaceTension.surfaceTension) + "+/-" + Double.toString(measuredSurfaceTension.surfaceTensionStandardError));
    }

    static public MeasuredSurfaceTension calculateSurfaceTension(DescriptiveStatistics lengthStatistics, PolymerSimulator polymerSimulator) {
        final long numSamples = lengthStatistics.getN();

        final double averageLength = lengthStatistics.getMean();
        final double lengthStandardDeviation = lengthStatistics.getStandardDeviation();

        final ExternalEnergyCalculator externalEnergyCalculator = polymerSimulator.getPhysicalConstants().getExternalEnergyCalculator();
        final double xTension = externalEnergyCalculator.getxTension();
        final double xQuadratic = externalEnergyCalculator.getxQuadratic();

        final double surfaceTension = -xTension - 2 * xQuadratic * averageLength; //should divide by two since there are two surfaces
        final double surfaceTensionStandardDeviation = 2 * xQuadratic * lengthStandardDeviation;
        final double surfaceTensionStandardError = surfaceTensionStandardDeviation / Math.sqrt(numSamples - 1);
        return new MeasuredSurfaceTension(surfaceTension, surfaceTensionStandardError);
    }

    static public void findSurfaceTensionWithParamters(int numChains, ExternalEnergyCalculator externalEnergyCalculator, double density) {
        PolymerSimulator polymerSimulator = makePolymerSimulator(numChains, externalEnergyCalculator, density);
        polymerSimulator.columnRandomizePositions();
        SystemViewer systemViewer = new SystemViewer(polymerSimulator);
        systemViewer.setVisible(true);

        System.out.println("System is initialized.");

        for (int i = 0; i < 10; i++) {
            polymerSimulator.doIterations(3000000);
            polymerSimulator.anneal();
            System.out.println("First equilibrate anneal iteration done.");
        }

        for (int i = 0; i < 7; i++) {
            System.out.println("Equilibrating System");

            polymerSimulator.anneal();
            polymerSimulator.equilibrate();

            System.out.println("System equilibrated.");
            System.out.println("Gathering statistics to find equilibrium length.");

            final int numSamples = 100;
            DescriptiveStatistics lengthStatistics = generateLengthStatistics(numSamples, polymerSimulator);
            MeasuredSurfaceTension measuredSurfaceTension = calculateSurfaceTension(lengthStatistics, polymerSimulator);
            outputSurfaceTension(measuredSurfaceTension);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="makePolymerSimulator">
    static private PolymerSimulator makePolymerSimulator(int numChains, ExternalEnergyCalculator externalEnergyCalculator, double density) {

        PolymerCluster polymerCluster = makePolymerCluster(numChains, density);
        PhysicalConstants physicalConstants = makePhysicalConstants(externalEnergyCalculator);
        SimulationParameters simulationParameters = new SimulationParameters(physicalConstants.idealStepLength(), 4);
        simulationParameters = simulationParameters.makeParametersFromPhysicalConstants(physicalConstants);
        physicalConstants = physicalConstants.getPhysicalConstantsFromParameters(simulationParameters);
        SystemGeometry systemGeometry = makeSystemGeometry(polymerCluster, simulationParameters);

        return new PolymerSimulator(
                systemGeometry,
                polymerCluster,
                physicalConstants);

    }

    static private PolymerCluster makePolymerCluster(int numChains, double density) {
        PolymerChain polymerChain = PolymerChain.makeChainStartingWithA(0, numBeadsPerChain);
        PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, numChains);
        polymerCluster.setConcentrationInWater(density);
        return polymerCluster;
    }

    static private PhysicalConstants makePhysicalConstants(ExternalEnergyCalculator externalEnergyCalculator) {
        PhysicalConstants.PhysicalConstantsBuilder physicalConstantsBuilder = new PhysicalConstants.PhysicalConstantsBuilder();

        physicalConstantsBuilder.setTemperature(1);
        physicalConstantsBuilder.setAAOverlapCoefficient(0);
        physicalConstantsBuilder.setBBOverlapCoefficient(-.06);
        physicalConstantsBuilder.setSpringCoefficient(1);

        physicalConstantsBuilder.setExternalEnergyCalculator(externalEnergyCalculator);

        return physicalConstantsBuilder.buildPhysicalConstants();
    }
//</editor-fold>

    private final int numAnneals = 50; //50
    private final int numSurfaceTensionTrials = 70; //70
    private final ExternalEnergyCalculator externalEnergyCalculator;
    private final double density;
    private final int numChains;
    private final PrintWriter dataWriter;

    public SurfaceTensionFinder(ExternalEnergyCalculator externalEnergyCalculator, double density, int numChains) throws FileNotFoundException {
        this.externalEnergyCalculator = externalEnergyCalculator;
        this.density = density;
        this.numChains = numChains;
        dataWriter = makeDataWriter();
        writeParamters();
    }

    private PrintWriter makeDataWriter() throws FileNotFoundException {
        final String path = "/home/bmoths/Desktop/projects/polymerMicelles/simulation/simulationResults/";
        File file;
        int fileNameNumber = -1;
        String fileName;
        do {
            fileNameNumber++;
            fileName = makeFileName(fileNameNumber);
            file = new File(path + fileName);
        } while (file.exists());

        return new PrintWriter(path + fileName);
    }

    private String makeFileName(int fileNameNumber) {
        StringBuilder fileNameBuilder = new StringBuilder();
        String datePrefix = makeDatePrefix();
        fileNameBuilder.append(datePrefix).append("_");
        fileNameBuilder.append(makeDoubleDigitString(fileNameNumber));
        return fileNameBuilder.toString();
    }

    private String makeDatePrefix() {
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
        return fileNameBuilder.toString();
    }

    private String makeDoubleDigitString(int num) {
        num %= 100;
        StringBuilder stringBuilder = new StringBuilder();
        if (num < 10) {
            stringBuilder.append("0");
        }
        stringBuilder.append(Integer.toString(num));
        return stringBuilder.toString();
    }

    private void writeParamters() {
        dataWriter.println("Number of Chains: " + Integer.toString(numChains));
        dataWriter.println("Number of Beads per Chain: " + Integer.toString(numBeadsPerChain));
        dataWriter.println("E=aL^2+bL with a: " + Double.toString(externalEnergyCalculator.getxQuadratic()));
        dataWriter.println("b: " + Double.toString(externalEnergyCalculator.getxTension()));
        dataWriter.println("Density: " + Double.toString(density));
        dataWriter.println("number  of anneals: " + Integer.toString(numAnneals));
        dataWriter.println("number of iterations finding surface tension: " + Integer.toString(numSurfaceTensionTrials));
        dataWriter.println("=====================");
        dataWriter.println();
    }

    public void findSurfaceTension() {
        PolymerSimulator polymerSimulator = makePolymerSimulator(numChains, externalEnergyCalculator, density);
        polymerSimulator.columnRandomizePositions();
        try {
            SystemViewer systemViewer = new SystemViewer(polymerSimulator);
            systemViewer.setVisible(true);
        } catch (java.awt.HeadlessException e) {
            System.out.println("Headless exception thrown when creating system viewer. I am unable to create system viewer.");
        }

        System.out.println("System is initialized.");

        for (int i = 0; i < numAnneals; i++) {
            polymerSimulator.doIterations(3000000);
            polymerSimulator.anneal();
            System.out.println("equilibrate anneal iteration done.");
        }

        for (int i = 0; i < numSurfaceTensionTrials; i++) {
            System.out.println("Equilibrating System");

            polymerSimulator.anneal();
            polymerSimulator.equilibrate();

            System.out.println("System equilibrated.");
            System.out.println("Gathering statistics to find equilibrium length.");

            final int numSamples = 100;
            DescriptiveStatistics lengthStatistics = generateLengthStatistics(numSamples, polymerSimulator);
            MeasuredSurfaceTension measuredSurfaceTension = calculateSurfaceTension(lengthStatistics, polymerSimulator);
            outputSurfaceTension(measuredSurfaceTension);
            writeSurfaceTensionToFile(measuredSurfaceTension);
        }
    }

    private void writeSurfaceTensionToFile(MeasuredSurfaceTension measuredSurfaceTension) {
        dataWriter.println("Surface Tension found: " + Double.toString(measuredSurfaceTension.surfaceTension) + "+/-" + Double.toString(measuredSurfaceTension.surfaceTensionStandardError));
    }

    public void closeWriter() {
        dataWriter.close();
    }

}
