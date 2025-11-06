package com.bibliothecaire.gestionbibliotheque.model;

public class CategorieLivre {
  // Attribut miasa mifanaraka ao @ table etudiant
  private int idCategorie;
  private String genre;

  // Constructeur mifanaraka @ io classe CategorieLivre io
  public CategorieLivre(int idCategorie, String genre) {
    this.idCategorie = idCategorie;
    this.genre = genre;
  }

  public CategorieLivre() {
  }

  // Getters & Setters miasa hahazoana donnees sy hanovana azy
  public int getIdCategorie() {
    return idCategorie;
  }

  public void setIdCategorie(int idCategorie) {
    this.idCategorie = idCategorie;
  }

  public String getGenre() {
    return genre;
  }

  public void setGenre(String genre) {
    this.genre = genre;
  }
}
