package com.bibliothecaire.gestionbibliotheque.repository;

import com.bibliothecaire.gestionbibliotheque.model.Etudiant;

import java.util.List;

public interface EtudiantRepository {
  boolean save(Etudiant etudiant);

  Etudiant findByMatricule(String matriculeEtudiant);

  List<Etudiant> findAll();

  boolean update(Etudiant etudiant);

  boolean delete(String matriculeEtudiant);

  int saveAll(List<Etudiant> etudiants);
}
