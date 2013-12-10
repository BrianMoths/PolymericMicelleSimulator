/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState;

import Engine.PolymerState.SystemGeometry.Interfaces.ImmutableSystemGeometry;
import Engine.SystemAnalyzer.AnalyzerListener;
import Engine.SystemAnalyzer.BeadPositionsGetter;

/**
 *
 * @author bmoths
 */
public interface ImmutablePolymerState {

    public ImmutableSystemGeometry getImmutableSystemGeometry();

    public ImmutablePolymerPosition getImmutablePolymerPosition();

    public ImmutableDiscretePolymerState getImmutableDiscretePolymerState();

    public void acceptBeadPositionGetter(BeadPositionsGetter beadPositionsGetter);

    public void acceptAnalyzerListener(AnalyzerListener analyzerListener);

}