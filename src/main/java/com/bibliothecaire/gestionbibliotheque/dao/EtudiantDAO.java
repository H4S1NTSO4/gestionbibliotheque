package com.bibliothecaire.gestionbibliotheque.dao;

import com.bibliothecaire.gestionbibliotheque.config.DatabaseConfig;
import com.bibliothecaire.gestionbibliotheque.model.Etudiant;
import com.bibliothecaire.gestionbibliotheque.repository.EtudiantRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EtudiantDAO implements EtudiantRepository {
    // Helper pour établir la connexion
    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    /**
     * Helper pour mapper un ResultSet à un objet Etudiant.
     */
    private Etudiant mapResultSetToEtudiant(ResultSet rs) throws SQLException {
        // Le constructeur de Etudiant a besoin de ces champs dans l'ordre:
        // Etudiant(matriculeEtudiant, nomPrenoms, email, telephone)

        // Note: rs.getString() gère le retour de NULL pour les colonnes email et telephone.
        return new Etudiant(
                rs.getString("matricule_etudiant"),
                rs.getString("nom_prenoms"),
                rs.getString("email"),
                rs.getString("telephone")
        );
    }

    /**
     * Enregistre un nouvel étudiant dans la base de données.
     */
    @Override
    public boolean save(Etudiant etudiant) {
        String sql = "INSERT INTO etudiant (matricule_etudiant, nom_prenoms, email, telephone) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, etudiant.getMatriculeEtudiant());
            stmt.setString(2, etudiant.getNomPrenoms());
            stmt.setString(3, etudiant.getEmail());
            stmt.setString(4, etudiant.getTelephone());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement de l'étudiant " + etudiant.getMatriculeEtudiant());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Récupère un étudiant par son matricule (Clé Primaire).
     */
    @Override
    public Etudiant findByMatricule(String matriculeEtudiant) {
        String sql = "SELECT * FROM etudiant WHERE matricule_etudiant = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, matriculeEtudiant);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEtudiant(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'étudiant " + matriculeEtudiant);
            e.printStackTrace();
        }
        return null; // Retourne null si non trouvé ou en cas d'erreur
    }

    /**
     * Récupère la liste de tous les étudiants.
     */
    @Override
    public List<Etudiant> findAll() {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM etudiant";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                etudiants.add(mapResultSetToEtudiant(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de tous les étudiants.");
            e.printStackTrace();
        }
        return etudiants;
    }
}
