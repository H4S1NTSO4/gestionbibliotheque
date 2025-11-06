package com.bibliothecaire.gestionbibliotheque.dao;

import com.bibliothecaire.gestionbibliotheque.config.DatabaseConfig;
import com.bibliothecaire.gestionbibliotheque.model.Livre;
import com.bibliothecaire.gestionbibliotheque.repository.LivreRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivreDAO implements LivreRepository {
  private Connection getConnection() throws SQLException {
    return DatabaseConfig.getConnection();
  }

  /**
   * Mappe un ResultSet vers un objet Livre.
   * Doit être mis à jour pour inclure le nom de la catégorie (colonne 'genre' de la jointure).
   */
  private Livre mapResultSetToLivre(ResultSet rs) throws SQLException {
    Livre livre = new Livre(
            rs.getString("reference_livre"),
            rs.getString("titre"),
            rs.getString("auteur"),
            rs.getInt("nb_exemplaire"),
            rs.getInt("nb_exemplaire_disponible"),
            rs.getInt("id_categorie"),
            rs.getBoolean("est_memoire"),
            rs.getObject("memoire_id") != null ? rs.getInt("memoire_id") : null
    );

    // AJOUT CRUCIAL : Mappage du nom de la catégorie (colonne 'genre' de la jointure)
    // Vérifie si la colonne existe dans le ResultSet avant de l'extraire pour ne pas causer d'erreur
    // dans les autres méthodes (comme findByReferenceLivre) qui n'utilisent pas la jointure.
    try {
      String nomCategorie = rs.getString("genre");
      livre.setNomCategorie(nomCategorie);
    } catch (SQLException e) {
      // Cette erreur est ignorée si la colonne 'genre' n'est pas présente (ex: dans findByReferenceLivre)
      // C'est un compromis pour ne pas avoir deux méthodes mapResultSetToLivre.
    }

    return livre;
  }

  @Override
  public Livre findByReferenceLivre(String referenceLivre) {
    // Cette méthode peut rester simple car elle n'a pas besoin du nom de la catégorie pour la logique métier
    String sql = "SELECT * FROM livre WHERE  reference_livre = ?";
    Livre livre = null;

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, referenceLivre);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          livre = mapResultSetToLivre(rs);
        }
      }
    } catch (SQLException ex) {
      System.err.println("Erreur lors de la recherche du livre par reference: " + referenceLivre);
      ex.printStackTrace();
    }
    return livre;
  }

  @Override
  public List<Livre> findAll() {
    List<Livre> livres = new ArrayList<>();

    // REQUÊTE MODIFIÉE : Jointure avec 'categorie_livre' pour obtenir le nom ('genre')
    String sql = "SELECT l.*, c.genre FROM livre l " +
            "JOIN categorie_livre c ON l.id_categorie = c.id_categorie " +
            "ORDER BY l.titre ASC";

    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        // mapResultSetToLivre va maintenant récupérer la colonne 'genre' et la définir
        livres.add(mapResultSetToLivre(rs));
      }
    } catch (SQLException e) {
      System.err.println("❌ Erreur lors de la recuperation de tous les livres.");
      e.printStackTrace();
    }
    return livres;
  }

  @Override
  public boolean save(Livre livre) {
    String sql = "INSERT INTO livre (reference_livre, titre, auteur, nb_exemplaire, nb_exemplaire_disponible, id_categorie, est_memoire, memoire_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, livre.getReferenceLivre());
      stmt.setString(2, livre.getTitre());
      stmt.setString(3, livre.getAuteur());
      int nbExemplairesTotal = livre.getNbExemplaire();
      stmt.setInt(4, nbExemplairesTotal);
      stmt.setInt(5, nbExemplairesTotal);
      stmt.setInt(6, livre.getIdCategorie());
      stmt.setBoolean(7, livre.isEstMemoire()); // Utiliser la valeur du modèle

      if (livre.getMemoireId() != null) {
        stmt.setInt(8, livre.getMemoireId());
      } else {
        stmt.setNull(8, Types.INTEGER);
      }

      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      System.err.println("❌ Erreur lors de l'enregistrement du livre: " + livre.getReferenceLivre());
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public int[] saveAll(List<Livre> livres) {
    String sql = "INSERT INTO livre (reference_livre, titre, auteur, nb_exemplaire, nb_exemplaire_disponible, id_categorie, est_memoire, memoire_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    int[] results = new int[0];
    Connection conn = null;

    try {
      conn = getConnection();
      conn.setAutoCommit(false); // Démarrer la transaction

      try (PreparedStatement stmt = conn.prepareStatement(sql)) {

        for (Livre livre : livres) {
          // Règle 1: nb_exemplaire_disponible = nb_exemplaire
          int nbExemplairesTotal = livre.getNbExemplaire();

          // Règle 2: est_memoire et memoire_id (pris de l'objet Livre, supposé être false/null)

          stmt.setString(1, livre.getReferenceLivre());
          stmt.setString(2, livre.getTitre());
          stmt.setString(3, livre.getAuteur());
          stmt.setInt(4, nbExemplairesTotal);
          stmt.setInt(5, nbExemplairesTotal);
          stmt.setInt(6, livre.getIdCategorie()); // Règle 3: Clé étrangère
          stmt.setBoolean(7, livre.isEstMemoire());

          if (livre.getMemoireId() != null) {
            stmt.setInt(8, livre.getMemoireId());
          } else {
            stmt.setNull(8, Types.INTEGER);
          }

          stmt.addBatch();
        }

        results = stmt.executeBatch();
        conn.commit(); // Valider la transaction en cas de succès
        System.out.println("✅ Insertion par lot réussie de " + livres.size() + " livres.");
        return results;

      } catch (SQLException e) {
        System.err.println("❌ Erreur lors de l'insertion en lot des livres. Tentative de rollback.");
        e.printStackTrace();
        if (conn != null) {
          conn.rollback(); // Annuler la transaction en cas d'erreur
        }
      }

    } catch (SQLException e) {
      System.err.println("Erreur de connexion ou de transaction: " + e.getMessage());
      e.printStackTrace();
    } finally {
      // Rétablir l'auto-commit et fermer la connexion
      if (conn != null) {
        try {
          conn.setAutoCommit(true);
          conn.close();
        } catch (SQLException ex) {
          System.err.println("Erreur lors de la fermeture de la connexion: " + ex.getMessage());
        }
      }
    }
    return results;
  }

  @Override
  public boolean decrementNbExemplaireDisponible(String referenceLivre) {
    String sql = "UPDATE livre SET nb_exemplaire_disponible = nb_exemplaire_disponible - 1 " +
            "WHERE reference_livre = ? AND nb_exemplaire_disponible > 0";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, referenceLivre);
      int rowsAffected = stmt.executeUpdate();

      if (rowsAffected > 0) {
        System.out.println("✅ Nombre d'exemplaires disponibles décrémenté pour: " + referenceLivre);
        return true;
      } else {
        System.err.println("❌ Aucun exemplaire disponible pour: " + referenceLivre);
        return false;
      }
    } catch (SQLException e) {
      System.err.println("❌ Erreur lors de la décrémentation pour: " + referenceLivre);
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean incrementNbExemplaireDisponible(String referenceLivre) {
    String sql = "UPDATE livre SET nb_exemplaire_disponible = nb_exemplaire_disponible + 1 " +
            "WHERE reference_livre = ? AND nb_exemplaire_disponible < nb_exemplaire";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, referenceLivre);
      int rowsAffected = stmt.executeUpdate();

      if (rowsAffected > 0) {
        System.out.println("✅ Nombre d'exemplaires disponibles incrémenté pour: " + referenceLivre);
        return true;
      } else {
        System.err.println("⚠️ Impossible d'incrémenter (limite atteinte) pour: " + referenceLivre);
        return false;
      }
    } catch (SQLException e) {
      System.err.println("❌ Erreur lors de l'incrémentation pour: " + referenceLivre);
      e.printStackTrace();
      return false;
    }
  }
}