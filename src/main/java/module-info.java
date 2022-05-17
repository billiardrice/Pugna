@SuppressWarnings ("all")
module dev.wrice {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires json.simple;
    requires java.logging;

    opens dev.wrice to javafx.fxml;

    exports dev.wrice;
}