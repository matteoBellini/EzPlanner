module com.mycompany.applicazione {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.apache.logging.log4j;

    opens it.unipi.EzPlannerClient to javafx.fxml;
    exports it.unipi.EzPlannerClient;
}
