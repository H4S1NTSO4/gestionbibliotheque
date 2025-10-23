package com.bibliothecaire.gestionbibliotheque;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class HelloController {

    @FXML
    private VBox sideMenu;

    @FXML
    private Button toggleMenuBtn;

    @FXML
    private FontIcon toggleIcon;

    @FXML
    private StackPane contentArea;

    @FXML
    private VBox menuContainer;

    @FXML
    private Button btnDashboard, btnVisiteurs, btnMembres, btnLivres, btnVentes, btnEmprunts, btnRapports, btnParametres, btnDeconnexion;

    private boolean menuExpanded = true;

    private Map<Button, HBox> fullGraphics = new HashMap<>();
    private Map<Button, FontIcon> iconOnlyGraphics = new HashMap<>();
    private Button currentActiveButton = null;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur...");

        // Vérifier que tous les boutons sont bien injectés
        if (btnDashboard == null) System.out.println("btnDashboard est null !");
        if (btnVisiteurs == null) System.out.println("btnVisiteurs est null !");
        if (btnMembres == null) System.out.println("btnMembres est null !");
        if (btnLivres == null) System.out.println("btnLivres est null !");
        if (btnVentes == null) System.out.println("btnVentes est null !");
        if (btnEmprunts == null) System.out.println("btnEmprunts est null !");
        if (btnRapports == null) System.out.println("btnRapports est null !");
        if (btnParametres == null) System.out.println("btnParametres est null !");
        if (btnDeconnexion == null) System.out.println("btnDeconnexion est null !");

        // Configuration des boutons du menu
        setupMenuButton(btnDashboard, "fas-th-large", "Tableau de bord");
        setupMenuButton(btnVisiteurs, "fas-eye", "Visiteurs");
        setupMenuButton(btnMembres, "fas-users", "Membres");
        setupMenuButton(btnLivres, "fas-book", "Livres");
        setupMenuButton(btnVentes, "fas-shopping-cart", "Ventes");
        setupMenuButton(btnEmprunts, "fas-exchange-alt", "Emprunts");
        setupMenuButton(btnRapports, "fas-chart-line", "Rapports");
        setupMenuButton(btnParametres, "fas-cog", "Paramètres");
        setupMenuButton(btnDeconnexion, "fas-sign-out-alt", "Déconnexion");

        // Stocker les graphiques
        storeButtonGraphics();

        // Activer Livres par défaut
        setActiveMenu(btnDashboard);

        System.out.println("Initialisation terminée !");
    }

    private void setupMenuButton(Button button, String iconLiteral, String title) {
        if (button == null) {
            System.out.println("Erreur: bouton null pour " + title);
            return;
        }

        try {
            // Créer l'icône
            FontIcon icon = new FontIcon(iconLiteral);
            icon.getStyleClass().add("menu-icon");

            // Créer le label
            Label label = new Label(title);
            label.getStyleClass().add("menu-label");

            // HBox pour contenir icône + label
            HBox content = new HBox(15, icon, label);
            content.setAlignment(Pos.CENTER_LEFT);

            // Définir le graphique
            button.setGraphic(content);
            button.setText("");

            // Ajouter un tooltip
            Tooltip tooltip = new Tooltip(title);
            button.setTooltip(tooltip);

            System.out.println("Bouton configuré: " + title);
        } catch (Exception e) {
            System.out.println("Erreur lors de la configuration du bouton " + title + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void storeButtonGraphics() {
        Button[] buttons = {btnDashboard, btnVisiteurs, btnMembres, btnLivres, btnVentes, btnEmprunts, btnRapports, btnParametres, btnDeconnexion};

        for (Button btn : buttons) {
            if (btn != null && btn.getGraphic() instanceof HBox) {
                HBox fullGraphic = (HBox) btn.getGraphic();
                fullGraphics.put(btn, fullGraphic);

                // Extraire l'icône
                if (!fullGraphic.getChildren().isEmpty() && fullGraphic.getChildren().get(0) instanceof FontIcon) {
                    FontIcon originalIcon = (FontIcon) fullGraphic.getChildren().get(0);
                    FontIcon iconCopy = new FontIcon(originalIcon.getIconLiteral());
                    iconCopy.getStyleClass().add("menu-icon");
                    iconOnlyGraphics.put(btn, iconCopy);
                }
            }
        }
    }

    @FXML
    protected void toggleMenu() {
        if (menuExpanded) {
            // Réduire le menu à 80px - afficher uniquement les icônes
            sideMenu.setPrefWidth(80);
            sideMenu.setMinWidth(80);
            sideMenu.setMaxWidth(80);

            // Réduire le padding du conteneur
            menuContainer.setStyle("-fx-padding: 20 5; -fx-spacing: 2;");

            // Changer les boutons pour afficher uniquement les icônes
            Button[] buttons = {btnDashboard, btnVisiteurs, btnMembres, btnLivres, btnVentes, btnEmprunts, btnRapports, btnParametres, btnDeconnexion};
            for (Button btn : buttons) {
                if (btn != null && iconOnlyGraphics.containsKey(btn)) {
                    btn.setGraphic(iconOnlyGraphics.get(btn));
                    btn.setAlignment(Pos.CENTER);
                    btn.setPrefWidth(60);
                    btn.setMaxWidth(60);
                    // Ajouter une classe CSS spéciale pour le mode icône seule
                    btn.getStyleClass().add("menu-item-icon-only");
                }
            }

            menuExpanded = false;
        } else {
            // Étendre le menu à 240px - afficher icônes + textes
            sideMenu.setPrefWidth(240);
            sideMenu.setMinWidth(240);
            sideMenu.setMaxWidth(240);

            // Restaurer le padding du conteneur
            menuContainer.setStyle("-fx-padding: 20 10; -fx-spacing: 2;");

            // Restaurer les graphiques complets
            Button[] buttons = {btnDashboard, btnVisiteurs, btnMembres, btnLivres, btnVentes, btnEmprunts, btnRapports, btnParametres, btnDeconnexion};
            for (Button btn : buttons) {
                if (btn != null && fullGraphics.containsKey(btn)) {
                    btn.setGraphic(fullGraphics.get(btn));
                    btn.setAlignment(Pos.CENTER_LEFT);
                    btn.setPrefWidth(Region.USE_COMPUTED_SIZE);
                    btn.setMaxWidth(Double.MAX_VALUE);
                    // Retirer la classe CSS du mode icône seule
                    btn.getStyleClass().remove("menu-item-icon-only");
                }
            }

            menuExpanded = true;
        }
    }

    private void setActiveMenu(Button activeButton) {
        // Retirer la classe active de tous les boutons
        Button[] buttons = {btnDashboard, btnVisiteurs, btnMembres, btnLivres, btnVentes, btnEmprunts, btnRapports, btnParametres, btnDeconnexion};
        for (Button btn : buttons) {
            if (btn != null) {
                btn.getStyleClass().remove("menu-item-active");
            }
        }

        // Ajouter la classe active au bouton sélectionné
        if (activeButton != null) {
            activeButton.getStyleClass().add("menu-item-active");
            currentActiveButton = activeButton;
        }
    }

    @FXML
    protected void showDashboard() {
        setActiveMenu(btnDashboard);
        updateContentMessage("Tableau de bord", "Vue d'ensemble de votre bibliothèque");
    }

    @FXML
    protected void showVisiteurs() {
        setActiveMenu(btnVisiteurs);
        updateContentMessage("Visiteurs", "Gestion des visiteurs de la bibliothèque");
    }

    @FXML
    protected void showMembres() {
        setActiveMenu(btnMembres);
        updateContentMessage("Membres", "Gestion des membres inscrits");
    }

    @FXML
    protected void showLivres() {
        setActiveMenu(btnLivres);
        updateContentMessage("Livres", "Catalogue complet de la bibliothèque");
    }

    @FXML
    protected void showVentes() {
        setActiveMenu(btnVentes);
        updateContentMessage("Ventes", "Gestion des ventes de livres");
    }

    @FXML
    protected void showEmprunts() {
        setActiveMenu(btnEmprunts);
        updateContentMessage("Emprunts", "Gestion des emprunts et retours");
    }

    @FXML
    protected void showRapports() {
        setActiveMenu(btnRapports);
        updateContentMessage("Rapports", "Statistiques et analyses");
    }

    @FXML
    protected void showParametres() {
        setActiveMenu(btnParametres);
        updateContentMessage("Paramètres", "Configuration de l'application");
    }

    @FXML
    protected void deconnexion() {
        System.out.println("Déconnexion...");
        // Logique de déconnexion à implémenter
    }

    private void updateContentMessage(String title, String description) {
        // Créer une vue temporaire avec le message
        VBox messageView = new VBox(30);
        messageView.setAlignment(Pos.CENTER);
        messageView.setStyle("-fx-padding: 60;");

        // Icône
        FontIcon icon = new FontIcon("fas-info-circle");
        icon.setIconSize(80);
        icon.setIconColor(javafx.scene.paint.Color.web("#6366f1"));

        // Textes
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #6b7280;");

        VBox textBox = new VBox(10, titleLabel, descLabel);
        textBox.setAlignment(Pos.CENTER);

        messageView.getChildren().addAll(icon, textBox);

        // Remplacer le contenu
        contentArea.getChildren().setAll(messageView);
    }
}