package com.example.demo4;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class HelloController {
    @FXML
    private Label calculatorDisplay;
    private double operand1 = 0;
    private double operand2 = 0;
    private double operand3 = 0;
    private String operator = "";

    private boolean clear = false;

    @FXML
    protected void buttonEventHandler(ActionEvent event){


        Button clickedButton = (Button) event.getSource() ;
        String data = (String) clickedButton.getUserData();
        System.out.println(data);
        String text = calculatorDisplay.getText();

        if( text.equals("0") || clear == true){
            calculatorDisplay.setText(data);
            clear = false;
        }
        else {
            text = text + data;
            calculatorDisplay.setText(text);
            clear = false;
        }
    }
    @FXML
    protected void operatorEventHandler(ActionEvent event){
        Button clickedButton = (Button) event.getSource() ;
        String data = (String) clickedButton.getUserData();
        String text = calculatorDisplay.getText();
        // DEAL WITH DECIMAL POINTS
        if (data.equals(".") && !text.contains(".")) {
            calculatorDisplay.setText(text + ".");
        }
        // DEAL WITH NEGATIVE/POSITIVE NUMBERS
        if (data.equals("-/+") && !text.contains("-")) {
            calculatorDisplay.setText("-"+text);
        }
        if (data.equals("-/+") && text.contains("-")) {
            calculatorDisplay.setText(text.substring(1));
        }
        // DEAL WITH Clear or Clear Everything
        if (data.equals("C")) {
            calculatorDisplay.setText("0");
        }
        if (data.equals("CE")) {
            calculatorDisplay.setText("0");
            operand1 = 0;
            operand2 = 0;
            operand3 = 0;
            operator = "";
        }
        if (operator.equals("") && (data.equals("+") || data.equals("-") || data.equals("*")  || data.equals("/"))  ){
            operator = data;
            operand1 = Double.parseDouble(calculatorDisplay.getText());
            calculatorDisplay.setText("0");

        } else if (data.equals("+") || data.equals("-") || data.equals("*")  || data.equals("/") || data.equals("=") ) {
            operand2 = Double.parseDouble(calculatorDisplay.getText());

            if (operator.equals("+")){
                operand3 = operand1 + operand2;
            }
            if (operator.equals("-")){
                operand3 = operand1 - operand2;
            }
            if (operator.equals("*")){
                operand3 = operand1 * operand2;
            }
            if (operator.equals("/")){
                operand3 = operand1 / operand2;
            }
            calculatorDisplay.setText(Double.toString(operand3));
            operand1 = operand3;
            operator = data;
            clear = true;

        }

    }
}