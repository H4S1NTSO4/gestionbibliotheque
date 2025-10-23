package com.bibliothecaire.gestionbibliotheque.repository;

import com.bibliothecaire.gestionbibliotheque.model.CategorieLivre;

import java.util.List;

public interface CategorieLivreRepository {
    boolean save(CategorieLivre categorieLivre);
    CategorieLivre findByIdCategorie(int idCategorie);
    List<CategorieLivre> findAll();
    boolean isReferencedByLivre(int idCategorie);
    boolean delete(int idCategorie);
}
