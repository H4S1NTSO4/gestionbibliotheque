package com.bibliothecaire.gestionbibliotheque.test;

import com.bibliothecaire.gestionbibliotheque.config.DatabaseConfig;
import com.bibliothecaire.gestionbibliotheque.dao.ConsultationLivreDAO;
import com.bibliothecaire.gestionbibliotheque.model.ConsultationLivre;
import com.bibliothecaire.gestionbibliotheque.repository.ConsultationLivreRepository;

public class TestConsultationLivreDAO {
    public static void main(String[] args) {
        System.out.println("Test de la connexion");
        DatabaseConfig.testConnection();

        ConsultationLivreRepository consultationLivreRepository = new ConsultationLivreDAO();
        System.out.println("Demarrage des tests ConsultationLivreDAO");

        testSave(consultationLivreRepository, 10, "GEST-001");
    }

    private static void testSave(ConsultationLivreRepository consultationLivreRepository, int visiteId, String referenceLivre) {
        ConsultationLivre nouvelleConsultationLivre = new ConsultationLivre();
        nouvelleConsultationLivre.setVisiteId(visiteId);
        nouvelleConsultationLivre.setReferenceLivre(referenceLivre);

        System.out.println("Insertion de consultation de livre pour la visite " + visiteId);
        boolean success =  consultationLivreRepository.save(nouvelleConsultationLivre);

        if (success) {
            System.out.println("✅ SUCCES: Consultation livre enregistree");
        }
    }
}
