package edu.wpi.cs3733.D21.teamF.controllers.ServiceRequestsControllers;

import com.jfoenix.controls.JFXRadioButton;
import edu.wpi.cs3733.D21.teamF.controllers.ServiceRequests;
import edu.wpi.cs3733.D21.teamF.database.DatabaseAPI;
import edu.wpi.cs3733.D21.teamF.utils.EmailHandler;
import edu.wpi.cs3733.D21.teamF.utils.SceneContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;


public class LaundryRequestController extends ServiceRequests {

    @FXML private JFXRadioButton darks;
    @FXML private JFXRadioButton lights;
    @FXML private JFXRadioButton both;
    @FXML private JFXRadioButton hot;
    @FXML private JFXRadioButton cold;
    @FXML private JFXRadioButton folded;

    @FXML public TextField email;
    @FXML public TextField clientName;
    @FXML public TextField additionalInstructions;

    public TextField getEmail() {
        return email;
    }

    public void setEmail(TextField email) {
        this.email = email;
    }

    public TextField getClientName() {
        return clientName;
    }

    public void setClientName(TextField clientName) {
        this.clientName = clientName;
    }

    public TextField getAdditionalInstructions() {
        return additionalInstructions;
    }

    public void setAdditionalInstructions(TextField additionalInstructions) {
        this.additionalInstructions = additionalInstructions;
    }

    @FXML
    public void initialize(){
    }

    @FXML
    public void handleSubmit(ActionEvent e) throws IOException, SQLException, MessagingException {
        if(formFilled()) {
            // Loads form submitted window and passes in current stage to return to request home
            String uuid = UUID.randomUUID().toString();
            String type = "Laundry Request";
            String person = "";
            String completed = "false";
            String additionalInstructions = additionalInformation();
            DatabaseAPI.getDatabaseAPI().addServiceReq(uuid, type, person, completed, additionalInstructions);
            EmailHandler.getEmailHandler().sendEmail(additionalInstructions.split(";")[1], "Service Request Confirmation",
                    "Hello,\nThis is a confirmation email for your service request " + type + " it will be completed as soon as possible");

            openSuccessWindow();
        }
    }

    private String additionalInformation(){
        ArrayList<JFXRadioButton> rButtons = new ArrayList<>();
        rButtons.add(darks);
        rButtons.add(lights);
        rButtons.add(both);
        rButtons.add(hot);
        rButtons.add(cold);
        rButtons.add(folded);
        StringBuilder additionalInfo = new StringBuilder("Laundry Instructions: ");

        for(JFXRadioButton r: rButtons){
            if(r.isSelected()){
                additionalInfo.append(", ").append(r.getText());
            }
        }

        additionalInfo.append(" ;").append(email.getText());

        return additionalInfo.toString();
    }

    /**
     * handles radial button groups
     */
    @FXML
    private void handleRadialButtonPushed(){
        ToggleGroup tempGroup = new ToggleGroup();
        hot.setToggleGroup(tempGroup);
        cold.setToggleGroup(tempGroup);

        ToggleGroup colorGroup = new ToggleGroup();
        darks.setToggleGroup(colorGroup);
        lights.setToggleGroup(colorGroup);
        both.setToggleGroup(colorGroup);
    }


    @Override
    public boolean formFilled() {
        boolean isFilled = true;

        setNormalStyle(email, hot, cold, darks, lights, both);

        if(email.getText().length() == 0){
            isFilled = false;
            setTextErrorStyle(email);
        }
//        if(clientEmail.getText().length() == 0){
//            isFilled = false;
//            setTextErrorStyle(clientEmail);
//        }
        if(! (hot.isSelected() || cold.isSelected())){
            isFilled = false;
            setButtonErrorStyle(hot, cold);
        }
        if (! (darks.isSelected() || lights.isSelected() || both.isSelected())) {
            isFilled = false;
            setButtonErrorStyle(darks, lights, both);
        }

        return isFilled;
    }

    @Override
    public void handleClear() {
        both.setSelected(false);
        lights.setSelected(false);
        darks.setSelected(false);
        hot.setSelected(false);
        cold.setSelected(false);
        folded.setSelected(false);
        email.setText("");
        //clientEmail.setText("");
        additionalInstructions.setText("");
        setNormalStyle(both, lights, darks, hot, cold, folded, email, additionalInstructions);
    }

    public void handleHelp(ActionEvent e) throws IOException {
        SceneContext.getSceneContext().switchScene("/edu/wpi/cs3733/D21/teamF/fxml/ServiceRequests/LaundryRequestHelpView.fxml");
    }

    public void goBack(ActionEvent actionEvent)throws IOException {
        SceneContext.getSceneContext().switchScene("/edu/wpi/cs3733/D21/teamF/fxml/ServiceRequests/LaundryRequest.fxml");
    }
}
