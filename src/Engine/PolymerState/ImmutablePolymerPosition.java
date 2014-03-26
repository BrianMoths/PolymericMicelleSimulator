/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState;

import java.io.Serializable;

/**
 *
 * @author bmoths
 */
public interface ImmutablePolymerPosition extends Serializable {

    public int getNumBeads();

    public double[] getBeadPosition(int bead);

    public double[][] getBeadPositions();

}
