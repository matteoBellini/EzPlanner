/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.EzPlannerClient;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author matte
 */
public class SignUpController {
    
    private static final Logger logger = LogManager.getLogger();
    
    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private PasswordField confirmPassword;
    @FXML private Label infoLabel;
    @FXML private Button SignUpButton;
    
    private SessionData sessionData = SessionData.getIstanza();
    
    
    @FXML
    public void signup() throws IOException {
        String name = firstName.getText();
        String surname = lastName.getText();
        String usr = username.getText();
        String passwd = password.getText();
        String confPasswd = confirmPassword.getText();
        
        //se anche uno dei campi è vuoto non si ppuò procedere
        if(name.equals("") || surname.equals("") || usr.equals("") || passwd.equals("") || confPasswd.equals("")){
            infoLabel.setText("You must fill all fields");
            return;
        }
        
        if(checkPassword()){
            //sono stati inseriti i dati correttamente quindi posso provare a registrare un nuovo utente
            Utente u = new Utente(usr, name, surname, passwd);
            Gson gson = new Gson();
            String utenteSerializzato = gson.toJson(u, Utente.class);
            
            URL url = new URL("http://localhost:8080/EzPlanner/signUp");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            PrintWriter out = new PrintWriter(con.getOutputStream());
            out.print(utenteSerializzato);
            out.close();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String risposta = gson.fromJson(br, String.class);
            
            if(risposta.equals("OK")){
                logger.info("Registrazione effettuata correttamente");
                sessionData.setUtente(u);
                sessionData.setDate(LocalDate.now());
                App.setRoot("home");    //se la registrazione va a buon fine porto l'utente alla schermata home
            } else {
                infoLabel.setText(risposta);    //la registrazione non è andata a buon fine e quindi mostro il messaggio di risposta ricevuto
            }
        }
    }
    
    @FXML
    public boolean checkPassword() {    //metodo che controlla che le password siano uguali (eventualmente mostra un messaggio di errore)
        if(!password.getText().equals(confirmPassword.getText())){
            infoLabel.setText("Passwords must be equal");
            return false;
        }
        
        infoLabel.setText("");
        return true;
    }
    
    @FXML
    public void backToLogin() throws IOException {
        App.setRoot("primary");
    }
}
