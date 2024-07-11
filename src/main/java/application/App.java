package application;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
    public static final String DEFAULT_FILE_PATH = System.getProperty("user.home") + File.separator + "TrackMyFunds"
            + File.separator + "data" + File.separator + "content.csv";

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {

        scene = new Scene(loadFXML("Main"));
        stage.getIcons().add(new Image(App.class.getResourceAsStream("icon.png")));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        Controller.shutdown();
        super.stop();
        System.exit(0);
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {

        String dataDirPath = System.getProperty("app.data.dir", DEFAULT_FILE_PATH);

        File dataDir = new File(dataDirPath);

        // Check if the directory exists, and if not, create it
        if (!dataDir.exists())
            dataDir.mkdirs();
        launch();

    }

}