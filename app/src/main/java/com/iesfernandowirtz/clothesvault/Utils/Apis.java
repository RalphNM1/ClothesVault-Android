package com.iesfernandowirtz.clothesvault.Utils;

import android.content.Context;

import com.iesfernandowirtz.clothesvault.DatabaseOperaciones;

public class Apis {
    private static String URL_001;

    public static void setDireccionIP(Context context) {
        DatabaseOperaciones dbOps = new DatabaseOperaciones(context);
        String ip = dbOps.obtenerDireccionIP();
        if (ip != null && !ip.isEmpty()) {
            URL_001 = "http://" + ip + ":8080";
        } else {
            //IP predeterminada
            URL_001 = "http://192.168.1.133:8080";
        }
        dbOps.cerrar();
    }

    public static ServicioUsuario getServicioUsuario(Context context) {
        if (URL_001 == null) {
            setDireccionIP(context);
        }
        return Cliente.getCliente(URL_001).create(ServicioUsuario.class);
    }

    public static ServicioProducto getServicioProducto(Context context) {
        if (URL_001 == null) {
            setDireccionIP(context);
        }
        return Cliente.getCliente(URL_001).create(ServicioProducto.class);
    }
}
