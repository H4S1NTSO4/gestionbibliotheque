package com.bibliothecaire.gestionbibliotheque.dao;

import com.bibliothecaire.gestionbibliotheque.config.DatabaseConfig;
import com.bibliothecaire.gestionbibliotheque.model.ConsultationLivre;
import com.bibliothecaire.gestionbibliotheque.repository.ConsultationLivreRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationLivreDAO implements ConsultationLivreRepository {
  private Connection getConnection() throws SQLException {
    return DatabaseConfig.getConnection();
  }

  private ConsultationLivre mapResultSetToConsultationLivre(ResultSet rs) throws SQLException {
    return new ConsultationLivre(
            rs.getInt("consultation_id"),
            rs.getInt("visite_id"),
            rs.getString("reference_livre"),
            rs.getTime("heure_prise").toLocalTime(),
            rs.getTime("heure_rendu") != null ? rs.getTime("heure_rendu").toLocalTime() : null
    );
  }

  @Override
  public boolean save(ConsultationLivre consultationLivre) {
    String sql = "INSERT INTO consultation_livre (visite_id, reference_livre, heure_prise) VALUES (?, ?, CURRENT_TIME())";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      stmt.setInt(1, consultationLivre.getVisiteId());
      stmt.setString(2, consultationLivre.getReferenceLivre());

      int rowsAffected = stmt.executeUpdate();

      if (rowsAffected > 0) {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            consultationLivre.setConsultationId(generatedKeys.getInt(1));
          }
        }

        // ✅ DÉCRÉMENTATION DU NOMBRE D'EXEMPLAIRES DISPONIBLES
        LivreDAO livreDAO = new LivreDAO();
        boolean decrementSuccess = livreDAO.decrementNbExemplaireDisponible(consultationLivre.getReferenceLivre());

        if (!decrementSuccess) {
          System.err.println("⚠️ Attention: La consultation a été enregistrée mais le nombre d'exemplaires n'a pas pu être décrémenté");
        }

        ConsultationLivre consultationComplete = findByConsultationId(consultationLivre.getConsultationId());
        if (consultationComplete != null) {
          consultationComplete.setHeureRendu(consultationLivre.getHeureRendu());
        }
        return true;
      }
      return false;
    } catch (SQLException e) {
      System.err.println("Erreur lors de l'enregistrement de la consultation du livre");
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean updateHeureRendu(int consultationId) {
    // D'abord, récupérer la référence du livre avant la mise à jour
    ConsultationLivre consultation = findByConsultationId(consultationId);
    if (consultation == null) {
      System.err.println("❌ Consultation introuvable avec ID: " + consultationId);
      return false;
    }

    String sql = "UPDATE consultation_livre SET heure_rendu = CURRENT_TIME() WHERE consultation_id = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, consultationId);

      int rowsAffected = stmt.executeUpdate();

      if (rowsAffected > 0) {
        // ✅ INCRÉMENTATION DU NOMBRE D'EXEMPLAIRES DISPONIBLES
        LivreDAO livreDAO = new LivreDAO();
        boolean incrementSuccess = livreDAO.incrementNbExemplaireDisponible(consultation.getReferenceLivre());

        if (incrementSuccess) {
          System.out.println("✅ Heure rendu mise à jour et exemplaire retourné pour la consultation ID: " + consultationId);
        } else {
          System.out.println("✅ Heure rendu mise à jour mais l'incrémentation a échoué pour la consultation ID: " + consultationId);
        }
        return true;
      }
      return false;
    } catch (SQLException e) {
      System.err.println("❌ Erreur lors de la mise à jour de l'heure rendu pour la consultation ID: " + consultationId);
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public ConsultationLivre findByConsultationId(int consultationId) {
    String sql = "SELECT * FROM consultation_livre WHERE consultation_id = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, consultationId);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToConsultationLivre(rs);
        }
      }
    } catch (SQLException e) {
      System.err.println("Erreur lors de la recherche de la visite numero " + consultationId);
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public List<ConsultationLivre> findConsultationEnCours() {
    List<ConsultationLivre> consultationEnCours = new ArrayList<>();
    String sql = "SELECT * FROM consultation_livre WHERE heure_rendu IS NULL ORDER BY heure_rendu DESC";

    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        consultationEnCours.add(mapResultSetToConsultationLivre(rs));
      }
    } catch (SQLException e) {
      System.err.println("Erreur pour lister les consultations en cours...");
      e.printStackTrace();
    }
    return consultationEnCours;
  }

  @Override
  public List<ConsultationLivre> findAllCompletedConsultation() {
    List<ConsultationLivre> consultationTerminee = new ArrayList<>();

    String sql = "SELECT * FROM consultation_livre WHERE heure_rendu IS NOT NULL ORDER BY consultation_id ASC";

    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        consultationTerminee.add(mapResultSetToConsultationLivre(rs));
      }
    } catch (SQLException e) {
      System.err.println("Erreur pour lister les historiques des consultations");
      e.printStackTrace();
    }
    return consultationTerminee;
  }
}
