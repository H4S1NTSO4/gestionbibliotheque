package com.bibliothecaire.gestionbibliotheque.controller;

import com.bibliothecaire.gestionbibliotheque.dao.EtudiantDAO;
import com.bibliothecaire.gestionbibliotheque.model.Etudiant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import java.util.stream.Collectors;

public class EtudiantController {
    @FXML
    private TableView<Etudiant> etudiantsTable;

    @FXML
    private TableColumn<Etudiant, String> colMatricule;

    @FXML
    private TableColumn<Etudiant, String> colNomPrenoms;

    @FXML
    private TableColumn<Etudiant, String> colEmail;

    @FXML
    private TableColumn<Etudiant, String> colTelephone;

    @FXML
    private TableColumn<Etudiant, Void> colActions;

    @FXML
    private TextField searchField;

    @FXML
    private Label lblTotal;

    @FXML
    private Label lblPagination;

    @FXML
    private Button btnPrevious;

    @FXML
    private Button btnNext;

    private EtudiantDAO etudiantDAO;
    private ObservableList<Etudiant> allEtudiants;
    private ObservableList<Etudiant> filteredEtudiants;

    // Pagination
    private static final int ITEMS_PER_PAGE = 10;
    private int currentPage = 0;
    private int totalPages = 0;

    @FXML
    public void initialize() {
        etudiantDAO = new EtudiantDAO();
        allEtudiants = FXCollections.observableArrayList();
        filteredEtudiants = FXCollections.observableArrayList();

        setupTableColumns();
        loadEtudiants();
    }

    private void setupTableColumns() {
        // Configuration des colonnes
        colMatricule.setCellValueFactory(new PropertyValueFactory<>("matriculeEtudiant"));
        colNomPrenoms.setCellValueFactory(new PropertyValueFactory<>("nomPrenoms"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));

        // Style pour les cellules
        colMatricule.setStyle("-fx-font-weight: 600;");

        // Colonne d'actions
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnModifier = new Button();
            private final Button btnSupprimer = new Button();
            private final HBox actionBox = new HBox(8);

            {
                // Bouton Modifier
                btnModifier.getStyleClass().add("btn-icon");
                FontIcon editIcon = new FontIcon("fas-edit");
                editIcon.setIconSize(14);
                btnModifier.setGraphic(editIcon);
                btnModifier.setTooltip(new Tooltip("Modifier"));
                btnModifier.setOnAction(e -> {
                    Etudiant etudiant = getTableView().getItems().get(getIndex());
                    handleModifier(etudiant);
                });

                // Bouton Supprimer
                btnSupprimer.getStyleClass().add("btn-icon");
                FontIcon deleteIcon = new FontIcon("fas-trash");
                deleteIcon.setIconSize(14);
                deleteIcon.setIconColor(javafx.scene.paint.Color.web("#ef4444"));
                btnSupprimer.setGraphic(deleteIcon);
                btnSupprimer.setTooltip(new Tooltip("Supprimer"));
                btnSupprimer.setOnAction(e -> {
                    Etudiant etudiant = getTableView().getItems().get(getIndex());
                    handleSupprimer(etudiant);
                });

                actionBox.getChildren().addAll(btnModifier, btnSupprimer);
                actionBox.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        });
    }

    private void loadEtudiants() {
        try {
            List<Etudiant> etudiants = etudiantDAO.findAll();
            allEtudiants.setAll(etudiants);
            filteredEtudiants.setAll(etudiants);
            currentPage = 0; // Réinitialiser à la première page
            updatePagination();
        } catch (Exception e) {
            showError("Erreur de chargement", "Impossible de charger la liste des étudiants.");
            e.printStackTrace();
        }
    }

    private void updatePagination() {
        // Calculer le nombre total de pages
        totalPages = (int) Math.ceil((double) filteredEtudiants.size() / ITEMS_PER_PAGE);

        if (totalPages == 0) {
            totalPages = 1;
        }

        // S'assurer que currentPage est dans les limites
        if (currentPage >= totalPages) {
            currentPage = totalPages - 1;
        }
        if (currentPage < 0) {
            currentPage = 0;
        }

        // Calculer les indices de début et fin
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredEtudiants.size());

        // Extraire la sous-liste pour la page actuelle
        if (filteredEtudiants.size() > 0) {
            List<Etudiant> pageItems = filteredEtudiants.subList(startIndex, endIndex);
            etudiantsTable.setItems(FXCollections.observableArrayList(pageItems));
        } else {
            etudiantsTable.setItems(FXCollections.observableArrayList());
        }

        updateStatistics();
    }

    private void updateStatistics() {
        int total = filteredEtudiants.size();
        lblTotal.setText("Total : " + total + " étudiant(s)");

        if (total > 0) {
            int startIndex = currentPage * ITEMS_PER_PAGE + 1;
            int endIndex = Math.min((currentPage + 1) * ITEMS_PER_PAGE, total);
            lblPagination.setText(startIndex + "-" + endIndex + " sur " + total);
        } else {
            lblPagination.setText("0 sur 0");
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();

        if (searchText.isEmpty()) {
            filteredEtudiants.setAll(allEtudiants);
        } else {
            List<Etudiant> filtered = allEtudiants.stream()
                    .filter(e ->
                            e.getMatriculeEtudiant().toLowerCase().contains(searchText) ||
                                    e.getNomPrenoms().toLowerCase().contains(searchText) ||
                                    (e.getEmail() != null && e.getEmail().toLowerCase().contains(searchText)) ||
                                    (e.getTelephone() != null && e.getTelephone().toLowerCase().contains(searchText))
                    )
                    .collect(Collectors.toList());
            filteredEtudiants.setAll(filtered);
        }

        currentPage = 0; // Retour à la première page après recherche
        updatePagination();
    }

    @FXML
    private void handleAjouter() {
        try {
            // Charger le FXML du dialog
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/bibliothecaire/gestionbibliotheque/view/etudiantDialogAjout.fxml")
            );
            VBox dialogContent = loader.load();

            // Récupérer le contrôleur
            EtudiantDialogController dialogController = loader.getController();

            // Créer et configurer le dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Ajouter un étudiant");
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.initOwner(etudiantsTable.getScene().getWindow());

            Scene scene = new Scene(dialogContent);
            scene.getStylesheets().add(
                    getClass().getResource("/com/bibliothecaire/gestionbibliotheque/style/dialog.css").toExternalForm()
            );
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            // Afficher et attendre
            dialogStage.showAndWait();

            // Traiter le résultat
            if (dialogController.isConfirmed()) {
                Etudiant nouvelEtudiant = dialogController.getEtudiant();

                // Enregistrer dans la base de données
                boolean success = etudiantDAO.save(nouvelEtudiant);

                if (success) {
                    showSuccess("Succès", "L'étudiant a été ajouté avec succès !");
                    loadEtudiants(); // Recharger la liste
                } else {
                    showError("Erreur", "Impossible d'ajouter l'étudiant. Vérifiez que le matricule n'existe pas déjà.");
                }
            }
        } catch (Exception e) {
            showError("Erreur", "Impossible d'ouvrir le formulaire d'ajout.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleImport() {
        // Sélectionner le fichier Excel
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Importer des étudiants depuis Excel");
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx", "*.xls")
        );

        java.io.File fichier = fileChooser.showOpenDialog(etudiantsTable.getScene().getWindow());

        if (fichier == null) {
            return; // L'utilisateur a annulé
        }

        // Afficher un indicateur de chargement
        javafx.scene.control.Alert loadingAlert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION
        );
        loadingAlert.setTitle("Import en cours");
        loadingAlert.setHeaderText("Veuillez patienter...");
        loadingAlert.setContentText("Lecture du fichier Excel en cours...");
        loadingAlert.show();

        // Effectuer l'import dans un thread séparé pour ne pas bloquer l'UI
        javafx.concurrent.Task<Integer> importTask = new javafx.concurrent.Task<>() {
            @Override
            protected Integer call() throws Exception {
                // Lire le fichier Excel
                List<com.bibliothecaire.gestionbibliotheque.model.Etudiant> etudiants =
                        com.bibliothecaire.gestionbibliotheque.model.ExcelImporter.importerEtudiants(fichier);

                // Enregistrer dans la base de données
                return etudiantDAO.saveAll(etudiants);
            }
        };

        importTask.setOnSucceeded(e -> {
            loadingAlert.close();
            int count = importTask.getValue();

            if (count > 0) {
                showSuccess("Import réussi", count + " étudiant(s) ont été importés avec succès !");
                loadEtudiants(); // Recharger la liste
            } else {
                showError("Aucun import", "Aucun étudiant n'a pu être importé.\nVérifiez le format du fichier.");
            }
        });

        importTask.setOnFailed(e -> {
            loadingAlert.close();
            Throwable exception = importTask.getException();
            showError("Erreur d'import",
                    "Impossible d'importer le fichier :\n" + exception.getMessage());
            exception.printStackTrace();
        });

        // Lancer le thread
        new Thread(importTask).start();
    }

    @FXML
    private void handleRefresh() {
        loadEtudiants();
        searchField.clear();
        showSuccess("Actualisation", "Liste des étudiants actualisée.");
    }

    @FXML
    private void handleExport() {
        // TODO: Exporter la liste des étudiants
        showInfo("Exporter", "Fonctionnalité en cours de développement.");
    }

    @FXML
    private void handlePrint() {
        // TODO: Imprimer la liste
        showInfo("Imprimer", "Fonctionnalité en cours de développement.");
    }

    @FXML
    private void handlePreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            updatePagination();
        }
    }

    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            updatePagination();
        }
    }

    private void handleModifier(Etudiant etudiant) {
        try {
            // Charger le FXML du dialog
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/bibliothecaire/gestionbibliotheque/view/etudiant-dialog.fxml")
            );
            VBox dialogContent = loader.load();

            // Récupérer le contrôleur et pré-remplir avec les données existantes
            EtudiantDialogController dialogController = loader.getController();
            dialogController.setEtudiant(etudiant);

            // Créer et configurer le dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modifier un étudiant");
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.initOwner(etudiantsTable.getScene().getWindow());

            Scene scene = new Scene(dialogContent);
            scene.getStylesheets().add(
                    getClass().getResource("/com/bibliothecaire/gestionbibliotheque/style/dialog.css").toExternalForm()
            );
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            // Afficher et attendre
            dialogStage.showAndWait();

            // Traiter le résultat
            if (dialogController.isConfirmed()) {
                Etudiant etudiantModifie = dialogController.getEtudiant();

                // TODO: Ajouter une méthode update() dans EtudiantDAO
                showInfo("Modification", "Modification de l'étudiant : " + etudiantModifie.getNomPrenoms() +
                        "\n(Ajoutez une méthode update() dans EtudiantDAO pour sauvegarder)");

                // Après avoir implémenté update() dans le DAO :
                // boolean success = etudiantDAO.update(etudiantModifie);
                // if (success) {
                //     showSuccess("Succès", "L'étudiant a été modifié avec succès !");
                //     loadEtudiants();
                // }
            }
        } catch (Exception e) {
            showError("Erreur", "Impossible d'ouvrir le formulaire de modification.");
            e.printStackTrace();
        }
    }

    private void handleSupprimer(Etudiant etudiant) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer l'étudiant ?");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer " + etudiant.getNomPrenoms() + " ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Implémenter la suppression dans le DAO
                showInfo("Suppression", "Suppression de l'étudiant : " + etudiant.getNomPrenoms() + "\n(Fonctionnalité à implémenter dans le DAO)");
            }
        });
    }

    // Méthodes utilitaires pour afficher des messages
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
