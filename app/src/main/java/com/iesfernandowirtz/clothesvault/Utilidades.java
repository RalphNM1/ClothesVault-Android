package com.iesfernandowirtz.clothesvault;


import android.app.Activity;

import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import  com.iesfernandowirtz.clothesvault.utils.ServicioProducto;
public class Utilidades {

    public static void ocultarTeclado(Activity activity, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            IBinder token = view.getWindowToken();
            inputMethodManager.hideSoftInputFromWindow(token, 0);
        }
    }
    public static boolean validarFormatoCorreo(String correo) {
        // Expresión regular para validar el formato del correo electrónico
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return correo.matches(regex);
    }
    private static Toast toastError = null; // Variable para almacenar el Toast

    public static void mostrarToastError(Context context, String texto) {
        if (toastError == null || !toastError.getView().isShown()) { // Verifica si el Toast no está en pantalla
            LayoutInflater inflater = LayoutInflater.from(context);
            View layout = inflater.inflate(R.layout.toast_failure, null);
            TextView textView = layout.findViewById(R.id.tvToastError);
            textView.setText(texto);

            toastError = new Toast(context);
            toastError.setDuration(Toast.LENGTH_SHORT);
            toastError.setView(layout);
            toastError.show();
        }
    }

    public static void mostrarToastSuccess(Context context, String texto) {
        if (toastError == null || !toastError.getView().isShown()) { // Verifica si el Toast no está en pantalla
            LayoutInflater inflater = LayoutInflater.from(context);
            View layout = inflater.inflate(R.layout.toast_success, null);
            TextView textView = layout.findViewById(R.id.tvToastSuccess);
            textView.setText(texto);

            toastError = new Toast(context);
            toastError.setDuration(Toast.LENGTH_SHORT);
            toastError.setView(layout);
            toastError.show();
        }
    }

    public static void agregarProductoAlCarrito(ServicioProducto servicioProducto, Context context, Long idProducto, String idPedido, int cantidad) {
        if (idPedido == null) {
            mostrarToastError(context, "El ID del pedido no puede ser nulo");
            return;
        }

        String idPedidoSinDecimales = idPedido.replaceAll("\\.\\d+", "");

        try {
            Long idPedidoLong = Long.valueOf(idPedidoSinDecimales);
            Map<String, Long> body = new HashMap<>();
            body.put("idPedido", idPedidoLong);
            body.put("idProducto", idProducto);

            Call<Void> call = servicioProducto.agregarProductoAlCarrito(body, cantidad);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        mostrarToastSuccess(context, "Producto agregado");
                    } else {
                        mostrarToastError(context, "Error al agregar el producto");
                        try {
                            Log.e("error", response.errorBody().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    mostrarToastError(context, "Error en la solicitud: " + t.getMessage());
                }
            });
        } catch (NumberFormatException e) {
            mostrarToastError(context, "Formato de ID del pedido no válido");
        }
    }

}
