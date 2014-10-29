/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.output;

import FocusedSimulation.FileLocations;
import FocusedSimulation.homopolymer.surfacetension.SurfaceTensionResultsWriter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bmoths
 */
public class OutputWriter {

    static public String makeDateString() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        return joinStringsWithUnderscore(makeDoubleDigitString(year), makeDoubleDigitString(month), makeDoubleDigitString(day));
    }

    static public String makeTimeString() {

        Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final int second = calendar.get(Calendar.SECOND);
        return joinStringsWithUnderscore(makeDoubleDigitString(hour), makeDoubleDigitString(minute), makeDoubleDigitString(second));
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

    static public String makeFileName(int fileNameNumber, String jobString) {
        StringBuilder fileNameBuilder = new StringBuilder();
        String dateString = makeDateString();
        String timeString = makeTimeString();
        String jobFileString = makeFileNameNumberJobStringLabel(fileNameNumber, jobString);
        fileNameBuilder.append(joinStringsWithUnderscore(dateString, jobFileString, timeString));
        return fileNameBuilder.toString();
    }

    static private String makeFileNameNumberJobStringLabel(int fileNameNumber, String jobString) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!"".equals(jobString)) {
            stringBuilder.append(jobString).append("_");
        }
        stringBuilder.append(makeDoubleDigitString(fileNameNumber));
        return stringBuilder.toString();
    }

    private static String joinStringsWithUnderscore(String... strings) {
        final int numStrings = strings.length;
        StringBuilder stringBuilder = new StringBuilder();
        if (numStrings == 0) {
            return stringBuilder.toString();
        }
        stringBuilder.append(strings[0]);
        for (int stringIndex = 1; stringIndex < numStrings; stringIndex++) {
            stringBuilder.append("_");
            stringBuilder.append(strings[stringIndex]);
        }
        return stringBuilder.toString();
    }

    private static String getPathAndFileString(int jobNumber, String jobString) throws AssertionError {
        String projectPath = FileLocations.SIMULATION_FOLDERS_PATH;
        final String path = projectPath + "../simulationResults/";
        String fileName = makeFileName(jobNumber, jobString);
        final String pathAndFileString = path + fileName;
        return pathAndFileString;
    }

    private final PrintWriter dataWriter;

    public OutputWriter(final int jobNumber) throws FileNotFoundException {
        this(jobNumber, "");
    }

    public OutputWriter(final int jobNumber, final String jobString) throws FileNotFoundException {
        dataWriter = makeDataWriter(jobNumber, jobString);
    }

    private PrintWriter makeDataWriter(int jobNumber, String jobString) throws FileNotFoundException {
        return new PrintWriter(getPathAndFileString(jobNumber, jobString));
    }

    public void printAndSoutString(final CharSequence string) {
        printString(string);
        System.out.print(string);
    }

    public void printString(final CharSequence string) {
        dataWriter.print(string);
        dataWriter.flush();
    }

    public void closeWriter() {
        try {
            Thread.sleep(10000); //sometimes the final output is not showing. I wonder if it closes before hte output can be written. This fix is a total guess. I don't know what else to do.
        } catch (InterruptedException ex) {
            Logger.getLogger(OutputWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        dataWriter.close();
        try {
            Thread.sleep(10000); //sometimes the final output is not showing. I wonder if it closes before hte output can be written. This fix is a total guess. I don't know what else to do. Having tested it, I think this one works.
        } catch (InterruptedException ex) {
            Logger.getLogger(OutputWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
