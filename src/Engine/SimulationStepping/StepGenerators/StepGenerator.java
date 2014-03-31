/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepGenerators;

import Engine.SimulationStepping.StepTypes.SimulationStep;
import Engine.SystemAnalyzer;
import java.io.Serializable;

/**
 *
 * @author bmoths
 */
public interface StepGenerator extends Serializable {

    public SimulationStep generateStep(SystemAnalyzer systemAnalyzer);

}
