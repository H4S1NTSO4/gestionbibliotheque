package com.bibliothecaire.gestionbibliotheque.model;

public class Etudiant {
    // Attribut miasa mifanaraka ao @ table etudiant
    private String matriculeEtudiant;
    private String nomPrenoms;
    private String email;
    private String telephone;

    // Constructeur mifanaraka @ io classe Etudiant io
    public Etudiant(String matriuleEtudiant, String nomPrenoms, String email, String telephone) {
        this.matriculeEtudiant = matriuleEtudiant;
        this.nomPrenoms = nomPrenoms;
        this.email = email;
        this.telephone = telephone;
    }
    public Etudiant(){}
    // Getters & Setters miasa hahazoana donnees sy hanovana azy
    public String getMatriculeEtudiant() {
        return matriculeEtudiant;
    }
    public void setMatriculeEtudiant(String matriculeEtudiant) {
        this.matriculeEtudiant = matriculeEtudiant;
    }

    public String getNomPrenoms() {
        return  nomPrenoms;
    }
    public void setNomPrenoms(String nomPrenoms) {
        this.nomPrenoms = nomPrenoms;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
