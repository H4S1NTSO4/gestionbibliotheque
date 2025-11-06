package com.bibliothecaire.gestionbibliotheque.dao;

import com.bibliothecaire.gestionbibliotheque.config.DatabaseConfig;
import com.bibliothecaire.gestionbibliotheque.model.Mention;
import com.bibliothecaire.gestionbibliotheque.repository.MentionRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MentionDAO implements MentionRepository {

  private Connection getConnection() throws SQLException {
    return DatabaseConfig.getConnection();
  }

  private Mention mapResultSetToMention(ResultSet rs) throws SQLException {
    return new Mention(
            rs.getString("mention_id"),
            rs.getString("nom_mention")
    );
  }

  @Override
  public Mention findById(String mentionId) {
    String sql = "SELECT mention_id, nom_mention FROM mention WHERE mention_id = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, mentionId);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToMention(rs);
        }
      }
    } catch (SQLException e) {
      System.err.println("Erreur lors de la recherche de la mention par ID: " + mentionId);
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public List<Mention> findAll() {
    List<Mention> mentions = new ArrayList<>();
    String sql = "SELECT mention_id, nom_mention FROM mention ORDER BY nom_mention ASC";

    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        mentions.add(mapResultSetToMention(rs));
      }
    } catch (SQLException e) {
      System.err.println("Erreur pour lister toutes les mentions");
      e.printStackTrace();
    }
    return mentions;
  }
}