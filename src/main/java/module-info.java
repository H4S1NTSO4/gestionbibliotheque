module com.bibliothecaire.gestionbibliotheque {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.bibliothecaire.gestionbibliotheque to javafx.fxml;
    exports com.bibliothecaire.gestionbibliotheque;
}