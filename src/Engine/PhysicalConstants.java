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
public class PhysicalConstants {

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

    public double getSpringCoefficient() {
        return springCoefficient;
    }
    //</editor-fold>
}
