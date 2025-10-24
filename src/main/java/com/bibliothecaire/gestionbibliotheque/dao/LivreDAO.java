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

    private Livre mapResultSetToLivre(ResultSet rs) throws SQLException {
        return new Livre(
                rs.getString("reference_livre"),
                rs.getString("titre"),
                rs.getString("auteur"),
                rs.getInt("nb_exemplaire"),
                rs.getInt("nb_exemplaire_disponible"),
                rs.getInt("id_categorie"),
                rs.getBoolean("est_memoire"),
                rs.getObject("memoire_id") != null ? rs.getInt("memoire_id") : null
        );
    }

    @Override
    public Livre findByReferenceLivre(String referenceLivre) {
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
        String sql = "SELECT * FROM livre ORDER BY titre ASC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation de tous les livres.");
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
            stmt.setBoolean(7, false);
            stmt.setNull(8, Types.INTEGER);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'enregistrement du livre: " +  livre.getReferenceLivre());
            e.printStackTrace();
            return false;
        }
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
