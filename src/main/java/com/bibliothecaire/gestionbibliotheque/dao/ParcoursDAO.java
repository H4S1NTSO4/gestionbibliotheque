package com.bibliothecaire.gestionbibliotheque.dao;

import com.bibliothecaire.gestionbibliotheque.config.DatabaseConfig;
import com.bibliothecaire.gestionbibliotheque.model.Parcours;
import com.bibliothecaire.gestionbibliotheque.repository.ParcoursRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParcoursDAO implements ParcoursRepository {

  private Connection getConnection() throws SQLException {
    return DatabaseConfig.getConnection();
  }

  private Parcours mapResultSetToParcours(ResultSet rs) throws SQLException {
    return new Parcours(
            rs.getString("parcours_id"),
            rs.getString("nom_parcours"),
            rs.getString("mention_id") // Clé étrangère
    );
  }

  @Override
  public Parcours findById(String parcoursId) {
    String sql = "SELECT parcours_id, nom_parcours, mention_id FROM parcours WHERE parcours_id = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, parcoursId);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToParcours(rs);
        }
      }
    } catch (SQLException e) {
      System.err.println("Erreur lors de la recherche du parcours par ID: " + parcoursId);
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public List<Parcours> findAll() {
    List<Parcours> parcoursList = new ArrayList<>();
    String sql = "SELECT parcours_id, nom_parcours, mention_id FROM parcours ORDER BY nom_parcours ASC";

    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        parcoursList.add(mapResultSetToParcours(rs));
      }
    } catch (SQLException e) {
      System.err.println("Erreur pour lister tous les parcours");
      e.printStackTrace();
    }
    return parcoursList;
  }

  @Override
  public List<Parcours> findByMentionId(String mentionId) {
    List<Parcours> parcoursList = new ArrayList<>();
    String sql = "SELECT parcours_id, nom_parcours, mention_id FROM parcours WHERE mention_id = ? ORDER BY nom_parcours ASC";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, mentionId);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          parcoursList.add(mapResultSetToParcours(rs));
        }
      }
    } catch (SQLException e) {
      System.err.println("Erreur lors de la recherche des parcours par mention: " + mentionId);
      e.printStackTrace();
    }
    return parcoursList;
  }
}