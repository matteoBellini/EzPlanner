package it.unipi.EzPlannerServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EzPlannerServerApplication {

	public static void main(String[] args) {
                InitServer.init();
            
		SpringApplication.run(EzPlannerServerApplication.class, args);
	}

}
