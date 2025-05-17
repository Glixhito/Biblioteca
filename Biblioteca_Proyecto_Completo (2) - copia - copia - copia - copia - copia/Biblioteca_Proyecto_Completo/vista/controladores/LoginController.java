package vista.controladores;

import gestor.GestorUsuarios;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Rol;
import modelo.Usuario;
import vista.MainFX;

import java.io.IOException;

/**
 * Controlador para la pantalla de inicio de sesión
 */
public class LoginController {
    
    @FXML
    private TextField campoUsuario;
    
    @FXML
    private PasswordField campoPassword;
    
    @FXML
    private Button botonLogin;
    
    @FXML
    private Button botonRegistro;
    
    private GestorUsuarios gestorUsuarios;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        // Obtener la instancia del gestor de usuarios
        gestorUsuarios = MainFX.getGestorUsuarios();
    }
    
    /**
     * Maneja el evento de inicio de sesión
     */
    @FXML
    private void handleLoginAction(ActionEvent event) {
        String cedula = campoUsuario.getText().trim();
        String password = campoPassword.getText();
        
        if (cedula.isEmpty() || password.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de inicio de sesión", 
                          "Por favor, ingrese cédula y contraseña.");
            return;
        }
        
        Usuario usuario = gestorUsuarios.autenticarUsuario(cedula, password);
        
        if (usuario == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de inicio de sesión", 
                          "Credenciales incorrectas o usuario no encontrado.");
            return;
        }
        
        navegarAVistaPrincipal(usuario);
    }
    
    /**
     * Navega a la vista principal correspondiente según el rol del usuario
     */
    private void navegarAVistaPrincipal(Usuario usuario) {
        try {
            String vistaFXML = usuario.getRol() == Rol.ADMIN ? 
                "/vista/fxml/principal_admin.fxml" : 
                "/vista/fxml/principal_lector.fxml";
                
            FXMLLoader loader = new FXMLLoader(getClass().getResource(vistaFXML));
            Parent root = loader.load();
            
            PrincipalController controller = loader.getController();
            controller.setUsuario(usuario);
            
            Stage stage = (Stage) campoUsuario.getScene().getWindow();
            stage.setTitle("Sistema de Biblioteca - " + usuario.getRol());
            stage.setScene(new Scene(root));
            stage.setWidth(800);
            stage.setHeight(600);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", 
                         "No se pudo cargar la pantalla principal.");
            e.printStackTrace();
        }
    }
    
    /**
     * Maneja el evento de navegación a la pantalla de registro
     */
    @FXML
    private void handleRegistroAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/fxml/registro.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) campoUsuario.getScene().getWindow();
            stage.setTitle("Registro de Usuario");
            stage.setScene(new Scene(root, 600, 500));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", 
                         "No se pudo abrir la ventana de registro.");
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra una alerta con el mensaje especificado
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
