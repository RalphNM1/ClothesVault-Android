package com.iesfernandowirtz.clothesvault;

import android.app.AlertDialog;
import android.app.Activity;
import android.view.LayoutInflater;

public class CargandoAlert {

    private Activity activity;
    private AlertDialog dialog;

    CargandoAlert(Activity miActividad) {
        activity = miActividad;
    }

    void startAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_cargar_layout, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    void closeAlertDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}