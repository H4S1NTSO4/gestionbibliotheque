package com.bibliothecaire.gestionbibliotheque.repository;

import com.bibliothecaire.gestionbibliotheque.model.CategorieLivre;

import java.util.List;

public interface CategorieLivreRepository {
  boolean save(CategorieLivre categorieLivre);

  CategorieLivre findByIdCategorie(int idCategorie);

  // NOUVEAU : Méthode pour trouver une catégorie par son nom (Genre)
  CategorieLivre findByGenre(String genre);

  List<CategorieLivre> findAll();

  boolean isReferencedByLivre(int idCategorie);

  boolean delete(int idCategorie);
}