package com.iesfernandowirtz.clothesvault.Utils;

public class Apis {
    public static  String URL_001="http://192.168.1.133:8080";

    public static ServicioUsuario getServicioUsuario(){
        return Cliente.getCliente(URL_001).create(ServicioUsuario.class);
    }

    public static ServicioProducto getServicioProducto(){
        return Cliente.getCliente(URL_001).create(ServicioProducto.class);
    }
}
