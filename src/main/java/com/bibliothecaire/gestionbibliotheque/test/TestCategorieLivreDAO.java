package com.bibliothecaire.gestionbibliotheque.test;

import com.bibliothecaire.gestionbibliotheque.config.DatabaseConfig;
import com.bibliothecaire.gestionbibliotheque.dao.CategorieLivreDAO;
import com.bibliothecaire.gestionbibliotheque.model.CategorieLivre;
import com.bibliothecaire.gestionbibliotheque.repository.CategorieLivreRepository;

import java.util.List;

public class TestCategorieLivreDAO {
  public static void main(String[] args) {
    System.out.println("Test de la Connexion");
    DatabaseConfig.testConnection();

    CategorieLivreRepository categorieLivreRepository = new CategorieLivreDAO();
    System.out.println("Demarrage des tests CategorieLivreDAO");

    //testFindByIdCategorie(categorieLivreRepository, 1);
    //testSave(categorieLivreRepository, "Entrepreneuriat");
    //testFindAll(categorieLivreRepository);
    //testDelete(categorieLivreRepository, 6);
  }

  private static void testFindByIdCategorie(CategorieLivreRepository categorieLivreRepository, int idCategorie) {
    System.out.println("\n [Test] findByIdCategorie(" + idCategorie + ")");
    CategorieLivre c = categorieLivreRepository.findByIdCategorie(idCategorie);

    if (c != null) {
      System.out.println("SUCCES: CategorieLivre trouve: " + c.getIdCategorie() + " " + c.getGenre());
    } else {
      System.out.println("ERREUR: CategorieLivre introuvable");
    }
  }

  private static void testSave(CategorieLivreRepository categorieLivreRepository, String nouveauGenre) {
    CategorieLivre nouvelleCategorie = new CategorieLivre(0, nouveauGenre);
    System.out.println("\n [Test] testSave pour le categorie livre " + nouveauGenre);

    int idAvant = nouvelleCategorie.getIdCategorie();
    boolean success = categorieLivreRepository.save(nouvelleCategorie);
    int idApres = nouvelleCategorie.getIdCategorie();

    if (success & idApres > idAvant) {
      System.out.println("✅ SUCCÈS : Catégorie " + nouveauGenre + " insere avec ID : " + idApres);

      CategorieLivre verifiee = categorieLivreRepository.findByIdCategorie(idApres);

      if (verifiee != null && verifiee.getGenre().equals(nouveauGenre)) {
        System.out.println("✅ VERIFICATION OK");
      } else {
        System.err.println("❌ VERIFICATION ECHOUEE");
      }
    } else if (success & idApres == idAvant) {
      System.err.println("❌ ECHEC PARTIEL");
    } else {
      System.err.println("❌ ERREUR TOTAL");
    }
  }

  private static void testFindAll(CategorieLivreRepository categorieLivreRepository) {
    System.out.println("\n [Test] Recuperation de tous les categories livres");
    List<CategorieLivre> categorieLivres = categorieLivreRepository.findAll();

    if (categorieLivres != null) {
      System.out.println("SUCCES: Les categories des livres sont trouves");

      if (!categorieLivres.isEmpty()) {
        System.out.println("LISTES DES CATEGORIES LIVRES");
        for (CategorieLivre categorieLivre : categorieLivres) {
          System.out.println(categorieLivre.getIdCategorie() + " " + categorieLivre.getGenre());
        }
      }
    } else {
      System.err.println("ECHEC : LISTES INTROUVABLES");
    }
  }

  private static void testDelete(CategorieLivreRepository categorieLivreRepository, int idCategorie) {
    System.out.println("\n Test sur la suppression de la categorie livre numero " + idCategorie);

    boolean failAttempt = categorieLivreRepository.delete(idCategorie);

    if (!failAttempt) {
      System.out.println("SUCCES (Echec attendu). La suppression de la categorie livre " + idCategorie + " a ete bloque, puisqu'elle est utilisee dans livre");
    } else {
      System.out.println("ECHEC (Succes inattendu). La suppression de la categorie livre " + idCategorie + " a ete effectuee, puisqu'il n'y a pas de livre associe");
    }
  }
}
