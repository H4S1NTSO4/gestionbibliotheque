package com.bibliothecaire.gestionbibliotheque.test;

import com.bibliothecaire.gestionbibliotheque.config.DatabaseConfig;
import com.bibliothecaire.gestionbibliotheque.dao.LivreDAO;
import com.bibliothecaire.gestionbibliotheque.model.Livre;
import com.bibliothecaire.gestionbibliotheque.repository.LivreRepository;

import java.util.List;

public class TestLivreDAO {
    public static void main(String[] args) {
        System.out.println("Test de la Connexion");
        DatabaseConfig.testConnection();

        LivreRepository livreRepository = new LivreDAO();
        System.out.println("Demarrage des tests LivreDAO");

        //testFindByReferenceLivre(livreRepository, "GEO-305-");
        testFindAllLivre(livreRepository);
        //testSave(livreRepository, "GEST-001", "L’efficacité au travail : méthodes et pratiques", "Thérèse Pérignon", 200, 4);
    }

    private static void testFindByReferenceLivre(LivreRepository livreRepository, String referenceLivre) {
        System.out.println("\n [Test] Trouver a partir du reference " +  referenceLivre);
        Livre l = livreRepository.findByReferenceLivre(referenceLivre);

        if (l != null) {
            System.out.println("SUCCES : Livre trouve: " + l.getReferenceLivre() + " " + l.getAuteur() + " " + l.getTitre() + " " + l.getNbExemplaire() + " " + l.getNbExemplaireDisponible());
        } else {
            System.out.println("ERREUR: Livre introuvable pour la reference : " + referenceLivre);
        }
    }

    private static void testFindAllLivre(LivreRepository livreRepository) {
        System.out.println("\n [Test] Recuperation de tous les livres");
        List<Livre> livres = livreRepository.findAll();

        if (livres != null) {
            System.out.println("SUCCES: Liste de tous les livres trouves !");

            if (!livres.isEmpty()) {
                System.out.println("Liste des livres ...");
                for (Livre l : livres) {
                    System.out.println(l.getReferenceLivre() + " " + l.getTitre() + " " + l.getAuteur());
                }
            } else {
                System.err.println("ECHEC: Liste des livres introuvables");
            }
        }
    }

    private static void testSave(LivreRepository livreRepository, String referenceLivre, String titre, String auteur, int nbExemplaire, int idCategorie) {
        System.out.println("\n [Test] Ajout d'un nouveau livre");
        Livre nouvelLivre = new Livre();

        nouvelLivre.setReferenceLivre(referenceLivre);
        nouvelLivre.setTitre(titre);
        nouvelLivre.setAuteur(auteur);
        nouvelLivre.setNbExemplaire(nbExemplaire);
        nouvelLivre.setIdCategorie(idCategorie);

        boolean success = livreRepository.save(nouvelLivre);

        if (success) {
            System.out.println("SUCCES : Livre insere. Verification ...");
            Livre verif = livreRepository.findByReferenceLivre(referenceLivre);
            if (verif != null) {
                System.out.println("Verification OK " + verif.getReferenceLivre() + " " + verif.getTitre() + " " + verif.getAuteur() + " " + verif.getNbExemplaire() + " " + verif.getNbExemplaireDisponible() + " " + verif.getIdCategorie() + " " + verif.getMemoireId() + " " + verif.isEstMemoire());
            } else {
                System.err.println("Verification ECHOUEE, Etudiant introuvable apres insertion");
            }
        } else {
            System.err.println("ECHEC : de sauvegarde du livre");
        }
    }
}
