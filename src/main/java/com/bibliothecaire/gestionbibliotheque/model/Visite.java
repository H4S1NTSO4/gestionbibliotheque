package com.bibliothecaire.gestionbibliotheque.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Visite {
    // Attribut miasa mifanaraka ao @ table visite
    private int visiteId;
    private String matriculeEtudiant;
    private LocalDate dateVisite;
    private LocalTime heureEntree;
    private LocalTime heureSortie;

    // Constructeur mifanaraka @ io classe Visite io
    public Visite(int visiteId, String matriculeEtudiant, LocalDate dateVisite, LocalTime heureEntree, LocalTime heureSortie) {
        this.visiteId = visiteId;
        this.matriculeEtudiant = matriculeEtudiant;
        this.dateVisite = dateVisite;
        this.heureEntree = heureEntree;
        this.heureSortie = heureSortie;
    }
    public Visite(){}
    // Getters & Setters miasa hahazoana donnees sy hanovana azy
    public int getVisiteId() {
        return visiteId;
    }
    public void setVisiteId(int visiteId) {
        this.visiteId = visiteId;
    }

    public String getMatriculeEtudiant() {
        return matriculeEtudiant;
    }
    public void setMatriculeEtudiant(String matriculeEtudiant) {
        this.matriculeEtudiant = matriculeEtudiant;
    }

    public LocalDate getDateVisite() {
        return dateVisite;
    }
    public void setDateVisite(LocalDate dateVisite) {
        this.dateVisite = dateVisite;
    }

    public LocalTime getHeureEntree() {
        return heureEntree;
    }
    public void setHeureEntree(LocalTime heureEntree) {
        this.heureEntree = heureEntree;
    }

    public LocalTime getHeureSortie() {
        return heureSortie;
    }
    public void setHeureSortie(LocalTime heureSortie) {
        this.heureSortie = heureSortie;
    }
}
