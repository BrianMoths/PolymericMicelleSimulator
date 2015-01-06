/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.output;

import Engine.PolymerSimulator;
import FocusedSimulation.FileLocations;
import FocusedSimulation.homopolymer.surfacetension.SurfaceTensionResultsWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bmoths
 */
public class PolymerSimulatorWriter {

    static private String makeFileName(int fileNameNumber, String jobString) {
        return OutputWriter.makeFileName(fileNameNumber, jobString) + ".simstate";
    }

    private static String getPathAndFileString(int jobNumber, String jobString) throws AssertionError {
        String projectPath = FileLocations.SIMULATION_FOLDERS_PATH;
        final String path = projectPath + "simulationSnapshots/";
        String fileName = makeFileName(jobNumber, jobString);
        final String pathAndFileString = path + fileName;
        return pathAndFileString;
    }

    private static ObjectOutputStream getObjectOutputStreamFromAbsoluteFile(String pathAndFileString) throws IOException {
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(pathAndFileString));
        return objectOutputStream;
    }

    private class PolymerSimulatorOutputRunnable implements Runnable {

        private final PolymerSimulator polymerSimulator;
        private final String absoluteFileName;

        public PolymerSimulatorOutputRunnable(final PolymerSimulator polymerSimulator, int jobNumber, String jobString) {
            this.polymerSimulator = polymerSimulator;
            this.absoluteFileName = getPathAndFileString(jobNumber, jobString);
        }

        @Override
        public void run() {
            while (isStillWriting) {
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PolymerSimulatorWriter.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    ObjectOutputStream objectOutputStream = getObjectOutputStreamFromAbsoluteFile(absoluteFileName);
                    synchronized (polymerSimulator) {
                        objectOutputStream.writeObject(polymerSimulator);
                    }
                    objectOutputStream.flush();
                    try {
                        objectOutputStream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(PolymerSimulatorWriter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(PolymerSimulatorWriter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    private boolean isStillWriting = true;

    public PolymerSimulatorWriter(final PolymerSimulator polymerSimulator, int jobNumber, String jobString) throws FileNotFoundException, IOException {
        Thread outputThread = new Thread(new PolymerSimulatorOutputRunnable(polymerSimulator, jobNumber, jobString));
        outputThread.start();
    }

    public void stopWriting() {
        isStillWriting = false;
    }

}
