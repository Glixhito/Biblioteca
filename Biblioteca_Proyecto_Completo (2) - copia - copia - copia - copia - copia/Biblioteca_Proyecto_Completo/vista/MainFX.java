package vista;

import gestor.GestorDatos;
import gestor.GestorPrestamos;
import gestor.GestorUsuarios;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación JavaFX para el sistema de Biblioteca.
 * Punto de entrada para la interfaz gráfica.
 */
public class MainFX extends Application {
    
    private static GestorDatos gestorDatos;
    private static GestorUsuarios gestorUsuarios;
    private static GestorPrestamos gestorPrestamos;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Inicializar los gestores
            gestorDatos = GestorDatos.getInstancia();
            gestorUsuarios = GestorUsuarios.getInstancia();
            gestorPrestamos = GestorPrestamos.getInstancia();
            
            // Cargar la vista de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/fxml/login.fxml"));
            Parent root = loader.load();
            
            // Configurar la ventana principal
            Scene scene = new Scene(root, 600, 400);
            primaryStage.setTitle("Sistema de Biblioteca - Inicio de Sesión");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();
            
        } catch (Exception e) {
            mostrarError("Error de Inicialización", 
                        "No se pudo iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Obtiene la instancia del gestor de datos
     */
    public static GestorDatos getGestorDatos() {
        return gestorDatos;
    }
    
    /**
     * Obtiene la instancia del gestor de usuarios
     */
    public static GestorUsuarios getGestorUsuarios() {
        return gestorUsuarios;
    }
    
    /**
     * Obtiene la instancia del gestor de préstamos
     */
    public static GestorPrestamos getGestorPrestamos() {
        return gestorPrestamos;
    }
    
    /**
     * Muestra un diálogo de error
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Método principal que inicia la aplicación
     */
    public static void main(String[] args) {
        launch(args);
    }
}
