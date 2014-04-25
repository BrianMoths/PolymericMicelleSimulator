/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.Energetics.EnergeticsConstants;
import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.PolymerState.SystemGeometry.Implementations.AbstractGeometry.AbstractGeometryBuilder;
import Engine.PolymerState.SystemGeometry.Implementations.PeriodicGeometry.PeriodicGeometryBuilder;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
import Engine.SimulationStepping.StepGenerators.CompoundStepGenerators.GeneralStepGenerator;
import java.io.Serializable;

/**
 *
 * @author bmoths
 */
public class SimulatorParameters implements Serializable {

    static public class SystemParametersBuilder {

        private static final long serialVersionUID = 0L;
        static private final double defaultAspectRatio = .1;
        static private final double defaultOverlapCoefficient = -.06;
        static private final double defaultInteractionLength = 4.;
        static private final double defaultXPosition = 50;
        static private final double defaultSpringConstant = 10;
        static private final int defaultNumBeadsPerChain = 15;
        static private final int defaultNumChains = 75;
        static private final double defaultDensity = .05;

        public static SystemParametersBuilder getDefaultSystemParametersBuilder() {
            SystemParametersBuilder systemParametersBuilder = new SystemParametersBuilder();
            systemParametersBuilder.setAspectRatio(defaultAspectRatio);
            EnergeticsConstantsBuilder energeticsConstantsBuilder = EnergeticsConstantsBuilder.zeroEnergeticsConstantsBuilder();
            energeticsConstantsBuilder.setBBOverlapCoefficient(defaultOverlapCoefficient);
            ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = energeticsConstantsBuilder.getExternalEnergyCalculatorBuilder();
            externalEnergyCalculatorBuilder.setXPositionAndSpringConstant(defaultXPosition, defaultSpringConstant);
            systemParametersBuilder.setEnergeticsConstantsBuilder(energeticsConstantsBuilder);
            systemParametersBuilder.setInteractionLength(defaultInteractionLength);
            systemParametersBuilder.setPolymerCluster(getDefaultPolymerCluster());
            return systemParametersBuilder;
        }

        private static PolymerCluster getDefaultPolymerCluster() {
            PolymerChain polymerChain = PolymerChain.makeChainStartingWithA(0, defaultNumBeadsPerChain);
            PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, defaultNumChains);
            polymerCluster.setConcentrationInWater(defaultDensity);
            return polymerCluster;
        }

        private double interactionLength;
        private double coreLength = 0;
        private EnergeticsConstantsBuilder energeticsConstantsBuilder;
        private PolymerCluster polymerCluster;
        private double aspectRatio;
        private GeneralStepGenerator generalStepGenerator;

        public SimulatorParameters buildSystemParametersWithAutomaticHardOverlap() {
            autosetCoreParameters();
            return buildSystemParameters();
        }

        public SimulatorParameters buildSystemParameters() {
            GeometricalParameters geometricalParameters = new GeometricalParameters(interactionLength, energeticsConstantsBuilder, coreLength);
            SystemGeometry systemGeometry = makeSystemGeometry(geometricalParameters);
            final EnergeticsConstants energeticsConstants = energeticsConstantsBuilder.buildEnergeticsConstants();
            return new SimulatorParameters(systemGeometry, polymerCluster, energeticsConstants);
        }

        public void autosetCoreParameters() {
            GeometricalParameters geometricalParameters = new GeometricalParameters(interactionLength, energeticsConstantsBuilder);
            coreLength = geometricalParameters.getCoreLength();
            energeticsConstantsBuilder.setHardOverlapCoefficientFromParameters(geometricalParameters);
        }

        public double getInteractionLength() {
            return interactionLength;
        }

        public void setInteractionLength(double interactionLength) {
            this.interactionLength = interactionLength;
        }

        public EnergeticsConstantsBuilder getEnergeticsConstantsBuilder() {
            return energeticsConstantsBuilder;
        }

        public void setEnergeticsConstantsBuilder(EnergeticsConstantsBuilder energeticsConstantsBuilder) {
            this.energeticsConstantsBuilder = energeticsConstantsBuilder;
        }

        public PolymerCluster getPolymerCluster() {
            return polymerCluster;
        }

        public void setPolymerCluster(PolymerCluster polymerCluster) {
            this.polymerCluster = polymerCluster;
        }

        public double getAspectRatio() {
            return aspectRatio;
        }

        public void setAspectRatio(double aspectRatio) {
            this.aspectRatio = aspectRatio;
        }

        public GeneralStepGenerator getGeneralStepGenerator() {
            return generalStepGenerator;
        }

        public void setGeneralStepGenerator(GeneralStepGenerator generalStepGenerator) {
            this.generalStepGenerator = generalStepGenerator;
        }

        public double getCoreLength() {
            return coreLength;
        }

        public void setCoreLength(double coreLength) {
            this.coreLength = coreLength;
        }

        private SystemGeometry makeSystemGeometry(GeometricalParameters geometricalParameters) {
            final AbstractGeometryBuilder systemGeometryBuilder = new PeriodicGeometryBuilder();
            final int numDimensions = 2;
            systemGeometryBuilder.setDimension(numDimensions);
            systemGeometryBuilder.makeConsistentWith(polymerCluster.getNumBeadsIncludingWater(), geometricalParameters, aspectRatio);
            SystemGeometry systemGeometry = systemGeometryBuilder.buildGeometry();
            return systemGeometry;
        }

    }

    public final SystemGeometry systemGeometry;
    public final PolymerCluster polymerCluster;
    public final EnergeticsConstants energeticsConstants;

    public SimulatorParameters(SystemGeometry systemGeometry, PolymerCluster polymerCluster, EnergeticsConstants energeticsConstants) {
        this.systemGeometry = systemGeometry;
        this.polymerCluster = polymerCluster;
        this.energeticsConstants = energeticsConstants;
    }

    public PolymerSimulator makePolymerSimulator() {
        return new PolymerSimulator(systemGeometry, polymerCluster, energeticsConstants);
    }

    public SystemGeometry getSystemGeometry() {
        return systemGeometry;
    }

    public PolymerCluster getPolymerCluster() {
        return polymerCluster;
    }

    public EnergeticsConstants getEnergeticsConstants() {
        return energeticsConstants;
    }

}
