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
public final class StressTrackable {

    static public final StressTrackable TOTAL_STRESS_TRACKABLE = new StressTrackable();
    private double[][] stress;
    private final TrackableVariable stress11, stress12, stress22;
    private boolean is11Retrieved = true, is12Retrieved = true, is22Retrieved = true;

    private StressTrackable() {
        stress11 = new TrackableVariable() {
            @Override
            public double getValue(PolymerSimulator polymerSimulator) {
                final double stressComponent = getStress(1, 1, polymerSimulator);
                is11Retrieved = true;
                return stressComponent;
            }

        };
        stress12 = new TrackableVariable() {
            @Override
            public double getValue(PolymerSimulator polymerSimulator) {
                final double stressComponent = getStress(1, 2, polymerSimulator);
                is12Retrieved = true;
                return stressComponent;
            }

        };
        stress22 = new TrackableVariable() {
            @Override
            public double getValue(PolymerSimulator polymerSimulator) {
                final double stressComponent = getStress(2, 2, polymerSimulator);
                is22Retrieved = true;
                return stressComponent;
            }

        };
    }

    private double getStress(int i, int j, PolymerSimulator polymerSimulator) {
        if (isCalculationNeeded()) {
            calculateStress(polymerSimulator);
            resetBooleans();
        }
        return stress[i - 1][j - 1];
    }

    private void resetBooleans() {
        is11Retrieved = false;
        is12Retrieved = false;
        is22Retrieved = false;
    }

    private void calculateStress(PolymerSimulator polymerSimulator) {
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
