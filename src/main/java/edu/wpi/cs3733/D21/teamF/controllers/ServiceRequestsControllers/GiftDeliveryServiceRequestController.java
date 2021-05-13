package edu.wpi.cs3733.D21.teamF.controllers.ServiceRequestsControllers;

import com.jfoenix.controls.*;
import com.sun.xml.internal.fastinfoset.util.StringArray;
import edu.wpi.cs3733.D21.teamF.controllers.ServiceRequests;
import edu.wpi.cs3733.D21.teamF.database.DatabaseAPI;
import edu.wpi.cs3733.D21.teamF.utils.CSVManager;
import edu.wpi.cs3733.D21.teamF.utils.EmailHandler;
import edu.wpi.cs3733.D21.teamF.utils.SceneContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;

import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class GiftDeliveryServiceRequestController extends ServiceRequests {
    @FXML private JFXTextField email;
    @FXML private JFXTextField ccNumber;
    @FXML private JFXTextField expirationDate;
    @FXML private JFXTextField csv;
    @FXML private JFXTextArea specialInstructions;
    @FXML private JFXCheckBox tbCheckBox;
    @FXML private JFXCheckBox balloonsCheckBox;
    @FXML private JFXCheckBox cardsCheckBox;
    @FXML private JFXCheckBox puzzlesCheckBox;
    @FXML private JFXCheckBox blanketCheckBox;
    @FXML private JFXCheckBox magazinesCheckBox;
    @FXML private JFXDatePicker dateField;
    @FXML private JFXTimePicker timeField;
    @FXML private JFXComboBox<String> destination;
    @FXML private JFXButton runRobot;
    @FXML private JFXTextField robotIP;



    public void initialize() throws Exception {
     List<String[]> nodeData = CSVManager.load("FullerMapNodes.csv");

        HashMap<String, Byte> lookup = new HashMap();

        lookup.put("lecture hall", Byte.parseByte("1"));
        lookup.put("B19", Byte.parseByte("2"));
        lookup.put("B20 and B34", Byte.parseByte("3"));
        lookup.put("B21", Byte.parseByte("4"));
        lookup.put("B33", Byte.parseByte("5"));
        lookup.put("B22 and B29", Byte.parseByte("6"));
        lookup.put("B23", Byte.parseByte("7"));
        lookup.put("B24", Byte.parseByte("8"));
        lookup.put("B26", Byte.parseByte("9"));
        lookup.put("B30", Byte.parseByte("10"));
        lookup.put("B27 and B28", Byte.parseByte("11"));

        destination.getItems().addAll(lookup.keySet());
        runRobot.setOnAction(event -> {
            if(destination.getValue() != null){
                final HttpPost postRequest = new HttpPost("http://" + robotIP.getText() + "/");
                final ByteArrayEntity byteArrayEntity = new ByteArrayEntity(new byte[]{lookup.get(destination.getValue())}, ContentType.DEFAULT_TEXT);
                postRequest.setEntity(byteArrayEntity);

                final HttpClient client = HttpClients.createDefault();
                try {
                    final HttpResponse response = client.execute(postRequest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public String setSpecialInstructions(){
        ArrayList<JFXCheckBox> checkBoxes = new ArrayList<>();
        checkBoxes.add(tbCheckBox);
        checkBoxes.add(balloonsCheckBox);
        checkBoxes.add(cardsCheckBox);
        checkBoxes.add(puzzlesCheckBox);
        checkBoxes.add(blanketCheckBox);
        checkBoxes.add(magazinesCheckBox);
        StringBuilder special = new StringBuilder(specialInstructions.getText());

        for(JFXCheckBox aCheckBox : checkBoxes){
            if(aCheckBox.isSelected()){
                special.append(", ").append(aCheckBox.getText());
            }
        }
        return special.toString();
    }

    public void handleSubmit(ActionEvent actionEvent) throws IOException, SQLException, MessagingException {
        if(formFilled()) {
            String uuid = UUID.randomUUID().toString();
            String type = "Gift Delivery";
            String additionalInformation = "CC Number: " + ccNumber.getText() +
                    "Expiration: " + expirationDate.getText() + "CSV: " + csv.getText() + "Special instructions: " + setSpecialInstructions() +
                    "Schedule Delivery: " + dateField.getValue() + timeField.getValue() + " Email;" + email.getText();
            DatabaseAPI.getDatabaseAPI().addServiceReq(uuid, type, "", "false", additionalInformation);
            EmailHandler.getEmailHandler().sendEmail(additionalInformation.split(";")[1], "Service Request Confirmation",
                    "Hello,\nThis is a confirmation email for your service request " + type + " it will be completed as soon as possible");
            // Loads form submitted window and passes in current stage to return to request home
            openSuccessWindow();
        }
    }

    public void handleClear(ActionEvent actionEvent) throws IOException {
        SceneContext.getSceneContext().switchScene("/edu/wpi/cs3733/D21/teamF/fxml/ServiceRequests/GiftDeliveryServiceRequest.fxml");

    }

    public boolean formFilled() {

        boolean isFilled = true;
        setNormalStyle(email, ccNumber, csv, expirationDate);

        if(email.getText().length() == 0){
            isFilled = false;
            setTextErrorStyle(email);
        }
        if(ccNumber.getText().length() == 0){
            isFilled = false;
            setTextErrorStyle(ccNumber);
        }
        if(csv.getText().length() == 0){
            isFilled = false;
            setTextErrorStyle(csv);
        }
        if(expirationDate.getText().length() == 0){
            isFilled = false;
            setTextErrorStyle(expirationDate);
        }
        return isFilled;
    }

    @FXML
    public void handleClear() {
        email.setText("");
        ccNumber.setText("");
        expirationDate.setText("");
        csv.setText("");
        specialInstructions.setText("");

    }

    public void handleHelp(ActionEvent actionEvent) throws IOException {
        SceneContext.getSceneContext().switchScene("/edu/wpi/cs3733/D21/teamF/fxml/ServiceRequests/GiftDeliveryHelpView.fxml");
    }

    public void goBack(ActionEvent actionEvent) throws IOException {
        SceneContext.getSceneContext().switchScene("/edu/wpi/cs3733/D21/teamF/fxml/ServiceRequests/GiftDeliveryServiceRequest.fxml");
    }
}

