package com.iesfernandowirtz.clothesvault;


import android.app.Activity;

import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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



}
