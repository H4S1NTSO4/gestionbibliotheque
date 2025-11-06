package com.bibliothecaire.gestionbibliotheque.repository;

import com.bibliothecaire.gestionbibliotheque.model.Mention;

import java.util.List;

public interface MentionRepository {
  Mention findById(String mentionId);

  List<Mention> findAll();
}