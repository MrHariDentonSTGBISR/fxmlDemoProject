package com.example.demo4;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.sql.*;
import java.time.LocalDateTime;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import java.util.Optional;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;


public class HelloController {

    static final String DB_URL = "jdbc:mysql://localhost:3306/sakila";
    static String driverName = "com.mysql.cj.jdbc.Driver";
    static final String USER = "admin";
    static final String PASS = "admin";
    static final String QUERY = "SELECT * FROM city";

    static Connection conn = null;

    static ResultSet rs;
    static boolean currentlyEditing = false;
    static boolean currentlyInserting = false;

    //TABLE VIEW AND DATA
    private static ObservableList<ObservableList<String>> data;

    @FXML
    TableView tableview;

    @FXML
    TextField txtCityID, txtCityName, txtCountryID, txtLastUpdate;

    @FXML
    Button btnEdit, btnNew, btnDelete, btnLast, btnSetTableActor , btnSetTableFilm;

    @FXML
    ComboBox<DataItem> cmbCountry;


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
        txtCityID.setEditable(editable);
        txtCityName.setEditable(editable);
        txtCountryID.setEditable(editable);
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
           if (!rs.previous()){rs.last();}
        }
        catch (Exception e) {
            try{
                rs.last();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        finally {
            updateFieldsInView();
        }
    }

    protected void updateFieldsInView() {
        try {
            txtCityID.setText(rs.getString("city_id"));
            txtCityName.setText(rs.getString("city"));
            txtCountryID.setText(rs.getString("country_id"));
            txtLastUpdate.setText(rs.getString("last_update"));
            populateComboBox(cmbCountry,"SELECT country_id, country FROM country ORDER BY country",
                    "country", "country_id", conn);
        } catch (Exception e) {
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
                rs.updateString("city", txtCityName.getText());
                rs.updateString("country_id", txtCountryID.getText());
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
            txtCityID.setEditable(false);
            txtCityID.clear();
            txtCityName.clear();
            txtCountryID.clear();
            txtLastUpdate.clear();
            currentlyInserting = true;
            btnNew.setText("Save");
        } else { try {
                rs.moveToInsertRow();
                rs.updateString("city", txtCityName.getText());
                rs.updateString("country_id", txtCountryID.getText());
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
                String delete_sql = "DELETE FROM city WHERE city_id="+ txtCityID.getText();
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
        String SQLFilm = "SELECT * FROM film";
        creatTableViewStructure(SQLFilm);
        buildData(SQLFilm);
    }
    @FXML public void buildData(String SQL) {
        data.clear();
        try {
            ResultSet buildDataForTableRS = conn.createStatement().executeQuery(SQL);
            while (buildDataForTableRS.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= buildDataForTableRS.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(buildDataForTableRS.getString(i));
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
            ResultSet TableViewStructureRS = conn.createStatement().executeQuery(SQL);
            for (int i = 0; i < TableViewStructureRS.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                int j = i;
                TableColumn col = new TableColumn(TableViewStructureRS.getMetaData().getColumnName(i + 1));
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

    protected void populateComboBox(ComboBox<DataItem> comboBox, String query, String displayColumn, String idColumn, Connection connection) throws SQLException {
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ObservableList<DataItem> options = FXCollections.observableArrayList();
            DataItem selected = new DataItem(-1,"Nothing selected");
            while (resultSet.next()) {
                DataItem newItem = new DataItem(Integer.parseInt(resultSet.getString(idColumn)),resultSet.getString(displayColumn));
                options.add(newItem);

                if(Integer.parseInt(txtCountryID.getText()) == newItem.getId()) {
                    selected = newItem;
                }
            }
            comboBox.setItems(options);
            comboBox.setValue(selected);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building ComboBox");
        }
    }

    protected int getSeelectedID(ComboBox<DataItem> comboBox){
        return comboBox.getValue().getId();
    }

}