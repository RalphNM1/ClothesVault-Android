package com.iesfernandowirtz.clothesvault;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ActividadBase extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyLocale();
    }

    private void applyLocale() {
        String languageCode = getSharedPreferences("AppPreferences", MODE_PRIVATE)
                .getString("language", "es"); // Default to Spanish
        setLocale(this, languageCode);
    }

    public void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
