package edu.wpi.cs3733.D21.teamF.controllers.ServiceRequestsControllers;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.D21.teamF.controllers.ServiceRequests;
import edu.wpi.cs3733.D21.teamF.database.DatabaseAPI;
import edu.wpi.cs3733.D21.teamF.entities.NodeEntry;
import edu.wpi.cs3733.D21.teamF.utils.EmailHandler;
import edu.wpi.cs3733.D21.teamF.utils.SceneContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Controller for Food Delivery Service View
 * @author karenhou
 */
public class FoodDeliveryServiceRequestController extends ServiceRequests {

    @FXML private JFXComboBox<String> deliveryLocationField;
    @FXML private JFXTimePicker deliveryTimeField;
    @FXML private JFXTextField allergyField;
    @FXML private JFXTextField emailField;
    @FXML private JFXRadioButton rButtonFood1;
    @FXML private JFXRadioButton rButtonFood2;
    @FXML private JFXRadioButton rButtonFood3;
    @FXML private JFXRadioButton rButtonFood4;
    @FXML private JFXRadioButton rButtonDrink1;
    @FXML private JFXRadioButton rButtonDrink2;
    @FXML private JFXRadioButton rButtonDrink3;
    @FXML private JFXRadioButton rButtonDrink4;
    @FXML private JFXCheckBox cbSide1;
    @FXML private JFXCheckBox cbSide2;
    @FXML private JFXCheckBox cbSide3;
    @FXML private JFXCheckBox cbSide4;

    public FoodDeliveryServiceRequestController() {
    }


    @FXML
    private void initialize(){
        try{
            List<NodeEntry> nodeEntries = DatabaseAPI.getDatabaseAPI().genNodeEntries();

            final ObservableList<String> nodeList = FXCollections.observableArrayList();
            for(NodeEntry n: nodeEntries){
                nodeList.add(n.getShortName());
            }
            //nodeList.addAll(nodeEntries.stream().map(NodeEntry::getShortName).sorted().collect(Collectors.toList()));
            this.deliveryLocationField.setItems(nodeList);

        } catch(Exception e){

        }
    }


    /**
     * handles submit being pressed
     * @param e is the button being pushed
     * @throws IOException
     * @author KH
     */
    @FXML
    public void handleSubmit(ActionEvent e) throws IOException, SQLException, MessagingException {
        if (formFilled()) {
            String uuid = UUID.randomUUID().toString();
            String type = "Food Delivery";
            String person = "";
            String completed = "false";
            String additionalInfo = "Delivery Location: " + deliveryLocationField.getValue() + "Delivery time: " + deliveryTimeField.getValue()
                    + " Email;" + emailField.getText();
            DatabaseAPI.getDatabaseAPI().addServiceReq(uuid, type, person, completed, additionalInfo);
            EmailHandler.getEmailHandler().sendEmail(additionalInfo.split(";")[1], "Service Request Confirmation",
                    "Hello,\nThis is a confirmation email for your service request " + type + " it will be completed as soon as possible");

            // Loads form submitted window and passes in current stage to return to request home
            openSuccessWindow();
        }
    }


    /**
     * Helper that checks if form is acceptably filled out
     * @return true if form is filled out
     * @author KH
     */
    public boolean formFilled() {
        boolean isFilled = true;

        setNormalStyle(deliveryLocationField, deliveryTimeField, allergyField, emailField,
                rButtonFood1, rButtonFood2, rButtonFood3, rButtonFood4, rButtonDrink1, rButtonDrink2,
                rButtonDrink3, rButtonDrink4, cbSide1, cbSide2, cbSide3, cbSide4);

        if(! (rButtonFood1.isSelected() || rButtonFood2.isSelected() || rButtonFood3.isSelected() || rButtonFood4.isSelected())) {
            isFilled = false;
            setButtonErrorStyle(rButtonFood1, rButtonFood2, rButtonFood3, rButtonFood4);
        }
        if(! (rButtonDrink1.isSelected() || rButtonDrink2.isSelected() || rButtonDrink3.isSelected() || rButtonDrink4.isSelected())){
            isFilled = false;
            setButtonErrorStyle(rButtonDrink1, rButtonDrink2, rButtonDrink3, rButtonDrink4);
        }
        if(! (cbSide1.isSelected() || cbSide2.isSelected() || cbSide3.isSelected() || cbSide4.isSelected())){
            isFilled = false;
            setButtonErrorStyle(cbSide1, cbSide2, cbSide3, cbSide4);
        }
        if(deliveryLocationField.getValue() == null){
            isFilled = false;
            setTextErrorStyle(deliveryLocationField);
        }
        if(deliveryTimeField.getValue() == null){
            isFilled = false;
            setTextErrorStyle(deliveryTimeField);
        }

        return isFilled;
    }

    /**
     * Handles radial button groups
     * @author KH
     */
    @FXML
    private void handleRadialButtonPushed(){
        ToggleGroup foodGroup = new ToggleGroup(); //group for foods
        rButtonFood1.setToggleGroup(foodGroup);
        rButtonFood2.setToggleGroup(foodGroup);
        rButtonFood3.setToggleGroup(foodGroup);
        rButtonFood4.setToggleGroup(foodGroup);

        ToggleGroup drinkGroup = new ToggleGroup(); //group for drinks
        rButtonDrink1.setToggleGroup(drinkGroup);
        rButtonDrink2.setToggleGroup(drinkGroup);
        rButtonDrink3.setToggleGroup(drinkGroup);
        rButtonDrink4.setToggleGroup(drinkGroup);

    }

    @Override
    public void handleClear() {
        deliveryLocationField.setValue(null);
        deliveryTimeField.setValue(null);
        allergyField.setText("");
        emailField.setText("");
        rButtonDrink1.setSelected(false);
        rButtonDrink2.setSelected(false);
        rButtonDrink3.setSelected(false);
        rButtonDrink4.setSelected(false);
        rButtonFood1.setSelected(false);
        rButtonFood2.setSelected(false);
        rButtonFood3.setSelected(false);
        rButtonFood4.setSelected(false);
        cbSide1.setSelected(false);
        cbSide2.setSelected(false);
        cbSide3.setSelected(false);
        cbSide4.setSelected(false);
        setNormalStyle(deliveryLocationField, deliveryTimeField, allergyField, emailField,
                rButtonFood1, rButtonFood2, rButtonFood3, rButtonFood4, rButtonDrink1, rButtonDrink2,
                rButtonDrink3, rButtonDrink4, cbSide1, cbSide2, cbSide3, cbSide4);

    }

    public void handleHelp(ActionEvent e) throws IOException {
        SceneContext.getSceneContext().switchScene("/edu/wpi/cs3733/D21/teamF/fxml/ServiceRequests/FoodDeliveryHelpView.fxml");
    }

    public void goBack(ActionEvent actionEvent)throws IOException {
        SceneContext.getSceneContext().switchScene("/edu/wpi/cs3733/D21/teamF/fxml/ServiceRequests/FoodDeliveryServiceRequestView.fxml");
    }
}
