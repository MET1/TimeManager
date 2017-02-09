package at.htl.filereader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @timeline .
 * 15.12.2015: MET 001  created class
 * 18.12.2015: MET 030  created function getFilteredFiles(...) for the filtering of files
 * 18.12.2015: MET 010  function for determining the extension of files
 * 18.12.2015: MET 025  basic structure for the collection of data
 * 20.12.2015: MET 040  implementation of method writeToCsvFile(...) for storing
 * 20.12.2015: MET 030  improved getData() and provided with messages
 * 08.01.2016: MET 055  read out specific data by using data.stream().(...)
 * 15.02.2016: MET 015  read members from detected data
 * 28.05.2016: MET 010  implemented function getFormattedData(...)
 * 27.06.2016: MET 010  write data into a csv file with header of columns
 */
public class MyFile {

    private static final String DEFAULT_COMMEND_BEGIN = "/**";
    private static final String DEFAULT_TIMELINE_BEGIN = " * @timeline";
    private static final String DEFAULT_COMMEND_END = " */";
    private static final String DEFAULT_SYNTAX
            = "[Datei] * [Datum]: [Zeit] [Name]  [Beschreibung]";
    private static final String DELIMITER = ";";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");

    /**
     * Filters some files in a folder and then are returned to a list.
     *
     * @param dir        folder with files
     * @param extensions file endings to be filtered
     * @return list of files with certain file extension
     */
    public static List<File> getFilteredFiles(File dir, List<String> extensions) {
        List<File> matches = new LinkedList<>();
        if (!dir.exists() && dir.isDirectory()) {
            System.out.println(String.format("Directory %s does not exist.", dir.getPath()));
        } else if (extensions == null || extensions.size() == 0) {
            System.out.println("There are no file extensions for filtering available.");
        } else {
            File[] files = dir.listFiles();
            for (File file : files == null ? new File[0] : files) {
                if (file.isDirectory()) {
                    matches.addAll(getFilteredFiles(file, extensions));
                } else if (extensions.contains(getSuffix(file))) {
                    matches.add(file);
                    System.out.println(" + " + file.getPath());
                }
            }
        }
        return matches;
    }

    /**
     * Determines the ending of a file
     *
     * @param file file which the extension is to be determined
     * @return extension of a file
     */
    private static String getSuffix(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Provides obsData corresponding to a specific syntax
     *
     * @param files  list of files with certain file extension
     * @param syntax sequence of the obsData
     * @return obsData
     */
    public static List<String[]> getData(List<File> files, String syntax) {
        List<String[]> data = new LinkedList<>();
        if (files == null || files.size() == 0) {
            System.out.println("There are no items in list of files");
            return null;
        } else if (syntax == null || syntax.length() == 0) {
            syntax = DEFAULT_SYNTAX;
        }
        String[] separators = syntax.split("\\[|\\]");
        for (File file : files) {
            if (!file.canRead()) {
                System.out.println(file.getPath() + " failed to read.");
                return null;
            } else if (!file.isFile()) {
                System.out.println(file.getPath() + " is not a file.");
                return null;
            }
            boolean isCommend = false;
            boolean timeline = false;
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(file.getPath()));
                String line = null;
                while ((line = in.readLine()) != null) {
                    if (!isCommend && line.startsWith(DEFAULT_COMMEND_BEGIN)) {
                        isCommend = true;
                    } else if (isCommend && line.startsWith(DEFAULT_TIMELINE_BEGIN)) {
                        timeline = true;
                    } else if (isCommend && line.startsWith(DEFAULT_COMMEND_END)) {
                        isCommend = false;
                        timeline = false;
                    } else if (timeline) {
                        data.add(getRecord(file.getName() + " " + line.trim(), separators));
                    }
                }
            } catch (IOException e) {
                System.out.println();
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        System.out.println("BufferedReader failed to close");
                        e.getMessage();
                    }
                }
            }
        }
        System.out.println(data.size() + " record(s) were successfully imported");
        return data;
    }

    /**
     * @param str
     * @param separators
     * @return
     */
    public static String[] getRecord(String str, String[] separators) {
        List<String> record = new LinkedList<>();
        for (int i = 0; i < separators.length - 2; i += 2) {
            if (str.startsWith(separators[i])) {
                str = str.trim().substring(separators[i].trim().length()).trim();
                String text = "";
                while (!str.startsWith(separators[i + 2])) {
                    text += str.charAt(0);
                    str = str.substring(1);
                }
                System.out.println(text);
                record.add(text.trim());
            }
        }
        record.add(str.trim());
        return record.toArray(new String[record.size()]);
    }

    public static List<String[]> getFormattedData(List<String[]> data) {
        for (String[] row : data) {
            row[1] = LocalDate.parse(row[1], MyUtils.DATE_FORMAT).format(DateTimeFormatter.ISO_DATE);
            //row[3] = "" + MyUtils.strToInt(row[3]);
        }
        return data.stream()
                .sorted((e1, e2) -> e2[1].compareTo(e1[1]))
                .collect(Collectors.toList());
    }

    /**
     * Reads members from data
     *
     * @param data
     * @return
     */
    public static List<String> getMembers(List<String[]> data) {
        return data.stream()
                .map(p -> p[2])
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public static List<String[]> getTasks(List<String[]> data) {
        return data.stream()
                .filter(p -> p[0].endsWith("00") && !p[0].startsWith("00"))
                .collect(Collectors.toList());
    }

    /**
     * @param data
     * @param name
     * @param reverse
     * @return
     */
    public static List<String[]> getSpecificData(List<String[]> data,
                                                 String name,
                                                 boolean reverse) {
        return data.stream()
                .filter(p -> p[2].equals(name))
                .sorted((e1, e2) -> reverse ? e2[1].compareTo(e1[1]) : e1[1].compareTo(e2[1]))
                .collect(Collectors.toList());
    }

    /**
     * @param data
     * @param name
     * @param from
     * @param to
     * @param reverse
     * @return
     */
    public static List<String[]> getSpecificData(List<String[]> data,
                                                 String name,
                                                 Date from,
                                                 Date to,
                                                 boolean reverse) {
        return data.stream()
                .filter(p -> p[2].equals(name))
                .filter(p -> {
                    try {
                        return DATE_FORMATTER.parse(p[1]).compareTo(from) != -1
                                && DATE_FORMATTER.parse(p[1]).compareTo(to) != 1;
                    } catch (ParseException e) {
                        return false;
                    }
                })
                .sorted((e1, e2) -> reverse ? e2[1].compareTo(e1[1]) : e1[1].compareTo(e2[1]))
                .collect(Collectors.toList());
    }

    public static LocalDate getStartDate(List<String[]> data) {
        return LocalDate.parse(data.stream()
                .sorted((e1, e2) -> e1[1].compareTo(e2[1]))
                .collect(Collectors.toList())
                .get(0)[1], DateTimeFormatter.ISO_DATE);
    }


    /**
     * Writes specified obsData in the indicated file.
     *
     * @param header   titles of the columns
     * @param data     obsData to be filled in the target file
     * @param fileName path of file to be written
     * @return Was writing a file with obsData successfully?
     */
    public static boolean writeToCsvFile(String[] header, List<String[]> data, String fileName) {
        data.add(0, header);
        return writeToCsvFile(data, fileName);
    }

    /**
     * Writes specified obsData in the indicated file.
     *
     * @param data     obsData to be filled in the target file
     * @param fileName path of file to be written
     * @return Was writing a file with obsData successfully?
     */
    public static boolean writeToCsvFile(List<String[]> data, String fileName) {
        boolean written = false;
        FileWriter fileWriter = null;
        if (data == null || data.size() == 0) {
            System.out.println("There are no obsData for writing available.");
        } else if (!fileName.toLowerCase().endsWith(".csv")) {
            System.out.println(fileName + " is a invalid filename of a csv file!");
        } else {
            try {
                fileWriter = new FileWriter(fileName);
                for (String[] record : data) {
                    fileWriter.append(String.join(DELIMITER, record));
                    fileWriter.append(NEW_LINE_SEPARATOR);
                }
                written = true;
                System.out.println(fileName + " was created successfully.");
            } catch (Exception e) {
                System.out.println("Error in FileWriter:");
                e.printStackTrace();
            } finally {
                try {
                    if (fileWriter != null) {
                        fileWriter.flush();
                        fileWriter.close();
                    }
                } catch (IOException e) {
                    System.out.println("Error while flushing/closing FileWriter.");
                    e.printStackTrace();
                }
            }
        }
        return written;
    }

    /**
     * Reads data from the specified file.
     *
     * @param fileName path of file to be read
     * @return data
     */
    public static List<String[]> readFromCsvFile(String fileName) {
        List<String[]> data = new LinkedList<>();
        if (!fileName.toLowerCase().endsWith(".csv")) {
            System.out.println(fileName + " is a invalid filename of a csv file!");
        } else {
            try {
                data.addAll(Files.readAllLines(new File(fileName).toPath(), StandardCharsets.UTF_8).stream()
                        .map(line -> line.split(DELIMITER)).collect(Collectors.toList()));
                System.out.println(fileName + " was imported successfully.");
            } catch (IOException e) {
                System.out.println("Error by reading from csv file: ");
                e.printStackTrace();
            }
        }
        return data;
    }


}
