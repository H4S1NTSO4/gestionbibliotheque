package com.bibliothecaire.gestionbibliotheque.dao;

import com.bibliothecaire.gestionbibliotheque.config.DatabaseConfig;
import com.bibliothecaire.gestionbibliotheque.model.Visite;
import com.bibliothecaire.gestionbibliotheque.repository.VisiteRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VisiteDAO implements VisiteRepository {
    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    private Visite mapResultSetToVisite(ResultSet rs) throws SQLException {
       return new Visite(
               rs.getInt("visite_id"),
               rs.getString("matricule_etudiant"),
               rs.getDate("date_visite").toLocalDate(),
               rs.getTime("heure_entree").toLocalTime(),
               rs.getTime("heure_sortie") != null ? rs.getTime("heure_sortie").toLocalTime() : null
       );
    }

    @Override
    public boolean save(Visite visite) {
        String sql = "INSERT INTO visite (matricule_etudiant, date_visite, heure_entree) VALUES (?, CURRENT_DATE(), CURRENT_TIME())";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, visite.getMatriculeEtudiant());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        visite.setVisiteId(generatedKeys.getInt(1));
                    }
                }

                Visite visiteComplete = findByVisiteId(visite.getVisiteId());
                if (visiteComplete != null) {
                    visite.setDateVisite(visiteComplete.getDateVisite());
                    visite.setHeureEntree(visiteComplete.getHeureEntree());
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement de la visite");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateHeureSortie(int visiteId) {
        String sql = "UPDATE visite SET heure_sortie = CURRENT_TIME() WHERE visite_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, visiteId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Heure de sortie mise à jour automatiquement pour Visite ID: " + visiteId);
            }
            return rowsAffected > 0;
        } catch (SQLException e){
            System.err.println("❌ Erreur lors de la mise à jour automatique de l'heure de sortie pour la visite " + visiteId);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Visite findByVisiteId(int visiteId) {
        String sql = "SELECT * FROM visite WHERE visite_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, visiteId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVisite(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la visite numero " + visiteId);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Visite> findVisiteEnCours() {
        List<Visite> visitesEnCours = new ArrayList<>();
        String sql = "SELECT * FROM visite WHERE heure_sortie IS NULL ORDER BY date_visite DESC, heure_entree DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                visitesEnCours.add(mapResultSetToVisite(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur pour lister les visites en cours (heure_sortie IS NULL)");
            e.printStackTrace();
        }
        return visitesEnCours;
    }

    @Override
    public List<Visite> findAllCompletedVisites() {
        List<Visite> visitesTermine = new ArrayList<>();

        String sql = "SELECT * FROM visite WHERE heure_sortie IS NOT NULL ORDER BY date_visite DESC, heure_entree DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                visitesTermine.add(mapResultSetToVisite(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur pour lister les historiques des visites terminees");
            e.printStackTrace();
        }
        return visitesTermine;
    }
}
