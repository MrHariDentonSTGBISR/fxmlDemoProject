package com.example.demo4;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.sql.*;


public class HelloController {

    static final String DB_URL = "jdbc:mysql://localhost:3306/sakila";
    static String driverName = "com.mysql.cj.jdbc.Driver";
    static final String USER = "admin";
    static final String PASS = "admin";
    static final String QUERY = "SELECT * FROM actor";

    static Connection conn = null;

    static ResultSet rs;
    static boolean currentlyEditing = false;

    @FXML TextField txtID;
    @FXML TextField txtFirstName;
    @FXML TextField txtLastName;
    @FXML TextField txtLastUpdate;
    @FXML Button btnEdit;




    @FXML
    public void initialize(){

        // Open a connection
        try{
            Class.forName(driverName).newInstance();
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(QUERY);
            rs.next();
            updateFieldsInView();
            setEditable(false);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setEditable(boolean editable) {
        txtID.setEditable(editable);
        txtFirstName.setEditable(editable);
        txtLastName.setEditable(editable);
        txtLastUpdate.setEditable(editable);
    }

    @FXML
    protected void next(ActionEvent event){
        // write code later
        // Open a connection
        try{
            rs.next();
            updateFieldsInView();
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
            updateFieldsInView();
        } catch (Exception e) {
            e.printStackTrace();
            //previous(event);
        }
    }

    protected void updateFieldsInView(){
        try{
            txtID.setText(rs.getString("actor_id"));
            txtFirstName.setText(rs.getString("first_name"));
            txtLastName.setText(rs.getString("last_name"));
            txtLastUpdate.setText(rs.getString("last_update"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML protected void update()
    {
        if (!currentlyEditing){
            setEditable(true);
            currentlyEditing = true;
            btnEdit.setText("Save");
        }
        else {
            try{
                rs.updateString("first_name", txtFirstName.getText());
                rs.updateString("last_name", txtLastName.getText());
                rs.updateTimestamp("last_update", Timestamp.valueOf(txtLastUpdate.getText()));
                rs.updateRow();

                setEditable(false);
                currentlyEditing = false;
                btnEdit.setText("Edit");
                updateFieldsInView();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}