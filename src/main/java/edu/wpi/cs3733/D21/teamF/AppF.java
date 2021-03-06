package edu.wpi.cs3733.D21.teamF;

import edu.wpi.cs3733.D21.teamF.controllers.AbsController;
import edu.wpi.cs3733.D21.teamF.database.ConnectionHandler;
import edu.wpi.cs3733.D21.teamF.database.DatabaseAPI;
import edu.wpi.cs3733.D21.teamF.utils.CSVManager;
import edu.wpi.cs3733.D21.teamF.utils.SceneContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class  AppF extends Application {

  @Override
  public void init() {
    System.out.println("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) throws Exception {

    primaryStage.setTitle("Brigham and Women's Hospital Kiosk | Fuchsia Falcons ");
    primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/imagesAndLogos/BandWLogo.png")));
    if (DatabaseAPI.getDatabaseAPI().createNodesTable())
    {
      DatabaseAPI.getDatabaseAPI().populateNodes(CSVManager.load("MapfAllNodes.csv"));
    }
    if (DatabaseAPI.getDatabaseAPI().createEdgesTable())
    {
      DatabaseAPI.getDatabaseAPI().populateEdges(CSVManager.load("MapfAllEdges.csv"));
    }
    DatabaseAPI.getDatabaseAPI().createUserTable();
    DatabaseAPI.getDatabaseAPI().createServiceRequestTable();
    DatabaseAPI.getDatabaseAPI().createSystemTable();
    DatabaseAPI.getDatabaseAPI().createCollectionsTable(); //FIXME: DO BETTER

    SceneContext.getSceneContext().setStage(primaryStage);

    //ConnectionHandler.main(false);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        ConnectionHandler.getConnection().close();
      } catch (SQLException exception) {
        exception.printStackTrace();
      }
    }));
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("/edu/wpi/cs3733/D21/teamF/fxml/DefaultPageView.fxml"));
      Parent root = loader.load();
      ((AbsController)loader.getController()).initLanguage();
      SceneContext.autoTranslate(root);

      primaryStage.setScene(new Scene(root));

      primaryStage.setMinWidth(960);//primaryStage.getWidth());
      primaryStage.setMinHeight(540);//primaryStage.getHeight());
      primaryStage.show();
      //SceneContext.getSceneContext().switchScene("/edu/wpi/cs3733/D21/teamF/fxml/DefaultPageView.fxml");//DefaultPageView.fxml"); Commented out to deal with initial boot ( the .getScene() call in switchScene() will return null unless we manually load it in to start) - KD
    } catch (IOException e) {
      e.printStackTrace();
      Platform.exit();
    }
  }


  @Override
  public void stop() {
    System.out.println("Shutting Down");
  }
}
