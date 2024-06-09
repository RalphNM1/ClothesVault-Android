package com.iesfernandowirtz.clothesvault.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class modeloPedido {

    private Long id;

    private modeloUsuario usuario;

    private String estado;

    private Date fechaPedido;

    private List<modeloDetallePedido> detallePedidos = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public modeloUsuario getUsuario() {
        return usuario;
    }

    public void setUsuario(modeloUsuario usuario) {
        this.usuario = usuario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public List<modeloDetallePedido> getDetallePedidos() {
        return detallePedidos;
    }

    public void setDetallePedidos(List<modeloDetallePedido> detallePedidos) {
        this.detallePedidos = detallePedidos;
    }
}
