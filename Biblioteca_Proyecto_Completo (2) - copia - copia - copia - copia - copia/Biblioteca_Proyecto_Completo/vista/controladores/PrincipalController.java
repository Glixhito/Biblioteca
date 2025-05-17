package vista.controladores;

import gestor.GestorDatos;
import gestor.GestorPrestamos;
import gestor.GestorUsuarios;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.Libro;
import modelo.Prestamo;
import modelo.Rol;
import modelo.Usuario;
import vista.MainFX;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PrincipalController {
    
    @FXML
    private Label labelUsuarioActual;
    
    @FXML
    private Label labelRolActual;
    
    @FXML
    private VBox contenidoPanel;
    
    private Usuario usuarioActual;
    private GestorDatos gestorDatos;
    private GestorUsuarios gestorUsuarios;
    private GestorPrestamos gestorPrestamos;
    
    @FXML
    public void initialize() {
        gestorDatos = MainFX.getGestorDatos();
        gestorUsuarios = MainFX.getGestorUsuarios();
        gestorPrestamos = MainFX.getGestorPrestamos();
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        labelUsuarioActual.setText(String.format("Usuario: %s (%s)", 
            usuario.getNombre(), usuario.getCedula()));
        if (labelRolActual != null) {
            labelRolActual.setText(String.format("[%s]", usuario.getRol()));
        }
    }
    
    // Acciones del menú Catálogo
    @FXML
    private void handleVerCatalogo(ActionEvent event) {
        mostrarConsoleOutputEnPanel(() -> gestorDatos.listarCatalogo());
    }
    
    @FXML
    private void handleAgregarCategoria(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva Categoría");
        dialog.setHeaderText("Crear nueva categoría");
        dialog.setContentText("Nombre de la categoría padre:");
        
        Optional<String> padre = dialog.showAndWait();
        if (padre.isPresent()) {
            dialog.setContentText("Nombre de la nueva categoría:");
            Optional<String> nueva = dialog.showAndWait();
            if (nueva.isPresent()) {
                gestorDatos.agregarCategoria(padre.get(), nueva.get());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", 
                    "Categoría creada correctamente.");
            }
        }
    }
    
    @FXML
    private void handleAgregarLibro(ActionEvent event) {
        Dialog<Libro> dialog = new Dialog<>();
        dialog.setTitle("Agregar Libro");
        dialog.setHeaderText("Ingrese los datos del libro");
        
        ButtonType buttonTypeOk = new ButtonType("Agregar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, ButtonType.CANCEL);
        
        TextField categoria = new TextField();
        TextField titulo = new TextField();
        TextField autor = new TextField();
        
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("Categoría:"), categoria,
            new Label("Título:"), titulo,
            new Label("Autor:"), autor
        );
        dialog.getDialogPane().setContent(content);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeOk) {
                if (!categoria.getText().isEmpty() && !titulo.getText().isEmpty() && !autor.getText().isEmpty()) {
                    return new Libro(titulo.getText(), autor.getText());
                }
            }
            return null;
        });
        
        Optional<Libro> result = dialog.showAndWait();
        result.ifPresent(libro -> {
            gestorDatos.agregarLibroEnCategoria(categoria.getText(), libro);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", 
                "Libro agregado correctamente.");
        });
    }
    
    // Acciones del menú Usuarios
    @FXML
    private void handleRegistrarUsuario(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/fxml/registro.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Registro de Usuario");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", 
                "No se pudo abrir la ventana de registro.");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleListarUsuarios(ActionEvent event) {
        mostrarConsoleOutputEnPanel(() -> gestorUsuarios.listarUsuarios());
    }
    
    // Acciones del menú Préstamos (Admin)
    @FXML
    private void handleRegistrarPrestamo(ActionEvent event) {
        Dialog<Prestamo> dialog = new Dialog<>();
        dialog.setTitle("Registrar Préstamo");
        dialog.setHeaderText("Ingrese los datos del préstamo");
        
        ButtonType buttonTypeOk = new ButtonType("Registrar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, ButtonType.CANCEL);
        
        TextField cedula = new TextField();
        TextField titulo = new TextField();
        TextField dias = new TextField();
        
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("Cédula del usuario:"), cedula,
            new Label("Título del libro:"), titulo,
            new Label("Días de préstamo:"), dias
        );
        dialog.getDialogPane().setContent(content);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeOk) {
                try {
                    Usuario usuario = gestorUsuarios.buscarUsuario(cedula.getText());
                    Libro libro = gestorDatos.buscarLibroPorTitulo(titulo.getText());
                    int diasPrestamo = Integer.parseInt(dias.getText());
                    
                    if (usuario != null && libro != null && diasPrestamo > 0) {
                        LocalDate hoy = LocalDate.now();
                        return new Prestamo(libro, usuario, hoy, hoy.plusDays(diasPrestamo));
                    }
                } catch (NumberFormatException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", 
                        "El número de días debe ser un número válido.");
                }
            }
            return null;
        });
        
        Optional<Prestamo> result = dialog.showAndWait();
        result.ifPresent(prestamo -> {
            gestorPrestamos.registrarPrestamo(prestamo);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", 
                "Préstamo registrado correctamente.");
        });
    }
    
    @FXML
    private void handleListarPrestamos(ActionEvent event) {
        mostrarConsoleOutputEnPanel(() -> gestorPrestamos.listarPrestamos());
    }
    
    @FXML
    private void handleVerificarVencimientos(ActionEvent event) {
        mostrarConsoleOutputEnPanel(() -> gestorPrestamos.verificarVencimientos());
    }
    
    @FXML
    private void handleReporteLibros(ActionEvent event) {
        mostrarConsoleOutputEnPanel(() -> gestorDatos.listarCatalogo());
    }
    
    // Acciones del menú Préstamos (Lector)
    @FXML
    private void handleSolicitarPrestamo(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Solicitar Préstamo");
        dialog.setHeaderText("Ingrese el título del libro");
        dialog.setContentText("Título:");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String titulo = result.get();
            Libro libro = gestorDatos.buscarLibroPorTitulo(titulo);
            
            if (libro != null) {
                LocalDate hoy = LocalDate.now();
                Prestamo prestamo = new Prestamo(libro, usuarioActual, hoy, hoy.plusDays(7));
                gestorPrestamos.registrarPrestamo(prestamo);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", 
                    "Préstamo registrado correctamente.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", 
                    "Libro no encontrado.");
            }
        }
    }
    
    @FXML
    private void handleDevolverLibro(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Devolver Libro");
        dialog.setHeaderText("Ingrese el título del libro a devolver");
        dialog.setContentText("Título:");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String titulo = result.get();
            boolean devuelto = gestorPrestamos.devolverPrestamo(usuarioActual, titulo);
            
            if (devuelto) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", 
                    "Libro devuelto correctamente.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", 
                    "No se encontró un préstamo activo con ese título.");
            }
        }
    }
    
    @FXML
    private void handleVerPrestamosActuales(ActionEvent event) {
        List<Prestamo> prestamos = gestorPrestamos.obtenerPrestamosDeUsuario(usuarioActual);
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        
        if (prestamos.isEmpty()) {
            textArea.setText("No tienes préstamos activos.");
        } else {
            StringBuilder sb = new StringBuilder("Tus préstamos activos:\n\n");
            for (Prestamo p : prestamos) {
                sb.append("• ").append(p.getLibro().getTitulo())
                  .append(" | Fecha préstamo: ").append(p.getFechaPrestamo())
                  .append(" | Vence: ").append(p.getFechaVencimiento())
                  .append("\n");
            }
            textArea.setText(sb.toString());
        }
        
        contenidoPanel.getChildren().clear();
        contenidoPanel.getChildren().add(textArea);
    }
    
    @FXML
    private void handleVerHistorialPrestamos(ActionEvent event) {
        List<Prestamo> historial = gestorPrestamos.obtenerHistorialUsuario(usuarioActual);
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        
        if (historial.isEmpty()) {
            textArea.setText("No tienes préstamos en tu historial.");
        } else {
            StringBuilder sb = new StringBuilder("Tu historial de préstamos:\n\n");
            for (Prestamo p : historial) {
                sb.append("• ").append(p.getLibro().getTitulo())
                  .append(" | Fecha préstamo: ").append(p.getFechaPrestamo())
                  .append(" | Devuelto: ").append(p.isDevuelto() ? "Sí" : "No")
                  .append("\n");
            }
            textArea.setText(sb.toString());
        }
        
        contenidoPanel.getChildren().clear();
        contenidoPanel.getChildren().add(textArea);
    }
    
    // Acciones del menú Sistema
    @FXML
    private void handleCerrarSesion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) labelUsuarioActual.getScene().getWindow();
            stage.setTitle("Inicio de Sesión");
            stage.setScene(new Scene(root, 600, 400));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", 
                "No se pudo volver a la pantalla de inicio de sesión.");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSalir(ActionEvent event) {
        Platform.exit();
    }
    
    // Métodos auxiliares
    private void mostrarConsoleOutputEnPanel(Runnable action) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream oldOut = System.out;
        System.setOut(printStream);
        
        try {
            action.run();
            String output = outputStream.toString();
            
            TextArea textArea = new TextArea(output);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            
            contenidoPanel.getChildren().clear();
            contenidoPanel.getChildren().add(textArea);
        } finally {
            System.setOut(oldOut);
        }
    }
    
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
