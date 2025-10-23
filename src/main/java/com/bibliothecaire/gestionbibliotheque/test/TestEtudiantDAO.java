package com.bibliothecaire.gestionbibliotheque.test;

import com.bibliothecaire.gestionbibliotheque.config.DatabaseConfig;
import com.bibliothecaire.gestionbibliotheque.dao.EtudiantDAO;
import com.bibliothecaire.gestionbibliotheque.model.Etudiant;
import com.bibliothecaire.gestionbibliotheque.repository.EtudiantRepository;

import java.util.List;

public class TestEtudiantDAO {
    public static void main(String[] args) {
        System.out.println("Test de la Connexion");
        DatabaseConfig.testConnection();

        EtudiantRepository etudiantRepository = new EtudiantDAO();
        System.out.println("Demarrage des tests EtudiantDAO");

        //testFindByMatricule(etudiantRepository, "E001");
        //testFindByMatricule(etudiantRepository, "E999");
        //testSave(etudiantRepository, "E007", "Jérôme Gaëtan", "jeromeGaetan@gmail.mg", "0315678963");
        testFindAll(etudiantRepository);
    }

    private static void testFindByMatricule(EtudiantRepository etudiantRepository, String matriculeEtudiant) {
        System.out.println("\n [Test] findByMatricule(" + matriculeEtudiant + ")");
        Etudiant e = etudiantRepository.findByMatricule(matriculeEtudiant);

        if (e != null) {
            System.out.println("SUCCES : Etudiant trouve: " + e.getNomPrenoms());
        } else {
            System.out.println("ERREUR : Etudiant introuvable");
        }
    }

    private static void testSave(EtudiantRepository etudiantRepository, String matriculeEtudiant, String nomPrenoms, String email, String telephone) {
        System.out.println("\n [Test] save(" + matriculeEtudiant + ", " + nomPrenoms + ")");
        Etudiant nouvelEtudiant = new Etudiant(matriculeEtudiant, nomPrenoms, email, telephone);

        boolean success = etudiantRepository.save(nouvelEtudiant);

        if (success) {
            System.out.println(" SUCCES : Etudiant insere. Verification immediate ...");
            Etudiant verif = etudiantRepository.findByMatricule(matriculeEtudiant);
            if (verif != null) {
                System.out.println("Verification OK " + verif.getNomPrenoms());
            } else {
                System.err.println("Verification echouee, Etudiant introuvable apres insertion.");
            }
        } else {
            System.err.println("Echec de sauvegarde de l'etudiant");
        }
    }

    private static void testFindAll(EtudiantRepository etudiantRepository) {
        System.out.println("\n [Test] findAll Recuperation de tous les etudiants");
        List<Etudiant> etudiants = etudiantRepository.findAll();

        if (etudiants != null) {
            System.out.println("SUCCES: Etudiants trouves !");

            if(!etudiants.isEmpty()) {
                System.out.println("Liste des etudiants:");
                for (Etudiant etudiant: etudiants) {
                    System.out.println(etudiant.getNomPrenoms());
                }
            }
        } else {
            System.err.println("ECHEC : Liste des etudiants introuvables");
        }
    }
}
