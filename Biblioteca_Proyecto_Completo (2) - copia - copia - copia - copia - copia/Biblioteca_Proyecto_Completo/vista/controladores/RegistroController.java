package vista.controladores;

import gestor.GestorUsuarios;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Rol;
import modelo.Usuario;
import vista.MainFX;

import java.io.IOException;

/**
 * Controlador para la pantalla de registro de usuarios
 */
public class RegistroController {
    
    @FXML
    private TextField campoNombre;
    
    @FXML
    private TextField campoCedula;
    
    @FXML
    private PasswordField campoPassword;
    
    @FXML
    private PasswordField campoConfirmarPassword;
    
    @FXML
    private ComboBox<String> comboRol;
    
    @FXML
    private Button botonRegistrar;
    
    @FXML
    private Button botonCancelar;
    
    private GestorUsuarios gestorUsuarios;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        gestorUsuarios = MainFX.getGestorUsuarios();
        comboRol.setItems(FXCollections.observableArrayList("ADMIN", "LECTOR"));
        comboRol.setValue("LECTOR"); // Valor por defecto
    }
    
    /**
     * Maneja el evento de registro de usuario
     */
    @FXML
    private void handleRegistrarAction(ActionEvent event) {
        if (validarFormulario()) {
            String nombre = campoNombre.getText().trim();
            String cedula = campoCedula.getText().trim();
            String password = campoPassword.getText();
            Rol rol = Rol.valueOf(comboRol.getValue());
            
            if (gestorUsuarios.buscarUsuario(cedula) != null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de registro", 
                              "Ya existe un usuario con esa cédula.");
                return;
            }
            
            Usuario nuevoUsuario = new Usuario(nombre, cedula, rol);
            gestorUsuarios.registrarUsuario(nuevoUsuario, password);
            
            mostrarAlerta(Alert.AlertType.INFORMATION, "Registro exitoso", 
                          "Usuario registrado correctamente.");
            
            volverALogin();
        }
    }
    
    /**
     * Maneja el evento de cancelar el registro
     */
    @FXML
    private void handleCancelarAction(ActionEvent event) {
        volverALogin();
    }
    
    /**
     * Valida los campos del formulario de registro
     */
    private boolean validarFormulario() {
        if (campoNombre.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de validación", 
                         "El nombre es obligatorio.");
            return false;
        }
        
        if (campoCedula.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de validación", 
                         "La cédula es obligatoria.");
            return false;
        }
        
        if (campoPassword.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de validación", 
                         "La contraseña es obligatoria.");
            return false;
        }
        
        if (!campoPassword.getText().equals(campoConfirmarPassword.getText())) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de validación", 
                         "Las contraseñas no coinciden.");
            return false;
        }
        
        if (comboRol.getValue() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de validación", 
                         "Debe seleccionar un rol.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Navega de vuelta a la pantalla de login
     */
    private void volverALogin() {
        try {
            // Obtener la ventana actual
            Stage stage = (Stage) botonCancelar.getScene().getWindow();
            
            // Cargar la vista de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/fxml/login.fxml"));
            Parent root = loader.load();
            
            // Configurar y mostrar la escena
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de navegación", 
                         "No se pudo volver a la pantalla de inicio de sesión.");
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
