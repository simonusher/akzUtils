package ml.szymonwozniak.akzutils.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class CoursePromptController implements Initializable{
    private static CoursePromptController instance;
    public TextArea textArea;

    public void setText(String text) {
        this.textArea.setText(text);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
    }

    public static CoursePromptController getInstance() {
        return instance;
    }
}
