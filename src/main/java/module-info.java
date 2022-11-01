module com.example.customerdatabaseprojectii {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.customerdatabaseprojectii to javafx.fxml;
    exports com.example.customerdatabaseprojectii;
    exports com.example.customerdatabaseprojectii.view;
    opens com.example.customerdatabaseprojectii.view to javafx.fxml;
}