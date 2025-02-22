package it.unipi.EzPlannerClient;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrimaryController {
    
    private static final Logger logger = LogManager.getLogger();
    
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Button loginButton;
    @FXML private Button signUpButton;
    @FXML private Button dataButton;
    @FXML private Label infoLabel;
    @FXML private Label dataLabel;
    
    private SessionData sessionData = SessionData.getIstanza();
    
    @FXML
    private void loadData() {       //metodo che permette di inizializzare il server effettuando la creazione e il popolamento del database
        Task task = new Task<Void>() {
            @Override public Void call() {
                try {
                    Gson gson = new Gson();
                    URL url = new URL("http://localhost:8080/EzPlanner/inizializza");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
            
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String result = gson.fromJson(br, String.class);
            
                    Platform.runLater(new Runnable(){
                        @Override public void run(){
                            dataLabel.setText(result);
                        }
                    });
                } catch (Exception e) {
                    logger.error("Errore nella fase di inizializzazione del database del server: " + e.getMessage());
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    @FXML
    private void login() throws IOException {
        String user = username.getText();
        String passwd = password.getText();
        
        if(user.equals("") || passwd.equals("")){       //se i campi username e password sono vuoti non si può procedere
            infoLabel.setText("You must fill all fields");
            return;
        }
        
        //effettua il login se va a buon fine passa alla schermata home altrimenti mostra il messaggio di errore
        Utente u = new Utente();
        u.setUsername(user);
        u.setPassword(passwd);
        Gson gson = new Gson();
        String utenteSerializzato = gson.toJson(u, Utente.class);
        
        try {
            URL url = new URL("http://localhost:8080/EzPlanner/login");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            PrintWriter out = new PrintWriter(con.getOutputStream());
            out.print(utenteSerializzato);
            out.close();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            u = gson.fromJson(in, Utente.class);
        } catch (Exception e) {
            logger.error("Errore nella fase di login: " + e.getMessage());
        }
        
        if(u == null){      //i dati inseriti non corrispondono ad un utente
            infoLabel.setText("Invalid username or password");
            return;
        }
        
        sessionData.setUtente(u);       //aggiorno i dati di sessione
        sessionData.setDate(LocalDate.now());
        App.setRoot("home");
    }
    
    @FXML
    private void switchToSignUpPage() throws IOException {      //metodo che permette di passare alla schermata di registrazione
        App.setRoot("signUp");
    }
}
