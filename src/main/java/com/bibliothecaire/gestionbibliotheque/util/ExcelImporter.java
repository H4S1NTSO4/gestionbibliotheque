package com.bibliothecaire.gestionbibliotheque.util;

import com.bibliothecaire.gestionbibliotheque.model.Etudiant;
import com.bibliothecaire.gestionbibliotheque.model.Livre;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelImporter {

  // ==========================================================
  // MÉTHODES POUR L'IMPORTATION DES LIVRES (NON MODIFIÉES ICI)
  // ==========================================================

  public static List<Livre> importerLivres(File fichierExcel) throws IOException {
    List<Livre> livres = new ArrayList<>();
    try (FileInputStream fis = new FileInputStream(fichierExcel);
         Workbook workbook = new XSSFWorkbook(fis)) {

      Sheet sheet = workbook.getSheetAt(0);
      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        if (row == null) {
          continue;
        }
        try {
          String referenceLivre = getCellValueAsString(row.getCell(0));
          String titre = getCellValueAsString(row.getCell(1));
          String auteur = getCellValueAsString(row.getCell(2));
          String nbExemplaireStr = getCellValueAsString(row.getCell(3));
          String nomCategorie = getCellValueAsString(row.getCell(4));

          if (referenceLivre != null && !referenceLivre.trim().isEmpty() &&
                  titre != null && !titre.trim().isEmpty() &&
                  nbExemplaireStr != null && !nbExemplaireStr.trim().isEmpty() &&
                  nomCategorie != null && !nomCategorie.trim().isEmpty()) {

            int nbExemplaire = Integer.parseInt(nbExemplaireStr.trim());
            Livre livre = new Livre();
            livre.setReferenceLivre(referenceLivre.trim());
            livre.setTitre(titre.trim());
            livre.setAuteur(auteur.trim());
            livre.setNbExemplaire(nbExemplaire);
            livre.setNomCategorie(nomCategorie.trim());
            livre.setNbExemplaireDisponible(nbExemplaire);
            livre.setEstMemoire(false);
            livre.setMemoireId(null);
            livres.add(livre);
          } else {
            System.err.println("⚠️ Ligne " + (i + 1) + " ignorée : Données manquantes (Ref, Titre, Nb Exemplaire ou Catégorie).");
          }
        } catch (NumberFormatException nfe) {
          System.err.println("❌ Erreur de conversion du nombre d'exemplaires à la ligne " + (i + 1) + " : " + nfe.getMessage());
        } catch (Exception e) {
          System.err.println("❌ Erreur lors de la lecture de la ligne " + (i + 1) + ": " + e.getMessage());
        }
      }
    }
    return livres;
  }

  public static boolean validerFormatLivreExcel(File fichierExcel) {

    try (FileInputStream fis = new FileInputStream(fichierExcel);
         Workbook workbook = new XSSFWorkbook(fis)) {
      Sheet sheet = workbook.getSheetAt(0);
      Row headerRow = sheet.getRow(0);
      if (headerRow == null) {
        return false;
      }

      String col0 = getCellValueAsString(headerRow.getCell(0));
      String col1 = getCellValueAsString(headerRow.getCell(1));
      String col2 = getCellValueAsString(headerRow.getCell(2));
      String col3 = getCellValueAsString(headerRow.getCell(3));
      String col4 = getCellValueAsString(headerRow.getCell(4));

      return col0 != null && col0.toLowerCase().contains("référence livre") &&
              col1 != null && col1.toLowerCase().contains("titre") &&
              col2 != null && col2.toLowerCase().contains("auteur") &&
              col3 != null && col3.toLowerCase().contains("nombre exemplaire") &&
              col4 != null && col4.toLowerCase().contains("catégorie");

    } catch (Exception e) {
      return false;
    }
  }

  // ==========================================================
  // MÉTHODES POUR L'IMPORTATION DES ÉTUDIANTS (CORRIGÉES)
  // ==========================================================

  public static List<Etudiant> importerEtudiants(File fichierExcel) throws IOException {
    List<Etudiant> etudiants = new ArrayList<>();

    try (FileInputStream fis = new FileInputStream(fichierExcel);
         Workbook workbook = new XSSFWorkbook(fis)) {

      Sheet sheet = workbook.getSheetAt(0);

      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);

        // *** DÉBUT DE LA CORRECTION : IGNORER LES LIGNES VIDES BASÉES SUR LE MATRICULE (col 0) ***
        // Ceci empêche l'erreur pour les lignes vides au-delà des données réelles.
        if (row == null || row.getCell(0) == null || getCellValueAsString(row.getCell(0)) == null || getCellValueAsString(row.getCell(0)).trim().isEmpty()) {
          continue;
        }
        // *** FIN DE LA CORRECTION ***

        try {
          // Les cellules sont relues ici après la vérification de la ligne vide.
          String matricule = getCellValueAsString(row.getCell(0));
          String nomPrenoms = getCellValueAsString(row.getCell(1));
          String email = getCellValueAsString(row.getCell(2));
          String telephone = getCellValueAsString(row.getCell(3));
          String mentionId = getCellValueAsString(row.getCell(4));
          String parcoursId = getCellValueAsString(row.getCell(5));
          String groupe = getCellValueAsString(row.getCell(6));

          // On sait déjà que le matricule est présent, on vérifie les deux autres champs obligatoires
          if (nomPrenoms != null && !nomPrenoms.trim().isEmpty() &&
                  mentionId != null && !mentionId.trim().isEmpty()) {

            Etudiant etudiant = new Etudiant(
                    matricule.trim(),
                    nomPrenoms.trim(),
                    email != null && !email.trim().isEmpty() ? email.trim() : null,
                    telephone != null && !telephone.trim().isEmpty() ? telephone.trim() : null,

                    mentionId.trim(),
                    parcoursId != null && !parcoursId.trim().isEmpty() ? parcoursId.trim() : null,
                    groupe != null && !groupe.trim().isEmpty() ? groupe.trim() : null
            );

            etudiants.add(etudiant);
          } else {
            // Ce message est maintenant plus précis et s'affiche uniquement si Nom/Prénom ou Mention manque, bien que le Matricule soit présent.
            System.err.println("⚠️ Ligne " + (i + 1) + " ignorée : Nom/Prénom ou Mention ID est manquant (Matricule: " + matricule + ").");
          }
        } catch (Exception e) {
          System.err.println("❌ Erreur lors de la lecture de la ligne " + (i + 1) + ": " + e.getMessage());
        }
      }
    }

    return etudiants;
  }

  public static boolean validerFormatEtudiantExcel(File fichierExcel) {
    try (FileInputStream fis = new FileInputStream(fichierExcel);
         Workbook workbook = new XSSFWorkbook(fis)) {

      Sheet sheet = workbook.getSheetAt(0);
      Row headerRow = sheet.getRow(0);

      if (headerRow == null) {
        return false;
      }

      String col0 = getCellValueAsString(headerRow.getCell(0)); // Matricule
      String col1 = getCellValueAsString(headerRow.getCell(1)); // Nom/Prénoms
      String col4 = getCellValueAsString(headerRow.getCell(4)); // mention_id
      String col5 = getCellValueAsString(headerRow.getCell(5)); // parcours_id
      String col6 = getCellValueAsString(headerRow.getCell(6)); // groupe

      // Vérification des champs clés (Matricule, Nom/Prénom, Mention, Parcours, Groupe)
      return col0 != null && col0.toLowerCase().contains("matricule") &&
              col1 != null && (col1.toLowerCase().contains("nom") || col1.toLowerCase().contains("prénom")) &&
              col4 != null && col4.toLowerCase().contains("mention") &&
              col5 != null && col5.toLowerCase().contains("parcours") &&
              col6 != null && col6.toLowerCase().contains("groupe");


    } catch (Exception e) {
      return false;
    }
  }


  // ==========================================================
  // MÉTHODE UTILITAIRE (NON MODIFIÉE)
  // ==========================================================

  private static String getCellValueAsString(Cell cell) {
    if (cell == null) {
      return null;
    }

    switch (cell.getCellType()) {
      case STRING:
        return cell.getStringCellValue();
      case NUMERIC:
        if (DateUtil.isCellDateFormatted(cell)) {
          return cell.getDateCellValue().toString();
        } else {
          double numericValue = cell.getNumericCellValue();
          if (numericValue == Math.floor(numericValue)) {
            return String.valueOf((long) numericValue);
          } else {
            return String.valueOf(numericValue);
          }
        }
      case BOOLEAN:
        return String.valueOf(cell.getBooleanCellValue());
      case FORMULA:
        return cell.getCellFormula();
      case BLANK:
        return null;
      default:
        return null;
    }
  }
}