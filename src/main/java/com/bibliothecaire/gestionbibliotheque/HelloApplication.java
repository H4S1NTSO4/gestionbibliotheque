package com.bibliothecaire.gestionbibliotheque;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class HelloApplication extends Application {
  public static void main(String[] args) {
    launch();
  }

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("view/main-layout.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);

    // Ajouter BootstrapFX à la scène
    scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

    stage.setTitle("Bibliothèque");
    stage.setScene(scene);
    stage.show();
  }
}