/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.EzPlannerClient;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author matte
 */

//Classe che rappresenta un'attività
public class Attivita implements Serializable {
    public Integer id;
    public String topic;
    public String descrizione;
    public LocalDate data;
    public String utente;
    
    public Attivita(Integer id, String topic, String descrizione, LocalDate data, String utente) {
        this.id = id;
        this.topic = topic;
        this.descrizione = descrizione;
        this.data = data;
        this.utente = utente;
    }
    
    public Attivita(String topic, String descrizione, LocalDate data, String utente) {
        this.topic = topic;
        this.descrizione = descrizione;
        this.data = data;
        this.utente = utente;
    }
    
    public Attivita() {
        
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getUtente() {
        return utente;
    }

    public void setUtente(String utente) {
        this.utente = utente;
    }
    
    
}
