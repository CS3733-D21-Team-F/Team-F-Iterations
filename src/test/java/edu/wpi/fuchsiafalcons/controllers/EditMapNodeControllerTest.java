package edu.wpi.fuchsiafalcons.controllers;

import edu.wpi.fuchsiafalcons.entities.NodeEntry;
import edu.wpi.fuchsiafalcons.pathfinding.Edge;
import edu.wpi.fuchsiafalcons.pathfinding.Vertex;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class EditMapNodeControllerTest extends ApplicationTest {


    private ObservableList<NodeEntry> nodeList;

    @Override
    public void start(Stage primaryStage) throws IOException, NoSuchFieldException, IllegalAccessException {

        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/fuchsiafalcons/fxml/EditMapNodeView.fxml"));
        Parent root = loader.load();

        final EditMapNodeController controller = loader.getController();

        //Uses JavaReflection to access classes so that we don't have to change their actual accessibility
        final Field nodeListField = controller.getClass().getDeclaredField("nodeList");

        //Set the fields to be accessible
        nodeListField.setAccessible(true);

        //Initialize our local lists
        this.nodeList = (ObservableList<NodeEntry>) nodeListField.get(controller);


        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Test
    public void testBackButton() {
        verifyThat("Save to File", Node::isVisible);
        clickOn("X");
        verifyThat("Welcome to the Navigation Page", Node::isVisible);
    }

    @Test
    public void testNewNodeButton() {
        verifyThat("New", Node::isVisible);
        clickOn("New");
       // verifyThat("Ok", Node::isVisible);
        //clickOn("Ok");
        //verifyThat("Ok", Node::isVisible);
    }

    @Test
    public void testNewNodeFeature() {

        //USed to get anything matching b/c we seem to have multiple now? //FIXME: IMPROVE
        List<NodeEntry> query = this.nodeList.stream().filter(node ->
                node.getNodeID().equals("TestNode") && node.getShortName().equals("Testing1")
        ).collect(Collectors.toList());

        final int pre = query.size();

        verifyThat("New", Node::isVisible);
        clickOn("New");
        verifyThat("OK", Node::isVisible);
        clickOn("#nodeIDField");
        write("TestNode");
        clickOn("#xCoordField");
        write("100");
        clickOn("#yCoordField");
        write("200");
        clickOn("#floorField");
        write("1");
        clickOn("#buildingField");
        write("TestBuilding");
        clickOn("#nodeTypeField");
        write("TestType");
        clickOn("#longNameField");
        write("Testing Node one");
        clickOn("#shortNameField");
        write("Testing1");
        clickOn("OK");

        query = this.nodeList.stream().filter(node ->
            node.getNodeID().equals("TestNode") && node.getShortName().equals("Testing1")
        ).collect(Collectors.toList());
        //verifyThat("TestNode", Node::isVisible);
        //verifyThat("Testing1", Node::isVisible);

        assertEquals(pre + 1, query.size());
        clickOn("Load From File/Reset Database");
    }

    @Test
    public void testEditNodeFeature() {
        verifyThat("Edit", Node::isVisible);
        clickOn("Load From File/Reset Database");
        clickOn("CCONF001L1");
        clickOn("Edit");
        verifyThat("OK", Node::isVisible);
        clickOn("#nodeIDField");
        press(KeyCode.CONTROL, KeyCode.A);
        release(KeyCode.CONTROL, KeyCode.A);
        type(KeyCode.BACK_SPACE);
        write("Test");
        clickOn("OK");
        verifyThat("Test", Node::isVisible);
        clickOn("Load From File/Reset Database");
    }

    @Test
    public void testNewNodeNotFilledOut() {
        verifyThat("New", Node::isVisible);
        clickOn("New");
        clickOn("#nodeIDField");
        write("Test");
        clickOn("OK");
        verifyThat("OK", Node::isVisible);
    }

    @Test
    public void testSaveToFileDisable() {
        verifyThat("Save to File", Node::isVisible);
        clickOn("#filenameField");
        verifyThat("Save to File", Node::isDisable);
        write("Test");
        verifyThat("Save to File", Node::isDisable);
    }

    @Test
    public void testDeleteNodeOnMap() {
        verifyThat("Load From File/Reset Database", Node::isVisible);
        clickOn("Load From File/Reset Database");
        verifyThat("CCONF001L1", Node::isVisible);
        clickOn("CCONF001L1");
        verifyThat("#CCONF001L1", Node::isVisible);
        clickOn("Delete");
        //verifyThat("#CCONF001L1", Node::isVisible);
    }
}

