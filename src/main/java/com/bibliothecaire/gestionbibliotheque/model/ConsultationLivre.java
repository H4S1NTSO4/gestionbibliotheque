package com.bibliothecaire.gestionbibliotheque.model;

import java.time.LocalTime;

public class ConsultationLivre {
    // Attribut miasa mifanaraka ao @ table consultation_livre
    private int consultationId;
    private int visiteId;
    private String referenceLivre;
    private LocalTime heurePrise;
    private LocalTime heureRendu;

    // Constructeur mifanaraka @ io classe ConsultationLivre io
    public ConsultationLivre(int consultationId, int visiteId, String referenceLivre, LocalTime heurePrise, LocalTime heureRendu) {
        this.consultationId = consultationId;
        this.visiteId = visiteId;
        this.referenceLivre = referenceLivre;
        this.heurePrise = heurePrise;
        this.heureRendu = heureRendu;
    }
    public ConsultationLivre(){}
    // Getters & Setters miasa hahazoana donnees sy hanovana azy
    public int getConsultationId() {
        return consultationId;
    }
    public void setConsultationId(int consultationId) {
        this.consultationId = consultationId;
    }

    public int getVisiteId() {
        return visiteId;
    }
    public void setVisiteId(int visiteId) {
        this.visiteId = visiteId;
    }

    public String getReferenceLivre() {
        return referenceLivre;
    }
    public void setReferenceLivre(String referenceLivre) {
        this.referenceLivre = referenceLivre;
    }

    public LocalTime getHeurePrise() {
        return heurePrise;
    }
    public void setHeurePrise(LocalTime heurePrise) {
        this.heurePrise = heurePrise;
    }

    public LocalTime getHeureRendu() {
        return heureRendu;
    }
    public void setHeureRendu(LocalTime heureRendu) {
        this.heureRendu = heureRendu;
    }
}
