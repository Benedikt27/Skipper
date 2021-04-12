package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        GridPane gRoot = new GridPane();
        gRoot.setId("pane");
        primaryStage.setTitle("Skipper");
        Scene scene = new Scene(root, 500, 400);
        scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Server.logout();
                Server.disconnect();
            }
        });
    }


    public static void main(String[] args) {

        Thread server = new Thread(new Runnable() {
            @Override
            public void run() {
                Server.connect();
            }
        });

        server.start();
        launch(args);
        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
