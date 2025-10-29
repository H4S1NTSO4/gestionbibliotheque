package com.bibliothecaire.gestionbibliotheque.model;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelImporter {
    public static List<Etudiant> importerEtudiants(File fichierExcel) throws IOException {
        List<Etudiant> etudiants = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(fichierExcel);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Récupérer la première feuille
            Sheet sheet = workbook.getSheetAt(0);

            // Parcourir les lignes (en commençant à 1 pour ignorer les en-têtes)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null) {
                    continue; // Ignorer les lignes vides
                }

                try {
                    // Lire les cellules
                    String matricule = getCellValueAsString(row.getCell(0));
                    String nomPrenoms = getCellValueAsString(row.getCell(1));
                    String email = getCellValueAsString(row.getCell(2));
                    String telephone = getCellValueAsString(row.getCell(3));

                    // Validation : matricule et nom sont obligatoires
                    if (matricule != null && !matricule.trim().isEmpty() &&
                            nomPrenoms != null && !nomPrenoms.trim().isEmpty()) {

                        Etudiant etudiant = new Etudiant(
                                matricule.trim(),
                                nomPrenoms.trim(),
                                email != null && !email.trim().isEmpty() ? email.trim() : null,
                                telephone != null && !telephone.trim().isEmpty() ? telephone.trim() : null
                        );

                        etudiants.add(etudiant);
                    }
                } catch (Exception e) {
                    // Log l'erreur mais continue avec les autres lignes
                    System.err.println("Erreur lors de la lecture de la ligne " + (i + 1) + ": " + e.getMessage());
                }
            }
        }

        return etudiants;
    }

    /**
     * Convertit une cellule Excel en String, peu importe son type
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // Si c'est un nombre, le convertir en string sans décimales
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
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

    /**
     * Valide le format du fichier Excel
     */
    public static boolean validerFormatExcel(File fichierExcel) {
        try (FileInputStream fis = new FileInputStream(fichierExcel);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                return false;
            }

            // Vérifier que les en-têtes sont corrects
            String col1 = getCellValueAsString(headerRow.getCell(0));
            String col2 = getCellValueAsString(headerRow.getCell(1));

            return col1 != null && col1.toLowerCase().contains("matricule") &&
                    col2 != null && (col2.toLowerCase().contains("nom") || col2.toLowerCase().contains("prénom"));

        } catch (Exception e) {
            return false;
        }
    }
}
