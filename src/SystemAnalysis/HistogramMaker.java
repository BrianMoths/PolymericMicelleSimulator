/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import Engine.SystemAnalyzer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class HistogramMaker {

    private static final int dimension = 1;
    private static final double binSize = 4;

    public static List<Integer> makeHistogram(SystemAnalyzer systemAnalyzer) {
        HistogramMaker histogramMaker = new HistogramMaker(systemAnalyzer);
        return histogramMaker.makeHistogram();
    }

    private final SystemAnalyzer systemAnalyzer;
    private final List<Integer> histogram;

    public HistogramMaker(SystemAnalyzer systemAnalyzer) {
        this.systemAnalyzer = systemAnalyzer;
        final int numBins = (int) (systemAnalyzer.getSystemGeometry().getSizeOfDimension(dimension) / binSize) + 1;
        histogram = new ArrayList<>(numBins);
        for (int i = 0; i < numBins; i++) {
            histogram.add(0);
        }
    }

    private List<Integer> makeHistogram() {
        final int numBeads = systemAnalyzer.getNumBeads();
        for (int bead = 0; bead < numBeads; bead++) {
            addBeadToHistogram(bead);
        }
        return histogram;
    }

    private void addBeadToHistogram(int bead) {
        int binNumber = getBinNumber(bead);
        histogram.set(binNumber, histogram.get(binNumber) + 1);
    }

    private int getBinNumber(int bead) {
        final double y = systemAnalyzer.getBeadPositionComponent(bead, dimension);
        return (int) (y / binSize);
    }

}
