package com.bibliothecaire.gestionbibliotheque.controller;

import com.bibliothecaire.gestionbibliotheque.dao.MentionDAO;
import com.bibliothecaire.gestionbibliotheque.dao.ParcoursDAO;
import com.bibliothecaire.gestionbibliotheque.model.Etudiant;
import com.bibliothecaire.gestionbibliotheque.model.Mention;
import com.bibliothecaire.gestionbibliotheque.model.Parcours;
import com.bibliothecaire.gestionbibliotheque.repository.MentionRepository;
import com.bibliothecaire.gestionbibliotheque.repository.ParcoursRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EtudiantDialogController {

  // Constante pour la validation simple de l'email
  private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
  // CHAMPS EXISTANTS
  @FXML
  private TextField txtMatricule;
  @FXML
  private TextField txtNomPrenoms;
  @FXML
  private TextField txtEmail;
  @FXML
  private TextField txtTelephone;
  // NOUVEAUX CHAMPS FXML
  @FXML
  private ComboBox<Mention> cbMention;
  @FXML
  private ComboBox<Parcours> cbParcours; // Parcours dépend de la Mention
  @FXML
  private TextField txtGroupe; // Groupe est un simple TextField
  @FXML
  private Button btnAnnuler;
  @FXML
  private Button btnEnregistrer;
  private Etudiant etudiant;
  private boolean confirmed = false;
  // NOUVEAUX REPOSITORIES ET DONNÉES
  private MentionRepository mentionRepository = new MentionDAO(); // Initialisation (supposée)
  private ParcoursRepository parcoursRepository = new ParcoursDAO(); // Initialisation (supposée)
  private ObservableList<Mention> allMentions = FXCollections.observableArrayList();
  private ObservableList<Parcours> allParcours = FXCollections.observableArrayList();

  @FXML
  public void initialize() {
    btnAnnuler.setOnAction(e -> handleAnnuler());
    btnEnregistrer.setOnAction(e -> handleEnregistrer());

    btnEnregistrer.setDisable(true);

    // Validation en temps réel pour les champs texte
    txtMatricule.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
    txtNomPrenoms.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
    txtEmail.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
    txtTelephone.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
    // Validation pour le Groupe
    txtGroupe.textProperty().addListener((obs, oldVal, newVal) -> validateForm());

    // Initialisation et setup des ComboBox
    loadDataAndSetupListeners();
  }

  /**
   * Charge les listes de Mention et Parcours et configure les listeners de dépendance.
   */
  private void loadDataAndSetupListeners() {
    // 1. Charger toutes les Mentions et Parcours
    allMentions.setAll(mentionRepository.findAll());
    allParcours.setAll(parcoursRepository.findAll());

    cbMention.setItems(allMentions);
    cbParcours.setDisable(true);

    // 2. Configurer le CellFactory pour afficher le nom de la Mention/Parcours
    cbMention.setCellFactory(lv -> new ListCell<Mention>() {
      @Override
      protected void updateItem(Mention item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : item.getNomMention());
      }
    });
    cbMention.setButtonCell(new ListCell<Mention>() {
      @Override
      protected void updateItem(Mention item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : item.getNomMention());
      }
    });

    cbParcours.setCellFactory(lv -> new ListCell<Parcours>() {
      @Override
      protected void updateItem(Parcours item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : item.getNomParcours());
      }
    });
    cbParcours.setButtonCell(new ListCell<Parcours>() {
      @Override
      protected void updateItem(Parcours item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : item.getNomParcours());
      }
    });

    // 3. Configurer le listener de dépendance Mention -> Parcours
    setupParcoursListener();

    // 4. Validation immédiate après sélection d'une Mention/Parcours
    cbMention.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
    cbParcours.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
  }

  /**
   * Gère la dépendance entre la ComboBox Mention et la ComboBox Parcours.
   */
  private void setupParcoursListener() {
    cbMention.valueProperty().addListener((obs, oldVal, newMention) -> {
      cbParcours.getSelectionModel().clearSelection();

      if (newMention == null) {
        cbParcours.setItems(FXCollections.emptyObservableList());
        cbParcours.setDisable(true);
      } else {
        // Filtrer les parcours selon le mentionId sélectionné
        List<Parcours> filteredParcours = allParcours.stream()
                .filter(p -> p.getMentionId().equals(newMention.getMentionId()))
                .collect(Collectors.toList());

        cbParcours.setItems(FXCollections.observableArrayList(filteredParcours));
        // Les parcours sont toujours optionnels dans ce contexte
        cbParcours.setDisable(filteredParcours.isEmpty());
      }
      validateForm(); // Re-valider après changement de Mention
    });
  }

  /**
   * Retourne l'étudiant créé/modifié avec les données académiques.
   */
  public Etudiant getEtudiant() {
    if (!confirmed) {
      return null;
    }

    String matricule = txtMatricule.getText().trim();
    String nomPrenoms = txtNomPrenoms.getText().trim();
    String email = txtEmail.getText().trim();
    String telephone = txtTelephone.getText().trim();
    String groupe = txtGroupe.getText().trim();

    // Récupération des IDs des objets sélectionnés
    String mentionId = cbMention.getValue() != null ? cbMention.getValue().getMentionId() : null;
    // Le parcours est optionnel (peut être NULL)
    String parcoursId = cbParcours.getValue() != null ? cbParcours.getValue().getParcoursId() : null;


    // Créer un nouvel objet Etudiant avec le CONSTRUCTEUR à 7 arguments (ou utiliser un constructeur par défaut et les setters)
    Etudiant etudiantResult = new Etudiant(
            matricule,
            nomPrenoms,
            email.isEmpty() ? null : email,
            telephone.isEmpty() ? null : telephone,

            // NOUVEAUX CHAMPS DANS L'ORDRE DU CONSTRUCTEUR CORRIGÉ
            mentionId,
            parcoursId,
            groupe.isEmpty() ? null : groupe
    );

    // Si c'est une modification, s'assurer que l'objet Etudiant existant est mis à jour
    if (this.etudiant != null) {
      etudiantResult.setNomMention(this.etudiant.getNomMention()); // Conserver le nom pour le rafraîchissement
      etudiantResult.setNomParcours(this.etudiant.getNomParcours()); // Conserver le nom pour le rafraîchissement
    }

    return etudiantResult;
  }

  /**
   * Préremplit le formulaire pour la modification.
   */
  public void setEtudiant(Etudiant etudiant) {
    this.etudiant = etudiant;
    if (etudiant != null) {
      // Champs de base
      txtMatricule.setText(etudiant.getMatriculeEtudiant());
      txtMatricule.setDisable(true); // Matricule non modifiable
      txtNomPrenoms.setText(etudiant.getNomPrenoms());
      txtEmail.setText(etudiant.getEmail() == null ? "" : etudiant.getEmail());
      txtTelephone.setText(etudiant.getTelephone() == null ? "" : etudiant.getTelephone());

      // Nouveaux champs académiques
      txtGroupe.setText(etudiant.getGroupe() == null ? "" : etudiant.getGroupe());

      // Sélectionner la Mention
      Optional<Mention> mention = allMentions.stream()
              .filter(m -> m.getMentionId().equals(etudiant.getMentionId()))
              .findFirst();
      mention.ifPresent(cbMention::setValue);

      // Sélectionner le Parcours (dépend de la Mention déjà sélectionnée ci-dessus)
      if (etudiant.getParcoursId() != null) {
        // S'assurer que les éléments du cbParcours ont été filtrés (via le listener de cbMention)
        ObservableList<Parcours> parcoursItems = cbParcours.getItems();
        Optional<Parcours> parcours = parcoursItems.stream()
                .filter(p -> p.getParcoursId().equals(etudiant.getParcoursId()))
                .findFirst();
        parcours.ifPresent(cbParcours::setValue);
      }
    }
    validateForm();
  }

  /**
   * Retourne true si l'utilisateur a confirmé.
   */
  public boolean isConfirmed() {
    return confirmed;
  }

  @FXML
  private void handleEnregistrer() {
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

  //-------------------------------------------------------------------------
  // VALIDATION
  //-------------------------------------------------------------------------

  private boolean isEmailValid(String email) {
    if (email.isEmpty()) {
      return true;
    }
    return email.matches(EMAIL_REGEX);
  }

  /**
   * Vérifie si tous les champs obligatoires sont remplis et les formats respectés.
   * Les champs requis sont: Matricule, Nom/Prénoms, Mention.
   */
  private boolean isFormValid() {
    boolean requiredTextValid = !txtMatricule.getText().trim().isEmpty() &&
            !txtNomPrenoms.getText().trim().isEmpty();

    // Mention est maintenant obligatoire
    boolean mentionSelected = cbMention.getValue() != null;

    // Le Parcours et le Groupe sont optionnels

    boolean emailFormatValid = isEmailValid(txtEmail.getText().trim());

    return requiredTextValid && mentionSelected && emailFormatValid;
  }

  /**
   * Met à jour l'état visuel du formulaire et l'activation du bouton.
   */
  private void validateForm() {
    boolean isMatriculeValid = !txtMatricule.getText().trim().isEmpty();
    boolean isNomPrenomsValid = !txtNomPrenoms.getText().trim().isEmpty();
    boolean isEmailFormatValid = isEmailValid(txtEmail.getText().trim());
    boolean isMentionValid = cbMention.getValue() != null;

    // Appliquer les styles d'erreur
    applyErrorStyle(txtMatricule, isMatriculeValid);
    applyErrorStyle(txtNomPrenoms, isNomPrenomsValid);
    applyErrorStyle(txtEmail, isEmailFormatValid);
    applyErrorStyle(cbMention, isMentionValid);

    // Le bouton est activé uniquement si TOUS les champs requis sont remplis/valides
    btnEnregistrer.setDisable(!isFormValid());
  }

  /**
   * Surcharge pour appliquer le style aux ComboBox.
   */
  private void applyErrorStyle(ComboBox<?> comboBox, boolean isValid) {
    if (isValid) {
      comboBox.getStyleClass().remove("form-field-error");
    } else {
      if (!comboBox.getStyleClass().contains("form-field-error")) {
        comboBox.getStyleClass().add("form-field-error");
      }
    }
  }

  /**
   * Applique ou retire la classe CSS d'erreur sur un champ TextField.
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
   * Affiche un message d'erreur si la validation échoue.
   */
  private void showValidationError() {
    String errorMessage = "Veuillez remplir les champs obligatoires (Matricule, Nom/Prénom, Mention).";

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