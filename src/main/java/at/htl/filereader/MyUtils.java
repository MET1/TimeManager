package at.htl.filereader;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static javafx.stage.FileChooser.ExtensionFilter;

/**
 * @timeline .
 * 03.01.2016: MET 001  created class
 * 03.01.2016: MET 015  selection of folders and files corrected: chooseDirectory() and chooseFile()
 * 15.02.2016: MET 005  function: converting string to int
 * 15.04.2016: MET 005  converting LocalDate in Date
 * 17.03.2016: MET 015  functions: getCheckBoxExtensions(), getSelectedExtensions()
 * 28.05.2016: MET 010  initialize data of the line diagram
 */
public class MyUtils {

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter MONTH_YEAR_FORMAT = DateTimeFormatter.ofPattern("MMM yy");
    private static final String DEFAULT_CHOOSE_DIRECTORY_TITLE = "Select directory";
    private static final String DEFAULT_CHOOSE_FILE_TITLE = "Select file";
    private static final String DEFAULT_INITIAL_DIRECTORY = "user.home";

    /**
     * opens a window in which a folder can be chosen
     *
     * @return selected directory
     */
    public static File chooseDirectory() {
        return chooseDirectory(null, null);
    }

    /**
     * opens a window in which a folder can be chosen
     *
     * @param title            heading of DirectoryChooser
     * @param initialDirectory start directory after opening
     * @return selected directory
     */
    public static File chooseDirectory(String title, String initialDirectory) {
        System.out.println(initialDirectory);
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle(title == null ? DEFAULT_CHOOSE_DIRECTORY_TITLE : title);
        dc.setInitialDirectory(new File(initialDirectory == null || initialDirectory.length() == 0 ?
                System.getProperty(DEFAULT_INITIAL_DIRECTORY) : initialDirectory));
        File directory = dc.showDialog(new Stage());
        System.out.println("Directory chosen");
        return directory;
    }

    /**
     * opens a window in which a file can be chosen
     *
     * @return selected file
     */
    public static File chooseFile() {
        return chooseFile(null, null, null);
    }

    /**
     * opens a window in which a file can be chosen
     *
     * @param title            heading of FileChooser
     * @param initialDirectory start directory after opening
     * @param filter           limitation of file extensions
     * @return selected file
     */
    public static File chooseFile(String title, String initialDirectory, ExtensionFilter filter) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title == null ? DEFAULT_CHOOSE_FILE_TITLE : title);
        fc.setInitialDirectory(new File(initialDirectory == null || initialDirectory.length() == 0 ?
                System.getProperty(DEFAULT_INITIAL_DIRECTORY) : initialDirectory));
        if (filter != null) {
            fc.getExtensionFilters().add(filter);
        }
        File file = fc.showOpenDialog(new Stage());
        System.out.println("File chosen");
        return file;
    }

    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Converts string to int
     *
     * @param s to be converted string
     * @return number
     */
    public static int strToInt(String s) {
        try {
            return Integer.valueOf(s);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @param date
     * @return
     */
    public static Map<String, Integer> initLineChartData(LocalDate date) {
        Map<String, Integer> data = new LinkedHashMap<>();
        while (date.isBefore(LocalDate.now())) {
            data.put(date.format(MONTH_YEAR_FORMAT), 0);
            date = date.plusMonths(1);
        }
        return data;
    }

    //region deprecated

    /**
     * Displays text on a Label
     *
     * @param alert Label in which the text should be displayed
     * @param text  specifies the message to show
     * @param error TRUE   if it is an error-message
     *              FALSE  if it is a success-message
     */
    public static void setMsg(Label alert, String text, boolean error) {
        alert.setText(text);
        alert.setStyle("-fx-background-color: " + (error ? "red" : "#dddddd") + "; -fx-opacity: 0.75");
    }

    public static List<CheckBox> getCheckBoxExtensions(List<String> extensions) {
        List<CheckBox> list = new LinkedList<>();
        for (String extension : extensions) {
            list.add(new CheckBox(extension));
        }
        return list;
    }

    public static List<String> getSelectedExtensions(List<CheckBox> list) {
        List<String> extensions = new LinkedList<>();
        for (CheckBox e : list) {
            if (e.isSelected()) {
                extensions.add(e.getText());
            }
        }
        return extensions;
    }
    //endregion

}
