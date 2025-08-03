package ipo.ipo_lab_1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DominoApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DominoApp.class.getResource("/ipo/ipo_lab_1/board-redesigned2.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Domino Game");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
