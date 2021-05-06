package ml.szymonwozniak.akzutils;

import ml.szymonwozniak.akzutils.controller.MainScreenController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static Main instance;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
        primaryStage.setTitle("AKZUtils");
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest(e -> Platform.exit());
        primaryStage.show();
        MainScreenController.getInstance().start();
        instance = this;
    }

    public static Main getInstance(){
        return instance;
    }

    public Stage getHelpStage() {
        try{
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("HelpScreen.fxml")));
            Stage stage = new Stage();
            stage.setTitle("Pomoc");
            stage.setScene(scene);
            return stage;
        }catch (IOException e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("Błąd wczytywania");
            a.setContentText("Nie udało się wczytać okna pomocy.");
            a.show();
            return null;
        }
    }

    public Stage getAboutStage() {
        try{
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("About.fxml")));
            Stage stage = new Stage();
            stage.setTitle("O aplikacji");
            stage.setScene(scene);
            return stage;
        }catch (IOException e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("Błąd wczytywania");
            a.setContentText("Nie udało się wczytać okna 'O aplikacji'.");
            a.show();
            return null;
        }
    }

    public Stage getJsosStage(){
        try{
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("LoginScreen.fxml")));
            Stage stage = new Stage();
            stage.setTitle("JSOS");
            stage.setScene(scene);
            return stage;
        }catch (IOException e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("Błąd wczytywania");
            a.setContentText("Nie udało się wczytać okna 'JSOS'.");
            a.show();
            return null;
        }
    }

    public Scene getCoursePromptScene(){
        try {
            return new Scene(FXMLLoader.load(getClass().getResource("CoursePromptDialog.fxml")));
        }catch (IOException e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("Błąd wczytywania");
            a.setContentText("Nie udało się wczytać okna z informacjami o kursie.");
            a.show();
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
