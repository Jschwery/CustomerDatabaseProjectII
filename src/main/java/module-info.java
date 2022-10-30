module com.example.customerdatabaseprojectii {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.customerdatabaseprojectii to javafx.fxml;
    exports com.example.customerdatabaseprojectii;
}