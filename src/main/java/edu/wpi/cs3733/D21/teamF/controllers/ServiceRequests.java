package edu.wpi.cs3733.D21.teamF.controllers;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.D21.teamF.utils.SceneContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.SQLException;

public abstract class ServiceRequests {

    @FXML
    private JFXButton submitButton;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private JFXButton helpButton;

    public void handleSubmit(ActionEvent e) throws IOException, SQLException {

    }

    public void handleCancel(ActionEvent e) throws IOException {
        SceneContext.getSceneContext().switchScene("/edu/wpi/cs3733/D21/teamF/fxml/ServiceRequestHomeNewView.fxml");
    }

    public void handleHelp(ActionEvent e) throws IOException {

    }

    public void handleHome(MouseEvent e) throws IOException {
        SceneContext.getSceneContext().loadDefault();
    }

    public boolean formFilled() {
        return false;
    }

}
