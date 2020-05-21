package com.company;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    public static void main(String[] args) {
	    Application.launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Конвертор");
        stage.setScene(scene);
        stage.show();
    }
}