package com.bibliothecaire.gestionbibliotheque.dao;

import com.bibliothecaire.gestionbibliotheque.config.DatabaseConfig;
import com.bibliothecaire.gestionbibliotheque.model.CategorieLivre;
import com.bibliothecaire.gestionbibliotheque.repository.CategorieLivreRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieLivreDAO implements CategorieLivreRepository {
    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    private CategorieLivre mapResultSetToCategorieLivre(ResultSet rs) throws SQLException {
        return new CategorieLivre(
                rs.getInt("id_categorie"),
                rs.getString("genre")
        );
    }

    @Override
    public boolean save(CategorieLivre categorieLivre) {
        String sql = "INSERT INTO categorie_livre (genre) VALUES (?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, categorieLivre.getGenre());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        categorieLivre.setIdCategorie(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement de la catégorie: " + categorieLivre.getGenre());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CategorieLivre findByIdCategorie(int idCategorie) {
        String sql = "SELECT id_categorie, genre FROM categorie_livre WHERE id_categorie = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCategorie);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategorieLivre(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la catégorie " + idCategorie);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CategorieLivre> findAll() {
        List<CategorieLivre> categorieLivres = new ArrayList<>();
        String sql = "SELECT id_categorie, genre FROM categorie_livre";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categorieLivres.add(mapResultSetToCategorieLivre(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des listes des catégories des livres.");
            e.printStackTrace();
        }
        return categorieLivres;
    }

    @Override
    public boolean isReferencedByLivre(int idCategorie) {
        String sql = "SELECT COUNT(*) FROM livre WHERE id_categorie = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCategorie);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la verification des references pour la categorie: " + idCategorie);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int idCategorie) {
        if (isReferencedByLivre(idCategorie)) {
            System.err.println("❌ ECHEC DE SUPPRESSION: Des livres sont associes a la categorie ID " + idCategorie + " Suppression bloquee.");
            return false;
        }

        String sql = "DELETE FROM categorie_livre WHERE id_categorie = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCategorie);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression de la categorie " + idCategorie);
            e.printStackTrace();
            return false;
        }
    }
}
