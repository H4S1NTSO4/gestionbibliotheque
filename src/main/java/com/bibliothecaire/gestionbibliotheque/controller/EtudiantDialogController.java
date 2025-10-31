package com.bibliothecaire.gestionbibliotheque.controller;

import com.bibliothecaire.gestionbibliotheque.model.Etudiant;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

    // Constante pour la validation simple de l'email
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    @FXML
    public void initialize() {
        // Configurer les actions des boutons
        btnAnnuler.setOnAction(e -> handleAnnuler());
        btnEnregistrer.setOnAction(e -> handleEnregistrer());

        // Désactiver le bouton initialement (sera réactivé par validateForm)
        btnEnregistrer.setDisable(true);

        // Validation en temps réel : met à jour le style et l'état du bouton
        txtMatricule.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        txtNomPrenoms.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        txtEmail.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        txtTelephone.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
    }

    /**
     * Préremplit le formulaire pour la modification
     */
    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
        if (etudiant != null) {
            txtMatricule.setText(etudiant.getMatriculeEtudiant());
            // Le matricule est désactivé et non modifiable en mode modification
            txtMatricule.setDisable(true);
            txtNomPrenoms.setText(etudiant.getNomPrenoms());
            // Gérer les valeurs nulles
            txtEmail.setText(etudiant.getEmail() == null ? "" : etudiant.getEmail());
            txtTelephone.setText(etudiant.getTelephone() == null ? "" : etudiant.getTelephone());
        }
        // Valider le formulaire après le pré-remplissage
        validateForm();
    }

    /**
     * Retourne l'étudiant créé/modifié.
     */
    public Etudiant getEtudiant() {
        if (confirmed) {
            String matricule = txtMatricule.getText().trim();
            String nomPrenoms = txtNomPrenoms.getText().trim();
            String email = txtEmail.getText().trim();
            String telephone = txtTelephone.getText().trim();

            // Créer un nouvel objet Etudiant avec les données nettoyées
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
     * Retourne true si l'utilisateur a confirmé.
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    @FXML
    private void handleEnregistrer() {
        // Double vérification de la validation au moment de l'enregistrement
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

    /**
     * Validation simple du format d'email (si fourni).
     */
    private boolean isEmailValid(String email) {
        if (email.isEmpty()) {
            return true; // L'email est optionnel
        }
        return email.matches(EMAIL_REGEX);
    }

    /**
     * Vérifie si tous les champs obligatoires sont remplis et les formats respectés.
     */
    private boolean isFormValid() {
        boolean requiredFieldsValid = !txtMatricule.getText().trim().isEmpty() &&
                !txtNomPrenoms.getText().trim().isEmpty();

        boolean emailFormatValid = isEmailValid(txtEmail.getText().trim());

        return requiredFieldsValid && emailFormatValid;
    }

    /**
     * Applique ou retire la classe CSS d'erreur sur un champ.
     */
    private void applyErrorStyle(TextField field, boolean isValid) {
        if (isValid) {
            field.getStyleClass().remove("form-field-error");
        } else {
            if (!field.getStyleClass().contains("form-field-error")) {
                field.getStyleClass().add("form-field-error");
            }
        }
    }

    /**
     * Met à jour l'état visuel du formulaire et l'activation du bouton.
     */
    private void validateForm() {
        boolean isMatriculeValid = !txtMatricule.getText().trim().isEmpty();
        boolean isNomPrenomsValid = !txtNomPrenoms.getText().trim().isEmpty();
        boolean isEmailFormatValid = isEmailValid(txtEmail.getText().trim());

        // Appliquer les styles d'erreur immédiatement
        applyErrorStyle(txtMatricule, isMatriculeValid);
        applyErrorStyle(txtNomPrenoms, isNomPrenomsValid);
        applyErrorStyle(txtEmail, isEmailFormatValid); // Feedback sur l'email même s'il est optionnel

        // Activer/désactiver le bouton Enregistrer
        btnEnregistrer.setDisable(!isFormValid());
    }

    /**
     * Affiche un message d'erreur si la validation échoue.
     */
    private void showValidationError() {
        String errorMessage = "Veuillez remplir les champs obligatoires (*).";

        if (!isEmailValid(txtEmail.getText().trim()) && !txtEmail.getText().trim().isEmpty()) {
            errorMessage += "\n\nLe format de l'adresse Email est invalide.";
        }

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation");
        alert.setHeaderText("Erreur de validation du formulaire");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    private void closeDialog() {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }
}