package com.iesfernandowirtz.clothesvault;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

/**
 * Clase base para actividades en la aplicación.
 * Gestiona la configuración del idioma de la aplicación.
 */
public class ActividadBase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyLocale(); // Aplica la configuración del idioma al crear la actividad.
    }

    /**
     * Aplica la configuración del idioma guardada en las preferencias de la aplicación.
     * Si no se encuentra, se establece el idioma predeterminado como español.
     */
    private void applyLocale() {
        String languageCode = getSharedPreferences("AppPreferences", MODE_PRIVATE)
                .getString("language", "es"); // Se establece el idioma predeterminado como español.
        setLocale(this, languageCode); // Establece el idioma de la aplicación.
    }

    /**
     * Establece el idioma de la aplicación.
     * @param activity La actividad actual.
     * @param languageCode El código del idioma a establecer.
     */
    public void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode); // Crea un objeto Locale con el código del idioma.
        Locale.setDefault(locale); // Establece el idioma por defecto.
        Resources resources = activity.getResources(); // Obtiene los recursos de la actividad.
        Configuration config = resources.getConfiguration(); // Obtiene la configuración actual.
        config.setLocale(locale); // Establece el idioma en la configuración.
        resources.updateConfiguration(config, resources.getDisplayMetrics()); // Actualiza la configuración.
    }
}
