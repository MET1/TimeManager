package at.htl.timemanager;

/**
 * Created by MET on 10.02.17.
 */
public class Settings {

    private static final int NUMBER_OF_SETTINGS = 5;

    public static boolean valid(String settingsStr) {
        String[] settings = settingsStr.split("\n");
        if (checkNumber(settings)) {
            return true;
        }
        return false;
    }

    public static boolean checkNumber(String[] settings) {
        return settings.length == NUMBER_OF_SETTINGS;
    }

    public static boolean checkBrackets(String[] settings) {
        return settings.length == NUMBER_OF_SETTINGS;
    }


}
