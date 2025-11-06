package com.bibliothecaire.gestionbibliotheque.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
  // Asiana characterEncoding sy serverTimezone anaty URL amin'ilay accent
  public static final String URL = "jdbc:mysql://localhost:3306/bibliotheque_db?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8&useUnicode=true&serverTimezone=UTC";
  public static final String USERNAME = "root";
  public static final String PASSWORD = "eliminator";

  public static Connection getConnection() throws SQLException {
    try {
      // Eto ilay pilote MySQL (Connector/J)
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      System.err.println("Erreur: Le pilote MySQL JDBC n'a pas été trouvé dans le classpath. (Jereo pom.xml)");
      throw new SQLException("Pilote JDBC MySQL manquant.", e);
    }

    return DriverManager.getConnection(URL, USERNAME, PASSWORD);
  }

  //Méthode hanamarinana hoe mety ve ilay connection @ alalan'ny test tsotra
  public static void testConnection() {
    try (Connection conn = getConnection()) {
      if (conn != null && !conn.isClosed()) {
        System.out.println("✅ Connexion à la base de données MySQL établie avec succès !");
      } else {
        System.err.println("❌ Erreur: La connexion a été établie mais est invalide ou fermée immédiatement.");
      }
    } catch (SQLException e) {
      System.err.println("❌ Échec de la connexion à la base de données MySQL.");
      System.err.println("Message d'erreur: " + e.getMessage());

      // Vérifier-na ny erreur mety hitranga de jerena ny etat
      String sqlState = e.getSQLState();

      if (sqlState != null && (sqlState.equals("08006") || sqlState.equals("00000"))) {
        System.err.println("Vérifiez : 1. Le serveur MySQL est-il démarré sur le port 3306 ? 2. L'URL et le nom de la DB sont-ils corrects ?");
      } else if (e.getMessage().contains("Access denied")) {
        System.err.println("Vérifiez : Le nom d'utilisateur et le mot de passe sont-ils corrects ?");
      }
    }
  }

  //Eto ny fonction principale anandramana an'ilay test
  public static void main(String[] args) {
    testConnection();
  }
}