package edu.wpi.fuchsiafalcons.database;

import edu.wpi.fuchsiafalcons.utils.CSVManager;

import javax.xml.crypto.Data;
import java.sql.*;

public class TestDriverCode {
    public static void main(String[] args) throws Exception {

        DatabaseAPI.getDatabaseAPI().populateDB(ConnectionHandler.getConnection(), CSVManager.load("MapfAllnodes.csv"), CSVManager.load("MapfAlledges.csv"));

        DatabaseAPI api = DatabaseAPI.getDatabaseAPI();
        boolean testAdd = api.addNode("TEST", 1, 1, "FLOOR", "BUILD", "TYPE",
                "TESTLONG", "TESTSHORT");
        boolean testDelete = api.deleteNode("TEST");
        boolean testAddEdge = api.addEdge("TESTEDGE", "start", "end");
        boolean testDeleteEdge = api.deleteEdge("TESTEDGE");
        boolean dummyNode = api.addNode("TEST", 1, 1, "f", "b", "t",
                "long", "short");
        boolean updateNode = api.updateEntry("L1Nodes", "node", "TEST",
                "id", "DECLAN");

        ConnectionHandler.getConnection().close();
    }
}
