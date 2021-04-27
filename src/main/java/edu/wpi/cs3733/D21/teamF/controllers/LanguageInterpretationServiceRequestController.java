package edu.wpi.cs3733.D21.teamF.controllers;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.D21.teamF.database.DatabaseAPI;
import edu.wpi.cs3733.D21.teamF.entities.ServiceEntry;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.UUID;


public class LanguageInterpretationServiceRequestController implements Initializable {
    @FXML private JFXButton close;
    @FXML private JFXTextField name;
    @FXML private JFXDatePicker date;
    @FXML private JFXTimePicker time;
    @FXML private JFXComboBox<String> appointment;
    @FXML private JFXTextField language;
    @FXML private JFXButton help;
    @FXML private JFXButton translate;
    @FXML private JFXButton submit;

    /**
     * closes the Language Interpretation Request form and returns to home
     * @param actionEvent
     * @throws IOException
     * @author Jay
     */
    public void handleClose(ActionEvent actionEvent) throws IOException {
        Stage currentStage = (Stage)close.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamF/fxml/ServiceRequestHomeView.fxml"));
        Scene homeScene = new Scene(root);
        currentStage.setScene(homeScene);
        currentStage.show();
    }

    /**
     * Opens the help window
     * @param actionEvent
     * @throws IOException
     * @author Jay Yen
     */
    public void handleHelp(ActionEvent actionEvent) throws IOException {

        Stage submittedStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamF/fxml/LanguageInterpretationHelpView.fxml"));
        Scene helpPopUp = new Scene(root);
        submittedStage.setScene(helpPopUp);
        submittedStage.setTitle("Language Interpretation Help");
        submittedStage.initModality(Modality.APPLICATION_MODAL);
        submittedStage.initOwner(((Button) actionEvent.getSource()).getScene().getWindow());
        submittedStage.showAndWait();
    }

    @FXML
    public void handleHoverOn(MouseEvent mouseEvent) {
        JFXButton btn = (JFXButton) mouseEvent.getSource();
        btn.setStyle("-fx-background-color: #F0C808; -fx-text-fill: #000000;");
    }

    @FXML
    public void handleHoverOff(MouseEvent mouseEvent) {
        JFXButton btn = (JFXButton) mouseEvent.getSource();
        btn.setStyle("-fx-background-color: #03256C; -fx-text-fill: #FFFFFF;");
    }

    public void handleTranslate(ActionEvent actionEvent) throws IOException{
    }

    /**
     * Opens a window that shows a request received message, and adds a service request to the database
     * @param actionEvent
     * @throws IOException
     * @author Jay Yen
     */
    public void handleSubmit(ActionEvent actionEvent) throws IOException, SQLException {

        if(formFilledOut() != true) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner((Stage) ((Button) actionEvent.getSource()).getScene().getWindow());
            alert.setTitle("Form not filled");
            alert.setHeaderText("Form incomplete");
            alert.setContentText("Please fill out the fields marked in red.");
            alert.showAndWait();
        }else{
            String uuid = UUID.randomUUID().toString();
            String additionalInstr = "Date: " + date.getValue().toString() + " Time: " + time.getValue() +
                    " Name: " + name.getText() + " Appointment: " + (String) appointment.getValue() + " Language: " + language.getText();
            ServiceEntry newServiceRequest = new ServiceEntry(uuid,"Language Interpretation Request", " ", "false", additionalInstr);
            DatabaseAPI.getDatabaseAPI().addServiceReq(newServiceRequest.getUuid(), newServiceRequest.getRequestType(),
                    newServiceRequest.getAssignedTo(), newServiceRequest.getCompleteStatus(), newServiceRequest.getAdditionalInstructions());

            Stage submittedStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamF/fxml/FormSubmittedView.fxml"));
            Scene submitScene = new Scene(root);
            submittedStage.setScene(submitScene);
            submittedStage.setTitle("Submission Complete");
            submittedStage.initModality(Modality.APPLICATION_MODAL);
            submittedStage.initOwner(((Button) actionEvent.getSource()).getScene().getWindow());
            submittedStage.showAndWait();
        }

    }

    /**
     * Sets the drop down options for appointment type and languages
     * @param location
     * @param resources
     * @author Jay Yen
     */
    public void initialize(URL location, ResourceBundle resources){
        appointment.getItems().add("Non-Specific");
        appointment.getItems().add("Multiple Departments");
        appointment.getItems().add("Allergy and Clinical Immunology");
        appointment.getItems().add("Alzheimer's Center");
        appointment.getItems().add("Anesthesiology");
        appointment.getItems().add("Arrhythmia Services");
        appointment.getItems().add("Arthritis and Joint Diseases Center");
        appointment.getItems().add("Asthma Center");
        appointment.getItems().add("Bone Marrow Transplant Program");
        appointment.getItems().add("Brain Tumor Program");
        appointment.getItems().add("Breast Center");
        appointment.getItems().add("Cardiac Surgery");
        appointment.getItems().add("Cardiology");
        appointment.getItems().add("Cardiomyopathy and Cardiac Transplantation");
        appointment.getItems().add("CAT Scan (CT Imaging");
        appointment.getItems().add("DF/BW Cancer Center");
        appointment.getItems().add("Dental");
        appointment.getItems().add("Dermatology");
        appointment.getItems().add("Diabetes");
        appointment.getItems().add("Ear, Nose and Throat");
        appointment.getItems().add("Emergency Medicine");
        appointment.getItems().add("Endocrinology, Diabetes and Hypertension");
        appointment.getItems().add("Epilepsy");
        appointment.getItems().add("Foot and Ankle Center/Faulkner");
        appointment.getItems().add("Gastroenterology");
        appointment.getItems().add("General and Gastrointestinal Surgery");
        appointment.getItems().add("Genetics");
        appointment.getItems().add("Gynecologic Oncology");
        appointment.getItems().add("Gynecology (General)");
        appointment.getItems().add("Hematology");
        appointment.getItems().add("Infectious Disease");
        appointment.getItems().add("Interventional Cardiology");
        appointment.getItems().add("Interventional Radiology");
        appointment.getItems().add("Lung Dancer Sceening (Low Dose CT)");
        appointment.getItems().add("Lung Transplantation Program");
        appointment.getItems().add("Lupus Center");
        appointment.getItems().add("Magnetic Resonance Imaging (MRI)");
        appointment.getItems().add("Mammography");
        appointment.getItems().add("Maternal-Fetal Medicine");
        appointment.getItems().add("Medicine");
        appointment.getItems().add("Metabolic and Nutrition Support Service");
        appointment.getItems().add("Multiple Sclerosis");
        appointment.getItems().add("Neurology");
        appointment.getItems().add("Neuroradiology");
        appointment.getItems().add("Newborn Medicine");
        appointment.getItems().add("Nuclear Medicine");
        appointment.getItems().add("Nutrition Consultation");
        appointment.getItems().add("Obstetric Anesthesia Service");
        appointment.getItems().add("Obstetrics");
        appointment.getItems().add("Ophthalmology");
        appointment.getItems().add("Oral Medicine, Oral and Maxillofacial Surgery and Dentistry");
        appointment.getItems().add("Orthopaedics");
        appointment.getItems().add("Osteoporosis Center");
        appointment.getItems().add("Otolaryngology");
        appointment.getItems().add("Pain Management Center");
        appointment.getItems().add("Pathology Department");
        appointment.getItems().add("Pediatric and Adolescent Gynecology");
        appointment.getItems().add("Pituitary Program");
        appointment.getItems().add("Plastic Surgery");
        appointment.getItems().add("Podiatry");
        appointment.getItems().add("Primary Care");
        appointment.getItems().add("Prostate Center");
        appointment.getItems().add("Psychiatry");
        appointment.getItems().add("Pulmonary and Critical Care Medicine");
        appointment.getItems().add("Radiation Oncology");
        appointment.getItems().add("Radiology");
        appointment.getItems().add("Renal (Kidney)");
        appointment.getItems().add("Renal (Kidney) Transplantation");
        appointment.getItems().add("Reproductive Medicine");
        appointment.getItems().add("Rheumatology, Inflammation and Immunity");
        appointment.getItems().add("Sleep Medicine");
        appointment.getItems().add("Spine Center");
        appointment.getItems().add("Sports Medicine and Rehabilitation Center");
        appointment.getItems().add("Surgery");
        appointment.getItems().add("Surgery Oncology");
        appointment.getItems().add("Thoracic Surgery");
        appointment.getItems().add("Thyroid");
        appointment.getItems().add("Trauma and Burn Center");
        appointment.getItems().add("Urogynecology");
        appointment.getItems().add("Urology");
        appointment.getItems().add("Vascular Medicine Services");
        appointment.getItems().add("Vascular Surgery");
        appointment.getItems().add("Weight Management");
        appointment.getItems().add("Women's Health");
        appointment.getItems().add("Other");
    }

    private boolean formFilledOut(){

        if(name.getText().length() <= 0){
            name.setStyle("-fx-border-color: red");
        }
        if(date.getValue() == null){
            date.setStyle("-fx-border-color: red");
        }
        if(time.getValue() == null){
            time.setStyle("-fx-border-color: red");
        }
        if(language.getText().length() <= 0){
            language.setStyle("-fx-border-color: red");
        }
        if(name.getText().length() > 0 && language.getText().length() > 0){
            return true;
        }
        return false;
    }
}