/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState;

import Engine.PolymerState.SystemGeometry.Interfaces.ImmutableSystemGeometry;
import Engine.SystemAnalyzer.AnalyzerListener;
import Engine.SystemAnalyzer.BeadPositionsGetter;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author bmoths
 */
public interface ImmutablePolymerState extends Serializable {

    public ImmutableSystemGeometry getImmutableSystemGeometry();

    public ImmutablePolymerPosition getImmutablePolymerPosition();

    public ImmutableDiscretePolymerState getImmutableDiscretePolymerState();

    public void acceptBeadPositionGetter(BeadPositionsGetter beadPositionsGetter);

    public void acceptAnalyzerListener(AnalyzerListener analyzerListener);

    List<double[]> getEndToEndDisplacements();

}
