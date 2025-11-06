package com.bibliothecaire.gestionbibliotheque.test;

import com.bibliothecaire.gestionbibliotheque.config.DatabaseConfig;
import com.bibliothecaire.gestionbibliotheque.dao.ConsultationLivreDAO;
import com.bibliothecaire.gestionbibliotheque.model.ConsultationLivre;
import com.bibliothecaire.gestionbibliotheque.repository.ConsultationLivreRepository;

import java.util.List;

public class TestConsultationLivreDAO {
  public static void main(String[] args) {
    System.out.println("Test de la connexion");
    DatabaseConfig.testConnection();

    ConsultationLivreRepository consultationLivreRepository = new ConsultationLivreDAO();
    System.out.println("Demarrage des tests ConsultationLivreDAO");

    //testSave(consultationLivreRepository, 10, "GEST-001");
    //findAllCompletedConsultation(consultationLivreRepository);
    //testUpdateHeureRendu(consultationLivreRepository, 4);
  }

  private static void testSave(ConsultationLivreRepository consultationLivreRepository, int visiteId, String referenceLivre) {
    ConsultationLivre nouvelleConsultationLivre = new ConsultationLivre();
    nouvelleConsultationLivre.setVisiteId(visiteId);
    nouvelleConsultationLivre.setReferenceLivre(referenceLivre);

    System.out.println("Insertion de consultation de livre pour la visite " + visiteId);
    boolean success = consultationLivreRepository.save(nouvelleConsultationLivre);

    if (success) {
      System.out.println("✅ SUCCES: Consultation livre enregistree");
    }
  }

  private static void findAllCompletedConsultation(ConsultationLivreRepository consultationLivreRepository) {
    System.out.println("\n Test sur les historiques des consultations terminees");
    List<ConsultationLivre> consultationLivreTerminee = consultationLivreRepository.findAllCompletedConsultation();

    if (consultationLivreTerminee != null) {
      System.out.println("SUCCES: Sur la requete des historiques des consultations terminees");

      if (!consultationLivreTerminee.isEmpty()) {
        System.out.println("Listes des consultations terminees ...");
        for (ConsultationLivre c : consultationLivreTerminee) {
          System.out.println(c.getVisiteId() + " " + c.getReferenceLivre() + " " + c.getHeurePrise() + " " + c.getHeureRendu());
        }
      } else {
        System.err.println("ECHEC: Liste des visites terminees introuvables");
      }
    }
  }

  private static void findConsultationEnCours(ConsultationLivreRepository consultationLivreRepository) {
    System.out.println("\n Test sur les historiques des consultations en cours");
    List<ConsultationLivre> consultationLivreEnCours = consultationLivreRepository.findConsultationEnCours();

    if (consultationLivreEnCours != null) {
      System.out.println("SUCCES: Sur la requete des historiques des consultations en cours");

      if (!consultationLivreEnCours.isEmpty()) {
        System.out.println("Liste des consultations en cours ...");
        for (ConsultationLivre c : consultationLivreEnCours) {
          System.out.println(c.getVisiteId() + " " + c.getReferenceLivre() + " " + c.getHeurePrise() + " " + c.getHeureRendu());
        }
      } else {
        System.err.println("ECHEC: Liste des consultations en cours introuvables");
      }
    }
  }

  private static void testUpdateHeureRendu(ConsultationLivreRepository consultationLivreRepository, int consultationId) {
    System.out.println("\n Test sur la mise a jour de l'heure rendu du livre avec l'identifiant:" + consultationId);
    ConsultationLivre consultationLivreInitiale = consultationLivreRepository.findByConsultationId(consultationId);

    if (consultationLivreInitiale == null) {
      System.out.println("ECHEC: La consultation avec l'identifiant: " + consultationId + " n'existe pas");
      return;
    }
    if (consultationLivreInitiale.getHeureRendu() != null) {
      System.err.println("ECHEC Precondition: La Consultation " + consultationId + " est deja cloturee, test non valide");
      return;
    }

    System.out.println("PRE-CONDITION OK: Consultation trouvee et active");
    boolean success = consultationLivreRepository.updateHeureRendu(consultationId);

    if (success) {
      ConsultationLivre consultationLivreFinale = consultationLivreRepository.findByConsultationId(consultationId);

      if (consultationLivreFinale != null && consultationLivreFinale.getHeureRendu() != null) {
        System.out.println("✅ Heure rendu mis a jour");
        System.out.println("Heure rendu enregistree : " + consultationLivreFinale.getHeureRendu());
      } else {
        System.err.println("ECHEC DE LA VERIFICATION: Mise a jour reussie mais l'heure rendu est toujours NULL");
      }
    } else {
      System.err.println("ECHEC TOTAL : La mise a jour de l'heure rendu a echouee completement");
    }
  }
}
