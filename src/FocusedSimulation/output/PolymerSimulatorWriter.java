/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.output;

import Engine.PolymerSimulator;
import FocusedSimulation.surfacetension.SurfaceTensionResultsWriter;
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

    static public String makeDatePrefix() {
        StringBuilder fileNameBuilder = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final int second = calendar.get(Calendar.SECOND);
        fileNameBuilder.append(makeDoubleDigitString(year))
                .append("_")
                .append(makeDoubleDigitString(month))
                .append("_")
                .append(makeDoubleDigitString(day))
                .append("_")
                .append(makeDoubleDigitString(hour))
                .append("_")
                .append(makeDoubleDigitString(minute))
                .append("_")
                .append(makeDoubleDigitString(second));
        return fileNameBuilder.toString();
    }

    static public String makeDoubleDigitString(int num) {
        num %= 100;
        StringBuilder stringBuilder = new StringBuilder();
        if (num < 10) {
            stringBuilder.append("0");
        }
        stringBuilder.append(Integer.toString(num));
        return stringBuilder.toString();
    }

    static public String getProjectPath() throws AssertionError {
        String jarPath = SurfaceTensionResultsWriter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (jarPath.contains("build/")) {
            jarPath = jarPath.substring(0, jarPath.lastIndexOf("build/"));
        } else if (jarPath.contains("dist/")) {
            jarPath = jarPath.substring(0, jarPath.lastIndexOf("dist/"));
        } else {
            throw new AssertionError("jar is neither in build/ or dist/", null);
        }
        return jarPath;
    }

    static private String makeFileName(int fileNameNumber) {
        StringBuilder fileNameBuilder = new StringBuilder();
        String datePrefix = makeDatePrefix();
        fileNameBuilder.append(datePrefix);
        fileNameBuilder.append("_").append(makeDoubleDigitString(fileNameNumber));
        return fileNameBuilder.toString();
    }

    private static String getPathAndFileString(int jobNumber) throws AssertionError {
        String projectPath = getProjectPath();
        final String path = projectPath + "../simulationSnapshots/";
        String fileName = makeFileName(jobNumber);
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

        public PolymerSimulatorOutputRunnable(final PolymerSimulator polymerSimulator, int jobNumber) {
            this.polymerSimulator = polymerSimulator;
            this.absoluteFileName = getPathAndFileString(jobNumber);
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
                    System.out.println("Object written.");
                } catch (IOException ex) {
                    Logger.getLogger(PolymerSimulatorWriter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    private boolean isStillWriting = true;

    public PolymerSimulatorWriter(final PolymerSimulator polymerSimulator, int jobNumber) throws FileNotFoundException, IOException {
        Thread outputThread = new Thread(new PolymerSimulatorOutputRunnable(polymerSimulator, jobNumber));
        outputThread.start();
    }

    public void stopWriting() {
        isStillWriting = false;
    }

}
