package com.bibliothecaire.gestionbibliotheque.controller;

import com.bibliothecaire.gestionbibliotheque.dao.EtudiantDAO;
import com.bibliothecaire.gestionbibliotheque.model.Etudiant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
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

    private EtudiantDAO etudiantDAO;
    private ObservableList<Etudiant> allEtudiants;
    private ObservableList<Etudiant> filteredEtudiants;

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
            etudiantsTable.setItems(filteredEtudiants);
            updateStatistics();
        } catch (Exception e) {
            showError("Erreur de chargement", "Impossible de charger la liste des étudiants.");
            e.printStackTrace();
        }
    }

    private void updateStatistics() {
        int total = filteredEtudiants.size();
        lblTotal.setText("Total : " + total + " étudiant(s)");

        if (total > 0) {
            lblPagination.setText("1-" + Math.min(total, 10) + " sur " + total);
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

        updateStatistics();
    }

    @FXML
    private void handleAjouter() {
        // TODO: Ouvrir un dialog pour ajouter un étudiant
        showInfo("Ajouter un étudiant", "Fonctionnalité en cours de développement.");
    }

    @FXML
    private void handleImport() {
        // TODO: Importer des étudiants depuis un fichier
        showInfo("Importer", "Fonctionnalité en cours de développement.");
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
        // TODO: Pagination
        showInfo("Pagination", "Fonctionnalité en cours de développement.");
    }

    @FXML
    private void handleNextPage() {
        // TODO: Pagination
        showInfo("Pagination", "Fonctionnalité en cours de développement.");
    }

    private void handleModifier(Etudiant etudiant) {
        // TODO: Ouvrir un dialog pour modifier l'étudiant
        showInfo("Modifier", "Modification de l'étudiant : " + etudiant.getNomPrenoms());
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
