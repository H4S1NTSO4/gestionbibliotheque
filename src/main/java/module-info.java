module com.bibliothecaire.gestionbibliotheque {
  // ==========================================================
  // MODULES JAVA STANDARD ET JAVA FX
  // ==========================================================
  requires javafx.controls;
  requires javafx.fxml;
  requires java.sql; // Pour la connexion à la base de données (JDBC)

  // ==========================================================
  // MODULES TIERS (Librairies)
  // ==========================================================

  // Modules Apache POI (pour l'import Excel)
  requires org.apache.poi.poi;      // Ajouté : Nécessaire pour les classes POI de base
  requires org.apache.poi.ooxml;    // Déjà présent : Pour les fichiers .xlsx

  // Modules ControlsFX, FormsFX et ValidatorFX (pour l'interface)
  //requires org.controlsfx.controls;     // CORRECTION : Ajout de ControlsFX
  //requires com.dlsc.formsfx;       // CORRECTION : Ajout de FormsFX
  //requires net.synedra.validatorfx;     // CORRECTION : Ajout de ValidatorFX

  // Modules Ikonli
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.ikonli.fontawesome5;
  requires org.kordamp.bootstrapfx.core;

  // ==========================================================
  // ACCÈS (OPENS) ET EXPORTS
  // ==========================================================

  // Ouvrir le modèle pour JavaFX (nécessaire pour PropertyValueFactory)
  opens com.bibliothecaire.gestionbibliotheque.model to javafx.base;

  // Ouvrir le contrôleur et l'application pour FXMLLoader
  opens com.bibliothecaire.gestionbibliotheque to javafx.fxml;
  opens com.bibliothecaire.gestionbibliotheque.controller to javafx.fxml;

  // Ouvrir les utilitaires si FXML ou JavaFX en a besoin (ex: dans les Dialogues)
  opens com.bibliothecaire.gestionbibliotheque.util to javafx.fxml;

  // Exporter les packages principaux
  exports com.bibliothecaire.gestionbibliotheque;
  exports com.bibliothecaire.gestionbibliotheque.controller;
  exports com.bibliothecaire.gestionbibliotheque.model;
  exports com.bibliothecaire.gestionbibliotheque.util;
}