package modelo;

public class Libro implements ElementoCatalogo {
    public String titulo;
    public String autor;
    public String isbn;
    public int añoPublicacion;
    public String editorial;

    public Libro(String titulo, String autor) {
        this(titulo, autor, "ISBN-NO-ASIGNADO", 0, "Desconocida");
    }

    public Libro(String titulo, String autor, String isbn, int añoPublicacion, String editorial) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.añoPublicacion = añoPublicacion;
        this.editorial = editorial;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getAñoPublicacion() {
        return añoPublicacion;
    }

    public String getEditorial() {
        return editorial;
    }

    @Override
    public void mostrar(String prefijo) {
        System.out.println(prefijo + "📘 " + titulo + " - " + autor);
        if (!isbn.equals("ISBN-NO-ASIGNADO") || añoPublicacion != 0 || !editorial.equals("Desconocida")) {
            System.out.println(prefijo + "   ISBN: " + isbn + " | Año: " + añoPublicacion + " | Editorial: " + editorial);
        }
    }
}