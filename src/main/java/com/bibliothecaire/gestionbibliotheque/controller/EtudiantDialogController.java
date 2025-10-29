package com.bibliothecaire.gestionbibliotheque.controller;

import com.bibliothecaire.gestionbibliotheque.model.Etudiant;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EtudiantDialogController {
    @FXML
    private TextField txtMatricule;

    @FXML
    private TextField txtNomPrenoms;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtTelephone;

    @FXML
    private Button btnAnnuler;

    @FXML
    private Button btnEnregistrer;

    private Etudiant etudiant;
    private boolean confirmed = false;

    @FXML
    public void initialize() {
        // Configurer les boutons
        btnAnnuler.setOnAction(e -> handleAnnuler());
        btnEnregistrer.setOnAction(e -> handleEnregistrer());

        // Validation en temps réel (optionnel)
        txtMatricule.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        txtNomPrenoms.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
    }

    /**
     * Préremplit le formulaire pour la modification
     */
    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
        if (etudiant != null) {
            txtMatricule.setText(etudiant.getMatriculeEtudiant());
            txtMatricule.setDisable(true); // Le matricule ne peut pas être modifié
            txtNomPrenoms.setText(etudiant.getNomPrenoms());
            txtEmail.setText(etudiant.getEmail());
            txtTelephone.setText(etudiant.getTelephone());
        }
    }

    /**
     * Retourne l'étudiant créé/modifié
     */
    public Etudiant getEtudiant() {
        if (confirmed) {
            String matricule = txtMatricule.getText().trim();
            String nomPrenoms = txtNomPrenoms.getText().trim();
            String email = txtEmail.getText().trim();
            String telephone = txtTelephone.getText().trim();

            // Gérer les champs vides pour email et téléphone (peuvent être null)
            return new Etudiant(
                    matricule,
                    nomPrenoms,
                    email.isEmpty() ? null : email,
                    telephone.isEmpty() ? null : telephone
            );
        }
        return null;
    }

    /**
     * Retourne true si l'utilisateur a confirmé
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    @FXML
    private void handleEnregistrer() {
        // Validation
        if (!isFormValid()) {
            showValidationError();
            return;
        }

        confirmed = true;
        closeDialog();
    }

    @FXML
    private void handleAnnuler() {
        confirmed = false;
        closeDialog();
    }

    private boolean isFormValid() {
        return !txtMatricule.getText().trim().isEmpty() &&
                !txtNomPrenoms.getText().trim().isEmpty();
    }

    private void validateForm() {
        // Activer/désactiver le bouton Enregistrer selon la validation
        btnEnregistrer.setDisable(!isFormValid());
    }

    private void showValidationError() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.WARNING
        );
        alert.setTitle("Validation");
        alert.setHeaderText("Champs obligatoires manquants");
        alert.setContentText("Veuillez remplir le matricule et le nom/prénoms.");
        alert.showAndWait();
    }

    private void closeDialog() {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }
}
