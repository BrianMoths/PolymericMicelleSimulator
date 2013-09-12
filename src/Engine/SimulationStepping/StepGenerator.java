/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping;

import Engine.SystemAnalyzer;

/**
 *
 * @author bmoths
 */
public interface StepGenerator {

    public SimulationStep generateStep(SystemAnalyzer systemAnalyzer);

}
