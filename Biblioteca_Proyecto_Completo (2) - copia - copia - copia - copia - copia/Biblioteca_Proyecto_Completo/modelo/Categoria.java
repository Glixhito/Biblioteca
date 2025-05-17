package modelo;

import java.util.ArrayList;
import java.util.List;

public class Categoria implements ElementoCatalogo {
    public String nombre;
    public List<ElementoCatalogo> elementos = new ArrayList<>();

    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public List<ElementoCatalogo> getElementos() {
        return elementos;
    }

    public void agregarElemento(ElementoCatalogo e) {
        elementos.add(e);
    }

    @Override
    public void mostrar(String prefijo) {
        System.out.println(prefijo + "📂 " + nombre);
        for (ElementoCatalogo e : elementos) {
            e.mostrar(prefijo + "    ");
        }
    }
}