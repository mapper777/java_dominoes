module ipo.ipo_lab_1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;



    opens ipo.ipo_lab_1 to javafx.fxml;
    exports ipo.ipo_lab_1;
}