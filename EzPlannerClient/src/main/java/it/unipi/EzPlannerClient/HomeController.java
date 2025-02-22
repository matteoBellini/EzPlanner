package it.unipi.EzPlannerClient;

import com.google.gson.Gson;
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
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HomeController {
    
    private static final Logger logger = LogManager.getLogger();
    
    @FXML private Label welcomeLabel;
    @FXML private GridPane calendarContainer;
    @FXML private DatePicker dateField;
    @FXML private Label monthYearLabel;
    
    private SessionData sessionData = SessionData.getIstanza();

    
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
    
    @FXML
    public void initialize() {
        logger.info("Caricamento schermata home");
        
        welcomeLabel.setText("Hi " + sessionData.getUtente().getNome());    //Scrivo il nome utente sulla schermata
        
        dateField.setValue(sessionData.getDate());      //La data predefinita sarà quella selezionata (all'avvio la data corrente)
        
        buildCalendar(sessionData.getDate());
    }
    
    //metodo per la costruzione del calendario
    private void buildCalendar(LocalDate d) {
        calendarContainer.getChildren().clear();    //Elimina il calendario costruito precedentemente
        
        int month = d.getMonthValue();
        int year = d.getYear();
        
        ArrayList<NumeroAttivitaGiornaliere> attivitaMensili = new ArrayList();     //contiene per ogni giorno del mese il numero di attività memorizzate
        
        Task task = new Task<Void>() {
            @Override public Void call() {
                try {
                    Gson gson = new Gson();

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("username", sessionData.getUtente().getUsername());
                    jsonObject.addProperty("mese", month);
                    jsonObject.addProperty("anno", year);
                    String requestData = jsonObject.toString();

                    URL url = new URL("http://localhost:8080/EzPlanner/attivitaMese");      //indirizzo per la richiesta delle attivita al server
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);
                    PrintWriter out = new PrintWriter(con.getOutputStream());
                    out.print(requestData);
                    out.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line;
                    StringBuffer receivedInput = new StringBuffer();
                    while((line = br.readLine()) != null) {
                        receivedInput.append(line);
                    }
                    br.close();

                    JsonElement json = gson.fromJson(receivedInput.toString(), JsonElement.class);      //deserializzazione dei dati ricevuti dal server
                    JsonArray attivita = json.getAsJsonArray();

                    for(int i = 0; i < attivita.size(); i++) {
                        JsonObject obj = attivita.get(i).getAsJsonObject();
                        NumeroAttivitaGiornaliere a = new NumeroAttivitaGiornaliere(obj.get("data").getAsString(), obj.get("numAttivita").getAsInt());
                        attivitaMensili.add(a);
                    }
                } catch (Exception e) {
                    logger.error("Errore nel reperire le attività mensili: " + e.getMessage());
                }
                
                Platform.runLater(new Runnable(){
                    @Override public void run() {
                        monthYearLabel.setText(d.getMonth() + " " + year);      //aggiorno il label della data con quella selezionata

                        int rowIndex = 0;   //indice di riga all'interno del calendario
                        int activityIndex = 0;

                        for(int i = 1; i <= d.lengthOfMonth(); i++){
                            LocalDate date = LocalDate.of(year, month, i);

                            String dayInfo = "\n";
                            if(activityIndex < attivitaMensili.size()){
                                if(attivitaMensili.get(activityIndex).getData().equals(date.toString())){
                                    dayInfo += attivitaMensili.get(activityIndex).getNumAttivita() + " activities";
                                    activityIndex++;
                                } else
                                    dayInfo += "No activity";
                            } else {
                                dayInfo += "No activity";
                            }
                            Label l = new Label(i + " " + date.getDayOfWeek().toString().substring(0, 3) + dayInfo);    //Il label rappresenta una cella del calendario

                            l.setStyle("-fx-border-color: #5BC0DE; -fx-border-width: 2; -fx-border-radius: 3;");

                            if(date.getDayOfWeek().ordinal() >= 5)
                                l.setTextFill(Color.rgb(220, 53, 69));        //Se il giorno è Sabato o Domenica viene mostrato di colore Rosso
                            if(date.equals(sessionData.getDate()))
                                l.setStyle("-fx-background-color: #BEE3F8; -fx-background-radius: 3;");  //La cella corrispondente alla data selezionata viene mostrata con sfondo lightblue

                            l.setPadding(new Insets(5));
                            l.setAlignment(Pos.TOP_LEFT);
                            l.setOnMouseClicked(event -> handleCellClick(event, date));     //Aggiungo un click event handler su ogni cella del calendario (per passare alla schermata di gestione delle attività)
                            l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                            calendarContainer.add(l, date.getDayOfWeek().ordinal(), rowIndex);      //aggiunta della cella all'interno del GridContainer

                            if(date.getDayOfWeek().ordinal() == 6)
                                rowIndex++;
                        }
                    }
                });
                return null;
            }
        };
        new Thread(task).start();
    }
    
    private void handleCellClick(MouseEvent e, LocalDate d) {       //handler degli eventi click su cella del calendario
        logger.info("Premuto: " + d);
        sessionData.setDate(d);
        try {
            App.setRoot("activity");
        } catch(IOException ex) {
            logger.error("Errore nel passare alla gestione delle attività: " + ex.getMessage());
        }
    }
    
    @FXML
    public void prevMonth() {       //metodo che permette di sottrarre un mese alla data selezionata (viene aggiornato anche il calendario)
        sessionData.setDate(sessionData.getDate().minusMonths(1));
        dateField.setValue(sessionData.getDate());
        
        buildCalendar(sessionData.getDate());
    }
    
    @FXML
    public void nextMonth() {       //metodo che permette di aggiungere un mese alla data selezionata (viene aggiornato anche il calendario)
        sessionData.setDate(sessionData.getDate().plusMonths(1));
        dateField.setValue(sessionData.getDate());
        
        buildCalendar(sessionData.getDate());
    }
    
    @FXML
    public void updateFromPicker() {    //Alla modifica della data tramite il DatePicker aggiorno la data selezionata e il calendario
        sessionData.setDate(dateField.getValue());
        
        buildCalendar(sessionData.getDate());
    }
    
    @FXML
    public void resetCalendar() {       //reimposta la data selezionata alla data corrente (aggiornando anche il calendario)
        sessionData.setDate(LocalDate.now());
        dateField.setValue(sessionData.getDate());
        
        buildCalendar(sessionData.getDate());
    }
    
    @FXML
    public void goToSelectedDate() throws IOException {
        App.setRoot("activity");
    }
}