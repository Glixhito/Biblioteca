package modelo;

import util.SecurityUtils;

public class Usuario implements Observador {
    public String nombre;
    public String cedula;
    public Rol rol;
    public String passwordHash;
    public String salt;

    public Usuario(String nombre, String cedula, Rol rol) {
        this(nombre, cedula, rol, "", "");
    }

    public Usuario(String nombre, String cedula, Rol rol, String passwordHash, String salt) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.rol = rol;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public Rol getRol() {
        return rol;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public boolean verificarPassword(String password) {
        return SecurityUtils.verificarPassword(password, this.passwordHash, this.salt);
    }

    @Override
    public void actualizar(String mensaje) {
        System.out.println("🔔 Notificación para " + nombre + ": " + mensaje);
    }
}