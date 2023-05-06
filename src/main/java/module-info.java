module com.grzegorz2047.genetic.pathfinders {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.desktop;

    opens com.grzegorz2047.genetic.pathfinders to javafx.fxml;
    exports com.grzegorz2047.genetic.pathfinders;
    exports com.grzegorz2047.genetic.pathfinders.maploader;
    opens com.grzegorz2047.genetic.pathfinders.maploader to javafx.fxml;
}