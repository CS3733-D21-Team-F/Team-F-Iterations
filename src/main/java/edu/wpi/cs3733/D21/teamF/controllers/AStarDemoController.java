package edu.wpi.cs3733.D21.teamF.controllers;

import edu.wpi.cs3733.D21.teamF.database.DatabaseAPI;
import edu.wpi.cs3733.D21.teamF.entities.EdgeEntry;
import edu.wpi.cs3733.D21.teamF.entities.NodeEntry;
import edu.wpi.cs3733.D21.teamF.pathfinding.*;
import edu.wpi.cs3733.D21.teamF.pathfinding.Graph;
import edu.wpi.cs3733.D21.teamF.pathfinding.GraphLoader;
import edu.wpi.cs3733.D21.teamF.pathfinding.Path;
import edu.wpi.cs3733.D21.teamF.pathfinding.Vertex;
import edu.wpi.cs3733.D21.teamF.utils.UIConstants;
import edu.wpi.cs3733.uicomponents.MapPanel;
import edu.wpi.cs3733.uicomponents.entities.DrawableEdge;
import edu.wpi.cs3733.uicomponents.entities.DrawableNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AStarDemoController implements Initializable {

    @FXML
    private Button goBack;

    @FXML
    private ComboBox<String> startComboBox;

    @FXML
    private ComboBox<String> endComboBox;

    //FIXME: DO BETTER
    private Graph graph;

    @FXML
    private MapPanel mapPanel;

    @FXML
    private Button Go;

    @FXML
    private Button End;

    @FXML
    private Button Prev;

    @FXML
    private Button Next;

    @FXML
    private Label Instruction;

    @FXML
    private Label ETA;

    private DoublyLinkedHashSet<Vertex> recentlyUsed, favorites;
    private final int MAX_RECENTLY_USED = 5;


    /**
     * These are done for displaying the start & end nodes. This should be done better (eventually)
     *
     * @author Alex Friedman (ahf)
     */
    private DrawableNode startNodeDisplay;
    private DrawableNode endNodeDisplay;
    private DrawableNode userNodeDisplay;

    // Global variables for the stepper
    private List<Vertex> pathVertex;

    List<NodeEntry> allNodeEntries = new ArrayList<>();
    List<EdgeEntry> allEdgeEntries = new ArrayList<>();

    boolean pathFinding;
    List<Integer> stops;
    List<String> instructions;
    List<String> eta;
    int curStep;
    String curFloor;

    DrawableNode direction;
    private static final double PIXEL_TO_METER_RATIO = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //ahf - yes this should be done better. At some point.

        allNodeEntries = new ArrayList<>();
        try {
            allNodeEntries = DatabaseAPI.getDatabaseAPI().genNodeEntries();
            List<EdgeEntry> allEdgeEntries = DatabaseAPI.getDatabaseAPI().genEdgeEntries();

            final List<NodeEntry> nodeEntries = allNodeEntries.stream().collect(Collectors.toList());

            final List<EdgeEntry> edgeEntries = allEdgeEntries.stream().filter(node -> hasNode(nodeEntries, node.getStartNode())
                    && hasNode(nodeEntries, node.getEndNode())).collect(Collectors.toList());
            this.graph = GraphLoader.load(nodeEntries, edgeEntries);
        } catch (Exception e) {
            this.graph = new Graph();
            e.printStackTrace();
            //return;
        }

        final ObservableList<String> nodeList = FXCollections.observableArrayList();
        nodeList.addAll(this.graph.getVertices().stream().map(Vertex::getID)
                .sorted().collect(Collectors.toList()));

        startComboBox.setItems(nodeList);
        endComboBox.setItems(nodeList);

        pathFinding = false;

        final ContextMenu contextMenu = new ContextMenu();

        //FIXME: CHANGE TEXT TO BE MORE ACCESSABLE
        final MenuItem startPathfind = new MenuItem("Path from Here");
        final MenuItem endPathfind = new MenuItem("Path end Here");

        contextMenu.getItems().addAll(startPathfind, endPathfind);


        List<NodeEntry> finalAllNodeEntries = allNodeEntries;

        mapPanel.getMap().setOnContextMenuRequested(event -> {
            if(pathFinding){
                return;
            }
            contextMenu.show(mapPanel.getMap(), event.getScreenX(), event.getScreenY());

            final double zoomLevel = mapPanel.getZoomLevel().getValue();

            startPathfind.setOnAction((ActionEvent e) -> {
                startComboBox.setValue(getClosest(finalAllNodeEntries, event.getX() * zoomLevel, event.getY() * zoomLevel).getNodeID());
            });

            endPathfind.setOnAction(e -> {
                endComboBox.setValue(getClosest(finalAllNodeEntries, event.getX() * zoomLevel, event.getY() * zoomLevel).getNodeID());
            });
        });

        Go.setDisable(true);
        End.setDisable(true);
        Prev.setDisable(true);
        Next.setDisable(true);
        pathVertex = null;
        Instruction.setVisible(false);

        direction = null;

        loadRecentlyUsedVertices();
        loadFavorites();
    }
    private void loadFavorites() {
        this.favorites = new DoublyLinkedHashSet<>();
        //TODO: load recentlyUsed
    }
    /**
     * Given a list of NodeEntries, returns the one closest to the current location
     *
     * @param entries The list of NodeEntries
     * @param x the x coordinate of the mouse
     * @param y the y cordinate
     * @return the closest nodeentry
     * @author Alex Friedman (ahf)
     */
    private final NodeEntry getClosest(List<NodeEntry> entries, double x, double y)
    {
        double minDist2 = Integer.MAX_VALUE;
        NodeEntry closest = null;

        for(NodeEntry nodeEntry : entries)
        {
            if(!nodeEntry.getFloor().equals(mapPanel.getFloor().getValue()))
                continue;

            final double currDist2 = Math.pow(x - Integer.parseInt(nodeEntry.getXcoord()), 2) + Math.pow(y - Integer.parseInt(nodeEntry.getYcoord()), 2);

            if(currDist2 < minDist2)
            {
                minDist2 = currDist2;
                closest = nodeEntry;
            }
        }
        return closest;
    }

    /**
     * Search a list of node and see if exist node with given ID
     * @param nodeEntries the list to be searched
     * @param nodeID the ID to be used in search
     * @return true if node exist in list, false otherwise
     * @author ZheCheng Song
     */
    private boolean hasNode(List<NodeEntry> nodeEntries, String nodeID){
        for(NodeEntry n : nodeEntries){
            if (n.getNodeID().equals(nodeID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handles the pushing of a button on the screen
     *
     * @param actionEvent the button's push
     * @throws IOException in case of scene switch, if the next fxml scene file cannot be found
     * @author ZheCheng Song
     */
    @FXML
    private void handleButtonPushed(ActionEvent actionEvent) throws IOException {

        Button buttonPushed = (Button) actionEvent.getSource();  //Getting current stage
        Stage stage;
        Parent root;

        if (buttonPushed == goBack) {
            stage = (Stage) buttonPushed.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamF/fxml/DefaultPageView.fxml"));
            stage.getScene().setRoot(root);
            stage.setTitle("Default Page");
            stage.show();
        }
    }

    /**
     *
     * @author Alex Friedman (ahf)
     */
    @FXML
    public void handleStartBoxAction() throws SQLException {
        checkInput();
        if(this.startNodeDisplay != null)
            mapPanel.unDraw(this.startNodeDisplay.getId());
        drawStartNode(startComboBox.getValue());
        mapPanel.switchMap(findNodeEntry(startNodeDisplay.getId()).getFloor());
        mapPanel.centerNode(startNodeDisplay);
        loadRecentlyUsedVertices();
    }

    /**
     * Helper function used to draw the startNode with given ID, snatched from handleStartBoxAction()
     * @param nodeID the ID of the Node
     * @author Alex Friedman (ahf) / ZheCheng Song
     */
    private void drawStartNode(String nodeID) throws SQLException{
        final NodeEntry startNode = DatabaseAPI.getDatabaseAPI().getNode(nodeID);
        if(startNode != null)
        {
            final DrawableNode drawableNode = startNode.getDrawable();
            drawableNode.setFill(UIConstants.NODE_COLOR);
            drawableNode.setRadius(10);

            Tooltip tt = new Tooltip();
            tt.setText("ID: " + startNode.getNodeID()  + "\nShort name: " + startNode.getShortName() +
                    "\nFloor: " + startNode.getFloor() + "\nX: " + startNode.getXcoord() + " Y: " + startNode.getYcoord());
            tt.setStyle("-fx-font: normal bold 15 Langdon; "
                    + "-fx-base: #AE3522; "
                    + "-fx-text-fill: orange;");
            Tooltip.install(drawableNode, tt);

            mapPanel.draw(drawableNode);
            this.startNodeDisplay = drawableNode;
        }else {
            System.out.println("Can't find node!");
        }
    }

    /**
     * Loads recently used vertices from the database into the controller
     * @author Tony Vuolo (bdane)
     */
    private void loadRecentlyUsedVertices() {
        this.recentlyUsed = new DoublyLinkedHashSet<>();
        //TODO: load recently used from database
    }

    /**
     *
     * @author Alex Friedman (ahf)
     */
    @FXML
    public void handleEndBoxAction() throws SQLException {
        checkInput();
        if(this.endNodeDisplay != null)
            mapPanel.unDraw(this.endNodeDisplay.getId());
        drawEndNode(endComboBox.getValue());
        mapPanel.switchMap(findNodeEntry(endNodeDisplay.getId()).getFloor());
        mapPanel.centerNode(endNodeDisplay);
        loadRecentlyUsedVertices();
    }

    /**
     * Helper function used to draw the endNode with given ID, snatched from handleEndBoxAction()
     * @param nodeID the ID of the Node
     * @author Alex Friedman (ahf) / ZheCheng Song
     */
    private void drawEndNode(String nodeID) throws SQLException{
        final NodeEntry endNode = DatabaseAPI.getDatabaseAPI().getNode(nodeID);
        if(endNode != null)
        {
            final DrawableNode drawableNode = endNode.getDrawable();
            drawableNode.setFill(Color.GREEN);
            drawableNode.setRadius(10);

            Tooltip tt = new Tooltip();
            tt.setText("ID: " + endNode.getNodeID()  + "\nShort name: " + endNode.getShortName() +
                    "\nFloor: " + endNode.getFloor() + "\nX: " + endNode.getXcoord() + " Y: " + endNode.getYcoord());
            tt.setStyle("-fx-font: normal bold 15 Langdon; "
                    + "-fx-base: #AE3522; "
                    + "-fx-text-fill: orange;");
            Tooltip.install(drawableNode, tt);

            mapPanel.draw(drawableNode);

            this.endNodeDisplay = drawableNode;
        }
    }

    /**
     * Helper function used to draw the userNode with given ID
     * @param nodeID the ID of the Node
     * @author Alex Friedman (ahf) / ZheCheng Song
     */
    private void drawUserNode(String nodeID) throws SQLException{
        final NodeEntry userNode = DatabaseAPI.getDatabaseAPI().getNode(nodeID);
        if(userNode != null)
        {
            final DrawableNode drawableNode = userNode.getDrawable();
            drawableNode.setFill(Color.PURPLE);
            drawableNode.setRadius(10);

            Tooltip tt = new Tooltip();
            tt.setText("User");
            tt.setStyle("-fx-font: normal bold 15 Langdon; "
                    + "-fx-base: #AE3522; "
                    + "-fx-text-fill: orange;");
            Tooltip.install(drawableNode, tt);

            mapPanel.draw(drawableNode);

            this.userNodeDisplay = drawableNode;
        }
    }

    /**
     * This is used to clear the pathfinding drawn path.
     *
     * @author Alex Friedman (ahf)
     */
    private void clearPath()
    {
        mapPanel.clearMap();
    }

    /**
     * This is used to re-render the A* path
     *
     * @author Alex Friedman (ahf)
     */
    private boolean updatePath()
    {

        if(this.startNodeDisplay != null)
            mapPanel.draw(this.startNodeDisplay);
        if(this.endNodeDisplay != null)
            mapPanel.draw(this.endNodeDisplay);

        final String currentFloor = mapPanel.getFloor().getValue();

        final Color LINE_STROKE_TRANSPARENT = new Color(UIConstants.LINE_COLOR.getRed(), UIConstants.LINE_COLOR.getGreen(), UIConstants.LINE_COLOR.getBlue(), 0.4);

        final Vertex startVertex = this.graph.getVertex(startComboBox.getValue());
        final Vertex endVertex = this.graph.getVertex(endComboBox.getValue());

        updateRecentlyUsed(endVertex);

        if(startVertex != null && endVertex != null && !startVertex.equals(endVertex))
        {
            final Path path = this.graph.getPath(startVertex, endVertex);
            pathVertex = null;
            if(path != null)
            {
                pathVertex = path.asList();
                drawPathFromIndex(0);
                return true;
            }
        }
        else
        {
            //FIXME: INFORM USER OF ERROR
        }

        return false; //We had an error
    }

    /**
     * Helper function to draw the path starting from given index, input 0 as index to draw the whole path
     * snatched from updatePath()
     * @param index starting index
     * @author Alex Friedman (ahf) / ZheCheng Song
     */
    private void drawPathFromIndex(int index){
        final String currentFloor = mapPanel.getFloor().getValue();

        final Color LINE_STROKE_TRANSPARENT = new Color(UIConstants.LINE_COLOR.getRed(), UIConstants.LINE_COLOR.getGreen(), UIConstants.LINE_COLOR.getBlue(), 0.4);


        for (int i = index; i < pathVertex.size() - 1; i++)
        {
            final Vertex start = pathVertex.get(i);
            final Vertex end = pathVertex.get(i + 1);

            //int startX, int startY, int endX, int endY, String ID, String startFloor, String endFloor
            //FIXME: DO BETTER ID WHEN WE HAVE MULTIPLE PATH DIRECTIONS!!!
            final DrawableEdge edge = new DrawableEdge((int)start.getX(), (int)start.getY(), (int)end.getX(), (int)end.getY(), start.getID() + "_" + end.getID(), start.getFloor(), end.getFloor());
            // final Line line = new Line(start.getX()/zoomLevel, start.getY()/zoomLevel, end.getX()/zoomLevel, end.getY()/zoomLevel);
            edge.setStrokeWidth(UIConstants.LINE_STROKE_WIDTH);

            final LinearGradient lineGradient = new LinearGradient(edge.getStartX(), edge.getStartY(), edge.getEndX(), edge.getEndY(), false, CycleMethod.NO_CYCLE,
                    new Stop(0, (start.getFloor().equals(currentFloor) ? Color.ORANGE : LINE_STROKE_TRANSPARENT)),
                    new Stop(1, (end.getFloor().equals(currentFloor) ? Color.ORANGE : LINE_STROKE_TRANSPARENT)));

            edge.setStroke(lineGradient);

            mapPanel.draw(edge);
        }
    }

    /**
     * Updates the recently used DLHS with the newest destination Vertex
     * @param endVertex the new destination to be considered a recently used Vertex
     * @author Tony Vuolo (bdane)
     */
    private void updateRecentlyUsed(Vertex endVertex) {
        if(this.recentlyUsed.size() == MAX_RECENTLY_USED) {
            this.recentlyUsed.add(this.recentlyUsed.removeIndex(0));
        } else if(this.recentlyUsed.containsKey(endVertex)) {
            this.recentlyUsed.remove(endVertex);
            this.recentlyUsed.add(endVertex);
        }
    }

    /**
     * Used to check if our input is valid to run the pathfinding algorithm or not
     *
     * @author Alex Friedman (ahf)
     */
    private void checkInput() {
        if (startComboBox.getValue() == null ||
                endComboBox.getValue() == null){
            clearPath();

        }else{
            clearPath();
            updatePath();
            ETA.setText(calculateETA(0, pathVertex.size() - 1));
            Go.setDisable(false);
        }
    }

    /**
     * Helper function used to find the corresponding NodeEntry with nodeID
     * @param nodeID the ID used to get the corresponding NodeEntry
     * @return a NodeEntry with given ID
     * @author ZheCheng Song
     */
    private NodeEntry findNodeEntry(String nodeID){
        for(NodeEntry n : allNodeEntries){
            if (n.getNodeID().equals(nodeID)) {
                return n;
            }
        }
        return null;
    }

    /**
     * Function to parse through the path list and extract potential stops(turn, change floor) and instructions
     * @author ZheCheng Song
     */
    private void parseRoute(){
        stops = new ArrayList<>();
        instructions = new ArrayList<>();
        eta = new ArrayList<>();
        if(this.pathVertex == null) return;

        // Assert "Up" is forward for start
        double prevAngle = Math.toDegrees(Math.atan2(-1.0,0.0)) + 180;
        double currAngle;
        String prevDirect = "Look forward";
        String currDirect = "";
        double distance;
        boolean lastSE = false;

        stops.add(0);

        for(int i = 0; i < pathVertex.size() -1; i++){
            Vertex curV = pathVertex.get(i);
            Vertex nexV = pathVertex.get(i + 1);

            NodeEntry curN = findNodeEntry(curV.getID());
            if (curN == null) return;

            // Stair or Elevator found
            if ((curN.getNodeType().equals("STAI") || curN.getNodeType().equals("ELEV"))
            && curV.getID().substring(0, 5).equals(nexV.getID().substring(0, 5))){
                // Not first node, finish line search
                if(i!=0){
                    stops.add(i);
                    distance = calculateDistance(pathVertex, stops.get(stops.size()-2), stops.get(stops.size()-1));
                    instructions.add(prevDirect + " and walk " + Math.round(distance) + " m");
                }
                i = searchSE(i);
                if(i == pathVertex.size() - 1) {
                    lastSE = true;
                    break;
                }
                curV = pathVertex.get(i);
                nexV = pathVertex.get(i + 1);
                // do better
                prevAngle = Math.toDegrees(Math.atan2(nexV.getY() - curV.getY(), nexV.getX() - curV.getX())) + 180;
                stops.add(i);
            }

            currAngle = Math.toDegrees(Math.atan2(nexV.getY() - curV.getY(), nexV.getX() - curV.getX())) + 180;
            currDirect = calculateDirection(prevAngle, currAngle);
            prevAngle = currAngle;
            if(!currDirect.equals("Look forward") && i != 0){
                stops.add(i);
                distance = calculateDistance(pathVertex, stops.get(stops.size()-2), stops.get(stops.size()-1));
                instructions.add(prevDirect + " and walk " + Math.round(distance) + " m");
                prevDirect = currDirect;
            }
        }
        stops.add(pathVertex.size() - 1);
        if(!lastSE) {
            distance = calculateDistance(pathVertex, stops.get(stops.size() - 2), stops.get(stops.size() - 1));
            instructions.add(prevDirect + " and walk " + Math.round(distance) + " m");
        }
        instructions.add("Reach Destination!");

        if(instructions.size()==0) return;
        // Fixing Directions. Hard code, do better!
        boolean lookAtNext = false;
        for(int i = 0; i < instructions.size() - 1; i++){
            String ins = instructions.get(i);
            int step = stops.get(i);
            if(step == pathVertex.size() - 1 || step < 1)
                continue;
            if(lookAtNext) {
                Vertex curV = pathVertex.get(step);
                Vertex nexV = pathVertex.get(step + 1);
                currAngle = Math.toDegrees(Math.atan2(nexV.getY() - curV.getY(), nexV.getX() - curV.getX())) + 180;
                currDirect = calculateDirection(prevAngle, currAngle);
                String firstInst[] = instructions.get(i).split(" ", 3);
                instructions.set(i, currDirect + " " + firstInst[2]);
                lookAtNext = false;
            }
            if (ins.split(" ")[0].equals("Take")) {
                Vertex preV = pathVertex.get(step - 1);
                Vertex curV = pathVertex.get(step);
                prevAngle = Math.toDegrees(Math.atan2(curV.getY() - preV.getY(), curV.getX() - preV.getX())) + 180;
                lookAtNext = true;
            }
        }
        if (!instructions.get(0).split(" ")[0].equals("Take")){
            Vertex curV = pathVertex.get(0);
            Vertex nexV = pathVertex.get(1);
            prevAngle = Math.toDegrees(Math.atan2(-1.0, 0.0)) + 180;
            currAngle = Math.toDegrees(Math.atan2(nexV.getY() - curV.getY(), nexV.getX() - curV.getX())) + 180;
            currDirect = calculateDirection(prevAngle, currAngle);
            String firstInst[] = instructions.get(0).split(" ", 3);
            instructions.set(0, currDirect + " " + firstInst[2]);
        }else{
            Vertex curV = pathVertex.get(1);
            Vertex nexV = pathVertex.get(2);
            prevAngle = Math.toDegrees(Math.atan2(-1.0, 0.0)) + 180;
            currAngle = Math.toDegrees(Math.atan2(nexV.getY() - curV.getY(), nexV.getX() - curV.getX())) + 180;
            currDirect = calculateDirection(prevAngle, currAngle);
            String firstInst[] = instructions.get(1).split(" ", 3);
            instructions.set(1, currDirect + " " + firstInst[2]);
        }

        // Calculate ETA
        for(int i = 0; i < stops.size(); i ++) {
            eta.add(calculateETA(stops.get(i), pathVertex.size() - 1));
        }
        //System.out.println(pathVertex);
        //System.out.println(instructions);
        //System.out.println(stops);
    }

    private String calculateETA(int startIndex, int endIndex){
        int distance = (int)Math.round(calculateDistance(pathVertex, startIndex, endIndex));
        // Assume walks in 1.2m/s
        int totalSecond = (int)Math.round(distance / 1.2);
        int hour = totalSecond / 3600;
        if(hour > 0) totalSecond -= 3600 * hour;
        int min = totalSecond / 60;
        if(min > 0) totalSecond -= 60 * min;
        String hZero = "";
        String mZero = "";
        String sZero = "";
        if(hour / 10 < 1) hZero = "0";
        if(min / 10 < 1) mZero = "0";
        if(totalSecond / 10 < 1) sZero = "0";
        return ("ETA : " + hZero + hour + ":" + mZero + min + ":" + sZero + totalSecond);
    }

    private int searchSE(int startIndex){
        NodeEntry curN;
        Vertex preV = pathVertex.get(startIndex);
        Vertex curV;
        for(int i = startIndex + 1; i < pathVertex.size(); i++){
            curV = pathVertex.get(i);
            if(!curV.getID().substring(0, 5).equals(preV.getID().substring(0, 5)) || i == pathVertex.size() - 1){
                curN = findNodeEntry(curV.getID());
                if (curN == null) return -1;
                String type = curN.getNodeType();
                if (type.equals("STAI"))
                    type = "Stair";
                else
                    type = "Elevator";
                instructions.add("Take " + type + " to Floor " + preV.getFloor());
                if(i == pathVertex.size() - 1)
                    return pathVertex.size() - 1;
                else
                    return i - 1;
            }
            preV = curV;
        }
        // Error
        return -1;
    }

    private String calculateDirection(double prevAngle, double curAngle){
        double angle = (curAngle + (360 - prevAngle)) % 360;

        // small angle (45) alternation ignored
        if (angle <= 45 || angle >= 315) {
            return "Look forward";
        } else {
            if (Math.abs(Math.abs(curAngle - prevAngle) - 180) <= 15) {
                return "Turn around";
            } else if (angle < 180) {
                return "Turn right";
            } else {
                return "Turn left";
            }
        }
    }

    String curD;
    private void drawDirection() throws SQLException {
        if(direction != null)
            mapPanel.unDraw(this.direction.getId());
        Vertex curV = pathVertex.get(stops.get(curStep));
        if(curD.equals("UP")){
            direction = new DrawableNode((int)Math.round(curV.getX()), (int)Math.round(curV.getY() - 50.0),
                    "direction", curV.getFloor());
        }else if(curD.equals("LEFT")){
            direction = new DrawableNode((int)Math.round(curV.getX() - 50.0), (int)Math.round(curV.getY()),
                    "direction", curV.getFloor());
        }else if(curD.equals("RIGHT")){
            direction = new DrawableNode((int)Math.round(curV.getX() + 50.0), (int)Math.round(curV.getY()),
                    "direction", curV.getFloor());
        }else if(curD.equals("DOWN")){
            direction = new DrawableNode((int)Math.round(curV.getX()), (int)Math.round(curV.getY() + 50.0),
                    "direction", curV.getFloor());
        }
        direction.setFill(Color.RED);
        direction.setRadius(4);

        mapPanel.draw(direction);
    }

    private void changeDirection(String inst){
        String instruction[] = inst.split(" ");
        if(!instruction[0].equals("Take") && !instruction[0].equals("Look")){
            if(instruction[1].equals("around")){
                switch (curD) {
                    case "UP" : curD = "DOWN"; break;
                    case "LEFT" : curD = "RIGHT"; break;
                    case "RIGHT" : curD = "LEFT"; break;
                    case "DOWN" : curD = "UP"; break;
                }
            }else if(instruction[1].equals("left")){
                switch (curD) {
                    case "UP" : curD = "LEFT"; break;
                    case "LEFT" : curD = "DOWN"; break;
                    case "RIGHT" : curD = "UP"; break;
                    case "DOWN" : curD = "RIGHT"; break;
                }
            }else if(instruction[1].equals("right")){
                switch (curD) {
                    case "UP" : curD = "RIGHT"; break;
                    case "LEFT" : curD = "UP"; break;
                    case "RIGHT" : curD = "DOWN"; break;
                    case "DOWN" : curD = "LEFT"; break;
                }
            }
        }
    }

    private void changeDirectionRevert(String inst){
        String instruction[] = inst.split(" ");
        if(!instruction[0].equals("Take") && !instruction[0].equals("Look")){
            if(instruction[1].equals("around")){
                switch (curD) {
                    case "UP" : curD = "DOWN"; break;
                    case "LEFT" : curD = "RIGHT"; break;
                    case "RIGHT" : curD = "LEFT"; break;
                    case "DOWN" : curD = "UP"; break;
                }
            }else if(instruction[1].equals("left")){
                switch (curD) {
                    case "UP" : curD = "RIGHT"; break;
                    case "LEFT" : curD = "UP"; break;
                    case "RIGHT" : curD = "DOWN"; break;
                    case "DOWN" : curD = "LEFT"; break;
                }
            }else if(instruction[1].equals("right")){
                switch (curD) {
                    case "UP" : curD = "LEFT"; break;
                    case "LEFT" : curD = "DOWN"; break;
                    case "RIGHT" : curD = "UP"; break;
                    case "DOWN" : curD = "RIGHT"; break;
                }
            }
        }
    }

    /**
     * Function to react to 'Start Navigation' button being pressed and start the route stepper
     * @param actionEvent
     * @throws SQLException
     * @author ZheCheng Song
     */
    public void startNavigation(ActionEvent actionEvent) throws SQLException {
        startComboBox.setDisable(true);
        endComboBox.setDisable(true);
        Go.setDisable(true);
        Next.setDisable(false);
        End.setDisable(false);
        Instruction.setVisible(true);
        curStep = 0;
        pathFinding = true;

        parseRoute();
        mapPanel.switchMap(pathVertex.get(0).getFloor());

        clearPath();
        drawPathFromIndex(0);
        drawStartNode(pathVertex.get(0).getID());
        drawEndNode(pathVertex.get(pathVertex.size()-1).getID());
        drawUserNode(pathVertex.get(stops.get(curStep)).getID());
        mapPanel.centerNode(userNodeDisplay);

        Instruction.setText(instructions.get(curStep));
        ETA.setText(eta.get(curStep));
        curFloor = pathVertex.get(0).getFloor();

        curD = "UP";
        drawDirection();
    }

    /**
     * Function to react to 'Prev' button being pressed and go to the previous point with stepper
     * @param actionEvent
     * @throws SQLException
     * @author ZheCheng Song
     */
    public void goToPrevNode(ActionEvent actionEvent) throws SQLException {
        curStep--;
        if(curStep == 0){
            Prev.setDisable(true);
        }
        else {
            Prev.setDisable(false);
            Next.setDisable(false);
        }
        if(!pathVertex.get(curStep).getFloor().equals(curFloor)){
            mapPanel.switchMap(pathVertex.get(stops.get(curStep)).getFloor());
        }
        curFloor = pathVertex.get(stops.get(curStep)).getFloor();

        clearPath();
        drawPathFromIndex(0);
        drawStartNode(pathVertex.get(0).getID());
        drawEndNode(pathVertex.get(pathVertex.size()-1).getID());
        drawUserNode(pathVertex.get(stops.get(curStep)).getID());
        mapPanel.centerNode(userNodeDisplay);

        Instruction.setText(instructions.get(curStep));
        ETA.setText(eta.get(curStep));

        changeDirectionRevert(instructions.get(curStep));
        drawDirection();
    }

    /**
     * Function to react to 'Next' button being pressed and go to the next point with stepper
     * @param actionEvent
     * @throws SQLException
     * @author ZheCheng Song
     */
    public void goToNextNode(ActionEvent actionEvent) throws SQLException {
        changeDirection(instructions.get(curStep));

        curStep++;
        if(curStep == Math.min(stops.size() - 1, instructions.size() - 1)){
            Next.setDisable(true);
        }
        else {
            Prev.setDisable(false);
            Next.setDisable(false);
        }
        if(!pathVertex.get(stops.get(curStep)).getFloor().equals(curFloor)){
            mapPanel.switchMap(pathVertex.get(stops.get(curStep)).getFloor());
        }
        curFloor = pathVertex.get(stops.get(curStep)).getFloor();

        clearPath();
        drawPathFromIndex(0);
        drawStartNode(pathVertex.get(0).getID());
        drawEndNode(pathVertex.get(pathVertex.size()-1).getID());
        drawUserNode(pathVertex.get(stops.get(curStep)).getID());
        mapPanel.centerNode(userNodeDisplay);

        Instruction.setText(instructions.get(curStep));
        ETA.setText(eta.get(curStep));

        drawDirection();

        if(direction != null &&curStep == Math.min(stops.size() - 1, instructions.size() - 1))
                mapPanel.unDraw(this.direction.getId());

    }

    /**
     * Function to react to 'End Navigation' button being pressed and stop the stepper
     * @param actionEvent
     * @throws SQLException
     * @author ZheCheng Song
     */
    public void endNavigation(ActionEvent actionEvent) throws SQLException {
        startComboBox.setDisable(false);
        endComboBox.setDisable(false);
        Go.setDisable(false);
        Prev.setDisable(true);
        Next.setDisable(true);
        End.setDisable(true);
        Instruction.setVisible(false);
        curStep = 0;
        pathFinding = false;

        mapPanel.switchMap(pathVertex.get(0).getFloor());

        clearPath();
        drawPathFromIndex(0);
        drawStartNode(pathVertex.get(0).getID());
        drawEndNode(pathVertex.get(pathVertex.size()-1).getID());
        mapPanel.centerNode(startNodeDisplay);

        ETA.setText(calculateETA(0, pathVertex.size() - 1));
    }

    /**
     * Helper function to calculate total distance from start index to end index of a list of vertex
     * @param path List of Vertex to be used
     * @param start Start index represent the starting node in list
     * @param end End index represent the ending node in list
     * @return sumDist the total distance from start Vertex to end Vertex
     */
    private double calculateDistance(List<Vertex> path, int start, int end) {
        double sumDist = 0.0;
        for (int i = start; i < end; i++) {
            sumDist += path.get(i).EuclideanDistance(path.get(i + 1));
        }
        return sumDist / PIXEL_TO_METER_RATIO;
    }
}
