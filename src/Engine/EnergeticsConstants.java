/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.SystemGeometry.GeometricalParameters;
import Engine.SystemGeometry.AreaOverlap;
import Engine.SystemGeometry.SystemGeometry;
import java.io.Serializable;
import java.util.Random;

/**
 *
 * @author bmoths
 */
public final class EnergeticsConstants implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="builder class">
    static public class PhysicalConstantsBuilder {

        private double temperature = 1,
                AAOverlapCoefficient = 5. / 120.,
                BBOverlapCoefficient = 5. / 120.,
                ABOverlapCoefficient = .1,
                springCoefficient = 1. / 3.,
                hardOverlapCoefficient = 0;
        private ExternalEnergyCalculator externalEnergyCalculator = new ExternalEnergyCalculator();

        public EnergeticsConstants buildPhysicalConstants() {
            hardOverlapCoefficient = calculateHardOverlapCoefficient();
            return new EnergeticsConstants(this);
        }

        public double getTemperature() {
            return temperature;
        }

        public PhysicalConstantsBuilder setTemperature(double temperature) {
            if (temperature > 0) {
                this.temperature = temperature;
            }
            return this;
        }

        public double getAAOverlapCoefficient() {
            return AAOverlapCoefficient;
        }

        public PhysicalConstantsBuilder setAAOverlapCoefficient(double AAOverlapCoefficient) {
            this.AAOverlapCoefficient = AAOverlapCoefficient;
            return this;
        }

        public double getBBOverlapCoefficient() {
            return BBOverlapCoefficient;
        }

        public PhysicalConstantsBuilder setBBOverlapCoefficient(double BBOverlapCoefficient) {
            this.BBOverlapCoefficient = BBOverlapCoefficient;
            return this;
        }

        public double getABOverlapCoefficient() {
            return ABOverlapCoefficient;
        }

        public PhysicalConstantsBuilder setABOverlapCoefficient(double ABOverlapCoefficient) {
            this.ABOverlapCoefficient = ABOverlapCoefficient;
            return this;
        }

        public double getSpringCoefficient() {
            return springCoefficient;
        }

        public PhysicalConstantsBuilder setSpringCoefficient(double springCoefficient) {
            this.springCoefficient = springCoefficient;
            return this;
        }

        public double getHardOverlapCoefficient() {
            return hardOverlapCoefficient;
        }

        public PhysicalConstantsBuilder setHardOverlapCoefficient(double hardOverlapCoefficient) {
            this.hardOverlapCoefficient = hardOverlapCoefficient;
            return this;
        }

        private double calculateHardOverlapCoefficient() {
            return 3 * Math.max(
                    Math.max(
                    Math.abs(AAOverlapCoefficient),
                    Math.abs(BBOverlapCoefficient)),
                    Math.abs(ABOverlapCoefficient));
        }

        public ExternalEnergyCalculator getExternalEnergyCalculator() {
            return externalEnergyCalculator;
        }

        public void setExternalEnergyCalculator(ExternalEnergyCalculator externalEnergyCalculator) {
            this.externalEnergyCalculator = externalEnergyCalculator;
        }

    }
    //</editor-fold>

    static private Random randomNumberGenerator = new Random();

    static public EnergeticsConstants defaultPhysicalConstants() {
        return new PhysicalConstantsBuilder().buildPhysicalConstants();
    }

    private final double temperature,
            AAOverlapCoefficient,
            BBOverlapCoefficient,
            ABOverlapCoefficient,
            hardOverlapCoefficient,
            springCoefficient;
    private final ExternalEnergyCalculator externalEnergyCalculator;

    private EnergeticsConstants(EnergeticsConstants physicalConstants, GeometricalParameters parameters) {
        temperature = physicalConstants.temperature;
        AAOverlapCoefficient = physicalConstants.AAOverlapCoefficient;
        BBOverlapCoefficient = physicalConstants.BBOverlapCoefficient;
        ABOverlapCoefficient = physicalConstants.ABOverlapCoefficient;
        springCoefficient = physicalConstants.springCoefficient;
        hardOverlapCoefficient = hardOverlapCoefficientFromParameters(parameters);
        externalEnergyCalculator = physicalConstants.externalEnergyCalculator;
//        ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
//        externalEnergyCalculatorBuilder.xTension = -50.;
//        externalEnergyCalculatorBuilder.xQuadratic = .2;
//
//        externalEnergyCalculator = externalEnergyCalculatorBuilder.build();
    }

    private EnergeticsConstants(PhysicalConstantsBuilder physicalConstantsBuilder) {
        temperature = physicalConstantsBuilder.temperature;
        AAOverlapCoefficient = physicalConstantsBuilder.AAOverlapCoefficient;
        BBOverlapCoefficient = physicalConstantsBuilder.BBOverlapCoefficient;
        ABOverlapCoefficient = physicalConstantsBuilder.ABOverlapCoefficient;
        hardOverlapCoefficient = physicalConstantsBuilder.hardOverlapCoefficient;
        springCoefficient = physicalConstantsBuilder.springCoefficient;
        externalEnergyCalculator = physicalConstantsBuilder.externalEnergyCalculator;

    }

    public EnergeticsConstants getPhysicalConstantsFromParameters(GeometricalParameters parameters) {
        return new EnergeticsConstants(this, parameters);
    }

    private double hardOverlapCoefficientFromParameters(GeometricalParameters parameters) {
        if (parameters.getCoreLength() > 1e-10) {
            final double bondingEnergyInT = .5;
            final double coreRepulsionInT = 5; //5
            double minCoefficientForBonding = -bondingEnergyInT * temperature / (parameters.getInteractionLength() * parameters.getInteractionLength());
            double minAttraction = Math.min(Math.min(BBOverlapCoefficient, AAOverlapCoefficient), minCoefficientForBonding);
            return (coreRepulsionInT * temperature - parameters.getInteractionLength() * parameters.getInteractionLength() * minAttraction) / (parameters.getCoreLength() * parameters.getCoreLength());
        } else {
            return 0;
        }
    }

    public double springEnergy(double squareLength) {
        return springCoefficient * squareLength;
    }

    public double densityEnergy(AreaOverlap areaOverlap) {
        return 2 * (AAOverlapCoefficient * areaOverlap.AAOverlap
                + BBOverlapCoefficient * areaOverlap.BBOverlap
                + ABOverlapCoefficient * areaOverlap.ABOverlap
                + hardOverlapCoefficient * areaOverlap.hardOverlap);
    }

    public double externalEnergy(SystemGeometry systemGeometry) {
        return externalEnergyCalculator.calculateExternalEnergy(systemGeometry.getRMax());
    }

    public boolean isEnergeticallyAllowed(double energyChange) {
        return energyChange < 0
                || randomNumberGenerator.nextDouble() < Math.exp(-energyChange / temperature);
    }

    public double idealStepLength() {
        return Math.sqrt(getTemperature() / getSpringCoefficient());
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Temperature: ").append(Double.toString(temperature)).append("\n");
        stringBuilder.append("AA Overlap Coefficient: ").append(Double.toString(AAOverlapCoefficient)).append("\n");
        stringBuilder.append("BB Overlap Coefficient: ").append(Double.toString(BBOverlapCoefficient)).append("\n");
        stringBuilder.append("AB Overlap Coefficient: ").append(Double.toString(ABOverlapCoefficient)).append("\n");
        stringBuilder.append("Hard Overlap Coefficient: ").append(Double.toString(hardOverlapCoefficient)).append("\n");
        stringBuilder.append("Spring Coefficient: ").append(Double.toString(springCoefficient)).append("\n");
        return stringBuilder.toString();
    }

    //<editor-fold defaultstate="collapsed" desc="getters">
    public double getTemperature() {
        return temperature;
    }

    public double getAAOverlapCoefficient() {
        return AAOverlapCoefficient;
    }

    public double getBBOverlapCoefficient() {
        return BBOverlapCoefficient;
    }

    public double getABOverlapCoefficient() {
        return ABOverlapCoefficient;
    }

    public double getHardOverlapCoefficient() {
        return hardOverlapCoefficient;
    }

    public double getSpringCoefficient() {
        return springCoefficient;
    }

    public ExternalEnergyCalculator getExternalEnergyCalculator() {
        return externalEnergyCalculator;
    }

    //</editor-fold>
}
