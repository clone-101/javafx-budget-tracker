package application;

import java.io.IOException;

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
        // FIXME
        backend.Category c1 = new backend.Category("food");
        backend.Category c2 = new backend.Category("entertainment");
        backend.Category c4 = new backend.Category("bills");
        backend.Category c5 = new backend.Category("transportation");
        backend.Category c6 = new backend.Category("shopping");

        scene = new Scene(loadFXML("Main"));
        stage.getIcons().add(new Image(App.class.getResourceAsStream("icon.png")));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}