/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.EzPlannerServer;

import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author matte
 */
public interface UtenteRepository extends CrudRepository<Utente, String> {
    Utente findByUsernameAndPassword(String username, String password);     //utilizzato in login(...)

    Utente findByUsername(String username);     //utilizzato in signUp(...)
}
