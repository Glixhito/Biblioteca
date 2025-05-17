package gestor;

import modelo.*;
import java.time.LocalDate;
import java.util.*;

public class GestorPrestamos {
    public static GestorPrestamos instancia;
    public List<Prestamo> prestamosActivos = new ArrayList<>();
    public List<Prestamo> historialPrestamos = new ArrayList<>();

    private GestorPrestamos() {}

    public static GestorPrestamos getInstancia() {
        if (instancia == null) {
            instancia = new GestorPrestamos();
        }
        return instancia;
    }

    public void registrarPrestamo(Prestamo prestamo) {
        // Verificar si el libro ya está prestado y no devuelto
        for (Prestamo p : prestamosActivos) {
            if (p.getLibro().equals(prestamo.getLibro()) && !p.isDevuelto()) {
                System.out.println("❌ El libro '" + prestamo.getLibro().getTitulo() + "' ya está prestado.");
                return;
            }
        }

        prestamosActivos.add(prestamo);
        System.out.println("✅ Préstamo registrado para " + prestamo.getUsuario().getNombre() +
                ". Fecha de devolución: " + prestamo.getFechaVencimiento());
    }

    public boolean devolverPrestamo(Usuario usuario, String tituloLibro) {
        Iterator<Prestamo> it = prestamosActivos.iterator();
        while (it.hasNext()) {
            Prestamo p = it.next();
            if (p.getUsuario().equals(usuario) &&
                    p.getLibro().getTitulo().equalsIgnoreCase(tituloLibro) &&
                    !p.isDevuelto()) {

                p.marcarDevuelto();
                historialPrestamos.add(p);
                it.remove();
                return true;
            }
        }
        return false;
    }

    public List<Prestamo> obtenerPrestamosDeUsuario(Usuario usuario) {
        List<Prestamo> lista = new ArrayList<>();
        for (Prestamo p : prestamosActivos) {
            if (p.getUsuario().equals(usuario) && !p.isDevuelto()) {
                lista.add(p);
            }
        }
        return lista;
    }

    public List<Prestamo> obtenerHistorialUsuario(Usuario usuario) {
        List<Prestamo> lista = new ArrayList<>();
        for (Prestamo p : historialPrestamos) {
            if (p.getUsuario().equals(usuario)) {
                lista.add(p);
            }
        }
        return lista;
    }

    public void listarPrestamos() {
        System.out.println("\n📄 Préstamos activos:");
        if (prestamosActivos.isEmpty()) {
            System.out.println("No hay préstamos activos.");
            return;
        }

        for (Prestamo p : prestamosActivos) {
            if (!p.isDevuelto()) {
                System.out.println("• Libro: " + p.getLibro().getTitulo() +
                        " | Usuario: " + p.getUsuario().getNombre() +
                        " | Prestado: " + p.getFechaPrestamo() +
                        " | Vence: " + p.getFechaVencimiento());
            }
        }
    }

    public void verificarVencimientos() {
        LocalDate hoy = LocalDate.now();
        boolean hayVencimientos = false;

        for (Prestamo p : prestamosActivos) {
            if (!p.isDevuelto() && hoy.isAfter(p.getFechaVencimiento())) {
                System.out.println("⚠️ Préstamo vencido: " + p.getLibro().getTitulo() +
                        " para " + p.getUsuario().getNombre());
                hayVencimientos = true;
                p.verificarVencimiento();
            }
        }

        if (!hayVencimientos) {
            System.out.println("✅ No hay préstamos vencidos.");
        }
    }
}