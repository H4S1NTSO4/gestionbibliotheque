module com.bibliothecaire.gestionbibliotheque {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.bootstrapfx.core;

    opens com.bibliothecaire.gestionbibliotheque to javafx.fxml;
    exports com.bibliothecaire.gestionbibliotheque;

    opens com.bibliothecaire.gestionbibliotheque.controller to javafx.fxml;
    exports com.bibliothecaire.gestionbibliotheque.controller;

    opens com.bibliothecaire.gestionbibliotheque.model to javafx.fxml, javafx.base;
    exports com.bibliothecaire.gestionbibliotheque.model;
}