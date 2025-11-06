package com.bibliothecaire.gestionbibliotheque.controller;

import com.bibliothecaire.gestionbibliotheque.model.Etudiant;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class EtudiantDetailsController {

  // Déclaration des FXML IDs (correspondant à etudiantDialogDetails.fxml)
  @FXML
  private Label lblMatricule;
  @FXML
  private Label lblNomPrenoms;
  @FXML
  private Label lblMention;
  @FXML
  private Label lblParcours;
  @FXML
  private Label lblGroupe;
  @FXML
  private Label lblEmail;
  @FXML
  private Label lblTelephone;

  /**
   * Méthode appelée pour définir l'objet Etudiant et afficher ses détails.
   * Cette méthode doit recevoir un objet Etudiant qui a été chargé avec les
   * noms complets (nomMention, nomParcours) par la méthode EtudiantDAO.findByMatricule.
   * * @param etudiant L'étudiant à afficher.
   */
  public void setEtudiant(Etudiant etudiant) {
    if (etudiant != null) {
      lblMatricule.setText(etudiant.getMatriculeEtudiant());
      lblNomPrenoms.setText(etudiant.getNomPrenoms());

      // Affichage du nom complet de la Mention ou de l'ID si le nom est NULL
      String mentionDisplay = etudiant.getNomMention() != null
              ? etudiant.getNomMention()
              : (etudiant.getMentionId() != null ? etudiant.getMentionId() : "N/A");
      lblMention.setText(mentionDisplay);

      // Affichage du nom complet du Parcours ou "N/A"
      lblParcours.setText(etudiant.getNomParcours() != null ? etudiant.getNomParcours() : "N/A");

      // Affichage des autres champs
      lblGroupe.setText(etudiant.getGroupe() != null ? etudiant.getGroupe() : "N/A");
      lblEmail.setText(etudiant.getEmail() != null ? etudiant.getEmail() : "N/A");
      lblTelephone.setText(etudiant.getTelephone() != null ? etudiant.getTelephone() : "N/A");

    } else {
      // Afficher un message si l'étudiant est NULL (ne devrait pas arriver avec la vérification dans EtudiantController)
      lblNomPrenoms.setText("Erreur de chargement des données.");
      lblMatricule.setText("N/A");
    }
  }

  /**
   * Gère l'action du bouton "Fermer".
   */
  @FXML
  private void handleFermer() {
    // Ferme la fenêtre modale
    Stage stage = (Stage) lblMatricule.getScene().getWindow();
    stage.close();
  }
}