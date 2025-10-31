package com.bibliothecaire.gestionbibliotheque.model;

public class Livre {
    // Attribut miasa mifanaraka ao @ table livre
    private String referenceLivre;
    private String titre;
    private String auteur;
    private int nbExemplaire;
    private int nbExemplaireDisponible;
    private int idCategorie;
    private boolean estMemoire;
    private Integer memoireId;

    private String nomCategorie;

    // Constructeur mifanaraka @ io classe Etudiant io
    public  Livre(String referenceLivre, String titre, String auteur, int nbExemplaire,  int nbExemplaireDisponible, int idCategorie, boolean estMemoire, Integer memoireId) {
        this.referenceLivre = referenceLivre;
        this.titre = titre;
        this.auteur = auteur;
        this.nbExemplaire = nbExemplaire;
        this.nbExemplaireDisponible = nbExemplaireDisponible;
        this.idCategorie = idCategorie;
        this.estMemoire = estMemoire;
        this.memoireId = memoireId;
    }
    public Livre(){}
    // Getters & Setters miasa hahazoana donnees sy hanovana azy
    public String getReferenceLivre() {
        return referenceLivre;
    }
    public void setReferenceLivre(String referenceLivre) {
        this.referenceLivre = referenceLivre;
    }

    public String getTitre() {
        return titre;
    }
    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }
    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public int getNbExemplaire() {
        return nbExemplaire;
    }
    public void setNbExemplaire(int nbExemplaire) {
        this.nbExemplaire = nbExemplaire;
    }

    public int getNbExemplaireDisponible() {
        return nbExemplaireDisponible;
    }
    public void setNbExemplaireDisponible(int nbExemplaireDisponible) {
        this.nbExemplaireDisponible = nbExemplaireDisponible;
    }

    public int getIdCategorie() {
        return idCategorie;
    }
    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public boolean isEstMemoire() {
        return  estMemoire;
    }
    public void setEstMemoire(boolean estMemoire) {
        this.estMemoire = estMemoire;
    }

    public Integer getMemoireId() {
        return memoireId;
    }
    public void setMemoireId(Integer memoireId) {
        this.memoireId = memoireId;
    }

    public String getNomCategorie() {
        return nomCategorie;
    }
    public void setNomCategorie(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }
}
