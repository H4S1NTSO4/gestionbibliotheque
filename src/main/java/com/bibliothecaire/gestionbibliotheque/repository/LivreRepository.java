package com.bibliothecaire.gestionbibliotheque.repository;

import com.bibliothecaire.gestionbibliotheque.model.Livre;

import java.util.List;

public interface LivreRepository {
    boolean save(Livre livre);
    Livre findByReferenceLivre(String referenceLivre);
    List<Livre> findAll();
}
