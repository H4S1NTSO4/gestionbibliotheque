package com.bibliothecaire.gestionbibliotheque.model;

import java.time.LocalDate;

public class Memoire {
    // Attribut miasa mifanaraka ao @ table memoire
    private int memoireId;
    private String matriculeEtudiant;
    private String titreDepot;
    private LocalDate dateDepot;
    private String cycle;
    private String mention;
    private String cheminFichier;

    // Constructeur mifanaraka @ io classe Memoire io
    public Memoire(int memoireId, String matriculeEtudiant, String titreDepot, LocalDate dateDepot, String cycle, String mention, String cheminFichier) {
        this.memoireId = memoireId;
        this.matriculeEtudiant = matriculeEtudiant;
        this.titreDepot = titreDepot;
        this.dateDepot = dateDepot;
        this.cycle = cycle;
        this.mention = mention;
        this.cheminFichier = cheminFichier;
    }
    public Memoire(){}
    // Getters & Setters miasa hahazoana donnees sy hanovana azy
    public int getMemoireId() {
        return memoireId;
    }
    public void setMemoireId(int memoireId) {
        this.memoireId = memoireId;
    }

    public String getMatriculeEtudiant() {
        return matriculeEtudiant;
    }
    public void setMatriculeEtudiant(String matriculeEtudiant) {
        this.matriculeEtudiant = matriculeEtudiant;
    }

    public String getTitreDepot() {
        return titreDepot;
    }
    public void setTitreDepot(String titreDepot) {
        this.titreDepot = titreDepot;
    }

    public LocalDate getDateDepot() {
        return dateDepot;
    }
    public void setDateDepot(LocalDate dateDepot) {
        this.dateDepot = dateDepot;
    }

    public String getCycle() {
        return cycle;
    }
    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getMention() {
        return mention;
    }
    public void setMention(String mention) {
        this.mention = mention;
    }

    public String getCheminFichier() {
        return cheminFichier;
    }
    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }
}
