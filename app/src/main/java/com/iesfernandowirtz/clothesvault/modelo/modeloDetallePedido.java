package com.iesfernandowirtz.clothesvault.modelo;

public class modeloDetallePedido {

    private Long id;
    private modeloPedido pedido;
    private modeloProducto producto;
    private Integer cantidad;
    private Double precioTotal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public modeloPedido getPedido() {
        return pedido;
    }

    public void setPedido(modeloPedido pedido) {
        this.pedido = pedido;
    }

    public modeloProducto getProducto() {
        return producto;
    }

    public void setProducto(modeloProducto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(Double precioTotal) {
        this.precioTotal = precioTotal;
    }
}
