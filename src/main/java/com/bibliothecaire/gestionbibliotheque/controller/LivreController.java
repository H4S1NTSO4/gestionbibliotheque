package com.bibliothecaire.gestionbibliotheque.controller;

import com.bibliothecaire.gestionbibliotheque.dao.CategorieLivreDAO;
import com.bibliothecaire.gestionbibliotheque.dao.LivreDAO;
import com.bibliothecaire.gestionbibliotheque.model.CategorieLivre;
import com.bibliothecaire.gestionbibliotheque.model.Livre;
import com.bibliothecaire.gestionbibliotheque.repository.CategorieLivreRepository;
import com.bibliothecaire.gestionbibliotheque.repository.LivreRepository;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import java.util.stream.Collectors;

public class LivreController {

    // FXML IDs pour la gestion du chargement
    @FXML private VBox mainContent;
    @FXML private VBox loadingOverlay;

    // Champs FXML de la TableView
    @FXML private TableView<Livre> livresTable;
    @FXML private TableColumn<Livre, String> colReference;
    @FXML private TableColumn<Livre, String> colTitre;
    @FXML private TableColumn<Livre, String> colAuteur;
    @FXML private TableColumn<Livre, Integer> colExemplaires;
    @FXML private TableColumn<Livre, Integer> colDisponibles;
    // @FXML private TableColumn<Livre, String> colCategorie; <-- SUPPRIMÉ
    @FXML private TableColumn<Livre, String> colEtat;
    @FXML private TableColumn<Livre, Void> colActions;

    // Champs FXML de la barre de recherche et filtres
    @FXML private TextField txtRecherche;
    @FXML private ComboBox<String> cmbCategorieFilter;

    // Champs FXML de la pagination
    @FXML private Label lblTotalLivres;
    @FXML private Label lblPaginationInfo;
    @FXML private Button btnPrevious;
    @FXML private Button btnNext;

    // Repositories et données
    private LivreRepository livreRepository;
    private CategorieLivreRepository categorieRepository;
    private ObservableList<Livre> allLivres;
    private ObservableList<Livre> filteredLivres;

    // Pagination
    private static final int ITEMS_PER_PAGE = 10;
    private int currentPage = 0;
    private int totalPages = 0;

    @FXML
    public void initialize() {
        livreRepository = new LivreDAO();
        categorieRepository = new CategorieLivreDAO();
        allLivres = FXCollections.observableArrayList();
        filteredLivres = FXCollections.observableArrayList();

        if (txtRecherche != null) {
            // Utilise un listener pour déclencher la recherche à chaque frappe
            txtRecherche.textProperty().addListener((obs, oldVal, newVal) -> handleSearch());
        }

        setupTableColumns();
        setupCategoryFilter();
        loadLivres();
    }

    // --- Initialisation et Affichage des Données ---

    private void setupTableColumns() {
        colReference.setCellValueFactory(new PropertyValueFactory<>("referenceLivre"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colAuteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        colExemplaires.setCellValueFactory(new PropertyValueFactory<>("nbExemplaire"));
        colDisponibles.setCellValueFactory(new PropertyValueFactory<>("nbExemplaireDisponible"));
        // colCategorie.setCellValueFactory(new PropertyValueFactory<>("nomCategorie")); <-- SUPPRIMÉ

        colReference.setStyle("-fx-font-weight: 600;");
        // colTitre.setPrefWidth(200); <-- Largueur ajustée dans le FXML

        // --- LOGIQUE POUR LA COLONNE ÉTAT (Disponibilité) ---
        colEtat.setCellValueFactory(data -> {
            // Calcule l'état basé sur le nombre d'exemplaires disponibles
            Integer nbDispo = data.getValue().getNbExemplaireDisponible();
            return new SimpleObjectProperty<>(nbDispo > 0 ? "Disponible" : "Non Disponible");
        });

        colEtat.setCellFactory(column -> new TableCell<Livre, String>() {
            // Le Label affichera le texte et le style coloré
            private final Label statusLabel = new Label();
            private final HBox container = new HBox(statusLabel);

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    // Réinitialiser les classes CSS pour éviter les conflits
                    statusLabel.getStyleClass().removeAll("status-disponible", "status-non-disponible");

                    statusLabel.setText(item);
                    statusLabel.setStyle("-fx-font-weight: 600; -fx-padding: 3 8;"); // Style commun

                    if ("Disponible".equals(item)) {
                        statusLabel.getStyleClass().add("status-disponible");
                    } else {
                        statusLabel.getStyleClass().add("status-non-disponible");
                    }

                    // Centrer le Label dans la cellule
                    container.setAlignment(Pos.CENTER);
                    setGraphic(container);
                }
            }
        });

        // --- Colonne d'actions ---
        colActions.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Livre livre = (Livre) getTableRow().getItem();

                    ContextMenu contextMenu = new ContextMenu();

                    MenuItem modifierItem = new MenuItem("Modifier");
                    modifierItem.setGraphic(new FontIcon("fas-edit"));
                    modifierItem.setOnAction(e -> handleModifier(livre));

                    MenuItem supprimerItem = new MenuItem("Supprimer");
                    supprimerItem.setGraphic(new FontIcon("fas-trash"));
                    supprimerItem.setOnAction(e -> handleSupprimer(livre));

                    contextMenu.getItems().addAll(modifierItem, supprimerItem);

                    Button btnOptions = new Button();
                    btnOptions.getStyleClass().addAll("btn-icon", "action-btn-options");
                    btnOptions.setTooltip(new Tooltip("Actions"));
                    btnOptions.setGraphic(new FontIcon("fas-ellipsis-v"));

                    btnOptions.setOnMouseClicked(event -> {
                        contextMenu.show(btnOptions, event.getScreenX(), event.getScreenY());
                    });

                    HBox actionBox = new HBox(8, btnOptions);
                    actionBox.setAlignment(Pos.CENTER);

                    setAlignment(Pos.CENTER);
                    setGraphic(actionBox);
                }
            }
        });
    }

    private void setupCategoryFilter() {
        ObservableList<String> categories = FXCollections.observableArrayList("Tous");

        Task<List<CategorieLivre>> loadCategoriesTask = new Task<>() {
            @Override
            protected List<CategorieLivre> call() throws Exception {
                return categorieRepository.findAll();
            }
        };

        loadCategoriesTask.setOnSucceeded(e -> {
            loadCategoriesTask.getValue().forEach(cat -> categories.add(cat.getGenre()));

            if (cmbCategorieFilter != null) {
                cmbCategorieFilter.setItems(categories);
                cmbCategorieFilter.getSelectionModel().selectFirst();
            }
        });

        loadCategoriesTask.setOnFailed(e -> {
            // CORRECTION d'erreur : Utilisation de loadCategoriesTask.getException()
            System.err.println("Erreur de chargement des catégories: " + loadCategoriesTask.getException().getMessage());
            cmbCategorieFilter.setDisable(true);
        });

        new Thread(loadCategoriesTask).start();
    }

    private void loadLivres() {
        Task<List<Livre>> loadTask = new Task<>() {
            @Override
            protected List<Livre> call() throws Exception {
                return livreRepository.findAll();
            }
        };

        loadTask.setOnSucceeded(e -> {
            allLivres.setAll(loadTask.getValue());
            handleSearch();
            hideLoading();
        });

        loadTask.setOnFailed(e -> {
            hideLoading();
            // CORRECTION d'erreur : Utilisation de loadTask.getException()
            showError("Erreur de chargement", "Impossible de charger la liste des livres.");
            loadTask.getException().printStackTrace();
        });

        showLoading();
        new Thread(loadTask).start();
    }

    // --- Gestion de la Recherche et des Filtres ---

    @FXML
    private void handleSearch() {
        String searchText = txtRecherche.getText().toLowerCase().trim();

        final String selectedCategory = cmbCategorieFilter != null &&
                cmbCategorieFilter.getSelectionModel().getSelectedItem() != null
                ? cmbCategorieFilter.getSelectionModel().getSelectedItem()
                : "Tous";

        List<Livre> filtered = allLivres.stream()
                .filter(livre -> {
                    // 1. Recherche par mot-clé
                    boolean matchesSearch = livre.getReferenceLivre().toLowerCase().contains(searchText) ||
                            livre.getTitre().toLowerCase().contains(searchText) ||
                            livre.getAuteur().toLowerCase().contains(searchText);

                    // 2. Filtre par catégorie
                    boolean matchesCategory;

                    if ("Tous".equals(selectedCategory)) {
                        matchesCategory = true;
                    } else {
                        // Assurez-vous que Livre.getNomCategorie() existe et est correct
                        matchesCategory = livre.getNomCategorie() != null &&
                                livre.getNomCategorie().equals(selectedCategory);
                    }

                    return matchesSearch && matchesCategory;
                })
                .collect(Collectors.toList());

        filteredLivres.setAll(filtered);
        currentPage = 0;
        updatePagination();
    }

    @FXML
    private void handleCategoryFilter() {
        handleSearch();
    }

    // --- Gestion de la Pagination ---

    private void updatePagination() {
        int total = filteredLivres.size();
        totalPages = (int) Math.ceil((double) total / ITEMS_PER_PAGE);

        if (totalPages == 0) totalPages = 1;
        if (currentPage >= totalPages) currentPage = totalPages - 1;
        if (currentPage < 0) currentPage = 0;

        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, total);

        if (total > 0) {
            List<Livre> pageItems = filteredLivres.subList(startIndex, endIndex);
            livresTable.setItems(FXCollections.observableArrayList(pageItems));
        } else {
            livresTable.setItems(FXCollections.observableArrayList());
        }

        livresTable.refresh();

        if (btnPrevious != null && btnNext != null) {
            btnPrevious.setDisable(currentPage == 0);
            btnNext.setDisable(currentPage >= totalPages - 1 || filteredLivres.isEmpty());
        }

        updateStatistics(total, startIndex, endIndex);
    }

    private void updateStatistics(int total, int startIndex, int endIndex) {
        if (lblTotalLivres != null) {
            lblTotalLivres.setText("Total : " + total + " livre(s)");
        }
        if (lblPaginationInfo != null) {
            if (total > 0) {
                lblPaginationInfo.setText((startIndex + 1) + "-" + endIndex + " sur " + total);
            } else {
                lblPaginationInfo.setText("0 sur 0");
            }
        }
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


    // --- Actions sur les Livres ---

    @FXML
    private void handleAjouter() {
        showInfo("Action", "Ouverture du dialogue d'ajout de livre.");
    }

    private void handleModifier(Livre livre) {
        showInfo("Action", "Ouverture du dialogue de modification pour : " + livre.getTitre());
    }

    private void handleSupprimer(Livre livre) {
        showInfo("Action", "Suppression de : " + livre.getTitre());
    }

    @FXML
    private void handleRefresh() {
        loadLivres();
        if (txtRecherche != null) {
            txtRecherche.clear();
        }
        if (cmbCategorieFilter != null) {
            cmbCategorieFilter.getSelectionModel().selectFirst();
        }
    }

    // --- Méthodes utilitaires (Affichage/Chargement) ---

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

    private void showInfo(String title, String message) {
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
}