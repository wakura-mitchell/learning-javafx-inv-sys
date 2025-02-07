module com.sate.inv {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;


    opens com.sate.inv to javafx.fxml;
    exports com.sate.inv;
}