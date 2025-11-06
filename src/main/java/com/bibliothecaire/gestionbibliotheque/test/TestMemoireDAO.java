package com.bibliothecaire.gestionbibliotheque.test;

import com.bibliothecaire.gestionbibliotheque.config.DatabaseConfig;
import com.bibliothecaire.gestionbibliotheque.dao.MemoireDAO;
import com.bibliothecaire.gestionbibliotheque.model.Memoire;
import com.bibliothecaire.gestionbibliotheque.repository.MemoireRepository;

import java.util.List;

public class TestMemoireDAO {
  public static void main(String[] args) {
    System.out.println("Test de la connexion");
    DatabaseConfig.testConnection();

    MemoireRepository memoireRepository = new MemoireDAO();

    //testFindByMemoireId(memoireRepository, 1);
    //testFindAll(memoireRepository);
    //testFindByMention(memoireRepository, "Droit");
    //testFindByCycle(memoireRepository, "Licence");
  }

  private static void testFindByMemoireId(MemoireRepository memoireRepository, int memoireId) {
    Memoire m = memoireRepository.findByMemoireId(memoireId);

    if (m != null) {
      System.out.println("SUCCES: Memoire trouve:" + m.getMemoireId() + " " + m.getMatriculeEtudiant() + " " + m.getTitreDepot());
    } else {
      System.out.println("ERROR: Memoire non trouve");
    }
  }

  private static void testFindAll(MemoireRepository memoireRepository) {
    List<Memoire> memoires = memoireRepository.findAll();

    if (memoires != null) {
      System.out.println("SUCCES: La requete sur la liste des memoires marchent");

      if (!memoires.isEmpty()) {
        System.out.println("Liste des memoires deposes ...");
        for (Memoire memoire : memoires) {
          System.out.println(memoire.getMatriculeEtudiant() + " " + memoire.getDateDepot());
        }
      } else {
        System.err.println("ERROR: Liste des memoires introuvables");
      }
    }
  }

  private static void testFindByMention(MemoireRepository memoireRepository, String mention) {
    List<Memoire> memoires = memoireRepository.findByMention(mention);

    if (memoires != null) {
      System.out.println("SUCCES: La requete sur la liste des memoires par mention fonctionne");

      if (!memoires.isEmpty()) {
        System.out.println("Liste des memoires par mention ...");
        for (Memoire memoire : memoires) {
          System.out.println(memoire.getMatriculeEtudiant() + " " + memoire.getDateDepot() + " " + memoire.getMention());
        }
      } else {
        System.err.println("ERROR : Liste des memoires par mention introuvable");
      }
    }
  }

  private static void testFindByCycle(MemoireRepository memoireRepository, String cycle) {
    List<Memoire> memoires = memoireRepository.findByCycle(cycle);

    if (memoires != null) {
      System.out.println("SUCCES: La requete sur la liste des memoires par cycle fonctionne");

      if (!memoires.isEmpty()) {
        System.out.println("Liste des memoires par cycle ...");
        for (Memoire memoire : memoires) {
          System.out.println(memoire.getMatriculeEtudiant() + " " + memoire.getDateDepot() + " " + memoire.getMention());
        }
      } else {
        System.err.println("ERROR : Liste des memoires par cycle introuvable");
      }
    }
  }
}
