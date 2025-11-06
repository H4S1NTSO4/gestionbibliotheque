package com.bibliothecaire.gestionbibliotheque.model;

public class Etudiant {

  private String matriculeEtudiant;
  private String nomPrenoms;
  private String email;
  private String telephone;
  private String mentionId;
  private String parcoursId;
  private String groupe;
  private String nomMention;
  private String nomParcours;

  public Etudiant(String matriculeEtudiant, String nomPrenoms, String email, String telephone, String mentionId, String parcoursId, String groupe, String nomMention, String nomParcours) {
    this.matriculeEtudiant = matriculeEtudiant;
    this.nomPrenoms = nomPrenoms;
    this.email = email;
    this.telephone = telephone;
    this.mentionId = mentionId;
    this.parcoursId = parcoursId;
    this.groupe = groupe; // Déplacé ici
    this.nomMention = nomMention;
    this.nomParcours = nomParcours;
  }

  public Etudiant(String matriculeEtudiant, String nomPrenoms, String email, String telephone, String mentionId, String parcoursId, String groupe) {
    this.matriculeEtudiant = matriculeEtudiant;
    this.nomPrenoms = nomPrenoms;
    this.email = email;
    this.telephone = telephone;
    this.mentionId = mentionId;
    this.parcoursId = parcoursId;
    this.groupe = groupe; // Déplacé ici
  }

  public Etudiant() {
  }

  public String getMatriculeEtudiant() {
    return matriculeEtudiant;
  }

  public void setMatriculeEtudiant(String matriculeEtudiant) {
    this.matriculeEtudiant = matriculeEtudiant;
  }

  public String getNomPrenoms() {
    return nomPrenoms;
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

  public String getMentionId() {
    return mentionId;
  }

  public void setMentionId(String mentionId) {
    this.mentionId = mentionId;
  }

  public String getParcoursId() {
    return parcoursId;
  }

  public void setParcoursId(String parcoursId) {
    this.parcoursId = parcoursId;
  }

  public String getGroupe() { // Déplacé après parcoursId
    return groupe;
  }

  public void setGroupe(String groupe) { // Déplacé après parcoursId
    this.groupe = groupe;
  }

  public String getNomMention() {
    return nomMention;
  }

  public void setNomMention(String nomMention) {
    this.nomMention = nomMention;
  }

  public String getNomParcours() {
    return nomParcours;
  }

  public void setNomParcours(String nomParcours) {
    this.nomParcours = nomParcours;
  }
}