package edu.wpi.cs3733.D21.teamF.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.D21.teamF.database.DatabaseAPI;
import edu.wpi.cs3733.D21.teamF.entities.AccountEntry;
import edu.wpi.cs3733.D21.teamF.utils.SceneContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private JFXButton closeLogin;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXButton signIn;
    @FXML
    private Label errorMessage;
    @FXML
    private JFXButton skipSignIn;

    public void handleButtonPushed(ActionEvent actionEvent) throws IOException, SQLException, SQLException {
        Button buttonPushed = (JFXButton) actionEvent.getSource();  //Getting current stage
        if (buttonPushed == closeLogin) {
            SceneContext.getSceneContext().switchScene("/edu/wpi/cs3733/D21/teamF/fxml/DefaultPageView.fxml");
        } else if (buttonPushed == signIn) {
            if (!DatabaseAPI.getDatabaseAPI().verifyAdminExists()) {
                String[] admin = {"admin", "administrator", "admin", "admin"};
                String[] staff = {"staff", "employee", "staff", "staff"};
                String[] guest = {"guest", "visitor", "guest", "guest"};
                DatabaseAPI.getDatabaseAPI().addUser(admin, new String[]{""}, new String[]{""});
                DatabaseAPI.getDatabaseAPI().addUser(staff, new String[]{""}, new String[]{""});
                DatabaseAPI.getDatabaseAPI().addUser(guest, new String[]{""}, new String[]{""});
            }
            boolean authenticated = false;
            String user = username.getText();
            String pass = password.getText();

            authenticated = DatabaseAPI.getDatabaseAPI().authenticate(user, pass);
            if (authenticated) {
                AccountEntry userInfo = DatabaseAPI.getDatabaseAPI().getUser(user);
                errorMessage.setStyle("-fx-text-fill: #c6000000;");
                if (userInfo.getUserType().equals("administrator")) {
                    SceneContext.getSceneContext().switchScene("/edu/wpi/cs3733/D21/teamF/fxml/DefaultPageAdminView.fxml");
                    // set user privileges to employee
                } else if (userInfo.getUserType().equals("employee")) {
                    SceneContext.getSceneContext().switchScene("/edu/wpi/cs3733/D21/teamF/fxml/DefaultPageEmployeeView.fxml");
                } else{
                    SceneContext.getSceneContext().switchScene("/edu/wpi/cs3733/D21/teamF/fxml/DefaultPageView.fxml");
                }
            }
            else {
                errorMessage.setStyle("-fx-text-fill: #c60000FF;");
                password.setText("");
            }
        }
            else if (buttonPushed == skipSignIn) {
                SceneContext.getSceneContext().switchScene("/edu/wpi/cs3733/D21/teamF/fxml/DefaultPageView.fxml");
            }

    }



}
