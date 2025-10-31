package com.bibliothecaire.gestionbibliotheque.controller;

import com.bibliothecaire.gestionbibliotheque.dao.EtudiantDAO;
import com.bibliothecaire.gestionbibliotheque.model.Etudiant;
import com.bibliothecaire.gestionbibliotheque.repository.EtudiantRepository;
import com.bibliothecaire.gestionbibliotheque.util.ExcelImporter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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

    // FXML IDs pour la gestion du chargement (StackPane)
    @FXML
    private VBox mainContent;
    @FXML
    private VBox loadingOverlay;

    // Champs FXML de la TableView
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

    // Champs FXML de la barre de recherche
    @FXML
    private TextField txtRecherche;

    // Champs FXML de la pagination
    @FXML
    private Label lblTotalEtudiants;
    @FXML
    private Label lblPaginationInfo;

    @FXML
    private Button btnPrevious;
    @FXML
    private Button btnNext;

    // Données et Repositories
    private EtudiantRepository etudiantRepository;
    private ObservableList<Etudiant> allEtudiants;
    private ObservableList<Etudiant> filteredEtudiants;

    // Pagination
    private static final int ITEMS_PER_PAGE = 10;
    private int currentPage = 0;
    private int totalPages = 0;

    @FXML
    public void initialize() {
        etudiantRepository = new EtudiantDAO();
        allEtudiants = FXCollections.observableArrayList();
        filteredEtudiants = FXCollections.observableArrayList();

        if (txtRecherche != null) {
            txtRecherche.textProperty().addListener((obs, oldVal, newVal) -> handleSearch());
        }

        setupTableColumns();
        loadEtudiants();
    }

    private void setupTableColumns() {
        colMatricule.setCellValueFactory(new PropertyValueFactory<>("matriculeEtudiant"));
        colNomPrenoms.setCellValueFactory(new PropertyValueFactory<>("nomPrenoms"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));

        colMatricule.setStyle("-fx-font-weight: 600;");

        // Colonne d'actions : Implémentation du bouton 'trois points' avec ContextMenu
        colActions.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Etudiant etudiant = (Etudiant) getTableRow().getItem();

                    // 1. Création du Menu Contextuel
                    ContextMenu contextMenu = new ContextMenu();

                    // 1.1. Item Modifier
                    MenuItem modifierItem = new MenuItem("Modifier");
                    FontIcon editIcon = new FontIcon("fas-edit");
                    editIcon.setIconSize(14);
                    modifierItem.setGraphic(editIcon);
                    modifierItem.setOnAction(e -> handleModifier(etudiant));

                    // 1.2. Item Supprimer
                    MenuItem supprimerItem = new MenuItem("Supprimer");
                    FontIcon deleteIcon = new FontIcon("fas-trash");
                    deleteIcon.setIconSize(14);
                    deleteIcon.setIconColor(javafx.scene.paint.Color.web("#ef4444"));
                    supprimerItem.setGraphic(deleteIcon);
                    supprimerItem.setOnAction(e -> handleSupprimer(etudiant));

                    contextMenu.getItems().addAll(modifierItem, supprimerItem);

                    // 2. Création du Bouton "Trois Points"
                    Button btnOptions = new Button();
                    btnOptions.getStyleClass().addAll("btn-icon", "action-btn-options");
                    btnOptions.setTooltip(new Tooltip("Actions"));
                    FontIcon optionsIcon = new FontIcon("fas-ellipsis-v");
                    optionsIcon.setIconSize(14);
                    btnOptions.setGraphic(optionsIcon);

                    // 3. Afficher le menu au clic du bouton
                    btnOptions.setOnMouseClicked(event -> {
                        contextMenu.show(btnOptions, event.getScreenX(), event.getScreenY());
                    });

                    // 4. Mettre en place le graphique
                    HBox actionBox = new HBox(8);
                    actionBox.getChildren().add(btnOptions);
                    actionBox.setAlignment(Pos.CENTER);

                    setAlignment(Pos.CENTER);
                    setGraphic(actionBox);
                }
            }
        });
    }

    /**
     * Charge les étudiants de manière asynchrone avec un indicateur de chargement.
     */
    private void loadEtudiants() {
        Task<List<Etudiant>> loadTask = new Task<>() {
            @Override
            protected List<Etudiant> call() throws Exception {
                // Thread.sleep(500); // Simuler la latence si nécessaire
                return etudiantRepository.findAll();
            }
        };

        loadTask.setOnFailed(e -> {
            hideLoading();
            showError("Erreur de chargement", "Impossible de charger la liste des étudiants.");
            loadTask.getException().printStackTrace();
        });

        loadTask.setOnSucceeded(e -> {
            List<Etudiant> etudiants = loadTask.getValue();
            allEtudiants.setAll(etudiants);

            if (txtRecherche != null && !txtRecherche.getText().trim().isEmpty()) {
                handleSearch();
            } else {
                filteredEtudiants.setAll(etudiants);
            }

            currentPage = 0;
            updatePagination();
            hideLoading();
        });

        showLoading();
        new Thread(loadTask).start();
    }

    // Méthodes pour gérer l'overlay de chargement
    private void showLoading() {
        if (loadingOverlay != null && mainContent != null) {
            loadingOverlay.setVisible(true);
            loadingOverlay.setManaged(true);
            mainContent.setDisable(true);
        }
    }

    private void hideLoading() {
        if (loadingOverlay != null && mainContent != null) {
            loadingOverlay.setVisible(false);
            loadingOverlay.setManaged(false);
            mainContent.setDisable(false);
        }
    }


    private void updatePagination() {
        totalPages = (int) Math.ceil((double) filteredEtudiants.size() / ITEMS_PER_PAGE);

        if (totalPages == 0) {
            totalPages = 1;
        }

        if (currentPage >= totalPages) {
            currentPage = totalPages - 1;
        }
        if (currentPage < 0) {
            currentPage = 0;
        }

        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredEtudiants.size());

        if (filteredEtudiants.size() > 0) {
            List<Etudiant> pageItems = filteredEtudiants.subList(startIndex, endIndex);
            etudiantsTable.setItems(FXCollections.observableArrayList(pageItems));
        } else {
            etudiantsTable.setItems(FXCollections.observableArrayList());
        }

        etudiantsTable.refresh();

        if (btnPrevious != null && btnNext != null) {
            btnPrevious.setDisable(currentPage == 0);
            btnNext.setDisable(currentPage >= totalPages - 1 || filteredEtudiants.isEmpty());
        }

        updateStatistics();
    }

    private void updateStatistics() {
        int total = filteredEtudiants.size();

        if (lblTotalEtudiants != null) {
            lblTotalEtudiants.setText("Total : " + total + " étudiant(s)");
        }

        if (total > 0) {
            int startIndex = currentPage * ITEMS_PER_PAGE + 1;
            int endIndex = Math.min((currentPage + 1) * ITEMS_PER_PAGE, total);

            if (lblPaginationInfo != null) {
                lblPaginationInfo.setText(startIndex + "-" + endIndex + " sur " + total);
            }
        } else {
            if (lblPaginationInfo != null) {
                lblPaginationInfo.setText("0 sur 0");
            }
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = txtRecherche.getText().toLowerCase().trim();

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

        currentPage = 0;
        updatePagination();

        etudiantsTable.refresh();
    }

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/bibliothecaire/gestionbibliotheque/view/etudiantDialogAjout.fxml")
            );
            VBox dialogContent = loader.load();
            EtudiantDialogController dialogController = loader.getController();

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

            dialogStage.showAndWait();

            if (dialogController.isConfirmed()) {
                Etudiant nouvelEtudiant = dialogController.getEtudiant();
                boolean success = etudiantRepository.save(nouvelEtudiant);

                if (success) {
                    showSuccess("Succès", "L'étudiant a été ajouté avec succès !");
                    loadEtudiants();
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
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Importer des étudiants depuis Excel");
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx", "*.xls")
        );

        java.io.File fichier = fileChooser.showOpenDialog(etudiantsTable.getScene().getWindow());

        if (fichier == null) {
            return;
        }

        javafx.concurrent.Task<Integer> importTask = new javafx.concurrent.Task<>() {
            @Override
            protected Integer call() throws Exception {
                List<Etudiant> etudiants = ExcelImporter.importerEtudiants(fichier);
                return etudiantRepository.saveAll(etudiants);
            }
        };

        importTask.setOnSucceeded(e -> {
            int count = importTask.getValue();

            if (count > 0) {
                showSuccess("Import réussi", count + " étudiant(s) ont été importés avec succès !");
                loadEtudiants();
            } else {
                showError("Aucun import", "Aucun étudiant n'a pu être importé.\nVérifiez le format du fichier.");
            }
        });

        importTask.setOnFailed(e -> {
            Throwable exception = importTask.getException();
            showError("Erreur d'import",
                    "Impossible d'importer le fichier :\n" + exception.getMessage());
            exception.printStackTrace();
        });

        new Thread(importTask).start();
    }


    @FXML
    private void handleRefresh() {
        loadEtudiants();
        if (txtRecherche != null) {
            txtRecherche.clear();
        }
    }

    @FXML
    private void handleExport() {
        showInfo("Exporter", "Fonctionnalité en cours de développement.");
    }

    @FXML
    private void handlePrint() {
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
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/bibliothecaire/gestionbibliotheque/view/etudiantDialogModifier.fxml")
            );
            VBox dialogContent = loader.load();
            EtudiantDialogController dialogController = loader.getController();
            dialogController.setEtudiant(etudiant);

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

            dialogStage.showAndWait();

            if (dialogController.isConfirmed()) {
                Etudiant etudiantModifie = dialogController.getEtudiant();
                boolean success = etudiantRepository.update(etudiantModifie);

                if (success) {
                    showSuccess("Succès", "L'étudiant a été modifié avec succès !");
                    loadEtudiants();
                } else {
                    showError("Erreur", "Impossible de modifier l'étudiant.");
                }
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
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer " + etudiant.getNomPrenoms() +
                " (Matricule: " + etudiant.getMatriculeEtudiant() + ") ?\n\n" +
                "Cette action est irréversible.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = etudiantRepository.delete(etudiant.getMatriculeEtudiant());

                if (success) {
                    showSuccess("Succès", "L'étudiant a été supprimé avec succès !");
                    loadEtudiants();
                } else {
                    showError("Erreur", "Impossible de supprimer l'étudiant.\n" +
                            "Vérifiez qu'il n'est pas lié à des emprunts en cours.");
                }
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