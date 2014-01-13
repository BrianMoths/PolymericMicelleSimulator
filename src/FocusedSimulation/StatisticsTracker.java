/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import Engine.PolymerSimulator;
import java.util.HashMap;
import java.util.Map;
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

    public long getNumSamples(TrackableVariable trackableVariable) {
        return getStatisticsFor(trackableVariable).getN();
    }

    public double getAverage(TrackableVariable trackableVariable) {
        return getStatisticsFor(trackableVariable).getMean();
    }

    public double getStandardDeviation(TrackableVariable trackableVariable) {
        return getStatisticsFor(trackableVariable).getStandardDeviation();
    }

    public void clearAll() {
        for (DescriptiveStatistics descriptiveStatistics : statistics.values()) {
            descriptiveStatistics.clear();
        }
    }

    private DescriptiveStatistics getStatisticsFor(TrackableVariable trackableVariable) {
        return statistics.get(trackableVariable);
    }

}
