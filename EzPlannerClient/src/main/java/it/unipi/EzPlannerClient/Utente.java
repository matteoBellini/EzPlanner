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

//Classe che rappresenta un Utente
public class Utente implements Serializable {
    public String username;
    public String nome;
    public String cognome;
    public String password;
    
    public Utente(String username, String nome, String cognome, String password) {
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.password = password;
    }
    
    public Utente() {
        
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
