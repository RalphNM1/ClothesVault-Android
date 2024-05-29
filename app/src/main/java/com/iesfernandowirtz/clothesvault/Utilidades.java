package com.iesfernandowirtz.clothesvault;


import android.app.Activity;

import android.content.Context;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

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

}
