/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.EzPlannerServer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.jdbc.ScriptRunner;

/**
 *
 * @author matte
 */
public class InitServer {
    
    public static void init() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306", "root", "root")) {
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet rs = metaData.getCatalogs();
            
            while(rs.next()){
                String temp = rs.getString(1);
                if(temp.equals("655163")){
                    System.out.println("Database già esistente");
                    return;
                }
            }
            
            System.out.println("Database non trovato, creazione...");
            Reader reader = new BufferedReader(new FileReader("src/main/resources/it/unipi/EzPlannerServer/creazione.sql"));
            
            ScriptRunner sr = new ScriptRunner(con);
            sr.setStopOnError(true);    //se si verifica un errore l'esecuzione dello script viene interrotta
            try {
                sr.runScript(reader);
            } catch (Exception e) {
                System.out.println("Si è verificato un errore nella creazione del database: " + e.getMessage());
            }
        } catch(Exception ex) {
            System.out.println("Si è verificato un errore generico durante l'inizializzazione: " + ex.getMessage());
        }
    }
    
    public static String popola() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/655163", "root", "root")) {
            String sql = "SELECT COUNT(*) FROM utente";
            try (PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()){
                if(rs.next() && rs.getInt(1) > 0)
                    return "The database already contains data";
            } catch(SQLException e) {
                System.out.println("Errore nel controllare se il database è già popolato: " + e.getMessage());
                return "Error";
            }
            
            
            System.out.println("Dati non trovati, popolamento...");
            Reader reader = new BufferedReader(new FileReader("src/main/resources/it/unipi/EzPlannerServer/popolamento.sql"));

            ScriptRunner sr = new ScriptRunner(con);
            sr.setStopOnError(true);    //se si verifica un errore l'esecuzione dello script viene interrotta
            try {
                sr.runScript(reader);
            } catch (Exception e) {
                System.out.println("Si è verificato un errore nel popolamento del database: " + e.getMessage());
                return "Error";
            }

            return "The data has been added";
        } catch(Exception ex) {
            System.out.println("Si è verificato un errore generico durante il popolamento: " + ex.getMessage());
            return "Error";
        }
    }
}
