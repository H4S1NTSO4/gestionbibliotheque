package com.bibliothecaire.gestionbibliotheque.repository;

import com.bibliothecaire.gestionbibliotheque.model.Memoire;

import java.util.List;

public interface MemoireRepository {
    Memoire findByMemoireId(int memoireId);
    List<Memoire> findAll();
    List<Memoire> findByMention(String mention);
    List<Memoire> findByCycle(String cycle);
}
