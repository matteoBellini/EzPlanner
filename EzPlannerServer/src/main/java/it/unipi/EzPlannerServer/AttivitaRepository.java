/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.EzPlannerServer;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author matte
 */
public interface AttivitaRepository extends CrudRepository<Attivita, Integer> {
    @Query("SELECT new it.unipi.EzPlannerServer.NumeroAttivitaGiornaliere(a.data, COUNT(*)) " +
       "FROM Attivita a " +
       "WHERE a.utente = ?1 AND FUNCTION('MONTH', a.data) = ?2 AND FUNCTION('YEAR', a.data) = ?3 " +
       "GROUP BY a.data ORDER BY a.data")
    Iterable<NumeroAttivitaGiornaliere> getNumeroAttivitaGiornaliere(String username, int mese, int anno);      //utilizzato in getAttivitaMese(...)
    
    Iterable<Attivita> findByUtenteAndDataOrderByTopicAsc(String username, LocalDate data);    //utilizzato in getAttivitaGiorno(...)
    
    Attivita findFirstByUtenteAndTopicAndDescrizioneAndDataOrderByIdDesc(String username, String topic, String Descrizione, LocalDate data);    //utilizzato in inserisciAttivita(...)
}
