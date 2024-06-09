package com.iesfernandowirtz.clothesvault.modelo;

public class modeloProducto {

    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;

    private String talla;
    private String imagen; // Cambiado de byte[] a String
    private Categoria categoria; // Nueva propiedad
    private modeloProveedor proveedor; // Nueva propiedad

    // Getters y setters para todos los atributos
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }


    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public modeloProveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(modeloProveedor proveedor) {
        this.proveedor = proveedor;
    }
}
