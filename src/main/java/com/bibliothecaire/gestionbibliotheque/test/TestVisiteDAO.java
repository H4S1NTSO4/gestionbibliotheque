package com.bibliothecaire.gestionbibliotheque.test;

import com.bibliothecaire.gestionbibliotheque.config.DatabaseConfig;
import com.bibliothecaire.gestionbibliotheque.dao.VisiteDAO;
import com.bibliothecaire.gestionbibliotheque.model.Visite;
import com.bibliothecaire.gestionbibliotheque.repository.VisiteRepository;

import java.util.List;

public class TestVisiteDAO {
  public static void main(String[] args) {
    System.out.println("Test de la connexion");
    DatabaseConfig.testConnection();

    VisiteRepository visiteRepository = new VisiteDAO();
    System.out.println("Demarrage des tests VisiteDAO");

    //testFindByVisiteId(visiteRepository, 5);
    //testFindVisiteEnCours(visiteRepository);
    //testSave(visiteRepository, "E001");
    //testFindVisiteTermine(visiteRepository);
    //testUpdateHeureSortie(visiteRepository, 9);
  }

  private static void testFindByVisiteId(VisiteRepository visiteRepository, int visiteId) {
    System.out.println("\n [Test] findByVisiteId " + visiteId);
    Visite v = visiteRepository.findByVisiteId(visiteId);

    if (v != null) {
      System.out.println("SUCCES: Visite trouve :" + v.getVisiteId() + " " + v.getMatriculeEtudiant() + " " + v.getHeureEntree() + " " + v.getDateVisite() + " " + v.getHeureSortie());
    } else {
      System.out.println("ERREUR: Visite introuvable");
    }
  }

  private static void testSave(VisiteRepository visiteRepository, String matriculeEtudiant) {
    Visite nouvelleVisite = new Visite();
    nouvelleVisite.setMatriculeEtudiant(matriculeEtudiant);

    System.out.println("Insertion dans visite pour l'etudiant " + matriculeEtudiant);
    int idAvant = nouvelleVisite.getVisiteId();
    System.out.println("ID initial avant save:" + idAvant);

    boolean success = visiteRepository.save(nouvelleVisite);
    int idApres = nouvelleVisite.getVisiteId();

    if (success) {
      System.out.println("✅ SUCCES: Visite enregistre");
      System.out.println("ID genere : " + idApres);
      System.out.println(nouvelleVisite.getMatriculeEtudiant() + " " + nouvelleVisite.getDateVisite() + " " + nouvelleVisite.getHeureEntree());

      if (idApres > idAvant && nouvelleVisite.getDateVisite() != null && nouvelleVisite.getHeureEntree() != null) {
        System.out.println("✅ Verification ok");
      } else {
        System.out.println("❌ Verification echoue");
      }
    } else {
      System.out.println("❌ Sauvegarde echouee");
    }
  }

  private static void testFindVisiteEnCours(VisiteRepository visiteRepository) {
    System.out.println("\n [Test] findVisiteEnCours (Trouver les visites en cours)");
    List<Visite> visitesEnCours = visiteRepository.findVisiteEnCours();

    if (visitesEnCours != null) {
      System.out.println("SUCCES: Sur la requete des visites en cours");

      if (!visitesEnCours.isEmpty()) {
        System.out.println("Liste des visites en cours ...");
        for (Visite v : visitesEnCours) {
          System.out.println(v.getVisiteId() + " " + v.getDateVisite() + " " + v.getMatriculeEtudiant() + " " + v.getHeureEntree() + " " + v.getHeureSortie());
        }
      } else {
        System.err.println("ECHEC: Liste des visites en cours introuvables");
      }
    }
  }

  private static void testFindVisiteTermine(VisiteRepository visiteRepository) {
    System.out.println("\n [Test] Les historiques des visites terminees");
    List<Visite> visitesTermine = visiteRepository.findAllCompletedVisites();

    if (visitesTermine != null) {
      System.out.println("SUCEES: Sur la requete des visites terminees");

      if (!visitesTermine.isEmpty()) {
        System.out.println("Liste des visites terminees ...");
        for (Visite v : visitesTermine) {
          System.out.println(v.getVisiteId() + " " + v.getDateVisite() + " " + v.getMatriculeEtudiant() + " " + v.getHeureEntree() + " " + v.getHeureSortie());
        }
      } else {
        System.err.println("ECHEC: Liste des visites terminees introuvables");
      }
    }
  }

  private static void testUpdateHeureSortie(VisiteRepository visiteRepository, int visiteId) {
    System.out.println("\n [Test] sur la mise a jour de l'heure de sortie. Cloture de visite :" + visiteId);
    Visite visiteInitiale = visiteRepository.findByVisiteId(visiteId);

    if (visiteInitiale == null) {
      System.out.println("ECHEC : La visite avec l'identifiant : " + visiteId + " n'existe pas");
      return;
    }
    if (visiteInitiale.getHeureSortie() != null) {
      System.err.println("ECHEC Precondition: La visite " + visiteId + " est deja cloturee, test non valide");
      return;
    }

    System.out.println(" PRE-CONDITION OK : Visite trouvee et active.");
    boolean success = visiteRepository.updateHeureSortie(visiteId);

    if (success) {
      Visite visiteFinale = visiteRepository.findByVisiteId(visiteId);

      if (visiteFinale != null && visiteFinale.getHeureSortie() != null) {
        System.out.println("✅ Heure de sortie est mise a jour.");
        System.out.println("Heure de sortie enregistree : " + visiteFinale.getHeureSortie());
      } else {
        System.err.println("ECHEC DE LA VERIFICATION: Mise a jour signale mais heure sortie est toujours NULL");
      }
    } else {
      System.err.println("ECHEC TOTAL : La mise a jour de l'heure de sortie a echouee.");
    }
  }
}
