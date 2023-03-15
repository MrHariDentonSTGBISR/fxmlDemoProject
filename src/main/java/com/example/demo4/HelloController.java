package com.example.demo4;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.*;


public class HelloController {

    static final String DB_URL = "jdbc:mysql://localhost:3306/sakila";
    static String driverName = "com.mysql.cj.jdbc.Driver";
    static final String USER = "admin";
    static final String PASS = "admin";
    static final String QUERY = "SELECT * FROM actor";

    static ResultSet rs;

    @FXML TextField txtID;
    @FXML TextField txtFirstName;



    @FXML
    protected void connectToDB(ActionEvent event){

        // Open a connection
        try{Connection conn = null;
            Class.forName(driverName).newInstance();
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(QUERY);
            rs.next();
            txtID.setText(rs.getString("actor_id"));
            txtFirstName.setText(rs.getString("first_name"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    protected void next(ActionEvent event){
        // write code later
        // Open a connection
        try{
            rs.next();
            txtID.setText(rs.getString("actor_id"));
            txtFirstName.setText(rs.getString("first_name"));
            //System.out.println(rs.getString("actor_id") );
        } catch (Exception e) {
            e.printStackTrace();
            //previous(event);
        }
    }
    @FXML
    protected void previous(ActionEvent event){
        // write code later
        try{
            rs.previous();
            txtID.setText(rs.getString("actor_id"));
            txtFirstName.setText(rs.getString("first_name"));
            //System.out.println(rs.getString("actor_id") );
        } catch (Exception e) {
            e.printStackTrace();
            //previous(event);
        }
    }
}