/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author bmoths
 */
public interface ImmutablePolymerPosition extends Serializable {

    public int getNumBeads();

    public double[] getBeadPosition(int bead);

    public double[][] getBeadPositions();

    double[][] reasonableRandomPositions(ImmutableDiscretePolymerState immutableDiscretePolymerState, PositionGenerator positionGenerator);

    void reasonableChainRandomize(List<Integer> chainOfBead, PositionGenerator positionGenerator, double[][] randomPositions);

}
