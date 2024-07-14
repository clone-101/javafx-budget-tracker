package application;

import java.io.IOException;

import backend.Category;
import backend.Preferences;
import backend.Transaction;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        Preferences.initialize(); // initialize preferences
        Category.initialize(); // initialize categories
        Transaction.initializeCSV(); // initialize memory from csv

        // actual scene/stage stuff
        scene = new Scene(loadFXML("Main"));
        stage.getIcons().add(new Image(App.class.getResourceAsStream("icon.png")));
        stage.setResizable(false);
        stage.setTitle("Budget Tracker");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        Controller.shutdown(); // save to csv

        // shutdown program
        super.stop();
        System.exit(0);
    }

    // refreshes main when returning from other pages
    static void setRoot(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = loader.load();
        if (fxml.equals("Main")) {
            Controller controller = loader.getController();
            controller.refresh();
        }
        // Parent root = loadFXML(fxml);

        scene.setRoot(root);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}