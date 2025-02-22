/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.EzPlannerServer;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author matte
 */

//Classe che rappresenta un giorno e il corrispondente numero di attivita
public class NumeroAttivitaGiornaliere implements Serializable {
    private LocalDate data;
    private long numAttivita;
    
    public NumeroAttivitaGiornaliere(LocalDate data, long numAttivita) {
        this.data = data;
        this.numAttivita = numAttivita;
    }
    
    public NumeroAttivitaGiornaliere() {
        
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public long getNumAttivita() {
        return numAttivita;
    }

    public void setNumAttivita(long numAttivita) {
        this.numAttivita = numAttivita;
    }
}
