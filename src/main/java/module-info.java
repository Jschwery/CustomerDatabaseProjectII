 module com.example.customerdatabaseprojectii {
    requires java.sql;
    requires java.desktop;
    requires javafx.controls;
     requires javafx.graphics;
     requires javafx.fxml;

     opens com.example.customerdatabaseprojectii to javafx.fxml;
    exports com.example.customerdatabaseprojectii;
    exports com.example.customerdatabaseprojectii.view;
    opens com.example.customerdatabaseprojectii.view to javafx.fxml;
    exports com.example.customerdatabaseprojectii.entity;
    opens com.example.customerdatabaseprojectii.entity to javafx.fxml;

}
