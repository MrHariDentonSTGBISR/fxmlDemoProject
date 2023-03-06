package com.example.demo4;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    public void buttonEventHandler(ActionEvent event){
        Button node = (Button) event.getSource() ;
        String data = (String) node.getUserData();
        String text = welcomeText.getText();
        text = text + data;
        welcomeText.setText(text);

    }
}