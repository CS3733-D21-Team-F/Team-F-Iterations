package edu.wpi.cs3733.D21.teamF.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.D21.teamF.database.DatabaseAPI;
import edu.wpi.cs3733.D21.teamF.utils.SceneContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class externalTransController extends ServiceRequests{
    @FXML private JFXTextField patientName;
    @FXML private JFXTextField loc;
    @FXML private JFXTextField methodTrans;
    @FXML private JFXTextField special;


    @FXML
    public void handleSubmit(ActionEvent actionEvent) throws IOException, SQLException {
        if(formFilled()) {
            String uuid = UUID.randomUUID().toString();
            String type = "External Transit";
            String assignedPerson = "";
            String additionalInfo = "Location: " + loc.getText() + "Transit method: " + methodTrans.getText()
                    + "Special info:" + special.getText();
            DatabaseAPI.getDatabaseAPI().addServiceReq(uuid, type, assignedPerson, "false", additionalInfo);
            // Loads form submitted window and passes in current stage to return to request home
            openSuccessWindow();
        }
    }

    @Override
    public boolean formFilled() {
        return patientName.getText().length()>0 && methodTrans.getText().length()>0 && special.getText().length()>0;
    }

    @Override
    public void handleClear() {
        patientName.setText("");
        loc.setText("");
        methodTrans.setText("");
        special.setText("");
        special.setStyle("-fx-text-fill: #000000");
        loc.setStyle("-fx-text-fill: #000000");
        methodTrans.setStyle("-fx-text-fill: #000000");
    }
}
