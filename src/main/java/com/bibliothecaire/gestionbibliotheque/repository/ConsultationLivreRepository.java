package com.bibliothecaire.gestionbibliotheque.repository;

import com.bibliothecaire.gestionbibliotheque.model.ConsultationLivre;

import java.util.List;

public interface ConsultationLivreRepository {
  boolean save(ConsultationLivre consultationLivre);

  ConsultationLivre findByConsultationId(int consultationId);

  boolean updateHeureRendu(int consultationId);

  List<ConsultationLivre> findAllCompletedConsultation();

  List<ConsultationLivre> findConsultationEnCours();
}
