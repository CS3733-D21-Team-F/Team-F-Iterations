package edu.wpi.cs3733.D21.teamF.controllers;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.D21.teamF.database.DatabaseAPI;
import edu.wpi.cs3733.D21.teamF.entities.NodeEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RobotPageController extends AbsController implements Initializable {

    @FXML private JFXComboBox<String> startingLoc;
    @FXML private JFXComboBox<String> destination;


    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        try{
            List<NodeEntry> nodeEntries = DatabaseAPI.getDatabaseAPI().genNodeEntries();

            final ObservableList<String> nodeList = FXCollections.observableArrayList();
            for(NodeEntry n: nodeEntries){
                nodeList.add(n.getShortName());
            }
            this.startingLoc.setItems(nodeList);
            this.destination.setItems(nodeList);

        } catch(Exception e){

        }
    }
}
