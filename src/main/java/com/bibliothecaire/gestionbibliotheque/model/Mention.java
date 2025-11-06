package com.bibliothecaire.gestionbibliotheque.model;

public class Mention {
  private String mentionId;
  private String nomMention;

  public Mention(String mentionId, String nomMention) {
    this.mentionId = mentionId;
    this.nomMention = nomMention;
  }

  public Mention() {
  }

  public String getMentionId() {
    return mentionId;
  }

  public void setMentionId(String mentionId) {
    this.mentionId = mentionId;
  }

  public String getNomMention() {
    return nomMention;
  }

  public void setNomMention(String nomMention) {
    this.nomMention = nomMention;
  }
}
