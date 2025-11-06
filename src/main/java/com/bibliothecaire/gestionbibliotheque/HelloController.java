package com.bibliothecaire.gestionbibliotheque;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.HashMap;
import java.util.Map;

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
  private Button btnDashboard, btnVisiteurs, btnMembres, btnLivres, btnConsultationLivre,
          btnMemoire, btnRapports, btnParametres, btnDeconnexion;

  @FXML
  private Button btnVisiteEnCours, btnHistoriqueVisite, btnConsultationEnCours, btnHistoriqueConsultation;

  @FXML
  private VBox visiteursSubmenu, consultationSubmenu;

  private boolean menuExpanded = true;
  private boolean visiteursSubmenuOpen = false;
  private boolean consultationSubmenuOpen = false;

  private Map<Button, HBox> fullGraphics = new HashMap<>();
  private Map<Button, FontIcon> iconOnlyGraphics = new HashMap<>();
  private Button currentActiveButton = null;

  @FXML
  public void initialize() {
    // Menus principaux
    setupMenuButton(btnDashboard, "fas-th-large", "Tableau de bord");
    setupMenuButtonWithArrow(btnVisiteurs, "fas-eye", "Visiteurs");
    setupMenuButton(btnMembres, "fas-users", "Membres");
    setupMenuButton(btnLivres, "fas-book", "Livres");
    setupMenuButtonWithArrow(btnConsultationLivre, "fas-book-reader", "Consultation Livre");
    setupMenuButton(btnMemoire, "fas-graduation-cap", "Mémoire");
    setupMenuButton(btnRapports, "fas-chart-line", "Rapports");
    setupMenuButton(btnParametres, "fas-cog", "Paramètres");
    setupMenuButton(btnDeconnexion, "fas-sign-out-alt", "Déconnexion");

    // Sous-menus
    setupSubMenuButton(btnVisiteEnCours, "Visite en cours");
    setupSubMenuButton(btnHistoriqueVisite, "Historique visite");
    setupSubMenuButton(btnConsultationEnCours, "Consultation en cours");
    setupSubMenuButton(btnHistoriqueConsultation, "Historique consultation");

    storeButtonGraphics();
    setActiveMenu(btnDashboard);
  }

  private void setupMenuButton(Button button, String iconLiteral, String title) {
    if (button == null) return;

    FontIcon icon = new FontIcon(iconLiteral);
    icon.getStyleClass().add("menu-icon");

    Label label = new Label(title);
    label.getStyleClass().add("menu-label");

    HBox content = new HBox(15, icon, label);
    content.setAlignment(Pos.CENTER_LEFT);

    button.setGraphic(content);
    button.setText("");

    Tooltip tooltip = new Tooltip(title);
    button.setTooltip(tooltip);
  }

  private void setupMenuButtonWithArrow(Button button, String iconLiteral, String title) {
    if (button == null) return;

    FontIcon icon = new FontIcon(iconLiteral);
    icon.getStyleClass().add("menu-icon");

    Label label = new Label(title);
    label.getStyleClass().add("menu-label");

    FontIcon arrow = new FontIcon("fas-chevron-down");
    arrow.getStyleClass().add("menu-arrow");

    Region spacer = new Region();
    HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

    HBox content = new HBox(15, icon, label, spacer, arrow);
    content.setAlignment(Pos.CENTER_LEFT);

    button.setGraphic(content);
    button.setText("");

    Tooltip tooltip = new Tooltip(title);
    button.setTooltip(tooltip);
  }

  private void setupSubMenuButton(Button button, String title) {
    if (button == null) return;

    FontIcon bullet = new FontIcon("fas-circle");
    bullet.getStyleClass().add("submenu-bullet");

    Label label = new Label(title);
    label.getStyleClass().add("menu-label-sub");

    HBox content = new HBox(10, bullet, label);
    content.setAlignment(Pos.CENTER_LEFT);

    button.setGraphic(content);
    button.setText("");

    Tooltip tooltip = new Tooltip(title);
    button.setTooltip(tooltip);
  }

  private void storeButtonGraphics() {
    Button[] buttons = {btnDashboard, btnVisiteurs, btnMembres, btnLivres, btnConsultationLivre,
            btnMemoire, btnRapports, btnParametres, btnDeconnexion,
            btnVisiteEnCours, btnHistoriqueVisite, btnConsultationEnCours, btnHistoriqueConsultation};

    for (Button btn : buttons) {
      if (btn != null && btn.getGraphic() instanceof HBox) {
        HBox fullGraphic = (HBox) btn.getGraphic();
        fullGraphics.put(btn, fullGraphic);

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
      sideMenu.setPrefWidth(80);
      sideMenu.setMinWidth(80);
      sideMenu.setMaxWidth(80);

      menuContainer.setStyle("-fx-padding: 20 5; -fx-spacing: 2;");

      // Masquer les sous-menus
      visiteursSubmenu.setVisible(false);
      visiteursSubmenu.setManaged(false);
      consultationSubmenu.setVisible(false);
      consultationSubmenu.setManaged(false);

      Button[] buttons = {btnDashboard, btnVisiteurs, btnMembres, btnLivres, btnConsultationLivre,
              btnMemoire, btnRapports, btnParametres, btnDeconnexion};
      for (Button btn : buttons) {
        if (btn != null && iconOnlyGraphics.containsKey(btn)) {
          btn.setGraphic(iconOnlyGraphics.get(btn));
          btn.setAlignment(Pos.CENTER);
          btn.setPrefWidth(60);
          btn.setMaxWidth(60);
          btn.getStyleClass().add("menu-item-icon-only");
        }
      }

      menuExpanded = false;
    } else {
      sideMenu.setPrefWidth(240);
      sideMenu.setMinWidth(240);
      sideMenu.setMaxWidth(240);

      menuContainer.setStyle("-fx-padding: 20 10; -fx-spacing: 2;");

      Button[] buttons = {btnDashboard, btnVisiteurs, btnMembres, btnLivres, btnConsultationLivre,
              btnMemoire, btnRapports, btnParametres, btnDeconnexion};
      for (Button btn : buttons) {
        if (btn != null && fullGraphics.containsKey(btn)) {
          btn.setGraphic(fullGraphics.get(btn));
          btn.setAlignment(Pos.CENTER_LEFT);
          btn.setPrefWidth(Region.USE_COMPUTED_SIZE);
          btn.setMaxWidth(Double.MAX_VALUE);
          btn.getStyleClass().remove("menu-item-icon-only");
        }
      }

      menuExpanded = true;
    }
  }

  @FXML
  protected void toggleVisiteursSubmenu() {
    if (!menuExpanded) {
      showVisiteurs();
      return;
    }

    visiteursSubmenuOpen = !visiteursSubmenuOpen;
    visiteursSubmenu.setVisible(visiteursSubmenuOpen);
    visiteursSubmenu.setManaged(visiteursSubmenuOpen);

    // Changer l'icône de la flèche
    updateArrowIcon(btnVisiteurs, visiteursSubmenuOpen);
  }

  @FXML
  protected void toggleConsultationSubmenu() {
    if (!menuExpanded) {
      showConsultationLivre();
      return;
    }

    consultationSubmenuOpen = !consultationSubmenuOpen;
    consultationSubmenu.setVisible(consultationSubmenuOpen);
    consultationSubmenu.setManaged(consultationSubmenuOpen);

    // Changer l'icône de la flèche
    updateArrowIcon(btnConsultationLivre, consultationSubmenuOpen);
  }

  private void updateArrowIcon(Button button, boolean isOpen) {
    if (button.getGraphic() instanceof HBox) {
      HBox content = (HBox) button.getGraphic();
      if (content.getChildren().size() >= 4 && content.getChildren().get(3) instanceof FontIcon) {
        FontIcon arrow = (FontIcon) content.getChildren().get(3);
        arrow.setIconLiteral(isOpen ? "fas-chevron-up" : "fas-chevron-down");
      }
    }
  }

  private void setActiveMenu(Button activeButton) {
    Button[] buttons = {btnDashboard, btnVisiteurs, btnMembres, btnLivres, btnConsultationLivre,
            btnMemoire, btnRapports, btnParametres, btnDeconnexion,
            btnVisiteEnCours, btnHistoriqueVisite, btnConsultationEnCours, btnHistoriqueConsultation};
    for (Button btn : buttons) {
      if (btn != null) {
        btn.getStyleClass().remove("menu-item-active");
      }
    }

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
  protected void showVisiteEnCours() {
    setActiveMenu(btnVisiteEnCours);
    updateContentMessage("Visite en cours", "Liste des visites actuellement en cours");
  }

  @FXML
  protected void showHistoriqueVisite() {
    setActiveMenu(btnHistoriqueVisite);
    updateContentMessage("Historique visite", "Historique complet des visites");
  }

  @FXML
  protected void showMembres() {
    setActiveMenu(btnMembres);
    loadView("etudiant.fxml");
  }

  @FXML
  protected void showLivres() {
    setActiveMenu(btnLivres);
    loadView("livre.fxml");
  }

  @FXML
  protected void showConsultationLivre() {
    setActiveMenu(btnConsultationLivre);
    updateContentMessage("Consultation Livre", "Gestion des consultations de livres");
  }

  @FXML
  protected void showConsultationEnCours() {
    setActiveMenu(btnConsultationEnCours);
    updateContentMessage("Consultation en cours", "Liste des consultations actuellement en cours");
  }

  @FXML
  protected void showHistoriqueConsultation() {
    setActiveMenu(btnHistoriqueConsultation);
    updateContentMessage("Historique consultation", "Historique complet des consultations");
  }

  @FXML
  protected void showMemoire() {
    setActiveMenu(btnMemoire);
    updateContentMessage("Mémoire", "Gestion des mémoires universitaires");
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
    // Logique de déconnexion à implémenter
  }

  private void updateContentMessage(String title, String description) {
    VBox messageView = new VBox(30);
    messageView.setAlignment(Pos.CENTER);
    messageView.setStyle("-fx-padding: 60;");

    FontIcon icon = new FontIcon("fas-info-circle");
    icon.setIconSize(80);
    icon.setIconColor(javafx.scene.paint.Color.web("#6366f1"));

    Label titleLabel = new Label(title);
    titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #111827;");

    Label descLabel = new Label(description);
    descLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #6b7280;");

    VBox textBox = new VBox(10, titleLabel, descLabel);
    textBox.setAlignment(Pos.CENTER);

    messageView.getChildren().addAll(icon, textBox);

    contentArea.getChildren().setAll(messageView);
  }

  private void loadView(String fxmlFile) {
    try {
      javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
              getClass().getResource("/com/bibliothecaire/gestionbibliotheque/view/" + fxmlFile)
      );
      javafx.scene.Parent view = loader.load();
      contentArea.getChildren().setAll(view);
    } catch (Exception e) {
      System.err.println("Erreur lors du chargement de la vue: " + fxmlFile);
      e.printStackTrace();
      updateContentMessage("Erreur", "Impossible de charger la vue: " + fxmlFile);
    }
  }
}