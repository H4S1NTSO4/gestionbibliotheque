package com.bibliothecaire.gestionbibliotheque.dao;

import com.bibliothecaire.gestionbibliotheque.config.DatabaseConfig;
import com.bibliothecaire.gestionbibliotheque.model.Etudiant;
import com.bibliothecaire.gestionbibliotheque.repository.EtudiantRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EtudiantDAO implements EtudiantRepository {

  private Connection getConnection() throws SQLException {
    return DatabaseConfig.getConnection();
  }

  /**
   * Mappe le ResultSet à un objet Etudiant.
   *
   * @param withNames Indique si la requête inclut les colonnes 'nom_mention' et 'nom_parcours'.
   */
  private Etudiant mapResultSetToEtudiant(ResultSet rs, boolean withNames) throws SQLException {
    // 1. Utilisez le constructeur à 7 arguments (IDs)
    Etudiant etudiant = new Etudiant(
            rs.getString("matricule_etudiant"),
            rs.getString("nom_prenoms"),
            rs.getString("email"),
            rs.getString("telephone"),
            rs.getString("mention_id"), // ID
            rs.getString("parcours_id"), // ID
            rs.getString("groupe")
    );

    // 2. Tente de récupérer les noms complets SEULEMENT si withNames est true
    if (withNames) {
      try {
        etudiant.setNomMention(rs.getString("nom_mention"));

        String nomParcours = rs.getString("nom_parcours");
        if (nomParcours != null) {
          etudiant.setNomParcours(nomParcours);
        }
      } catch (SQLException ignore) {
        // Ignore l'erreur si la colonne n'existe pas (cela arrivera pour findAll())
      }
    }

    return etudiant;
  }

  // (Ancienne mapResultSetToEtudiant supprimée ou modifiée pour utiliser la nouvelle signature)


  @Override
  public boolean save(Etudiant etudiant) {

    String sql = "INSERT INTO etudiant (matricule_etudiant, nom_prenoms, email, telephone, mention_id, parcours_id, groupe) VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, etudiant.getMatriculeEtudiant());
      stmt.setString(2, etudiant.getNomPrenoms());
      stmt.setString(3, etudiant.getEmail());
      stmt.setString(4, etudiant.getTelephone());
      stmt.setString(5, etudiant.getMentionId());
      if (etudiant.getParcoursId() != null && !etudiant.getParcoursId().isEmpty()) {
        stmt.setString(6, etudiant.getParcoursId());
      } else {
        stmt.setNull(6, java.sql.Types.VARCHAR);
      }
      stmt.setString(7, etudiant.getGroupe());

      int rowsAffected = stmt.executeUpdate();
      return rowsAffected > 0;
    } catch (SQLException e) {
      System.err.println("Erreur lors de l'enregistrement de l'étudiant " + etudiant.getMatriculeEtudiant());
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public int saveAll(List<Etudiant> etudiants) {
    String sql = "INSERT INTO etudiant (matricule_etudiant, nom_prenoms, email, telephone, mention_id, parcours_id, groupe) VALUES (?, ?, ?, ?, ?, ?, ?)";
    int count = 0;

    Connection conn = null;
    PreparedStatement stmt = null;

    try {
      conn = getConnection();
      conn.setAutoCommit(false);

      stmt = conn.prepareStatement(sql);

      for (Etudiant etudiant : etudiants) {
        try {
          stmt.setString(1, etudiant.getMatriculeEtudiant());
          stmt.setString(2, etudiant.getNomPrenoms());
          stmt.setString(3, etudiant.getEmail());
          stmt.setString(4, etudiant.getTelephone());
          stmt.setString(5, etudiant.getMentionId());
          if (etudiant.getParcoursId() != null && !etudiant.getParcoursId().isEmpty()) {
            stmt.setString(6, etudiant.getParcoursId());
          } else {
            stmt.setNull(6, java.sql.Types.VARCHAR);
          }
          stmt.setString(7, etudiant.getGroupe());

          stmt.addBatch();
          count++;

          if (count % 100 == 0) {
            stmt.executeBatch();
          }
        } catch (SQLException e) {
          System.err.println("Erreur pour l'étudiant " + etudiant.getMatriculeEtudiant() +
                  ": " + e.getMessage());
        }
      }

      stmt.executeBatch();
      conn.commit();

      return count;

    } catch (SQLException e) {
      System.err.println("Erreur lors de l'enregistrement en batch");
      e.printStackTrace();

      if (conn != null) {
        try {
          conn.rollback();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
      return 0;
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (conn != null) {
        try {
          conn.setAutoCommit(true);
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public Etudiant findByMatricule(String matriculeEtudiant) {
    // REQUÊTE COMPLÈTE (avec JOIN) : Nécessaire pour obtenir nom_mention et nom_parcours pour le dialogue de modification
    String sql = "SELECT e.*, m.nom_mention, p.nom_parcours " +
            "FROM etudiant e " +
            "JOIN mention m ON e.mention_id = m.mention_id " +
            "LEFT JOIN parcours p ON e.parcours_id = p.parcours_id " +
            "WHERE e.matricule_etudiant = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, matriculeEtudiant);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          // Utilise withNames = true
          return mapResultSetToEtudiant(rs, true);
        }
      }
    } catch (SQLException e) {
      System.err.println("Erreur lors de la recherche de l'étudiant " + matriculeEtudiant);
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public List<Etudiant> findAll() {
    List<Etudiant> etudiants = new ArrayList<>();
    // REQUÊTE SIMPLIFIÉE (sans JOIN) : Optimisée pour le chargement du TableView
    String sql = "SELECT matricule_etudiant, nom_prenoms, email, telephone, mention_id, parcours_id, groupe " +
            "FROM etudiant " +
            "ORDER BY nom_prenoms ASC";

    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        // Utilise withNames = false
        etudiants.add(mapResultSetToEtudiant(rs, false));
      }
    } catch (SQLException e) {
      System.err.println("Erreur lors de la récupération de tous les étudiants.");
      e.printStackTrace();
    }
    return etudiants;
  }

  @Override
  public boolean update(Etudiant etudiant) {
    String sql = "UPDATE etudiant SET nom_prenoms = ?, email = ?, telephone = ?, mention_id = ?, parcours_id = ?, groupe = ? " +
            "WHERE matricule_etudiant = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, etudiant.getNomPrenoms());
      stmt.setString(2, etudiant.getEmail());
      stmt.setString(3, etudiant.getTelephone());

      // NOUVEL ORDRE
      stmt.setString(4, etudiant.getMentionId());
      if (etudiant.getParcoursId() != null && !etudiant.getParcoursId().isEmpty()) {
        stmt.setString(5, etudiant.getParcoursId());
      } else {
        stmt.setNull(5, java.sql.Types.VARCHAR);
      }
      stmt.setString(6, etudiant.getGroupe());

      // WHERE
      stmt.setString(7, etudiant.getMatriculeEtudiant());

      int rowsAffected = stmt.executeUpdate();
      return rowsAffected > 0;
    } catch (SQLException e) {
      System.err.println("Erreur lors de la mise à jour de l'étudiant " + etudiant.getMatriculeEtudiant());
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean delete(String matriculeEtudiant) {
    String sql = "DELETE FROM etudiant WHERE matricule_etudiant = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, matriculeEtudiant);

      int rowsAffected = stmt.executeUpdate();
      return rowsAffected > 0;
    } catch (SQLException e) {
      System.err.println("Erreur lors de la suppression de l'étudiant " + matriculeEtudiant);
      e.printStackTrace();
      return false;
    }
  }
}