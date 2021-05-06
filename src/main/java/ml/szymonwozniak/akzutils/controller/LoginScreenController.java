package ml.szymonwozniak.akzutils.controller;

import ml.szymonwozniak.akzutils.model.HtmlParsingException;
import ml.szymonwozniak.akzutils.model.Parser;
import ml.szymonwozniak.akzutils.model.Timetable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.jsoup.Jsoup;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginScreenController implements Initializable{

    public WebView webView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.setProperty("jsse.enableSNIExtension", "false");
        final WebEngine webEngine = webView.getEngine();
        webEngine.load("https://jsos.pwr.edu.pl/");
    }

    public void readTimetable(ActionEvent actionEvent) {
        try{
            Timetable t = new Timetable(Parser.parseJSOS(Parser.parse(Jsoup.parse((String) webView.getEngine().
                    executeScript("document.documentElement.outerHTML")), Parser.QueryType.Class, "kliknij")));
            MainScreenController.getInstance().openTimetable(t);
            MainScreenController.getInstance().getJsosStage().hide();

        }catch (HtmlParsingException e){
            Platform.runLater(() -> {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText("BŁĄD");
                a.setContentText("Wystąpił błąd podczas wczytywania planu.\nPodpowiedź: wejdź w zakładkę 'Zajęcia' i wybierz odpowiedni semsestr.");
                a.show();
            });
        }
    }
}
