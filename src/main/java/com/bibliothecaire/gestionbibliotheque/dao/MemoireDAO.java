package com.bibliothecaire.gestionbibliotheque.dao;

import com.bibliothecaire.gestionbibliotheque.config.DatabaseConfig;
import com.bibliothecaire.gestionbibliotheque.model.Memoire;
import com.bibliothecaire.gestionbibliotheque.repository.MemoireRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemoireDAO implements MemoireRepository {
  private Connection getConnection() throws SQLException {
    return DatabaseConfig.getConnection();
  }

  private Memoire mapResultSetToMemoire(ResultSet rs) throws SQLException {
    return new Memoire(
            rs.getInt("memoire_id"),
            rs.getString("matricule_etudiant"),
            rs.getString("titre_depot"),
            rs.getDate("date_depot").toLocalDate(),
            rs.getString("cycle"),
            rs.getString("mention"),
            rs.getString("chemin_fichier")
    );
  }

  @Override
  public Memoire findByMemoireId(int memoireId) {
    String sql = "SELECT * FROM memoire WHERE memoire_id = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, memoireId);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToMemoire(rs);
        }
      }
    } catch (SQLException e) {
      System.err.println("Erreur lors de la recherche du memoire  " + memoireId);
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public List<Memoire> findAll() {
    List<Memoire> memoires = new ArrayList<>();
    String sql = "SELECT * FROM memoire ORDER BY date_depot DESC";

    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        memoires.add(mapResultSetToMemoire(rs));
      }
    } catch (SQLException e) {
      System.err.println("Erreur pour lister les memoires");
      e.printStackTrace();
    }
    return memoires;
  }

  @Override
  public List<Memoire> findByMention(String mention) {
    List<Memoire> memoires = new ArrayList<>();

    String sql = "SELECT * FROM memoire WHERE mention = ? ORDER BY date_depot DESC";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, mention);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          memoires.add(mapResultSetToMemoire(rs));
        }
      }
    } catch (SQLException e) {
      System.err.println("Erreur lors de la recherche des memoires par mention: " + mention);
      e.printStackTrace();
    }
    return memoires;
  }

  @Override
  public List<Memoire> findByCycle(String cycle) {
    List<Memoire> memoires = new ArrayList<>();

    String sql = "SELECT * FROM memoire WHERE cycle = ? ORDER BY date_depot DESC";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, cycle);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          memoires.add(mapResultSetToMemoire(rs));
        }
      }
    } catch (SQLException e) {
      System.err.println("Erreur lors de la recherche des memoires par cycle: " + cycle);
      e.printStackTrace();
    }
    return memoires;
  }
}
