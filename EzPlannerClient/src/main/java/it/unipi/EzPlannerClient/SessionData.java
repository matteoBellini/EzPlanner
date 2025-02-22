/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.EzPlannerClient;

import java.time.LocalDate;

/**
 *
 * @author matte
 */

/* 
Classe Singleton che permette di scambiare dati tra una schermata e l'altra; i dati necessari sono quelli dell'utente
e la data selezionata
*/
public class SessionData {
    private static SessionData istanza = new SessionData();
    
    private Utente utente;
    private LocalDate selectedDate;
    
    public SessionData() {
        
    }
    
    public static SessionData getIstanza() {
        return istanza;
    }
    
    public void setUtente(Utente utente) {
        this.utente = utente;
    }
    
    public Utente getUtente() {
        return utente;
    }
    
    public void setDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }
    
    public LocalDate getDate() {
        return selectedDate;
    }
}
