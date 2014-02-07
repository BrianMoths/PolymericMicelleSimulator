/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import Engine.PolymerSimulator;
import FocusedSimulation.SimulationRunner.DoubleWithUncertainty;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author bmoths
 */
public class StatisticsTracker {

    public interface TrackableVariable {

        public double getValue(PolymerSimulator polymerSimulator);

    }

    private final int windowSize;
    private final Map<TrackableVariable, DescriptiveStatistics> statistics;

    public StatisticsTracker(int windowSize) {
        statistics = new HashMap<>();
        this.windowSize = windowSize;
    }

    public void addTrackableVariable(TrackableVariable trackableVariable) {
        statistics.put(trackableVariable, new DescriptiveStatistics(windowSize));
    }

    public void addSnapshotForPolymerSimulator(PolymerSimulator polymerSimulator) {
        for (TrackableVariable trackableVariable : statistics.keySet()) {
            final DescriptiveStatistics descriptiveStatistics = statistics.get(trackableVariable);
            descriptiveStatistics.addValue(trackableVariable.getValue(polymerSimulator));
        }
    }

    public DoubleWithUncertainty getDoubleWithUncertainty(TrackableVariable trackableVariable) {
        final double average = getAverage(trackableVariable);
        final double standardError = getStandardError(trackableVariable);
        return new DoubleWithUncertainty(average, standardError);
    }

    public long getNumSamples(TrackableVariable trackableVariable) {
        return getStatisticsFor(trackableVariable).getN();
    }

    public double getAverage(TrackableVariable trackableVariable) {
        return getStatisticsFor(trackableVariable).getMean();
    }

    public double getStandardDeviation(TrackableVariable trackableVariable) {
        return getStatisticsFor(trackableVariable).getStandardDeviation();
    }

    public double getStandardError(TrackableVariable trackableVariable) {
        return getStandardDeviation(trackableVariable) / Math.sqrt(getNumSamples(trackableVariable));
    }

    public void clearAll() {
        for (DescriptiveStatistics descriptiveStatistics : statistics.values()) {
            descriptiveStatistics.clear();
        }
    }

    public Set<TrackableVariable> getTrackedVariables() {
        return statistics.keySet();
    }

    public DescriptiveStatistics getStatisticsFor(TrackableVariable trackableVariable) {
        return statistics.get(trackableVariable);
    }

}
