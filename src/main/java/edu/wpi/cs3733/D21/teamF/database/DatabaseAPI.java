package edu.wpi.cs3733.D21.teamF.database;


import edu.wpi.cs3733.D21.teamF.entities.AccountEntry;
import edu.wpi.cs3733.D21.teamF.entities.EdgeEntry;
import edu.wpi.cs3733.D21.teamF.entities.NodeEntry;
import edu.wpi.cs3733.D21.teamF.entities.ServiceEntry;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAPI {
    //private static DatabaseAPI1 databaseAPI1;

    private final DatabaseEntry nodeHandler;
    private final DatabaseEntry edgeHandler;
    private final DatabaseEntry serviceRequestHandler;
    private final DatabaseEntry userHandler;
    private final DatabaseEntry systemHandler;
    private final CollectionHandler collectionHandler;

    public DatabaseAPI() {
        this.nodeHandler = new NodeHandler();
        this.edgeHandler = new EdgeHandler();
        this.userHandler = new UserHandler();
        this.serviceRequestHandler = new ServiceRequestHandler();
        this.systemHandler = new SystemPreferences();
        this.collectionHandler = new CollectionHandler();
    }

    public boolean createNodesTable() {
        return nodeHandler.createTable();
    }

    public boolean dropNodesTable() {
        return nodeHandler.dropTable();
    }

    public void populateNodes(List<String[]> entries) throws SQLException{
        nodeHandler.populateTable(entries);
    }

    public String makeNodeDescription(String[] values){
        return ((NodeHandler)this.nodeHandler).genNodeDescription(values);
    }

    public boolean addNode(String...colValues) throws SQLException {
        for (String s: colValues){
            if (!(this.filterInput(s))){
                return false;
            }
        }
        return nodeHandler.addEntry(colValues);
    }

    public boolean editNode(String id, String newVal, String colName) throws Exception{
        return nodeHandler.editEntry(id, newVal, colName);
    }

    public boolean deleteNode(String nodeID) throws SQLException{
        return nodeHandler.deleteEntry(nodeID);
    }

    public List<NodeEntry> genNodeEntries() throws SQLException{
        NodeHandler node = new NodeHandler();
        return node.genNodeEntryObjects();
    }

    public NodeEntry getNode(String id) throws SQLException{
        NodeHandler node = new NodeHandler();
        return node.getNode(id);
    }

    public boolean addEdge(String...colValues) throws SQLException{
        for (String s: colValues){
            if (!(this.filterInput(s))){
                return false;
            }
        }
        return edgeHandler.addEntry(colValues);
    }

    public boolean editEdge(String id, String newVal, String colName) throws Exception{
        return edgeHandler.editEntry(id, newVal, colName);
    }

    public boolean deleteEdge(String id) throws SQLException {
        return edgeHandler.deleteEntry(id);
    }

    public boolean createEdgesTable() {
        return edgeHandler.createTable();
    }

    public boolean dropEdgesTable() {
        return edgeHandler.dropTable();
    }

    public void populateEdges(List<String[]> data) throws SQLException{
        edgeHandler.populateTable(data);
    }

    public List<EdgeEntry> genEdgeEntries() throws SQLException
    {
        List<EdgeEntry> ret;
        EdgeHandler edge = new EdgeHandler();
        ret = edge.genEdgeEntryObjects();
        return ret;
    }

    public EdgeEntry getEdge(String id) throws SQLException{
        EdgeHandler edge = new EdgeHandler();
        return edge.getEdge(id);
    }

    public boolean addServiceReq(String...colValues) throws SQLException{
        for (String s: colValues){
            if (!(this.filterInput(s))){
                return false;
            }
        }
        return serviceRequestHandler.addEntry(colValues);
    }

    public boolean editServiceRequest(String id, String newVal, String colName) throws Exception{
        return serviceRequestHandler.editEntry(id, newVal, colName);
    }

    public boolean deleteServiceRequest(String id) throws SQLException {
        return serviceRequestHandler.deleteEntry(id);
    }

    public boolean createServiceRequestTable(){
        return serviceRequestHandler.createTable();
    }

    public boolean dropServiceRequestTable(){
        return serviceRequestHandler.dropTable();
    }

    public void populateServiceRequestTable(List<String[]> entries) throws SQLException {
        serviceRequestHandler.populateTable(entries);
    }

    public List<ServiceEntry> genServiceRequestEntries() throws SQLException{
        ServiceRequestHandler s = new ServiceRequestHandler();
        return s.genServiceRequestEntries();
    }

    public ServiceEntry getServiceEntry(String value, String colName) throws SQLException{
        return ((ServiceRequestHandler)this.serviceRequestHandler).getServiceRequest(value, colName);
    }

    public boolean addUser(String...colValues) throws SQLException{
        for (String s: colValues){
            if (!(this.filterInput(s))){
                return false;
            }
        }
        return userHandler.addEntry(colValues);
    }

    public boolean editUser(String id, String newVal, String colName) throws Exception {
        return userHandler.editEntry(id, newVal, colName);
    }

    public boolean deleteUser(String username) throws SQLException{
        return userHandler.deleteEntry(username);
    }

    public boolean createUserTable() {
        return userHandler.createTable();
    }

    public boolean dropUsersTable() {
        return userHandler.dropTable();
    }

    public void populateUsers(List<String[]> users) throws SQLException{
        userHandler.populateTable(users);
    }

    public AccountEntry getUser(String username) throws SQLException{
        return ((UserHandler)this.userHandler).getUser(username);
    }

    public boolean authenticate(String user, String pass) throws SQLException {
        return ((UserHandler)this.userHandler).authenticate(user, pass);
    }

    public String getEncryptedPass(String newPass, byte[] salt){
        return ((UserHandler)this.userHandler).encryptPassword(newPass, salt);
    }

    public List<AccountEntry> genAccountEntries() throws SQLException{
        return ((UserHandler)this.userHandler).genAccountEntryObjects();
    }

    public boolean verifyAdminExists() throws SQLException{
        return ((UserHandler)this.userHandler).verifyAdmin();
    }

    public boolean isValidEmail(String email){
        return ((UserHandler)this.userHandler).isEmail(email);
    }

    public boolean addSystemPreferences(String...colValues) throws SQLException{
        for (String s: colValues){
            if (!(this.filterInput(s))){
                return false;
            }
        }
        return systemHandler.addEntry(colValues);
    }

    public boolean dropSystemTable(){
        return systemHandler.dropTable();
    }

    public boolean createSystemTable(){
        return systemHandler.createTable();
    }

    public boolean editSystemSettings(String id, String newVal, String colName) throws Exception{
        return systemHandler.editEntry(id, newVal, colName);
    }

    public boolean deleteSystemPreference(String id) throws SQLException{
        return systemHandler.deleteEntry(id);
    }

    public String getCurrentAlgorithm() throws SQLException{
        return ((SystemPreferences)this.systemHandler).getAlgorithm();
    }

    public boolean createCollectionsTable(){
        return collectionHandler.createCollectionTable();
    }

    public boolean dropCollectionsTable(){
        return collectionHandler.dropCollectionsTable();
    }

    public boolean addCollectionEntry(String user, String node, String type) throws SQLException{
        String[] input = {user, node, type};
        for (String s: input){
            if (!(this.filterInput(s))){
                return false;
            }
        }
        return collectionHandler.addEntry(user, node, type);
    }

    public ArrayList<String> getUserNodes(String type, String userID) throws SQLException{
        return collectionHandler.getUserNodes(type, userID);
    }

    public boolean deleteUserNode(String nodeID, String username, String type) throws SQLException{
        return collectionHandler.deleteNodeEntry(nodeID, username, type);
    }
 /**
     * Filters user input to prevent SQL injections
     * @param input the user input
     * @return true if safe, false otherwise
     */
    public boolean filterInput(String input){
        char[] blacklist = {'\'', '-', '\"', '#', '(', ')', '|'};
        for (char c : blacklist){
            if (!(input.indexOf(c) == -1)){
                System.out.println("WARNING: Possible SQL Injection: " + input);
                return true;
            }
        }
        return true;
    }


    private static class DatabaseSingletonHelper{
        private static final DatabaseAPI databaseAPI1 = new DatabaseAPI();
    }

    public static DatabaseAPI getDatabaseAPI(){
        return DatabaseSingletonHelper.databaseAPI1;
    }
}
