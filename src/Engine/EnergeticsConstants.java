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
    static public class EnergeticsConstantsBuilder {

        private double temperature = 1,
                AAOverlapCoefficient = 5. / 120.,
                BBOverlapCoefficient = 5. / 120.,
                ABOverlapCoefficient = .1,
                springCoefficient = 1. / 3.,
                hardOverlapCoefficient = 0;
        private ExternalEnergyCalculator externalEnergyCalculator = new ExternalEnergyCalculator();

        public EnergeticsConstantsBuilder() {
        }

        public EnergeticsConstantsBuilder(EnergeticsConstants energeticsConstants) {
            temperature = energeticsConstants.temperature;
            AAOverlapCoefficient = energeticsConstants.AAOverlapCoefficient;
            BBOverlapCoefficient = energeticsConstants.BBOverlapCoefficient;
            ABOverlapCoefficient = energeticsConstants.ABOverlapCoefficient;
            springCoefficient = energeticsConstants.springCoefficient;
            hardOverlapCoefficient = energeticsConstants.hardOverlapCoefficient;
        }

        public EnergeticsConstants buildEnergeticsConstants() {
            return new EnergeticsConstants(this);
        }

        public void setHardOverlapCoefficientFromParameters(GeometricalParameters geometricalParameters) {
            hardOverlapCoefficient = hardOverlapCoefficientFromParameters(geometricalParameters);
        }

        private double hardOverlapCoefficientFromParameters(GeometricalParameters geometricalParameters) {
            if (geometricalParameters.getCoreLength() > 1e-10) {
                final double bondingEnergyInT = .5;
                final double coreRepulsionInT = 5; //5
                double minCoefficientForBonding = -bondingEnergyInT * temperature / (geometricalParameters.getInteractionLength() * geometricalParameters.getInteractionLength());
                double minAttraction = Math.min(Math.min(BBOverlapCoefficient, AAOverlapCoefficient), minCoefficientForBonding);
                return (coreRepulsionInT * temperature - geometricalParameters.getInteractionLength() * geometricalParameters.getInteractionLength() * minAttraction) / (geometricalParameters.getCoreLength() * geometricalParameters.getCoreLength());
            } else {
                return 0;
            }
        }

        public double idealStepLength() {
            return Math.sqrt(getTemperature() / getSpringCoefficient());
        }

        //<editor-fold defaultstate="collapsed" desc="setters">
        public EnergeticsConstantsBuilder setTemperature(double temperature) {
            if (temperature > 0) {
                this.temperature = temperature;
            }
            return this;
        }

        public EnergeticsConstantsBuilder setAAOverlapCoefficient(double AAOverlapCoefficient) {
            this.AAOverlapCoefficient = AAOverlapCoefficient;
            return this;
        }

        public EnergeticsConstantsBuilder setBBOverlapCoefficient(double BBOverlapCoefficient) {
            this.BBOverlapCoefficient = BBOverlapCoefficient;
            return this;
        }

        public EnergeticsConstantsBuilder setABOverlapCoefficient(double ABOverlapCoefficient) {
            this.ABOverlapCoefficient = ABOverlapCoefficient;
            return this;
        }

        public EnergeticsConstantsBuilder setSpringCoefficient(double springCoefficient) {
            this.springCoefficient = springCoefficient;
            return this;
        }

        public EnergeticsConstantsBuilder setHardOverlapCoefficient(double hardOverlapCoefficient) {
            this.hardOverlapCoefficient = hardOverlapCoefficient;
            return this;
        }

        public EnergeticsConstantsBuilder setExternalEnergyCalculator(ExternalEnergyCalculator externalEnergyCalculator) {
            this.externalEnergyCalculator = externalEnergyCalculator;
            return this;
        }
        //</editor-fold>

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

        public double getHardOverlapCoefficient() {
            return hardOverlapCoefficient;
        }

        public ExternalEnergyCalculator getExternalEnergyCalculator() {
            return externalEnergyCalculator;
        }
        //</editor-fold>

    }
    //</editor-fold>

    static private Random randomNumberGenerator = new Random();

    static public EnergeticsConstants defaultEnergeticsConstants() {
        return new EnergeticsConstantsBuilder().buildEnergeticsConstants();
    }

    private final double temperature,
            AAOverlapCoefficient,
            BBOverlapCoefficient,
            ABOverlapCoefficient,
            hardOverlapCoefficient,
            springCoefficient;
    private final ExternalEnergyCalculator externalEnergyCalculator;

    private EnergeticsConstants(EnergeticsConstantsBuilder energeticsConstantsBuilder) {
        temperature = energeticsConstantsBuilder.temperature;
        AAOverlapCoefficient = energeticsConstantsBuilder.AAOverlapCoefficient;
        BBOverlapCoefficient = energeticsConstantsBuilder.BBOverlapCoefficient;
        ABOverlapCoefficient = energeticsConstantsBuilder.ABOverlapCoefficient;
        hardOverlapCoefficient = energeticsConstantsBuilder.hardOverlapCoefficient;
        springCoefficient = energeticsConstantsBuilder.springCoefficient;
        externalEnergyCalculator = energeticsConstantsBuilder.externalEnergyCalculator;

    }

    //<editor-fold defaultstate="collapsed" desc="calculate energies">
    public double springEnergy(double squareLength) {
        return springCoefficient * squareLength;
    }

    public double densityEnergy(AreaOverlap areaOverlap) {
        return 2 * (AAOverlapCoefficient * areaOverlap.AAOverlap
                + BBOverlapCoefficient * areaOverlap.BBOverlap
                + ABOverlapCoefficient * areaOverlap.ABOverlap
                + hardOverlapCoefficient * areaOverlap.hardOverlap); //why a factor of two?
    }

    public double externalEnergy(SystemGeometry systemGeometry) {
        return externalEnergyCalculator.calculateExternalEnergy(systemGeometry.getRMax());
    }
    //</editor-fold>

    public boolean isEnergeticallyAllowed(double energyChange) {
        return energyChange < 0
                || randomNumberGenerator.nextDouble() < Math.exp(-energyChange / temperature);
    }

    public double idealStepLength() {
        return Math.sqrt(getTemperature() / getSpringCoefficient());
    }

    //<editor-fold defaultstate="collapsed" desc="to string">
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
    //</editor-fold>

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