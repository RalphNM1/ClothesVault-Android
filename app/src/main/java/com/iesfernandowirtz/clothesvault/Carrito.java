// Carrito.java
package com.iesfernandowirtz.clothesvault;

import com.iesfernandowirtz.clothesvault.modelo.modeloProducto;

import java.util.ArrayList;
import java.util.List;

public class Carrito {
    private static Carrito instance;
    private List<modeloProducto> productos;
    private List<Integer> cantidades;

    private Carrito() {
        productos = new ArrayList<>();
        cantidades = new ArrayList<>();
    }

    public static synchronized Carrito getInstance() {
        if (instance == null) {
            instance = new Carrito();
        }
        return instance;
    }

    public void agregarProducto(modeloProducto producto, int cantidad) {
        if (productos.contains(producto)) {
            int index = productos.indexOf(producto);
            cantidades.set(index, cantidades.get(index) + cantidad);
        } else {
            productos.add(producto);
            cantidades.add(cantidad);
        }
    }

    public List<modeloProducto> getProductos() {
        return productos;
    }

    public List<Integer> getCantidades() {
        return cantidades;
    }

    public void limpiarCarrito() {
        productos.clear();
        cantidades.clear();
    }

    public void actualizarProductos(List<modeloProducto> nuevosProductos) {
        productos.clear();
        productos.addAll(nuevosProductos);
    }

    public void actualizarCantidades(List<Integer> nuevasCantidades) {
        cantidades.clear();
        cantidades.addAll(nuevasCantidades);
    }

    public void actualizarCantidadProducto(modeloProducto producto, int nuevaCantidad) {
        if (productos.contains(producto)) {
            int index = productos.indexOf(producto);
            cantidades.set(index, nuevaCantidad);
        }
    }

    public void eliminarProducto(int position) {
        if (position >= 0 && position < productos.size()) {
            productos.remove(position);
            cantidades.remove(position);
        }
    }
}
