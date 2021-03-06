package edu.wpi.cs3733.D21.teamF.controllers;

import edu.wpi.cs3733.D21.teamF.database.DatabaseAPI;
import edu.wpi.cs3733.D21.teamF.utils.CSVManager;
import edu.wpi.cs3733.D21.teamF.utils.SceneContext;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;

import static org.testfx.api.FxAssert.verifyThat;

public class AStarDemoControllerTest extends ApplicationTest {


    @Override
    public void start(Stage primaryStage) throws IOException {
        setUp();
        SceneContext.getSceneContext().setStage(primaryStage);
        SceneContext.getSceneContext().switchScene("/edu/wpi/cs3733/D21/teamF/fxml/AStarDemoView.fxml");
    }

    @BeforeEach
    public void setUp() {
        DatabaseAPI.getDatabaseAPI().dropNodesTable();
        DatabaseAPI.getDatabaseAPI().dropEdgesTable();


        //FIXME: DO BETTER!
        DatabaseAPI.getDatabaseAPI().createNodesTable();

        try {
            DatabaseAPI.getDatabaseAPI().populateNodes(CSVManager.load("MapfAllNodes.csv")); //NOTE: now can specify CSV arguments
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        DatabaseAPI.getDatabaseAPI().createEdgesTable();

        try {
            DatabaseAPI.getDatabaseAPI().populateEdges(CSVManager.load("MapfAllEdges.csv"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    @Test
    public void testBackButton() {
        verifyThat("#goBack", Node::isVisible);
        clickOn("#goBack");
        verifyThat("Navigation", Node::isVisible);
    }


    @Test
    public void testComboBox() {
        verifyThat("#startComboBox", Node::isVisible);
        clickOn("#startComboBox");
        sleep(100);
        verifyThat("ACONF00102", Node::isVisible);
        clickOn("ACONF00102");
        verifyThat("#ACONF00102", Node::isVisible);
        verifyThat("#endComboBox", Node::isVisible);
        clickOn("#endComboBox");
        sleep(100);
        verifyThat("ADEPT00102", Node::isVisible);
        clickOn("ADEPT00102");
        verifyThat("#ADEPT00102", Node::isVisible);
    }

    @Test
    public void testNavigation() {
        verifyThat("#Go", Node::isDisable);
        verifyThat("#End", Node::isDisable);
        verifyThat("#Prev", Node::isDisable);
        verifyThat("#Next", Node::isDisable);
        testComboBox();
        clickOn("#Go");
        verifyThat("#Go", Node::isDisable);
        verifyThat("#Prev", Node::isDisable);
        verifyThat("2", Node::isVisible);
        clickOn("#Next");
        clickOn("#Next");
        clickOn("#Next");
        verifyThat("#Next", Node::isDisable);
        verifyThat("Arrived at Destination!", Node::isVisible);
        clickOn("#End");
        verifyThat("2", Node::isVisible);
    }
}

