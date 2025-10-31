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

    public int saveAll(List<Etudiant> etudiants) {
        String sql = "INSERT INTO etudiant (matricule_etudiant, nom_prenoms, email, telephone) VALUES (?, ?, ?, ?)";
        int count = 0;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Démarrer une transaction

            stmt = conn.prepareStatement(sql);

            for (Etudiant etudiant : etudiants) {
                try {
                    stmt.setString(1, etudiant.getMatriculeEtudiant());
                    stmt.setString(2, etudiant.getNomPrenoms());
                    stmt.setString(3, etudiant.getEmail());
                    stmt.setString(4, etudiant.getTelephone());

                    stmt.addBatch(); // Ajouter au batch
                    count++;

                    // Exécuter le batch tous les 100 éléments (performance)
                    if (count % 100 == 0) {
                        stmt.executeBatch();
                    }
                } catch (SQLException e) {
                    System.err.println("Erreur pour l'étudiant " + etudiant.getMatriculeEtudiant() +
                            ": " + e.getMessage());
                    // Continue avec les autres étudiants
                }
            }

            // Exécuter le reste du batch
            stmt.executeBatch();
            conn.commit(); // Valider la transaction

            return count;

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement en batch");
            e.printStackTrace();

            // Rollback en cas d'erreur
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return 0;
        } finally {
            // Fermer les ressources
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
        String sql = "SELECT * FROM etudiant ORDER BY nom_prenoms ASC";

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

    @Override
    public boolean update(Etudiant etudiant) {
        String sql = "UPDATE etudiant SET nom_prenoms = ?, email = ?, telephone = ? WHERE matricule_etudiant = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, etudiant.getNomPrenoms());
            stmt.setString(2, etudiant.getEmail());
            stmt.setString(3, etudiant.getTelephone());
            stmt.setString(4, etudiant.getMatriculeEtudiant());

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
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
