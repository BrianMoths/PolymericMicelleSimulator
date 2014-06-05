/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import FocusedSimulation.StatisticsTracker.TrackableVariable;

/**
 *
 * @author bmoths
 */
public interface StressTrackable {

    TrackableVariable getStress11Trackable();

    TrackableVariable getStress12Trackable();

    TrackableVariable getStress22Trackable();
}
