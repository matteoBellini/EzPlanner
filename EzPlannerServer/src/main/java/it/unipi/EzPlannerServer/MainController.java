/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.EzPlannerServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.time.LocalDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author matte
 */
@Controller
@RequestMapping(path="/EzPlanner")
public class MainController {
    
    private static final Logger logger = LogManager.getLogger(MainController.class);
    
    @Autowired
    private AttivitaRepository attivitaRepository;
    @Autowired
    private UtenteRepository utenteRepository;
    
    @GetMapping(path="/inizializza")
    public @ResponseBody String inizializzaDatabase() {
        Gson gson = new Gson();
        
        return gson.toJson(InitServer.popola());
    }
    
    @PostMapping(path="/login")
    public @ResponseBody String login(@RequestBody String s) {
        logger.info("Richiesta di login ricevuta");
        Gson gson = new Gson();
        Utente u = gson.fromJson(s, Utente.class);      //deserializzazione dati ricevuti dal client
        
        Utente result = utenteRepository.findByUsernameAndPassword(u.getUsername(), u.getPassword());   //controllo se esiste l'utente corrispondente
        
        return gson.toJson(result);     //restituisco l'oggetto Utente al client
    }
    
    @PostMapping(path="/signUp")
    public @ResponseBody String signUp(@RequestBody String s) {
        logger.info("Richiesta di registrazione ricevuta");
        Gson gson = new Gson();
        Utente u = gson.fromJson(s, Utente.class);      //deserializzazione dati ricevuti dal client
        
        Utente r = utenteRepository.findByUsername(u.getUsername());    //controllo che non esista già un utente con lo stesso username
        if(r != null) {
            logger.info("Username già utilizzato");
            return gson.toJson("Username already used");    //messaggio di errore da mostrare al client
        }
        
        //altrimenti posso aggiungere un nuovo utente
        utenteRepository.save(u);
        return gson.toJson("OK");
    }
    
    @PostMapping(path="/attivitaMese")
    public @ResponseBody Iterable<NumeroAttivitaGiornaliere> getAttivitaMese(@RequestBody String s) {
        logger.info("Richiesta attività mese ricevuta");
        JsonObject jsonObject = JsonParser.parseString(s).getAsJsonObject();    //deserializzazione dati ricevuti dal client (username, mese, anno)
        
        String username = jsonObject.get("username").getAsString();
        int mese = jsonObject.get("mese").getAsInt();
        int anno = jsonObject.get("anno").getAsInt();
        
        return attivitaRepository.getNumeroAttivitaGiornaliere(username, mese, anno);   //restituisce il numero di attività per ogni giorno del mese richiesto
    }
    
    @PostMapping(path="/attivitaGiorno")
    public @ResponseBody Iterable<Attivita> getAttivitaGiorno(@RequestBody String s) {
        logger.info("Richiesta attività griorno rivecuta");
        JsonObject jsonObject = JsonParser.parseString(s).getAsJsonObject();    //deserializzazione dati ricevuti dal client (username, yyyy-mm-dd)
        
        String username = jsonObject.get("username").getAsString();
        LocalDate data = LocalDate.parse(jsonObject.get("data").getAsString());
        
        return attivitaRepository.findByUtenteAndDataOrderByTopicAsc(username, data);   //restituisce la lista di tutte le attività per il giorno richiesto
    }
    
    @PostMapping(path="/inserisci")
    public @ResponseBody String inserisciAttita(@RequestBody String s) {
        logger.info("Richiesta inserimento attività ricevuta");
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        Attivita a = gson.fromJson(s, Attivita.class);      //deserializzazione dati ricevuti dal client
        
        attivitaRepository.save(a);     //aggiunta dell'attività ricevuta
        
        Attivita result = attivitaRepository.findFirstByUtenteAndTopicAndDescrizioneAndDataOrderByIdDesc(a.getUtente(), a.getTopic(), a.getDescrizione(), a.getData());
        return gson.toJson(result.getId());
    }
    
    @DeleteMapping(path="/elimina")
    public @ResponseBody String rimuoviAttivita(@RequestBody String s) {
        logger.info("Richiesta eliminazione attività ricevuta");
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        Attivita a = gson.fromJson(s, Attivita.class);      //deserializzazione dati ricevuti dal client
        
        attivitaRepository.delete(a);       //elimino l'attività ricevuta
        
        return gson.toJson("OK");
    }
    
    @GetMapping(path="/test")
    public @ResponseBody String testConnessione() {     //permette al client di effettuare un test di connessione con il server
        logger.info("richiesta test connessione ricevuta");
        Gson gson = new Gson();
        
        return gson.toJson("OK");
    }
}
