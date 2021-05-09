package edu.wpi.cs3733.D21.teamF.controllers.ServiceRequestsControllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import edu.wpi.cs3733.D21.teamF.Translation.Translator;
import edu.wpi.cs3733.D21.teamF.controllers.ServiceRequests;
import edu.wpi.cs3733.D21.teamF.database.DatabaseAPI;
import edu.wpi.cs3733.D21.teamF.utils.SceneContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;


public class LaundryRequestController extends ServiceRequests {

    @FXML private JFXButton cancel;
    @FXML private JFXButton clearButton;
    @FXML private JFXButton submitButton;
    @FXML private JFXButton help;
    @FXML private JFXRadioButton darks;
    @FXML private JFXRadioButton lights;
    @FXML private JFXRadioButton both;
    @FXML private JFXRadioButton hot;
    @FXML private JFXRadioButton cold;
    @FXML private JFXRadioButton folded;
    @FXML private Label nameLbl;
    @FXML private Label optionsLbl;
    @FXML private Label addInstructLbl;

    @FXML public TextField employeeID;
    @FXML public TextField clientName;
    @FXML public TextField additionalInstructions;

    public TextField getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(TextField employeeID) {
        this.employeeID = employeeID;
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
//        nameLbl.textProperty().bind(Translator.getTranslator().getTranslationBinding(nameLbl.getText()));
//        optionsLbl.textProperty().bind(Translator.getTranslator().getTranslationBinding(optionsLbl.getText()));
//        addInstructLbl.textProperty().bind(Translator.getTranslator().getTranslationBinding(addInstructLbl.getText()));
//        darks.textProperty().bind(Translator.getTranslator().getTranslationBinding(darks.getText()));
//        lights.textProperty().bind(Translator.getTranslator().getTranslationBinding(lights.getText()));
//        both.textProperty().bind(Translator.getTranslator().getTranslationBinding(both.getText()));
//        hot.textProperty().bind(Translator.getTranslator().getTranslationBinding(hot.getText()));
//        cold.textProperty().bind(Translator.getTranslator().getTranslationBinding(cold.getText()));
//        folded.textProperty().bind(Translator.getTranslator().getTranslationBinding(folded.getText()));
//
//        cancel.textProperty().bind(Translator.getTranslator().getTranslationBinding(cancel.getText()));
//        submitButton.textProperty().bind(Translator.getTranslator().getTranslationBinding(submitButton.getText()));
//        clearButton.textProperty().bind(Translator.getTranslator().getTranslationBinding(clearButton.getText()));
    }

    @FXML
    public void handleSubmit(ActionEvent e) throws IOException, SQLException {
        if(formFilled()) {
            // Loads form submitted window and passes in current stage to return to request home
            String uuid = UUID.randomUUID().toString();
            String type = "Laundry Request";
            String person = "";
            String completed = "false";
            DatabaseAPI.getDatabaseAPI().addServiceReq(uuid, type, person, completed, additionalInformation());

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

        setNormalStyle(employeeID, hot, cold, darks, lights, both);

        if(employeeID.getText().length() == 0){
            isFilled = false;
            setTextErrorStyle(employeeID);
        }
//        if(clientName.getText().length() == 0){
//            isFilled = false;
//            setTextErrorStyle(clientName);
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
        employeeID.setText("");
        //clientName.setText("");
        additionalInstructions.setText("");
        setNormalStyle(both, lights, darks, hot, cold, folded, employeeID, additionalInstructions);
    }

    public void handleHelp(ActionEvent e) throws IOException {
        SceneContext.getSceneContext().switchScene("/edu/wpi/cs3733/D21/teamF/fxml/ServiceRequests/LaundryRequestHelpView.fxml");
    }

    public void goBack(ActionEvent actionEvent)throws IOException {
        SceneContext.getSceneContext().switchScene("/edu/wpi/cs3733/D21/teamF/fxml/ServiceRequests/LaundryRequest.fxml");
    }
}
