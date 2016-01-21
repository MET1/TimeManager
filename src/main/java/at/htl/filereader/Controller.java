package at.htl.filereader;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.Serializable;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by MET on 03.12.15.
 */
public class Controller implements Serializable {

    @FXML
    TextField tfSourcePath;
    @FXML
    Button btnChoose;

    public Controller() {
    }

    public void initialize(URL location, ResourceBundle resources) {

    }

}
