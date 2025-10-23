package com.bibliothecaire.gestionbibliotheque.repository;

import com.bibliothecaire.gestionbibliotheque.model.Visite;

import java.util.List;

public interface VisiteRepository {
    boolean save(Visite visite);
    Visite findByVisiteId(int visiteId);
    boolean updateHeureSortie(int visiteId);
    List<Visite> findVisiteEnCours();
    List<Visite> findAllCompletedVisites();
}
