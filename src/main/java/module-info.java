module dev.wrice {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;

    opens dev.wrice to javafx.fxml;
    exports dev.wrice;
}