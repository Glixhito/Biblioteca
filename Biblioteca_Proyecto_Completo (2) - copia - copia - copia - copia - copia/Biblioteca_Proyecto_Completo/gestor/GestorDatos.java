package gestor;

import modelo.*;
import java.util.*;

public class GestorDatos {
    public static GestorDatos instancia;
    public Categoria raizCatalogo = new Categoria("Catálogo general");
    public Map<String, Categoria> mapaCategorias = new HashMap<>();
    public Set<Libro> todosLibros = new HashSet<>();

    private GestorDatos() {
        mapaCategorias.put(normalizarNombre(raizCatalogo.getNombre()), raizCatalogo);
    }

    public static GestorDatos getInstancia() {
        if (instancia == null) {
            instancia = new GestorDatos();
        }
        return instancia;
    }

    // Método para normalizar nombres
    public String normalizarNombre(String nombre) {
        return nombre.toLowerCase()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("ñ", "n");
    }

    public void agregarCategoria(String nombrePadre, String nombreNueva) {
        String nombrePadreNormalizado = normalizarNombre(nombrePadre);
        Categoria padre = mapaCategorias.get(nombrePadreNormalizado);

        if (padre != null) {
            Categoria nueva = new Categoria(nombreNueva);
            padre.agregarElemento(nueva);
            mapaCategorias.put(normalizarNombre(nombreNueva), nueva);
            System.out.println("✅ Categoría '" + nombreNueva + "' creada correctamente en '" + nombrePadre + "'.");
        } else {
            System.out.println("❌ Categoría padre '" + nombrePadre + "' no encontrada.");
        }
    }

    public void agregarLibroEnCategoria(String categoriaNombre, Libro libro) {
        if (todosLibros.contains(libro)) {
            System.out.println("❌ El libro '" + libro.getTitulo() + "' ya existe en el sistema.");
            return;
        }

        Categoria categoria = mapaCategorias.get(normalizarNombre(categoriaNombre));
        if (categoria != null) {
            categoria.agregarElemento(libro);
            todosLibros.add(libro);
            System.out.println("✅ Libro '" + libro.getTitulo() + "' agregado correctamente a '" + categoriaNombre + "'.");
        } else {
            System.out.println("❌ Categoría '" + categoriaNombre + "' no encontrada.");
        }
    }

    public void listarCatalogo() {
        System.out.println("\n📚 Catálogo completo:");
        raizCatalogo.mostrar("  ");
        System.out.println("Total de libros en sistema: " + todosLibros.size());
    }

    public Libro buscarLibroPorTitulo(String titulo) {
        for (Libro libro : todosLibros) {
            if (libro.getTitulo().equalsIgnoreCase(titulo)) {
                return libro;
            }
        }
        return null;
    }

    public List<Libro> buscarLibros(String criterio, String valor) {
        List<Libro> resultados = new ArrayList<>();
        String valorLower = valor.toLowerCase();

        for (Libro libro : todosLibros) {
            boolean coincide = false;

            switch (criterio.toLowerCase()) {
                case "titulo":
                    coincide = libro.getTitulo().toLowerCase().contains(valorLower);
                    break;
                case "autor":
                    coincide = libro.getAutor().toLowerCase().contains(valorLower);
                    break;
                case "isbn":
                    coincide = libro.getIsbn().toLowerCase().contains(valorLower);
                    break;
                case "año":
                    coincide = String.valueOf(libro.getAñoPublicacion()).contains(valor);
                    break;
                case "editorial":
                    coincide = libro.getEditorial().toLowerCase().contains(valorLower);
                    break;
                default:
                    coincide = libro.getTitulo().toLowerCase().contains(valorLower) ||
                            libro.getAutor().toLowerCase().contains(valorLower);
            }

            if (coincide) {
                resultados.add(libro);
            }
        }
        return resultados;
    }

    public Set<String> getCategoriasDisponibles() {
        return new HashSet<>(mapaCategorias.keySet());
    }
}
