/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import Engine.PolymerSimulator;
import FocusedSimulation.StatisticsTracker.TrackableVariable;

/**
 *
 * @author bmoths
 */
public class StressTrackable {

    private final PolymerSimulator polymerSimulator;
    private double[][] stress;
    private final TrackableVariable stress11, stress12, stress22;
    private boolean is11Retrieved = false, is12Retrieved = false, is22Retrieved = false;

    public StressTrackable(PolymerSimulator polymerSimulator) {
        this.polymerSimulator = polymerSimulator;
        stress11 = new TrackableVariable() {
            @Override
            public double getValue(PolymerSimulator polymerSimulator) {
                final double stressComponent = getStress(1, 1);
                is11Retrieved = true;
                return stressComponent;
            }

        };
        stress12 = new TrackableVariable() {
            @Override
            public double getValue(PolymerSimulator polymerSimulator) {
                final double stressComponent = getStress(1, 2);
                is12Retrieved = true;
                return stressComponent;
            }

        };
        stress22 = new TrackableVariable() {
            @Override
            public double getValue(PolymerSimulator polymerSimulator) {
                final double stressComponent = getStress(2, 2);
                is22Retrieved = true;
                return stressComponent;
            }

        };
    }

    private double getStress(int i, int j) {
        if (isCalculationNeeded()) {
            calculateStress();
            resetBooleans();
        }
        return stress[i][j];
    }

    private void resetBooleans() {
        is11Retrieved = false;
        is12Retrieved = false;
        is22Retrieved = false;
    }

    private void calculateStress() {
        stress = StressFinder.calculateTotalStress(polymerSimulator);
    }

    private boolean isCalculationNeeded() {
        return is11Retrieved && is12Retrieved && is22Retrieved;
    }

    public TrackableVariable getStress11Trackable() {
        return stress11;
    }

    public TrackableVariable getStress12Trackable() {
        return stress12;
    }

    public TrackableVariable getStress22Trackable() {
        return stress22;
    }

}
