package principal;

import gestor.*;
import modelo.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GestorDatos gestorDatos = GestorDatos.getInstancia();
        GestorUsuarios gestorUsuarios = GestorUsuarios.getInstancia();
        GestorPrestamos gestorPrestamos = GestorPrestamos.getInstancia();

        try (Scanner scanner = new Scanner(System.in)) {
            boolean continuar = true;

            while (continuar) {
                System.out.println("\n--- BIENVENIDO AL SISTEMA DE BIBLIOTECA ---");
                System.out.println("1. Registrarse");
                System.out.println("2. Iniciar sesión");
                System.out.println("0. Salir del sistema");
                int opcion = pedirEntradaEntero(scanner, "Seleccione una opción: ");

                Usuario usuarioActual = null;

                switch (opcion) {
                    case 1: // Registro
                        usuarioActual = procesoRegistro(scanner, gestorUsuarios);
                        if (usuarioActual != null) {
                            System.out.println("✅ Usuario registrado correctamente. Iniciando sesión...");
                            manejarSesion(scanner, usuarioActual, gestorDatos, gestorUsuarios, gestorPrestamos);
                        }
                        break;

                    case 2: // Iniciar sesión
                        usuarioActual = procesoInicioSesion(scanner, gestorUsuarios);
                        if (usuarioActual != null) {
                            manejarSesion(scanner, usuarioActual, gestorDatos, gestorUsuarios, gestorPrestamos);
                        }
                        break;

                    case 0: // Salir
                        continuar = false;
                        System.out.println("👋 Sistema finalizado.");
                        break;

                    default:
                        System.out.println("❌ Opción inválida.");
                }
            }
        }
    }

    private static void manejarSesion(Scanner scanner, Usuario usuarioActual, GestorDatos gestorDatos,
                                      GestorUsuarios gestorUsuarios, GestorPrestamos gestorPrestamos) {
        boolean enSesion = true;
        while (enSesion) {
            System.out.println("\n--- MENÚ " + usuarioActual.getRol() + " ---");
            System.out.println("Bienvenido, " + usuarioActual.getNombre() + " (" + usuarioActual.getCedula() + ")");
            System.out.println("1. Ver catálogo");

            if (usuarioActual.getRol() == Rol.ADMIN) {
                System.out.println("2. Crear nueva categoría");
                System.out.println("3. Agregar libro a categoría");
                System.out.println("4. Registrar usuario");
                System.out.println("5. Listar usuarios");
                System.out.println("6. Registrar préstamo");
                System.out.println("7. Listar préstamos");
                System.out.println("8. Verificar vencimientos");
                System.out.println("9. Reporte: Libros por categoría");
            } else {
                System.out.println("2. Solicitar préstamo");
                System.out.println("3. Devolver libro");
                System.out.println("4. Ver mis préstamos");
                System.out.println("5. Ver mi historial de préstamos");
            }

            System.out.println("0. Cerrar sesión");
            System.out.println("-1. Salir del sistema");
            int opcion = pedirEntradaEntero(scanner, "Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    gestorDatos.listarCatalogo();
                    break;

                case 2:
                    if (usuarioActual.getRol() == Rol.ADMIN) {
                        String padre = pedirEntradaTexto(scanner, "Nombre de la categoría padre: ");
                        String nueva = pedirEntradaTexto(scanner, "Nombre de la nueva categoría: ");
                        gestorDatos.agregarCategoria(padre, nueva);
                    } else {
                        String titulo = pedirEntradaTexto(scanner, "Título del libro: ");
                        Libro libro = gestorDatos.buscarLibroPorTitulo(titulo);
                        if (libro != null) {
                            LocalDate hoy = LocalDate.now();
                            Prestamo prestamo = new Prestamo(libro, usuarioActual, hoy, hoy.plusDays(7));
                            gestorPrestamos.registrarPrestamo(prestamo);
                        } else {
                            System.out.println("❌ Libro no encontrado.");
                        }
                    }
                    break;

                case 3:
                    if (usuarioActual.getRol() == Rol.ADMIN) {
                        String categoria = pedirEntradaTexto(scanner, "Nombre de la categoría: ");
                        String titulo = pedirEntradaTexto(scanner, "Título del libro: ");
                        String autor = pedirEntradaTexto(scanner, "Autor del libro: ");
                        gestorDatos.agregarLibroEnCategoria(categoria, new Libro(titulo, autor));
                    } else {
                        String titulo = pedirEntradaTexto(scanner, "Título del libro a devolver: ");
                        boolean devuelto = gestorPrestamos.devolverPrestamo(usuarioActual, titulo);
                        System.out.println(devuelto ? "✅ Libro devuelto correctamente." : "❌ No se encontró un préstamo activo con ese título.");
                    }
                    break;

                case 4:
                    if (usuarioActual.getRol() == Rol.ADMIN) {
                        String nombreUsuario = pedirEntradaTexto(scanner, "Nombre del usuario: ");
                        String cedUsuario = pedirEntradaTexto(scanner, "Cédula: ");
                        String password = pedirEntradaTexto(scanner, "Contraseña: ");

                        Rol rolUsuario = null;
                        while (rolUsuario == null) {
                            String rolStr = pedirEntradaTexto(scanner, "Rol (ADMIN/LECTOR): ").toUpperCase();
                            try {
                                rolUsuario = Rol.valueOf(rolStr);
                            } catch (IllegalArgumentException e) {
                                System.out.println("❌ Rol inválido.");
                            }
                        }

                        Usuario nuevoUsuario = new Usuario(nombreUsuario, cedUsuario, rolUsuario);
                        gestorUsuarios.registrarUsuario(nuevoUsuario, password);
                    } else {
                        List<Prestamo> prestamos = gestorPrestamos.obtenerPrestamosDeUsuario(usuarioActual);
                        System.out.println("\n📚 Tus préstamos activos:");
                        if (prestamos.isEmpty()) {
                            System.out.println("No tienes préstamos activos.");
                        } else {
                            for (Prestamo p : prestamos) {
                                System.out.println("• " + p.getLibro().getTitulo() +
                                        " | Fecha préstamo: " + p.getFechaPrestamo() +
                                        " | Vence: " + p.getFechaVencimiento());
                            }
                        }
                    }
                    break;

                case 5:
                    if (usuarioActual.getRol() == Rol.ADMIN) {
                        gestorUsuarios.listarUsuarios();
                    } else {
                        List<Prestamo> historial = gestorPrestamos.obtenerHistorialUsuario(usuarioActual);
                        System.out.println("\n📚 Tu historial de préstamos:");
                        if (historial.isEmpty()) {
                            System.out.println("No tienes préstamos en tu historial.");
                        } else {
                            for (Prestamo p : historial) {
                                System.out.println("• " + p.getLibro().getTitulo() +
                                        " | Fecha préstamo: " + p.getFechaPrestamo() +
                                        " | Devuelto: " + (p.isDevuelto() ? "Sí" : "No"));
                            }
                        }
                    }
                    break;

                case 6:
                    if (usuarioActual.getRol() == Rol.ADMIN) {
                        String ced = pedirEntradaTexto(scanner, "Cédula del usuario: ");
                        Usuario u = gestorUsuarios.buscarUsuario(ced);
                        if (u == null) {
                            System.out.println("❌ Usuario no encontrado.");
                            break;
                        }
                        String tituloLibro = pedirEntradaTexto(scanner, "Título del libro: ");
                        Libro libro = gestorDatos.buscarLibroPorTitulo(tituloLibro);
                        if (libro == null) {
                            System.out.println("❌ Libro no encontrado.");
                            break;
                        }
                        int dias = -1;
                        while (dias < 1) {
                            dias = pedirEntradaEntero(scanner, "Días de préstamo: ");
                            if (dias < 1) {
                                System.out.println("❌ Número de días inválido. Debe ser mayor que 0.");
                            }
                        }
                        LocalDate hoy = LocalDate.now();
                        Prestamo prestamo = new Prestamo(libro, u, hoy, hoy.plusDays(dias));
                        gestorPrestamos.registrarPrestamo(prestamo);
                    } else {
                        System.out.println("❌ Opción inválida.");
                    }
                    break;

                case 7:
                    if (usuarioActual.getRol() == Rol.ADMIN) {
                        gestorPrestamos.listarPrestamos();
                    } else {
                        System.out.println("❌ Opción inválida.");
                    }
                    break;

                case 8:
                    if (usuarioActual.getRol() == Rol.ADMIN) {
                        gestorPrestamos.verificarVencimientos();
                    } else {
                        System.out.println("❌ Opción inválida.");
                    }
                    break;

                case 9:
                    if (usuarioActual.getRol() == Rol.ADMIN) {
                        gestorDatos.listarCatalogo();
                    } else {
                        System.out.println("❌ Opción inválida.");
                    }
                    break;

                case 0:
                    System.out.println("👋 Sesión cerrada. Volviendo al menú principal...");
                    return;

                case -1:
                    System.out.println("👋 Sistema finalizado.");
                    System.exit(0);
                    break;

                default:
                    System.out.println("❌ Opción inválida.");
            }
        }
    }

    private static Usuario procesoRegistro(Scanner scanner, GestorUsuarios gestorUsuarios) {
        System.out.println("\n--- REGISTRO DE NUEVO USUARIO ---");
        String nombre = pedirEntradaTexto(scanner, "Nombre del usuario: ");
        String cedula = pedirEntradaTexto(scanner, "Cédula: ");
        String password = pedirEntradaTexto(scanner, "Contraseña: ");

        if (gestorUsuarios.buscarUsuario(cedula) != null) {
            System.out.println("❌ Usuario ya registrado con esa cédula.");
            return null;
        }

        Rol rol = null;
        while (rol == null) {
            String rolStr = pedirEntradaTexto(scanner, "Rol (ADMIN/LECTOR): ").toUpperCase();
            try {
                rol = Rol.valueOf(rolStr);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Rol inválido. Por favor, elija ADMIN o LECTOR.");
            }
        }

        Usuario nuevoUsuario = new Usuario(nombre, cedula, rol);
        gestorUsuarios.registrarUsuario(nuevoUsuario, password);

        return nuevoUsuario;
    }

    private static Usuario procesoInicioSesion(Scanner scanner, GestorUsuarios gestorUsuarios) {
        System.out.println("\n--- INICIO DE SESIÓN ---");
        String cedula = pedirEntradaTexto(scanner, "Ingrese su cédula: ");
        String password = pedirEntradaTexto(scanner, "Ingrese su contraseña: ");

        Usuario usuario = gestorUsuarios.autenticarUsuario(cedula, password);
        if (usuario == null) {
            System.out.println("❌ Credenciales incorrectas o usuario no encontrado.");
            return null;
        }
        return usuario;
    }

    private static String pedirEntradaTexto(Scanner scanner, String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    private static int pedirEntradaEntero(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("❌ Entrada no válida. Por favor, ingrese un número.");
            }
        }
    }
}
