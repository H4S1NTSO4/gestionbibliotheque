package com.bibliothecaire.gestionbibliotheque.model;

public class Parcours {
  private String parcoursId;
  private String nomParcours;
  private String mentionId;

  public Parcours(String parcoursId, String nomParcours, String mentionId) {
    this.parcoursId = parcoursId;
    this.nomParcours = nomParcours;
    this.mentionId = mentionId;
  }

  public Parcours() {
  }

  public String getParcoursId() {
    return parcoursId;
  }

  public void setParcoursId(String parcoursId) {
    this.parcoursId = parcoursId;
  }

  public String getNomParcours() {
    return nomParcours;
  }

  public void setNomParcours(String nomParcours) {
    this.nomParcours = nomParcours;
  }

  public String getMentionId() {
    return mentionId;
  }

  public void setMentionId(String mentionId) {
    this.mentionId = mentionId;
  }
}
