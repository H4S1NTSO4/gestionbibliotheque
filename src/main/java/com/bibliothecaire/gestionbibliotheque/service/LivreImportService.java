package com.bibliothecaire.gestionbibliotheque.service;

import com.bibliothecaire.gestionbibliotheque.dao.CategorieLivreDAO;
import com.bibliothecaire.gestionbibliotheque.dao.LivreDAO;
import com.bibliothecaire.gestionbibliotheque.model.CategorieLivre;
import com.bibliothecaire.gestionbibliotheque.model.Livre;
import com.bibliothecaire.gestionbibliotheque.util.ExcelImporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LivreImportService {

  private final LivreDAO livreDAO;
  private final CategorieLivreDAO categorieDAO;

  public LivreImportService() {
    this.livreDAO = new LivreDAO();
    this.categorieDAO = new CategorieLivreDAO();
  }

  /**
   * Importe les livres d'un fichier Excel, applique les règles de gestion et les sauvegarde.
   *
   * @param fichierExcel Le fichier à importer.
   * @return Le nombre de livres insérés avec succès.
   */
  public int importerEtSauvegarderLivres(File fichierExcel) throws IOException, IllegalStateException {

    // 1. Lire les données brutes du fichier Excel
    List<Livre> livresBruts = ExcelImporter.importerLivres(fichierExcel);

    if (livresBruts.isEmpty()) {
      return 0;
    }

    List<Livre> livresAInserer = new ArrayList<>();
    int erreursConversionCategorie = 0;

    // 2. Traitement et application de la Règle 3 (Conversion Catégorie Nom -> ID)
    for (Livre livre : livresBruts) {

      String nomCategorie = livre.getNomCategorie(); // Nom temporairement stocké par l'Importer

      // Règle 3: Trouver l'ID de la catégorie
      CategorieLivre categorie = categorieDAO.findByGenre(nomCategorie);

      if (categorie != null) {
        livre.setIdCategorie(categorie.getIdCategorie());

        // Règle 1 & 2 sont déjà appliquées dans ExcelImporter :
        // livre.setNbExemplaireDisponible(livre.getNbExemplaire());
        // livre.setEstMemoire(false);

        livresAInserer.add(livre);
      } else {
        System.err.println("❌ Catégorie '" + nomCategorie + "' non trouvée pour le livre: " + livre.getTitre());
        erreursConversionCategorie++;
      }
    }

    if (livresAInserer.isEmpty()) {
      throw new IllegalStateException("Aucun livre valide à insérer après la vérification des catégories.");
    }

    // 3. Sauvegarder en lot dans la base de données
    int[] results = livreDAO.saveAll(livresAInserer);

    // Compter les succès (selon JDBC, un succès est souvent représenté par un résultat >= 1)
    int successCount = 0;
    for (int res : results) {
      if (res >= 0) { // SUCCESS_NO_INFO (-2) ou un nombre positif (1)
        successCount++;
      }
    }

    return successCount;
  }
}