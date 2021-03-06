package edu.wpi.cs3733.D21.teamF.utils;

import com.jfoenix.controls.JFXTreeTableView;
import edu.wpi.cs3733.D21.teamF.Translation.Translator;
import edu.wpi.cs3733.D21.teamF.controllers.AbsController;
import javafx.animation.PauseTransition;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SceneContext {

    private Stage stage;

    private AbsController controller;

    private SceneContext() {}

    private static class SceneContextSingletonHelper {
        private static final SceneContext sceneContext = new SceneContext();
    }

    public static SceneContext getSceneContext() {
        return SceneContextSingletonHelper.sceneContext;
    }

    private void showLoadingPage() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/edu/wpi/cs3733/D21/teamF/fxml/LoadPage.fxml"));
        controller = loader.getController();
        if(!(stage.getScene()==null)) { // Added for first loading the app, allows us to use getScene().setRoot() since getScene() returns null at first - KD
            stage.getScene().setRoot(loader.load());
            stage.show();
        }
    }

        public void switchScene(String fxml) throws IOException {

        final Task<Parent> task = new Task<Parent>() {
            @Override
            public Parent call() {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource(fxml));

                try {
                    Parent root = loader.load();

                    controller = loader.getController();
                    controller.initLanguage();
                    //if(controller instanceof ServiceRequests || controller instanceof ServiceRequestHomeNewController);
                    autoTranslate(root);

                    return root;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return  null;
            }
        };
        task.setOnSucceeded( e-> {
            try {
                stage.getScene().setRoot(task.get());
                stage.show();
            } catch (InterruptedException | ExecutionException interruptedException) {
                interruptedException.printStackTrace();
            }
        });

        task.setOnScheduled( e -> {
            final PauseTransition timeOut = new PauseTransition(Duration.seconds(0.2));
            timeOut.setOnFinished((timeout) -> {
                if(task.isRunning()) {
                    try {
                        showLoadingPage();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            });
            timeOut.play();

        });

        Thread t = new Thread(task);
        t.start();
    }

    public static void autoTranslate(Parent root)
    {
        final List<Node> children = new ArrayList<>();
        getAllChildren(root, children);

        for(Node n : children)
        {
            if(n instanceof Labeled)
            {
                final Labeled labeled = (Labeled) n;

                if(labeled.getText().toLowerCase().startsWith("brigham and women's hospital"))
                    continue;
                labeled.textProperty().bind(Translator.getTranslator().getTranslationBinding(labeled.getText()));
            }
            else if(n instanceof Text)
            {
                final Text text = (Text) n;

                if(text.getText().toLowerCase().startsWith("brigham and women's hospital"))
                    continue;
                text.textProperty().bind(Translator.getTranslator().getTranslationBinding(text.getText()));
            }
            else if(n instanceof TextInputControl)
            {
                final TextInputControl textInputControl = (TextInputControl) n;

                if(textInputControl.getPromptText().toLowerCase().startsWith("brigham and women's hospital"))
                    continue;
                textInputControl.promptTextProperty().bind(Translator.getTranslator().getTranslationBinding(textInputControl.getPromptText()));
            }
            else if(n instanceof ComboBoxBase<?>)
            {
                final ComboBoxBase<?> comboBoxBase = (ComboBoxBase<?>) n;

                if(comboBoxBase.getPromptText().toLowerCase().startsWith("brigham and women's hospital"))
                    continue;
                comboBoxBase.promptTextProperty().bind(Translator.getTranslator().getTranslationBinding(comboBoxBase.getPromptText()));
            }
            else if(n instanceof JFXTreeTableView)
            {
                final TreeTableView<?> treeTableView = (TreeTableView<?>) n;

                for(TreeTableColumn<?, ?> c : treeTableView.getColumns()) {
                    if (c.getText().toLowerCase().startsWith("brigham and women's hospital"))
                        continue;
                    c.textProperty().bind(Translator.getTranslator().getTranslationBinding(c.getText()));
                }
            }
        }
    }

    private static void getAllChildren(Parent root, List<Node> children)
    {
        if(root == null || children == null)
            return;
        for(Node node : root.getChildrenUnmodifiable())
        {
            children.add(node);
            if(node instanceof Parent)
            {
                getAllChildren((Parent) node, children);
            }
            //Should not be else
            if(node instanceof ButtonBar)
            {
                children.addAll(((ButtonBar)node).getButtons());
            }

//            if(node instanceof TreeView)
//            {
//                TreeView<?> treeView = (TreeView<?>) node;
//                if(treeView.get)
//                //TreeView<String> treeView = (TreeView<String>) node;
//                treeView.getChildrenUnmodifiable()
//            }
        }
    }

    public static void bindTreeItem(TreeItem<String> item, boolean recursive)
    {
        //Can bind tree items, but text cannot be dynamically changed. This could be fixed by adding a custom item type
        if(item == null)
            return;

        item.valueProperty().bind(Translator.getTranslator().getTranslationBinding(item.getValue()));

        for(TreeItem<String> child : item.getChildren())
        {
            child.valueProperty().bind(Translator.getTranslator().getTranslationBinding(child.getValue()));
        }

        item.getChildren().addListener((ListChangeListener<TreeItem<?>>) c -> {
            while(c.next()) {} //get all changes this needs to stay like this!!!

            for(TreeItem<String> child : item.getChildren())
            {
                if(recursive) {
                    bindTreeItem(child, true);
                }
                else
                {
                    if(!child.valueProperty().isBound())
                        child.valueProperty().bind(Translator.getTranslator().getTranslationBinding(child.getValue()));
                }
            }
        });
    }

    public void loadDefault() throws IOException {
        this.switchScene("/edu/wpi/cs3733/D21/teamF/fxml/DefaultPageView.fxml");
    }

    public AbsController getController() {return controller; }

    public void setController(AbsController controller) { this.controller = controller; }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
