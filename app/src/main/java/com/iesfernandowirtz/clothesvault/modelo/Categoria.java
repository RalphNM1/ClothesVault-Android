package com.iesfernandowirtz.clothesvault.modelo;

import java.util.ArrayList;
import java.util.List;

public class Categoria {

    private Long id;
    private String nombre;

    private List<modeloProducto> productos = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<modeloProducto> getProductos() {
        return productos;
    }

    public void setProductos(List<modeloProducto> productos) {
        this.productos = productos;
    }
}
