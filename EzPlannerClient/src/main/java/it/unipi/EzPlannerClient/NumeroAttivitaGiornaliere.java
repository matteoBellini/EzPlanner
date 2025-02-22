/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.EzPlannerClient;

import java.io.Serializable;

/**
 *
 * @author matte
 */
//Classe che rappresenta un giorno e il corrispondente numero di attivita
public class NumeroAttivitaGiornaliere implements Serializable {
    private String data;
    private int numAttivita;
    
    public NumeroAttivitaGiornaliere(String data, int numAttivita) {
        this.data = data;
        this.numAttivita = numAttivita;
    }
    
    public NumeroAttivitaGiornaliere() {
        
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getNumAttivita() {
        return numAttivita;
    }

    public void setNumAttivita(int numAttivita) {
        this.numAttivita = numAttivita;
    }
}
