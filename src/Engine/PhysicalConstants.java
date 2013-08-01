/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.SystemGeometry.AreaOverlap;
import java.util.Random;

/**
 *
 * @author bmoths
 */
public final class PhysicalConstants {

    //<editor-fold defaultstate="collapsed" desc="builder class">
    static public class PhysicalConstantsBuilder {

        private double temperature = 300,
                AAOverlapCoefficient = 1,
                BBOverlapCoefficient = 1,
                ABOverlapCoefficient = 4,
                springCoefficient = 40;

        public PhysicalConstants buildPhysicalConstants() {
            return new PhysicalConstants(temperature,
                    AAOverlapCoefficient,
                    BBOverlapCoefficient,
                    ABOverlapCoefficient,
                    calculateHardOverlapCoefficient(),
                    springCoefficient);
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

        private double calculateHardOverlapCoefficient() {
            return 3 * Math.max(
                    Math.max(
                    Math.abs(AAOverlapCoefficient),
                    Math.abs(BBOverlapCoefficient)),
                    Math.abs(ABOverlapCoefficient));
        }
    }
    //</editor-fold>
    static private Random randomNumberGenerator = new Random();
    private final double temperature,
            AAOverlapCoefficient,
            BBOverlapCoefficient,
            ABOverlapCoefficient,
            hardOverlapCoefficient,
            springCoefficient;

    static public PhysicalConstants defaultPhysicalConstants() {
        return new PhysicalConstantsBuilder().buildPhysicalConstants();
    }

    public PhysicalConstants getPhysicalConstantsFromParameters(SimulationParameters parameters) {
        return new PhysicalConstants(this, parameters);
    }

    private PhysicalConstants(PhysicalConstants physicalConstants, SimulationParameters parameters) {
        this.temperature = physicalConstants.temperature;
        this.AAOverlapCoefficient = physicalConstants.AAOverlapCoefficient;
        this.BBOverlapCoefficient = physicalConstants.BBOverlapCoefficient;
        this.ABOverlapCoefficient = physicalConstants.ABOverlapCoefficient;
        this.springCoefficient = physicalConstants.springCoefficient;
        this.hardOverlapCoefficient = hardOverlapCoefficientFromParameters(parameters);
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

    private double hardOverlapCoefficientFromParameters(SimulationParameters parameters) {
        if (parameters.getCoreLength() > 1e-10) {
            double minCoefficientForBonding = -.5 * temperature / (parameters.getInteractionLength() * parameters.getInteractionLength());
            double minAttraction = Math.min(Math.min(BBOverlapCoefficient, AAOverlapCoefficient), minCoefficientForBonding);
            return (5 * temperature - parameters.getInteractionLength() * parameters.getInteractionLength() * minAttraction) / (parameters.getCoreLength() * parameters.getCoreLength());
        } else {
            return 0;
        }
    }

    private PhysicalConstants(double temperature, double AAOverlapCoefficient, double BBOverlapCoefficient, double ABOverlapCoefficient, double hardOverlapCoefficient, double springCoefficient) {
        this.temperature = temperature;
        this.AAOverlapCoefficient = AAOverlapCoefficient;
        this.BBOverlapCoefficient = BBOverlapCoefficient;
        this.ABOverlapCoefficient = ABOverlapCoefficient;
        this.hardOverlapCoefficient = hardOverlapCoefficient;
        this.springCoefficient = springCoefficient;
    }

    public double springEnergy(double squareLength) {
        return springCoefficient * squareLength;
    }

    public double densityEnergy(double similarOverlap, double differentOverlap) {
        return 2 * ((AAOverlapCoefficient + BBOverlapCoefficient) / 2 * similarOverlap + ABOverlapCoefficient * differentOverlap);
    }

    public double densityEnergy(AreaOverlap areaOverlap) {
        return 2 * (AAOverlapCoefficient * areaOverlap.AAOverlap
                + BBOverlapCoefficient * areaOverlap.BBOverlap
                + ABOverlapCoefficient * areaOverlap.ABOverlap);
    }

    public double densityEnergyWithCore(AreaOverlap areaOverlap) {
        return 2 * (AAOverlapCoefficient * areaOverlap.AAOverlap
                + BBOverlapCoefficient * areaOverlap.BBOverlap
                + ABOverlapCoefficient * areaOverlap.ABOverlap
                + hardOverlapCoefficient * areaOverlap.hardOverlap);
    }

    public boolean isEnergeticallyAllowed(double energyChange) {
        return energyChange < 0
                || randomNumberGenerator.nextDouble() < Math.exp(-energyChange / temperature);
    }

    public double idealStepLength() {
        return Math.sqrt(getTemperature() / getSpringCoefficient());
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
    //</editor-fold>
}
