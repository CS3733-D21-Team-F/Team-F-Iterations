package edu.wpi.cs3733.D21.teamF.database;

import edu.wpi.cs3733.D21.teamF.entities.EdgeEntry;
import edu.wpi.cs3733.D21.teamF.entities.ServiceEntry;
import edu.wpi.cs3733.D21.teamF.pathfinding.NodeEntry;
import edu.wpi.cs3733.D21.teamF.utils.CSVManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseAPITest {
    @BeforeEach
    public void setUp() {
        DatabaseAPI.getDatabaseAPI().dropNodesTable();
        DatabaseAPI.getDatabaseAPI().dropEdgesTable();
        DatabaseAPI.getDatabaseAPI().dropUsersTable();
        DatabaseAPI.getDatabaseAPI().dropServiceRequestTable();
        DatabaseAPI.getDatabaseAPI().dropSystemTable();

        DatabaseAPI.getDatabaseAPI().createNodesTable();
        DatabaseAPI.getDatabaseAPI().createEdgesTable();
        DatabaseAPI.getDatabaseAPI().createUserTable();
        DatabaseAPI.getDatabaseAPI().createServiceRequestTable();
        DatabaseAPI.getDatabaseAPI().createSystemTable();

        /*
        //FIXME: DO BETTER!
        try {
            DatabaseAPI.getDatabaseAPI().addUser("admin", "administrator", "admin", "admin");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        */
    }

    @Test()
    @DisplayName("test dropping nodes table")
    public void testDropNodesTable() {
        String[] newNode = {"test", "10", "10", "floor", "building", "type", "long", "short"};
        assertTrue(DatabaseAPI.getDatabaseAPI().dropNodesTable());
        assertThrows(SQLException.class, () -> DatabaseAPI.getDatabaseAPI().addNode(newNode));
        DatabaseAPI.getDatabaseAPI().createNodesTable();
    }

    @Test()
    @DisplayName("test dropping edges table")
    public void testDropEdgesTable() {
        String[] newEdge = {"test", "start", "end"};
        assertTrue(DatabaseAPI.getDatabaseAPI().dropEdgesTable());
        assertThrows(SQLException.class, () -> DatabaseAPI.getDatabaseAPI().addEdge(newEdge));
    }

    @Test
    @DisplayName("test adding nodes table")
    public void testAddNodesTable() throws SQLException
    {
        String[] newNode = {"test", "10", "10", "floor", "building", "type", "long", "short"};
        DatabaseAPI.getDatabaseAPI().dropNodesTable();
        assertTrue(DatabaseAPI.getDatabaseAPI().createNodesTable());
        assertTrue(DatabaseAPI.getDatabaseAPI().addNode(newNode));
    }

    @Test
    @DisplayName("test adding edges table")
    public void testAddEdgesTable() throws SQLException
    {
        String[] newEdge = {"test",  "start", "end"};
        DatabaseAPI.getDatabaseAPI().dropEdgesTable();
        assertTrue(DatabaseAPI.getDatabaseAPI().createEdgesTable());
        assertTrue(DatabaseAPI.getDatabaseAPI().addEdge(newEdge));
    }

    @Test
    @DisplayName("test adding a node")
    public void testAddNodeBasic() throws SQLException
    {
        String[] newNode = {"test", "10", "10", "floor", "building", "type", "long", "short"};
        assertTrue(DatabaseAPI.getDatabaseAPI().addNode(newNode));
    }

    @Test
    @DisplayName("test adding a edge")
    public void testAddEdgeBasic() throws SQLException
    {
        String[] newEdge = {"test", "start", "end"};
        assertTrue(DatabaseAPI.getDatabaseAPI().addEdge(newEdge));
    }

    @Test
    @DisplayName("test invalid node add")
    public void testInvalidNodeAdd() {
        String[] newNode = {"test", "1234"};
        assertThrows(SQLException.class, () -> DatabaseAPI.getDatabaseAPI().addNode(newNode));
    }

    @Test
    @DisplayName("test invalid edge add")
    public void testInvalidEdgeAdd() {
        String[] newEdge = {"test", "1234"};
        assertThrows(SQLException.class, () -> DatabaseAPI.getDatabaseAPI().addEdge(newEdge));
    }

    @Test
    @DisplayName("test delete node")
    public void testDeleteNode() throws SQLException
    {
        String[] newNode = {"test", "10", "10", "floor", "building", "type", "long", "short"};
        DatabaseAPI.getDatabaseAPI().addNode(newNode);
        assertTrue(DatabaseAPI.getDatabaseAPI().deleteNode("test"));
    }

    @Test
    @DisplayName("test delete invalid node")
    public void testDeleteInvalidNode() throws SQLException
    {
        String[] newNode = {"test", "10", "10", "floor", "building", "type", "long", "short"};
        DatabaseAPI.getDatabaseAPI().addNode(newNode);
        assertFalse(DatabaseAPI.getDatabaseAPI().deleteNode("notTest"));
    }

    @Test
    @DisplayName("test populate nodes")
    public void testPopulateNodes() throws Exception
    {
        DatabaseAPI.getDatabaseAPI().populateNodes(CSVManager.load("MapfAllNodes.csv"));
        assertTrue(DatabaseAPI.getDatabaseAPI().deleteNode("ACONF00102"));
    }

    @Test
    @DisplayName("test generating node entry list")
    public void testGenerateNodeEntries() throws SQLException
    {
        NodeEntry entry = new NodeEntry("test", "1", "1", "f", "b", "t", "l", "s");
        ArrayList<NodeEntry> expected = new ArrayList<>();
        expected.add(entry);
        String[] newNode = {"test", "1", "1", "f", "b", "t", "l", "s"};
        DatabaseAPI.getDatabaseAPI().addNode(newNode);
        List<NodeEntry> actual = DatabaseAPI.getDatabaseAPI().genNodeEntries();
        assertEquals(expected.get(0).getNodeID(), actual.get(0).getNodeID());
    }

    @Test
    @DisplayName("test editing a node")
    public void testEditNode() throws Exception
    {
        String[] newNode = {"test", "1", "2", "f", "b", "t", "l", "s"};
        DatabaseAPI.getDatabaseAPI().addNode(newNode);
        assertTrue(DatabaseAPI.getDatabaseAPI().editNode("test", "test1", "nodeid"));
    }

    @Test
    @DisplayName("test editing a Edge")
    public void testEditEdge() throws Exception
    {
        String[] newEdge = {"test", "start", "end"};
        DatabaseAPI.getDatabaseAPI().addEdge(newEdge);
        assertTrue(DatabaseAPI.getDatabaseAPI().editEdge("test", "test1", "startnode"));
    }

    @Test
    @DisplayName("test deleting an edge")
    public void testDeleteEdge() throws SQLException
    {
        String[] newEdge = {"test", "start", "end"};
        DatabaseAPI.getDatabaseAPI().addEdge(newEdge);
        assertTrue(DatabaseAPI.getDatabaseAPI().deleteEdge("test"));
        assertTrue(DatabaseAPI.getDatabaseAPI().addEdge(newEdge));
    }

    @Test
    @DisplayName("test populating edges table")
    public void testPopulateEdges() throws Exception
    {
        DatabaseAPI.getDatabaseAPI().populateEdges(CSVManager.load("MapfAllEdges.csv"));
        assertTrue(DatabaseAPI.getDatabaseAPI().deleteEdge("AHALL00202_AHALL00302"));
    }

    @Test
    @DisplayName("test generating edge entry list")
    public void testGenerateEdgeEntries() throws SQLException
    {
        EdgeEntry entry = new EdgeEntry("test", "start", "end");
        ArrayList<EdgeEntry> expected = new ArrayList<>();
        expected.add(entry);
        String[] newEdge = {"test", "start", "end"};
        DatabaseAPI.getDatabaseAPI().addEdge(newEdge);
        List<EdgeEntry> actual = DatabaseAPI.getDatabaseAPI().genEdgeEntries();
        assertEquals(expected.get(0).getEdgeID(), actual.get(0).getEdgeID());
    }

    @Test
    @DisplayName("test adding a user")
    public void testAddUser() throws SQLException
    {
        String[] newUser = {"1", "employee", "declan", "password"};
        assertTrue(DatabaseAPI.getDatabaseAPI().addUser(newUser));
    }

    @Test
    @DisplayName("test deleting a user")
    public void testDeleteUser() throws SQLException
    {
        String[] newUser = {"1", "employee", "declan", "password"};
        DatabaseAPI.getDatabaseAPI().addUser(newUser);
        assertTrue(DatabaseAPI.getDatabaseAPI().deleteUser("declan"));
    }

    @Test
    @DisplayName("test editing a user")
    public void testEditUser() throws Exception
    {
        String[] newUser = {"1", "employee", "declan", "password"};
        DatabaseAPI.getDatabaseAPI().addUser(newUser);
        assertTrue(DatabaseAPI.getDatabaseAPI().editUser("declan", "password123", "password"));
    }

    @Test
    @DisplayName("test dropping users table")
    public void testDropUsersTable() {
        assertTrue(DatabaseAPI.getDatabaseAPI().dropUsersTable());
    }

    @Test
    @DisplayName("test adding users table")
    public void testAddUsersTable() {
        DatabaseAPI.getDatabaseAPI().dropUsersTable();
        assertTrue(DatabaseAPI.getDatabaseAPI().createUserTable());
    }

    @Test
    @DisplayName("test populating users table")
    public void testPopulateUsers() throws SQLException
    {
        ArrayList<String[]> users = new ArrayList<>();
        String[] user1 = {"1", "admin", "username", "password"};
        String[] user2 = {"2", "employee", "testuser", "testpass"};
        users.add(user1);
        users.add(user2);

        DatabaseAPI.getDatabaseAPI().populateUsers(users);
        assertTrue(DatabaseAPI.getDatabaseAPI().deleteUser("testuser"));
        assertTrue(DatabaseAPI.getDatabaseAPI().deleteUser("username"));
    }

    @Test
    @DisplayName("test adding a service request")
    public void testAddServiceRequest() throws SQLException
    {
        String[] newServiceRequest = {"1", "Sample Task", "Ben", "false", "extra instructions"};
        assertTrue(DatabaseAPI.getDatabaseAPI().addServiceReq(newServiceRequest));
    }

    @Test
    @DisplayName("test deleting a serviceRequest")
    public void testDeleteServiceRequest() throws SQLException
    {
        String[] newServiceRequest = {"1", "Sample Task", "Ben", "false", "additional steps"};
        DatabaseAPI.getDatabaseAPI().addServiceReq(newServiceRequest);
        assertTrue(DatabaseAPI.getDatabaseAPI().deleteServiceRequest("1"));
    }

    @Test
    @DisplayName("test editing a service request")
    public void testEditServiceRequest() throws Exception
    {
        String[] newServiceRequest = {"1", "Sample Task", "Ben", "false", "additional steps"};
        DatabaseAPI.getDatabaseAPI().addServiceReq(newServiceRequest);
        assertTrue(DatabaseAPI.getDatabaseAPI().editServiceRequest("1", "Declan", "assignedperson"));
    }

    @Test
    @DisplayName("test dropping service_request table")
    public void testDropServiceReqTable() {
        assertTrue(DatabaseAPI.getDatabaseAPI().dropServiceRequestTable());
    }

    @Test
    @DisplayName("test adding service requests table")
    public void testAddServiceReqTable() {
        DatabaseAPI.getDatabaseAPI().dropServiceRequestTable();
        assertTrue(DatabaseAPI.getDatabaseAPI().createServiceRequestTable());
    }

    @Test
    @DisplayName("test populating service request table")
    public void testPopulateServiceReqs() throws SQLException
    {
        ArrayList<String[]> sreqs = new ArrayList<>();
        String[] sreq1 = {"1", "A task", "Ben", "false", "test"};
        String[] sreq2 = {"2", "Another task", "Declan", "true", "test"};
        sreqs.add(sreq1);
        sreqs.add(sreq2);

        DatabaseAPI.getDatabaseAPI().populateServiceRequestTable(sreqs);
        assertTrue(DatabaseAPI.getDatabaseAPI().deleteServiceRequest("1"));
        assertTrue(DatabaseAPI.getDatabaseAPI().deleteServiceRequest("2"));
    }

    @Test
    @DisplayName("test generating service request entry list")
    public void testGenerateServiceRequestEntries() throws SQLException
    {
        ServiceEntry entry = new ServiceEntry("1", "a task", "Ben","true", "instructions");
        ArrayList<ServiceEntry> expected = new ArrayList<>();
        expected.add(entry);
        String[] newService = {"1", "a task", "Ben","true", "instructions"};
        DatabaseAPI.getDatabaseAPI().addServiceReq(newService);
        List<ServiceEntry> actual = DatabaseAPI.getDatabaseAPI().genServiceRequestEntries();
        assertEquals(expected.get(0).getUuid(), actual.get(0).getUuid());
    }

    @Test
    @DisplayName("Test authentication and encryption")
    public void testAuthentication() throws SQLException{
        String[] newUser = {"1", "admin", "declan", "password"};
        DatabaseAPI.getDatabaseAPI().addUser(newUser);
        UserHandler handler = new UserHandler();
        assertTrue(handler.authenticate("declan", "password"));
    }

    @Test
    @DisplayName("test getting a single node")
    public void testGetNode() throws SQLException{
        NodeEntry expected = new NodeEntry("id", "1", "2", "f", "b", "t", "l", "s");
        String[] newNode = {"id", "1", "2", "f", "b", "t", "l", "s"};
        DatabaseAPI.getDatabaseAPI().addNode(newNode);
        NodeEntry actual = DatabaseAPI.getDatabaseAPI().getNode("id");
        assertEquals(expected.getNodeID(), actual.getNodeID());
        assertEquals(expected.getFloor(), actual.getFloor());
    }

    @Test
    @DisplayName("test getting a single edge")
    public void testGetEdge() throws SQLException{
        EdgeEntry expected = new EdgeEntry("test", "start", "end");
        String[] newEdge = {"test", "start", "end"};
        DatabaseAPI.getDatabaseAPI().addEdge(newEdge);
        EdgeEntry actual = DatabaseAPI.getDatabaseAPI().getEdge("test");
        assertEquals(expected.getEdgeID(), actual.getEdgeID());
        assertEquals(expected.getStartNode(), actual.getStartNode());
    }

    @Test
    @DisplayName("test verifying the admin user entry")
    public void testAdmin() throws SQLException{
        String[] admin = {"admin", "administrator", "admin", "admin"};
        DatabaseAPI.getDatabaseAPI().addUser(admin);
        assertTrue(DatabaseAPI.getDatabaseAPI().verifyAdminExists());
    }

    @Test
    @DisplayName("test admin doesnt exist")
    public void testNoAdmin() throws SQLException{
        assertFalse(DatabaseAPI.getDatabaseAPI().verifyAdminExists());
    }

    @Test
    @DisplayName("test adding system preferences")
    public void testAddSystemPreferences() throws SQLException{
        assertTrue(DatabaseAPI.getDatabaseAPI().addSystemPreferences("2", "BFS"));
    }

    @Test
    @DisplayName("test deleting system preferences")
    public void testDeleteSystemPreferences() throws SQLException{
        DatabaseAPI.getDatabaseAPI().addSystemPreferences("1", "DFS");
        assertTrue(DatabaseAPI.getDatabaseAPI().deleteSystemPreference("1"));
    }

    @Test
    @DisplayName("test dropping system preferences table")
    public void testDropSystemPreferences() throws SQLException{
        assertTrue(DatabaseAPI.getDatabaseAPI().dropSystemTable());
    }

    @Test
    @DisplayName("test creating system preferences table")
    public void testCreatingSystemTable() throws SQLException{
        DatabaseAPI.getDatabaseAPI().dropSystemTable();
        assertTrue(DatabaseAPI.getDatabaseAPI().createSystemTable());
    }

    @Test
    @DisplayName("test getting and adding correct algorithm (1st entry always)")
    public void testGetAlgorithm() throws SQLException{
        DatabaseAPI.getDatabaseAPI().addSystemPreferences("MASTER", "A*");
        assertEquals(DatabaseAPI.getDatabaseAPI().getCurrentAlgorithm(), "A*");
    }
}
