// Carrito.java
package com.iesfernandowirtz.clothesvault;

import com.iesfernandowirtz.clothesvault.modelo.modeloProducto;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa el carrito de compras de la aplicación.
 */
public class Carrito {
    private static Carrito instance;
    private List<modeloProducto> productos;
    private List<Integer> cantidades;

    private Carrito() {
        productos = new ArrayList<>();
        cantidades = new ArrayList<>();
    }

    /**
     * Método estático para obtener la única instancia del carrito.
     * @return La instancia del carrito.
     */
    public static synchronized Carrito getInstance() {
        if (instance == null) {
            instance = new Carrito();
        }
        return instance;
    }

    /**
     * Agrega un producto al carrito.
     * @param producto El producto a agregar.
     * @param cantidad La cantidad del producto a agregar.
     */
    public void agregarProducto(modeloProducto producto, int cantidad) {
        if (productos.contains(producto)) {
            int index = productos.indexOf(producto);
            cantidades.set(index, cantidades.get(index) + cantidad);
        } else {
            productos.add(producto);
            cantidades.add(cantidad);
        }
    }

    /**
     * Obtiene la lista de productos en el carrito.
     * @return La lista de productos en el carrito.
     */
    public List<modeloProducto> getProductos() {
        return productos;
    }

    /**
     * Obtiene la lista de cantidades correspondientes a los productos en el carrito.
     * @return La lista de cantidades correspondientes a los productos en el carrito.
     */
    public List<Integer> getCantidades() {
        return cantidades;
    }

    /**
     * Limpia el carrito, eliminando todos los productos y cantidades.
     */
    public void limpiarCarrito() {
        productos.clear();
        cantidades.clear();
    }

    /**
     * Actualiza los productos en el carrito con una nueva lista de productos.
     * @param nuevosProductos La nueva lista de productos.
     */
    public void actualizarProductos(List<modeloProducto> nuevosProductos) {
        productos.clear();
        productos.addAll(nuevosProductos);
    }

    /**
     * Actualiza las cantidades de los productos en el carrito con una nueva lista de cantidades.
     * @param nuevasCantidades La nueva lista de cantidades.
     */
    public void actualizarCantidades(List<Integer> nuevasCantidades) {
        cantidades.clear();
        cantidades.addAll(nuevasCantidades);
    }

    /**
     * Actualiza la cantidad de un producto en el carrito.
     * @param producto El producto cuya cantidad se actualizará.
     * @param nuevaCantidad La nueva cantidad del producto.
     */
    public void actualizarCantidadProducto(modeloProducto producto, int nuevaCantidad) {
        if (productos.contains(producto)) {
            int index = productos.indexOf(producto);
            cantidades.set(index, nuevaCantidad);
        }
    }

    /**
     * Elimina un producto del carrito en la posición especificada.
     * @param position La posición del producto a eliminar.
     */
    public void eliminarProducto(int position) {
        if (position >= 0 && position < productos.size()) {
            productos.remove(position);
            cantidades.remove(position);
        }
    }
}
