/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package it.unipi.EzPlannerClient;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author matte
 */
public class AppTest {
    
    public AppTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }
    
    @Test
    public void testConnection() {
        try {
            Gson gson = new Gson();
            
            URL url = new URL("http://localhost:8080/EzPlanner/test");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String response = gson.fromJson(br, String.class);
            
            if(response.equals("OK"))
                System.out.println("Test di connessione completato con successo");
            else
                fail("Test di connessione non riuscito");
        } catch(Exception e) {
            fail("Test di connessione non riuscito");
        }
    }
    
}
