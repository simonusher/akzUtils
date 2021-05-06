package ml.szymonwozniak.akzutils.controller;

import ml.szymonwozniak.akzutils.Main;
import ml.szymonwozniak.akzutils.model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable{
    public TableView akzCoursesTable;
    public TableColumn<Course, String> courseCodeColumn;
    public TableColumn<Course, String> groupCodeColumn;
    public TableColumn<Course, String> nameColumn;
    public TableColumn<Course, List<TimeAndPlace>> timesAndPlacesColumn;
    public TableColumn<Course, String> lecturersColumn;
    public TableColumn<Course, Integer> freePlacesColumn;
    public TableColumn<Course, Integer> zzuColumn;
    public TableColumn<Course, String> stationaryColumn;
    public TableColumn<Course, String> gradeColumn;
    public TableColumn<Course, String> commentsColumn;
    public TextField searchTextField;
    public ComboBox courseTypeCombo;
    public RadioButton showOnlyFitCoursesRadioButton;
    public TextField notSearchTextField;
    public RadioButton showOnlyFreeCourses;
    public ComboBox stationaryCombo;
    public ComboBox degreeCombo;
    private Stage helpStage;
    private Stage jsosStage;
    private Stage aboutStage;
    private static MainScreenController instance;
    private FilterListUtils filterListUtils;
    Timetable t;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        groupCodeColumn.setCellValueFactory(new PropertyValueFactory<>("groupCode"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        timesAndPlacesColumn.setCellValueFactory(new PropertyValueFactory<>("timesAndPlaces"));
        lecturersColumn.setCellValueFactory(new PropertyValueFactory<>("lecturers"));
        freePlacesColumn.setCellValueFactory(new PropertyValueFactory<>("freePlaces"));
        zzuColumn.setCellValueFactory(new PropertyValueFactory<>("ZZU"));
        stationaryColumn.setCellValueFactory(new PropertyValueFactory<>("stationary"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        commentsColumn.setCellValueFactory(new PropertyValueFactory<>("comments")); //TODO: REMOVE THIS COLUMN AND REPLACE IT WITH PROMPT MESSAGE WHEN HOVERING OVER THE CELL WITH A CURSOR, AS IT IS UNNECESSARY AND CLUTTERS UP THE SCREEN
        courseTypeCombo.setItems(FXCollections.observableArrayList(Course.CourseType.values()));
        stationaryCombo.setItems(FXCollections.observableArrayList(Course.Stationary.values()));
        degreeCombo.setItems(FXCollections.observableArrayList(Course.Grade.values()));


        courseTypeCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterListUtils.addPredicate(MyPredicate.PredicateType.COURSETYPE, course -> {
                if(newValue == Course.CourseType.WSZYSTKIE || ((Course)course).getCourseType() == newValue) return true;
                else return false;
            });
        });


        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterListUtils.addPredicate(MyPredicate.PredicateType.SEARCH,
                course -> {
                    if(newValue.equals("") || newValue.equals(" ")) return true;
                    else return ((Course)course).containsAll(Arrays.asList(searchTextField.getText().trim().toLowerCase().split(" ")));
                });
        });

        notSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterListUtils.addPredicate(MyPredicate.PredicateType.NOT,
                    course -> {
                        if(newValue.equals("") || newValue.equals(" ")) return true;
                        else return !((Course)course).containsAny(Arrays.asList(notSearchTextField.getText().trim().toLowerCase().split(" ")));
                    });
        });

        showOnlyFitCoursesRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filterListUtils.addPredicate(MyPredicate.PredicateType.TIMETABLE,
                    course -> {
                        if (!newValue) return true;
                        else return ((Course) course).fitsTimetable(t);
                    }
                    );
        });
        showOnlyFitCoursesRadioButton.setDisable(true);

        showOnlyFreeCourses.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filterListUtils.addPredicate(MyPredicate.PredicateType.FREE,
                    course -> {
                        if (!newValue) return true;
                        else return ((Course) course).getFreePlaces() > 0;
                    }
            );
        });

        stationaryCombo.valueProperty().addListener(((observable, oldValue, newValue) -> {
            filterListUtils.addPredicate(MyPredicate.PredicateType.STATIONARY, course -> newValue == Course.Stationary.WSZYSTKIE ||
                    (newValue == Course.Stationary.STACJONARNE && ((Course) course).getStationary().trim().equals("Tak")) ||
                    (newValue == Course.Stationary.NIESTACJONARNE && ((Course) course).getStationary().trim().equals("Nie")));
            }));

        degreeCombo.valueProperty().addListener(((observable, oldValue, newValue) -> {
            filterListUtils.addPredicate(MyPredicate.PredicateType.GRADE,
                    course -> {
                        if(newValue == Course.Grade.WSZYSTKIE || ((Course)course).getGrade() == null || ((Course)course).getGrade().equals("")) return true;
                        else if(newValue == Course.Grade.II && ((Course) course).getGrade().contains("II")) return true;
                        else if(newValue == Course.Grade.I){
                            if(((Course) course).getGrade().equals("I")) return true;
                            else {
                                return Arrays.stream(((Course) course).getGrade().split(" "))
                                        .anyMatch(s -> s.equals("I"));
                            }
                        }
                        else return false;
                    }
            );
        }));


        akzCoursesTable.setRowFactory( tv -> {
            TableRow<Course> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    showCoursePromptMessage(row.getItem());
                }
            });
            row.setTooltip(new Tooltip("Kliknij 2 razy, aby wyświetlić więcej informacji."));
            return row ;
        });
        instance = this;

    }

    public void start(){
        Parser p = Parser.getInstance();
        filterListUtils = new FilterListUtils(new FilteredList<>(FXCollections.observableArrayList(Parser.parseAllCourses(p.getElements())), t -> true));
        SortedList<Course> sortedData = new SortedList<>(filterListUtils.getFilteredList());
        sortedData.comparatorProperty().bind(akzCoursesTable.comparatorProperty());
        akzCoursesTable.setItems(sortedData);
    }

    public static MainScreenController getInstance() {
        return instance;
    }

    public void openFileChooser(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Otwórz plan");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Wszystkie pliki", "*.*"),
                new FileChooser.ExtensionFilter("HTML", "*.html")
        );
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            if (file.getName().endsWith(".html") || file.getName().endsWith(".htm")){
                openTimetable(file);
            }
        }
    }

    public void openTimetable(File file){
        try{
            t = new Timetable(file);
            showOnlyFitCoursesRadioButton.setDisable(false);
            if(showOnlyFitCoursesRadioButton.selectedProperty().getValue()){
                showOnlyFitCoursesRadioButton.selectedProperty().setValue(false);
                showOnlyFitCoursesRadioButton.selectedProperty().setValue(true);
            }
        }catch (HtmlParsingException e){
            showOnlyFitCoursesRadioButton.selectedProperty().setValue(false);
            showOnlyFitCoursesRadioButton.setDisable(true);
            Platform.runLater(() -> {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText("BŁĄD");
                a.setContentText("Nieprawidłowy format pliku HTML");
                a.show();
            });
        }
    }

    public void openTimetable(Timetable t){
        this.t = t;
        showOnlyFitCoursesRadioButton.setDisable(false);
        if(showOnlyFitCoursesRadioButton.selectedProperty().getValue()){
            showOnlyFitCoursesRadioButton.selectedProperty().setValue(false);
            showOnlyFitCoursesRadioButton.selectedProperty().setValue(true);
        }

    }

    public void deleteTimetable(){
        t = null;
    }

    public void openHelpMenu(ActionEvent actionEvent) {
        if(this.helpStage == null){
            this.helpStage = Main.getInstance().getHelpStage();
            if(this.helpStage != null){
                this.helpStage.setOnCloseRequest(e -> this.helpStage = null);
                this.helpStage.show();
            }
        }
        else {
           this.helpStage.toFront();
        }
    }

    public void openAbout(ActionEvent actionEvent) {
        if(this.aboutStage == null){
            this.aboutStage = Main.getInstance().getAboutStage();
            if(this.aboutStage != null){
                this.aboutStage.setOnCloseRequest(e -> this.aboutStage = null);
                this.aboutStage.show();
            }
        }
        else {
            this.aboutStage.toFront();
        }
    }

    private void showCoursePromptMessage(Course course){
        Platform.runLater(() -> {
            Stage s = new Stage();
            s.setTitle(course.getTitle());
            s.setScene(Main.getInstance().getCoursePromptScene());
            ((TextArea)s.getScene().getRoot().getChildrenUnmodifiable().get(0)).setText(course.toPrompt());
            s.show();
        });
    }

    public void update(){
        Parser p = Parser.getInstance();
        filterListUtils.setFilteredList(new FilteredList<>(FXCollections.observableArrayList(Parser.parseAllCourses(p.getElements())), t -> true));
        SortedList<Course> sortedData = new SortedList<>(filterListUtils.getFilteredList());
        sortedData.comparatorProperty().bind(akzCoursesTable.comparatorProperty());
        akzCoursesTable.setItems(sortedData);
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Update");
            a.setContentText("Pobrano dane z AKZ");
            a.show();
        });
    }

    public void close(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void openJsos(ActionEvent actionEvent) {
        if(this.jsosStage == null){
            this.jsosStage = Main.getInstance().getJsosStage();
            if(this.jsosStage != null){
                this.jsosStage.setOnCloseRequest(e -> this.jsosStage = null);
                this.jsosStage.show();
            }
        }
        else {
            this.jsosStage.toFront();
        }
    }

    public Stage getJsosStage() {
        return jsosStage;
    }
}