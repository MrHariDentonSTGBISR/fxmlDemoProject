package com.example.demo4;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.sql.*;


public class HelloController {

    static final String DB_URL = "jdbc:mysql://localhost:3306/sakila";
    static String driverName = "com.mysql.cj.jdbc.Driver";
    static final String USER = "admin";
    static final String PASS = "admin";
    static final String QUERY = "SELECT * FROM actor";

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
    @FXML
    protected void connectToDB(ActionEvent event){

        // Open a connection
        try{Connection conn = null;
            Class.forName(driverName).newInstance();
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(QUERY);

            while(rs.next()){
                //Display values
                System.out.print("ID: " + rs.getInt("actor_id"));
                System.out.print(", FirstName: " + rs.getString("first_name"));
                System.out.print(", LasstName: " + rs.getString("last_name"));
                System.out.println(", LastUpdate: " + rs.getString("last_update"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
}