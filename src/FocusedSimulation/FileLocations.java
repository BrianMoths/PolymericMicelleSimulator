/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import java.io.File;

/**
 *
 * @author bmoths
 */
public class FileLocations {

    static public final String PROJECT_PATH = findProjectPath();
    static public final String SIMULATION_FOLDERS_PATH = findSimulationFoldersPath();

    private static String findProjectPath() {
        String jarPath = FileLocations.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (jarPath.contains("build/")) {
            jarPath = jarPath.substring(0, jarPath.lastIndexOf("build/"));
        } else if (jarPath.contains("dist/")) {
            jarPath = jarPath.substring(0, jarPath.lastIndexOf("dist/"));
        } else {
            throw new AssertionError("jar is neither in build/ or dist/", null);
        }
        return jarPath;
    }

    static private String findSimulationFoldersPath() {
        if (isHostNode()) {
            return "/work/bmoths/polymerSimulation/";
        } else {
            return PROJECT_PATH + "../";
        }
    }

    static private boolean isHostNode() {
        final File file = new File("/work");
        return file.exists();
    }

}
