package at.htl.timemanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @timeline .
 * 15.12.2015: MET 001  created class
 * 18.12.2015: MET 030  created a coarse GUI for the first
 * 09.01.2016: MET 020  slight improvements in the GUI
 * 04.02.2016: MET 070  designed GUI much more appealing and easier to use
 * 13.02.2016: MET 010  set a background image: whistle.jpg
 * 15.02.2016: MET 010  created simple pie chart
 * 15.02.2016: MET 010  created simple line chart
 * 27.05.2016: MET 060  GUI more user-friendly designed (Tabs: Settings, Import)
 * 27.05.2016: MET 003  Prompt text for source and target directory
 */
public class TimeManagerGui extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/TimeManager.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setTitle("Time Manager");
        stage.setScene(scene);

        stage.show();
    }

}
