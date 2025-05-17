package modelo;

import java.time.LocalDate;

public class Prestamo {
    public Libro libro;
    public Usuario usuario;
    public LocalDate fechaPrestamo;
    public LocalDate fechaVencimiento;
    public boolean devuelto;

    public Prestamo(Libro libro, Usuario usuario, LocalDate fechaPrestamo, LocalDate fechaVencimiento) {
        this.libro = libro;
        this.usuario = usuario;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaVencimiento = fechaVencimiento;
        this.devuelto = false;
    }

    public Libro getLibro() {
        return libro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void marcarDevuelto() {
        this.devuelto = true;
    }

    public void verificarVencimiento() {
        LocalDate hoy = LocalDate.now();
        if (!devuelto && hoy.isAfter(fechaVencimiento.minusDays(1))) {
            usuario.actualizar("El préstamo del libro " + libro.getTitulo() + " está por vencer o ha vencido.");
        }
    }
}