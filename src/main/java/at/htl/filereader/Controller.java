package at.htl.filereader;

import at.htl.filereader.taskmanager.GoogleSheetsAPI;
import at.htl.filereader.taskmanager.entity.Task;
import at.htl.filereader.taskmanager.table.DatePickerCell;
import at.htl.filereader.taskmanager.table.TextAreaCell;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static javafx.stage.FileChooser.ExtensionFilter;

/**
 * @timeline .
 * 15.12.2015: MET 001  created class
 * 19.12.2015: MET 010  determining the Controls and initialisation of the GUI
 * 13.02.2016: MET 040  simple filling of the TableView from the captured obsData
 * 15.02.2016: MET 010  function for setting the total time
 * 15.02.2016: MET 030  basic configuration of the pie chart
 * 15.02.2016: MET 025  fill combo box of members and adapt data according to selection
 * 17.03.2016: MET 040  selection of extensions (ListView with CheckBoxes)
 * 12.05.2016: MET 005  formatting the output of information (time, entries)
 * 24.05.2016: MET 010  selecting a target directory (for saving a CSV-file)
 * 27.05.2016: MET 020  saving displayed data as CSV-file
 * 27.05.2016: MET 040  basic configuration of the line chart
 * 28.05.2016: MET 090  line chart: working hours per month
 * 28.05.2016: MET 030  line chart: axis label, display a value on hover
 * 28.05.2016: MET 080  reset data in combo box, line chart and pie chart (bugs fixed)
 * 29.05.2016: MET 050  deleted Tab "Settings" and adaptation to the code
 * 29.05.2016: MET 030  new structure of settings (TextArea): ext, begin1, begin2 syntax, end
 * 29.05.2016: MET 010  switching between source directory and source file
 * 29.05.2016: MET 005  switching source file/folder: change path with triangular swap
 * 29.05.2016: MET 005  set chart labels only once
 * 29.05.2016: MET 020  styling "Save Data"-Button
 * 29.05.2016: MET 010  set initial source and target directory
 * 29.05.2016: MET 001  To Do: Import CSV, Syntax, Diagram-Labels
 * 16.06.2016: MET 040  choose file: set extension filter, bug-fix, message
 * 16.06.2016: MET 015  fixed TableView-Error when updating
 */
public class Controller implements Initializable {

    @FXML
    private TabPane tabPane;
    @FXML
    private Label lbText;

    //region Import
    @FXML
    private Tab tabImport;
    @FXML
    private CheckBox cbCsvFile;
    @FXML
    private TextField tfSourcePath;
    @FXML
    private Button btnChooseSource;
    @FXML
    private TextArea taSettings;
    @FXML
    private Button btnImportData;
    //endregion

    //region Data
    @FXML
    private Tab tabData;
    @FXML
    private ComboBox<String> cbMembers;
    @FXML
    private Text txtInfo;
    @FXML
    private TableView<ObservableList<String>> tvData;
    @FXML
    private TextField tfTargetPath;
    @FXML
    private Button btnSaveData;
    //endregion

    //region Statistics
    @FXML
    private Tab tabStatistics;
    @FXML
    private LineChart lineChart;
    //endregion

    //region Division
    @FXML
    private Tab tabDivision;
    @FXML
    private PieChart pieChart;
    //endregion
    //region Tasks
    @FXML
    private Tab tabTasks;
    @FXML
    private Button btnFilterStatus;
    @FXML
    private ComboBox cbFilterAssignee;
    @FXML
    private DatePicker dpFilterFromDate;
    @FXML
    private Text txTasksInfo;
    @FXML
    private Button btnMilestones;
    @FXML
    private CheckBox cbTaskFilter;
    @FXML
    private TableView<Task> tvTasks;
    //endregion

    private String lastSourcePath;
    private ObservableList<String> obsMembers;
    private ObservableList<ObservableList<String>> obsData;
    private ObservableList<Task> obsTasks;
    private List<String[]> dataAll;
    private List<String[]> dataDisplay;

    /**
     * Called to initialize after the root element has been completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object,
     *                  or null if the location is not known.
     * @param resources The resources used to localize the root object,
     *                  or null if the root object was not localized.
     */
    public void initialize(URL location, ResourceBundle resources) {
        lbText.setText("by Tobias Melhorn");
        setVisibleTabs(false);
        //tabPane.getSelectionModel().select(1);
        lastSourcePath = "";
        setSourceType();
        obsMembers = FXCollections.observableArrayList();
        obsData = FXCollections.observableArrayList();
        System.out.println("You can import obsData");
        //btnMilestones.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/stone.png"), 15, 15, true, true)));
        //set();
        //setTasks();
    }

    /**
     * Activates and deactivates tabs (depending on import of data)
     *
     * @param visible visible tabs
     */
    private void setVisibleTabs(boolean visible) {
        if (visible) {
            tabPane.getTabs().addAll(tabData, tabStatistics, tabDivision /*, tabTasks*/);
        } else {
            tabPane.getTabs().removeAll(tabData, tabStatistics, tabDivision, tabTasks);
        }
    }

    /**
     * Switching between source directory and source file
     */
    @FXML
    public void setSourceType() {
        boolean selected = cbCsvFile.isSelected();
        taSettings.setVisible(!selected);
        String help = tfSourcePath.getText();
        tfSourcePath.setText(lastSourcePath);
        lastSourcePath = help;
        if (selected) {
            tfSourcePath.setPromptText("Source File");
        } else {
            tfSourcePath.setPromptText("Source Directory");
        }
    }

    /**
     * Shows a dialog-screen to choose the working-directory where
     * the project will be and saves the path of it.
     */
    @FXML
    public void chooseSourceDirectory() {
        if (cbCsvFile.isSelected()) {
            tfSourcePath.setText(
                    MyUtils.chooseFile("Select CSV-File",
                            tfSourcePath.getText().equals("") ? null
                                    : new File(tfSourcePath.getText()).getParentFile().getPath(),
                            new ExtensionFilter("Comma Separated Values (CSV)", "*.csv")).getPath());
        } else {
            tfSourcePath.setText(
                    MyUtils.chooseDirectory("Select Source Directory",
                            tfSourcePath.getText()).getPath());
        }
    }

    @FXML
    public void importData() {
        List<String[]> data;
        String[] header;
        if (cbCsvFile.isSelected()) {
            data = MyFile.readFromCsvFile(tfSourcePath.getText());
            header = data.get(0);
            data.remove(0);
            System.out.println(data);
        } else {
            System.out.println("started filtering files ...");
            String[] settings = taSettings.getText().split("\n");
            List<String> extensions = new ArrayList<>(Arrays.asList(settings[0].split(", ")));
            List<File> files = MyFile.getFilteredFiles(new File(tfSourcePath.getText()), extensions);
            System.out.println(files.size() + " files filtered");
            data = MyFile.getFormattedData(MyFile.getData(files, null));
            header = new String[]{"File", "Date", "Name", "Time", "Description"};
        }
        dataAll = data;
        setMembers(MyFile.getMembers(data));
        tvData.getColumns().clear();
        setColumns(header);
        setData(data, null);
        System.out.println("Data imported");
        setVisibleTabs(true);
        tabPane.getSelectionModel().selectNext();
        setLineChart();
        setPieChart();
        tfTargetPath.setText(tfSourcePath.getText());
    }

    @FXML
    public void setData() {
        setData(dataAll, cbMembers.getSelectionModel().getSelectedItem());
    }

    private void setData(List<String[]> data, String name) {
        int minutes = 0;
        int entries = 0;
        obsData.clear();
        if (name != null && !name.equals("All")) {
            data = MyFile.getSpecificData(data, name, true);
        }
        if (data != null) {
            for (String[] row : data) {
                minutes += MyUtils.strToInt(row[3]);
                entries++;
                obsData.add(FXCollections.observableArrayList(row));
            }
        }
        tvData.setItems(obsData);
        dataDisplay = data;
        //if (name != null && name.equals("GNA")) minutes *= 1.5;
        //if (name != null && name.equals("PON")) minutes *= 1.5;
        setInfo(minutes, entries);
        btnSaveData.setStyle("");
    }

    private void setColumns(String[] columnNames) {
        for (int i = 0; i < columnNames.length; i++) {
            final int j = i;
            TableColumn column = new TableColumn(columnNames[i]);
            column.setCellValueFactory(
                    new Callback<CellDataFeatures<ObservableList, String>,
                            ObservableValue<String>>() {
                        public ObservableValue<String> call(
                                CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
            if (i == columnNames.length - 1) {
                column.maxWidthProperty().bind(tvData.widthProperty());
            }
            tvData.getColumns().add(column);
        }
    }

    private void setMembers(List<String> members) {
        if (members != null) {
            obsMembers.clear();
            cbMembers.getItems().clear();
            if (members.size() > 1) {
                obsMembers.add("All");
            }
            obsMembers.addAll(members);
            cbMembers.getItems().addAll(obsMembers);
            cbMembers.getSelectionModel().select(0);
        }
    }

    private void setInfo(int minutes, int entries) {
        String entriesInfo = "Entries";
        if (entries == 1) {
            entriesInfo = "Entry";
        }
        txtInfo.setText(String.format("Total time:  %1.1f hr  (%d min)  - %d %s",
                minutes / 60.0, minutes, entries, entriesInfo));
    }

    @FXML
    public void chooseTargetDirectory() {
        tfTargetPath.setText(MyUtils.chooseDirectory("Select location", tfTargetPath.getText()).getPath());
        if (tfSourcePath.getText().length() > 0) {
            btnSaveData.setDisable(false);
        } else {
            btnSaveData.setDisable(true);
        }
    }

    @FXML
    public void saveData() {
        if (dataDisplay != null) {
            MyFile.writeToCsvFile(new String[]{"File", "Date", "Name", "Time", "Description"}, dataDisplay,
                    tfTargetPath.getText() + "/" + cbMembers.getSelectionModel().getSelectedItem()
                            + MyUtils.DATE_TIME_FORMAT.format(LocalDateTime.now()) + ".csv");
            btnSaveData.setStyle("-fx-background-color: lawngreen");
        }
    }

    private void setLineChart() {
        if (lineChart.getData().isEmpty()) {
            lineChart.setTitle("Working Time");
            lineChart.getXAxis().setLabel("Month");
            lineChart.getYAxis().setLabel("Time in minutes");
        } else {
            lineChart.getData().clear();
        }
        ObservableList<XYChart.Series> series = FXCollections.observableArrayList();
        for (int i = obsMembers.size() > 1 ? 1 : 0; i < obsMembers.size(); i++) {
            XYChart.Series<String, Number> ser = new XYChart.Series<>();
            ser.setName(obsMembers.get(i));
            Map<String, Integer> lineChartData = MyUtils.initLineChartData(MyFile.getStartDate(dataAll));
            List<String[]> data = MyFile.getSpecificData(dataAll, ser.getName(), false);
            if (data != null) {
                for (String[] row : data) {
                    String key = LocalDate.parse(row[1], DateTimeFormatter.ISO_DATE).format(MyUtils.MONTH_YEAR_FORMAT);
                    lineChartData.put(key, lineChartData.getOrDefault(key, 0) + MyUtils.strToInt(row[3]));
                }
            }
            if (lineChartData != null) {
                for (Map.Entry<String, Integer> entry : lineChartData.entrySet()) {
                    XYChart.Data point = new XYChart.Data<>(entry.getKey(), entry.getValue());
                    point.setNode(new HoveredThresholdNode(entry.getValue()));
                    ser.getData().add(point);
                }
            }
            series.add(ser);
        }
        lineChart.setData(series);
    }


    /**
     * a node which displays a value on hover, but is otherwise empty
     */
    class HoveredThresholdNode extends StackPane {
        HoveredThresholdNode(int value) {
            final Label label = createDataThresholdLabel(value);
            setOnMouseEntered(mouseEvent -> {
                getChildren().setAll(label);
                setCursor(Cursor.NONE);
                toFront();
            });
            setOnMouseExited(mouseEvent -> {
                getChildren().clear();
                setCursor(Cursor.CROSSHAIR);
            });
        }

        private Label createDataThresholdLabel(int value) {
            final Label label = new Label(format(value));
            label.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-background-color: white");
            label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
            return label;
        }

        private String format(int value) {
            if (value < 60) {
                return value + " min";
            } else if (value % 60 == 0) {
                return value / 60 + " hr";
            }
            return String.format("%d hr %d min", value / 60, value % 60);
        }
    }

    private void setPieChart() {
        if (pieChart.getData().isEmpty()) {
            pieChart.setTitle("Project Participation");
            //pieChart.setLabelLineLength(10);
            pieChart.setLegendSide(Side.RIGHT);
        } else {
            pieChart.getData().clear();
        }
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (int i = obsMembers.size() > 1 ? 1 : 0; i < obsMembers.size(); i++) {
            int minutes = 0;
            List<String[]> data = MyFile.getSpecificData(dataAll, obsMembers.get(i), false);
            if (data != null) {
                for (String[] row : data) {
                    minutes += MyUtils.strToInt(row[3]);
                }
            }
            //if (name != null && name.equals("GNA")) minutes *= 1.5;
            //if (name != null && name.equals("PON")) minutes *= 1.5;
            pieChartData.add(new PieChart.Data(obsMembers.get(i), minutes));
        }
        pieChart.setData(pieChartData);
    }


    @FXML
    public void setTaskFilter() {
        boolean selected = cbTaskFilter.isSelected();
        btnFilterStatus.setVisible(selected);
        cbFilterAssignee.setVisible(selected);
        dpFilterFromDate.setVisible(selected);
        if (selected) {
            txTasksInfo.setLayoutX(dpFilterFromDate.getLayoutX() + dpFilterFromDate.getWidth());
        } else {
            txTasksInfo.setLayoutX(btnFilterStatus.getLayoutX());
        }
    }


    private void setTasks() {
        tvTasks.setEditable(true);

        //List<String[]> list = MyFile.getTasks(MyFile.readFromCsvFile("/Users/MET/IdeaProjects/SYP/Tasks.csv"));
        List<String[]> list = GoogleSheetsAPI.getInstance().getData();
        System.out.println(list.size());
        list.remove(0);
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        for (String[] s : list) {
            tasks.add(new Task(s[0], s[1], s[2], s[3], s[4]));
        }

        TableColumn<Task, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        idCol.setStyle("-fx-alignment: center;");
        idCol.setCellFactory(TextFieldTableCell.forTableColumn());
        idCol.setOnEditCommit(
                t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setNumber(t.getNewValue())
        );

        TableColumn<Task, Integer> actionCol = new TableColumn<>("Status");
        actionCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStatus()));
        actionCol.setStyle("-fx-alignment: center;");
        actionCol.setCellFactory(c -> new TableCell<Task, Integer>() {
            int status = 0;
            private final Button button = new Button();

            {
                button.setPrefWidth(100);
                button.setOnAction(t -> {
                    if (status > 3) {
                        status = 0;
                    }
                    setButton(++status);
                });
            }

            private void setButton(int status) {
                switch (status) {
                    case 0:
                        button.setText("open");
                        button.setStyle("-fx-background-color: yellow");
                        break;
                    case 1:
                        button.setText("in progress");
                        button.setStyle("-fx-background-color: dodgerblue");
                        break;
                    case 2:
                        button.setText("bug");
                        button.setStyle("-fx-background-color: red");
                        break;
                    case 3:
                        button.setText("finished");
                        button.setStyle("-fx-background-color: lawngreen");
                        break;
                    default:
                        break;
                }
                commitEdit(status);
            }

            @Override
            public void updateItem(Integer status, boolean empty) {
                super.updateItem(status, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    this.status = status;
                    setButton(this.status);
                    setGraphic(button);
                }
                System.out.println("update" + status);

            }
        });
        actionCol.setOnEditCommit(
                t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setStatus(t.getNewValue())
        );

        ObservableList<String> cbValues = FXCollections.observableArrayList();
        cbValues.addAll(MyFile.getMembers(list));

        TableColumn<Task, String> assigneeCol = new TableColumn<>("Assignee");
        assigneeCol.setStyle("-fx-alignment: center;");
        assigneeCol.setCellValueFactory(new PropertyValueFactory<>("assignee"));
        assigneeCol.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), cbValues));
        assigneeCol.setOnEditCommit(
                t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setAssignee(t.getNewValue())
        );

        TableColumn<Task, Date> dateCol = new TableColumn<>("Date");
        dateCol.setMinWidth(130);
        dateCol.setStyle("-fx-alignment: center;");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setCellFactory((TableColumn<Task, Date> param) -> new DatePickerCell());
        dateCol.setOnEditCommit(
                t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setDate(t.getNewValue())
        );

        TableColumn<Task, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setStyle("-fx-alignment: center_left;");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setCellFactory(TextAreaCell.forTableColumn());
        descriptionCol.setOnEditCommit(
                t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setDescription(t.getNewValue())
        );

        tvTasks.setItems(tasks);
        tvTasks.getColumns().addAll(idCol, actionCol, assigneeCol, dateCol, descriptionCol);
        tvTasks.getColumns().get(tvTasks.getColumns().size() - 1).prefWidthProperty().bind(getRemainingWidth(tvTasks));

        /*//btnFilterStatus.layoutYProperty().bindBidirectional(actionCol.property);
        btnFilterStatus.minWidthProperty().bindBidirectional(actionCol.minWidthProperty());
        cbFilterAssignee.prefWidthProperty().bindBidirectional(assigneeCol.prefWidthProperty());
        dpFilterFromDate.prefWidthProperty().bindBidirectional(dateCol.prefWidthProperty());*/
    }

    private ObservableValue<Number> getRemainingWidth(TableView<?> tv) {
        ReadOnlyDoubleProperty prop = tv.widthProperty();
        for (int i = 0; i < tv.getColumns().size() - 1; i++) {
            prop.subtract(tv.getColumns().get(i).widthProperty());
        }
        return prop.subtract(2); // a border stroke?
    }


    @FXML
    public void generateMilestones() {
        tvTasks.getItems().clear();
        //List<String[]> list = MyFile.getTasks(MyFile.readFromCsvFile("/Users/MET/IdeaProjects/SYP/Tasks.csv"));
        List<String[]> list = MyFile.getTasks(GoogleSheetsAPI.getInstance().getData());
        list.remove(0);
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        for (String[] s : list) {
            tasks.add(new Task(s[0], s[1], s[2], s[3], s[4]));
        }
        tvTasks.setItems(tasks);
    }

}
