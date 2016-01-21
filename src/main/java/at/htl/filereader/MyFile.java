package at.htl.filereader;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @timeline .
 * 19.12.2015: MET 000  dsf
 * 20.12.2015: MET 040  implementation of method writeToCsvFile(...)
 * 20.12.2015: MET 030  improved getData and provided with messages
 */
public class MyFile {

    private static final String COMMEND_BEGIN = "/**";
    private static final String COMMEND_END = " */";
    private static final String DELIMITER = ";";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String DEFAULT_SYNTAX
            = "[Datei] * [Datum]: [Zeit] [Name] [Beschreibung]";

    /**
     * Filters some files in a folder and then are returned to a list.
     *
     * @param dir     folder with files
     * @param endings file extensions to be filtered
     * @return list of files with certain file extension
     */
    public static List<File> getFilteredFiles(File dir, List<String> endings) {
        List<File> matches = new LinkedList<File>();
        if (!dir.exists() && dir.isDirectory()) {
            System.out.println(String.format("Directory %s does not exist.", dir.getPath()));
        } else if (endings == null || endings.size() == 0) {
            System.out.println("There are no file extensions for filtering available.");
        } else {
            File[] files = dir.listFiles();
            for (File file : files == null ? new File[0] : files) {
                if (file.isDirectory()) {
                    matches.addAll(getFilteredFiles(file, endings));
                } else if (endings.contains(getSuffix(file))) {
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
     * @param files
     * @param syntax
     * @return
     */
    public static List<String[]> getData(List<File> files, String syntax) {
        List<String[]> data = new LinkedList<String[]>();
        if (files == null || files.size() == 0) {
            System.out.println("There are no items in list of files");
            return null;
        } else if (syntax == null || syntax.length() == 0) {
            syntax = DEFAULT_SYNTAX;
        }
        String[] separators = syntax.split("\\[|\\]");
        for (int i = 1; i < separators.length; i += 2) {

        }
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
                    if (!isCommend && line.startsWith(COMMEND_BEGIN)) {
                        isCommend = true;
                    } else if (isCommend && line.startsWith(" * @timeline ")) {
                        timeline = true;
                    } else if (isCommend && line.startsWith(COMMEND_END)) {
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
        List<String> record = new LinkedList<String>();
        for (int i = 0; i < separators.length - 2; i += 2) {
            if (str.startsWith(separators[i])) {
                str = str.trim().substring(
                        separators[i].trim().length()).trim();
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



    /**
     * Writes specified data in the indicated file.
     *
     * @param data     data to be filled in the target file
     * @param fileName path of file to be written
     * @return Was writing a file with data successfully?
     */
    public static boolean writeToCsvFile(List<String[]> data, String fileName) {
        boolean written = false;
        FileWriter fileWriter = null;
        if (data == null || data.size() == 0) {
            System.out.println("There are no data for writing available.");
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

}
