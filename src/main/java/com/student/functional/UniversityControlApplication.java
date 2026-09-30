package com.student.functional;

import com.github.spring.boot.javafx.SpringJavaFXApplication;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UniversityControlApplication extends SpringJavaFXApplication {

    public static void main(String[] args) {
        launch(UniversityControlApplication.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        super.start(primaryStage);

        Label label = new Label("JavaFX + Spring Boot работает!");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 400, 200);

        primaryStage.setTitle("Uchet");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}