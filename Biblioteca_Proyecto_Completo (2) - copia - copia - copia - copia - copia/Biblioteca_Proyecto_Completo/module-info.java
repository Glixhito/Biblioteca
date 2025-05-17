/**
 * Módulo principal de la aplicación de biblioteca
 */
module biblioteca {
    // Dependencias requeridas
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;
    requires jdk.unsupported;
    
    // Apertura de paquetes para JavaFX
    opens vista to javafx.fxml;
    opens vista.controladores to javafx.fxml;
    opens modelo to javafx.base;
    
    // Exportación de paquetes
    exports vista;
    exports vista.controladores;
    exports modelo;
    exports gestor;
}
