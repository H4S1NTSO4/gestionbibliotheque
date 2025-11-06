package com.bibliothecaire.gestionbibliotheque.repository;

import com.bibliothecaire.gestionbibliotheque.model.Parcours;

import java.util.List;

public interface ParcoursRepository {
  Parcours findById(String parcoursId);

  List<Parcours> findAll();

  List<Parcours> findByMentionId(String mentionId);
}