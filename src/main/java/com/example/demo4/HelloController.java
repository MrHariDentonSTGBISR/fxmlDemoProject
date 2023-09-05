package com.example.demo4;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.sql.*;
import java.time.LocalDateTime;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;


public class HelloController {

    static final String DB_URL = "jdbc:mysql://localhost:3306/sakila";
    static String driverName = "com.mysql.cj.jdbc.Driver";
    static final String USER = "admin";
    static final String PASS = "admin";
    static final String QUERY = "SELECT * FROM actor";

    static Connection conn = null;

    static ResultSet rs;
    static boolean currentlyEditing = false;
    static boolean currentlyInserting = false;

    //TABLE VIEW AND DATA
    private static ObservableList<ObservableList<String>> data;
    @FXML Button btnSetTableActor;
    @FXML Button btnSetTableFilm;

    @FXML
    TableView tableview;

    @FXML
    TextField txtID;
    @FXML
    TextField txtFirstName;
    @FXML
    TextField txtLastName;
    @FXML
    TextField txtLastUpdate;
    @FXML
    Button btnEdit;
    @FXML
    Button btnNew;
    @FXML
    Button btnDelete;
    @FXML
    Button btnLast;

    @FXML
    public void initialize() {
        data = FXCollections.observableArrayList();
        // Open a connection
        try {
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
    protected void next(ActionEvent event) {
        try {
            if (!rs.next()){rs.first();}
        }
        catch (Exception e) {
            try{
                rs.first();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        finally {
            updateFieldsInView();
        }
    }

    @FXML
    protected void previous(ActionEvent event) {
        // write code later
        try {
           if (! rs.previous()){rs.last();}
        }
        catch (Exception e) {
            try{
                System.out.println("2");
                rs.first();
                System.out.println("3");
                rs.last();
                System.out.println("4");
            }
            catch (Exception ex) {
                System.out.println("5");
                ex.printStackTrace();
            }
        }
        finally {
            updateFieldsInView();
        }
    }

    protected void updateFieldsInView() {
        try {
            txtID.setText(rs.getString("actor_id"));
            txtFirstName.setText(rs.getString("first_name"));
            txtLastName.setText(rs.getString("last_name"));
            txtLastUpdate.setText(rs.getString("last_update"));
        } catch (Exception e) {
            System.out.println("6");
            e.printStackTrace();
        }
    }

    @FXML
    protected void update() {
        if (!currentlyEditing) {
            setEditable(true);
            currentlyEditing = true;
            btnEdit.setText("Save");
        } else {
            try {
                rs.updateString("first_name", txtFirstName.getText());
                rs.updateString("last_name", txtLastName.getText());
                rs.updateTimestamp("last_update", Timestamp.valueOf(txtLastUpdate.getText()));
                rs.updateRow();

                setEditable(false);
                currentlyEditing = false;
                btnEdit.setText("Edit");
                updateFieldsInView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public void insert() {
        if (!currentlyInserting) {
            setEditable(true);
            txtID.setEditable(false);
            txtID.clear();
            txtFirstName.clear();
            txtLastName.clear();
            txtLastUpdate.clear();
            currentlyInserting = true;
            btnNew.setText("Save");
        } else { try {
                rs.moveToInsertRow();
                rs.updateString("first_name", txtFirstName.getText());
                rs.updateString("last_name", txtLastName.getText());
                rs.updateTimestamp("last_update", Timestamp.valueOf(LocalDateTime.now()));
                rs.insertRow();
                setEditable(false);
                currentlyEditing = false;
                btnNew.setText("New");
                updateFieldsInView();
            } catch (Exception e) { e.printStackTrace();}
        }
    }

    @FXML public void delete(ActionEvent actionEvent) {

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Attention: Action cannot be undone.");
        alert.setHeaderText("Are you sure you would like to delete this and all related records?");
        alert.setContentText("Click OK to DELETE or CANCEL to return to the main app.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // ... user chose OK
            try {
                //Delete the child references to this actor first
                String delete_sql = "DELETE FROM film_actor WHERE actor_id="+txtID.getText();
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(delete_sql);
                //once the references to this actor are deleted we can delete the actor record
                rs.deleteRow();
                // call initilize to refresh the resultSet and show the first record
                initialize();
                System.out.println("RECORD DELETED");
            } catch (Exception e)
            {
                System.out.println("ERROR IN DELETING");
                e.printStackTrace();
            }
        } else {
            // ... user chose CANCEL or closed the dialog
            System.out.println("DELETE CANCELLED BY USER");
        }

    }//end of method delete

    @FXML public void setTableActor(){
        String SQLActor = "SELECT * FROM actor";
        creatTableViewStructure(SQLActor);
        buildData(SQLActor);
    }

    @FXML public void setTableFilm(){
        String SQLFilm = "SELECT * FROM film LIMIT 10";
        creatTableViewStructure(SQLFilm);
        buildData(SQLFilm);
    }
    @FXML public void buildData(String SQL) {
        data.clear();
        try {
            ResultSet rs = conn.createStatement().executeQuery(SQL);
            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                System.out.println("Row [1] added " + row);
                data.add(row);
            }
            tableview.setItems(data);
            tableview.refresh();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }// end buildData

    @FXML public void creatTableViewStructure(String SQL) {
        tableview.getColumns().clear();
        try {
            //ResultSet
            rs = conn.createStatement().executeQuery(SQL);
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        if (param.getValue().get(j)==null){ return new SimpleStringProperty("");}
                        else { return new SimpleStringProperty(param.getValue().get(j).toString());}
                    }
                });
                tableview.getColumns().addAll(col);
                System.out.println("Column [" + i + "] ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }// end creatTableViewStructure

}