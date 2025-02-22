/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.EzPlannerClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author matte
 */
public class ActivityController {
    private static final Logger logger = LogManager.getLogger();
    
    @FXML private Button calendarButton;
    @FXML private Button addButton;
    @FXML private TextField topic;
    @FXML private TextArea description;
    @FXML private Label dayLabel;
    @FXML private Label infoLabel;
    @FXML private TableView<Attivita> activityTable = new TableView<>();
    
    private ObservableList<Attivita> activityList;
    
    private SessionData sessionData = SessionData.getIstanza();
    
    @FXML
    public void initialize() {
        logger.info("Caricamento schermata gestione attività");
        
        dayLabel.setText(sessionData.getDate() + " activities");
        
        TableColumn dataCol = new TableColumn("Date");
        dataCol.setCellValueFactory(new PropertyValueFactory<>("data"));
        
        TableColumn topicCol = new TableColumn("Topic");
        topicCol.setCellValueFactory(new PropertyValueFactory<>("topic"));
        
        TableColumn descCol = new TableColumn("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("descrizione"));
        
        activityTable.getColumns().addAll(dataCol, topicCol, descCol);      //aggiunta delle colonne create alla tabella
        
        activityList = FXCollections.observableArrayList();     //creazione di una lista osservabile di attività
        activityTable.setItems(activityList);   //collegamento della lista alla tabella
        
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", sessionData.getUtente().getUsername());
        jsonObject.addProperty("data", sessionData.getDate().toString());
        String requestData = jsonObject.toString();
        
        Task task = new Task<Void>() {
            @Override public Void call() {
                try {
                    URL url = new URL("http://localhost:8080/EzPlanner/attivitaGiorno");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);
                    PrintWriter out = new PrintWriter(con.getOutputStream());
                    out.print(requestData);
                    out.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line;
                    StringBuffer content = new StringBuffer();
                    while((line = br.readLine()) != null){
                        content.append(line);
                    }
                    br.close();

                    Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
                    JsonElement json = gson.fromJson(content.toString(), JsonElement.class);
                    JsonArray attivita = json.getAsJsonArray();

                    for(int i = 0; i < attivita.size(); i++){
                        JsonObject obj = attivita.get(i).getAsJsonObject();
                        Attivita a = new Attivita(obj.get("id").getAsInt(), obj.get("topic").getAsString(), obj.get("descrizione").getAsString(), LocalDate.parse(obj.get("data").getAsString()), obj.get("utente").getAsString());
                        Platform.runLater(new Runnable(){
                            @Override public void run() {
                                activityList.add(a);
                            }
                        });
                    }
                } catch(Exception e) {
                    logger.error("Errore nel reperire le attività della data selezionata: " + e.getMessage());
                }
                
                return null;
            }
        };
        new Thread(task).start();
    }
    
    @FXML
    public void addActivity() {     //funzione che permette di aggiungere un'attività alla pressione del bottone Add
        String t = topic.getText();
        String descr = description.getText();
        if(t.equals("")){       //controllo che sia stato specificato almeno il topic dell'attività
            infoLabel.setText("You must specify at least the Topic");
            return;
        }
        
        Attivita a = new Attivita(t, descr, sessionData.getDate(), sessionData.getUtente().getUsername());
        
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        String attivitaSerializzato = gson.toJson(a, Attivita.class);
        
        Task task = new Task<Void>() {
            @Override public Void call() {
                try {
                    URL url = new URL("http://localhost:8080/EzPlanner/inserisci");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);
                    PrintWriter out = new PrintWriter(con.getOutputStream());
                    out.print(attivitaSerializzato);
                    out.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    Integer risposta = gson.fromJson(br, Integer.class);
                    if(risposta > 0){
                        logger.info("Inserimento effettuato");
                        a.setId(risposta);
                        Platform.runLater(new Runnable(){
                            @Override public void run() {
                                activityList.add(a);
                                topic.setText("");
                                description.setText("");
                            }
                        });
                    } else {
                        logger.error("Errore inserimento");
                    }
                } catch (Exception e) {
                    logger.error("Errore nell'inserimento dell'attivita: " + e.getMessage());
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    @FXML
    public void backToCalendar() throws IOException {
        App.setRoot("home");
    }
    
    @FXML
    public void removeActivity() {
        Attivita a = activityTable.getSelectionModel().getSelectedItem();
        if(a == null)
            return;
        activityList.remove(a);
        
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        String attivitaSerializzato = gson.toJson(a, Attivita.class);
        
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    URL url = new URL("http://localhost:8080/EzPlanner/elimina");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("DELETE");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);
                    PrintWriter out = new PrintWriter(con.getOutputStream());
                    out.print(attivitaSerializzato);
                    out.close();
                    
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String r = gson.fromJson(br, String.class);
                    if(r.equals("OK")){
                        logger.info("Eliminazione effettuata");
                    } else {
                        logger.error("Errore nell'eliminazione");
                    }
                } catch(Exception e) {
                    logger.error("Errore nell'eleminazione dell'attività: " + e.getMessage());
                }
                return null;
            }
        };
        
        new Thread(task).start();
    }
}
