package gestor;

import modelo.Usuario;
import modelo.Rol;
import util.SecurityUtils;
import java.util.*;

public class GestorUsuarios {
    public static GestorUsuarios instancia;
    public Map<String, Usuario> usuarios = new HashMap<>();

    public GestorUsuarios() {}

    public static GestorUsuarios getInstancia() {
        if (instancia == null) {
            instancia = new GestorUsuarios();
        }
        return instancia;
    }

    public Usuario autenticarUsuario(String cedula, String password) {
        Usuario usuario = buscarUsuario(cedula);
        if (usuario != null && usuario.verificarPassword(password)) {
            return usuario;
        }
        return null;
    }

    public void registrarUsuario(Usuario usuario, String password) {
        if (usuarios.containsKey(usuario.getCedula())) {
            System.out.println("❌ Usuario con cédula " + usuario.getCedula() + " ya registrado.");
            return;
        }

        String salt = SecurityUtils.generarSalt();
        String passwordHash = SecurityUtils.hashPassword(password, salt);

        usuario = new Usuario(
                usuario.getNombre(),
                usuario.getCedula(),
                usuario.getRol(),
                passwordHash,
                salt
        );

        usuarios.put(usuario.getCedula(), usuario);
        System.out.println("✅ Usuario " + usuario.getNombre() + " registrado correctamente.");
    }

    public Usuario buscarUsuario(String cedula) {
        return usuarios.get(cedula);
    }

    public void listarUsuarios() {
        System.out.println("\n👤 Usuarios registrados (" + usuarios.size() + "):");
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        for (Usuario u : usuarios.values()) {
            System.out.println("• " + u.getNombre() + " - " + u.getCedula() + " - " + u.getRol());
        }
    }

    public Collection<Usuario> getTodosUsuarios() {
        return usuarios.values();
    }
}