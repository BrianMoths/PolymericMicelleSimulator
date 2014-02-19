/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import FocusedSimulation.surfacetension.SurfaceTensionResultsWriter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Calendar;

/**
 *
 * @author bmoths
 */
public class OutputWriter {

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

    private final PrintWriter dataWriter;

    public OutputWriter(final int jobNumber) throws FileNotFoundException {
        dataWriter = makeDataWriter(jobNumber);
    }

    private PrintWriter makeDataWriter(int jobNumber) throws FileNotFoundException {
        String projectPath = getProjectPath();
        System.out.println(projectPath);
        final String path = projectPath + "../simulationResults/";
        String fileName;
        fileName = makeFileName(jobNumber);

        return new PrintWriter(path + fileName);
    }

    private String makeFileName(int fileNameNumber) {
        StringBuilder fileNameBuilder = new StringBuilder();
        String datePrefix = makeDatePrefix();
        fileNameBuilder.append(datePrefix);
        fileNameBuilder.append("_").append(makeDoubleDigitString(fileNameNumber));
        return fileNameBuilder.toString();
    }

    public void printAndSoutString(final String string) {
        printString(string);
        System.out.println(string);
    }

    public void printString(final String string) {
        dataWriter.print(string);
        dataWriter.flush();
    }

    public void closeWriter() {
        dataWriter.close();
    }

}
