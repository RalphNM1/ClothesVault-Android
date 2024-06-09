package com.iesfernandowirtz.clothesvault;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * DatabaseOperaciones es una clase que proporciona métodos para interactuar con la base de datos SQLite.
 */
public class DatabaseOperaciones {
    // Helper para la gestión de la base de datos
    private DatabaseHelper dbHelper;
    // Instancia de SQLiteDatabase para ejecutar operaciones de lectura y escritura
    private SQLiteDatabase database;

    /**
     * Constructor de DatabaseOperaciones
     * @param context Contexto de la aplicación
     */
    public DatabaseOperaciones(Context context) {
        // Crear una instancia de DatabaseHelper
        dbHelper = new DatabaseHelper(context);
        // Obtener una base de datos en modo escritura
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Método para guardar la dirección IP en la base de datos.
     * Si ya existe una dirección IP, se actualiza. Si no, se inserta una nueva.
     * @param direccionIP Dirección IP a guardar
     */
    public void guardarDireccionIP(String direccionIP) {
        // Crear un ContentValues para almacenar los valores
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IP_ADDRESS, direccionIP);

        // Consultar la tabla para verificar si ya hay un registro
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            // Si existe, actualizar el registro
            database.update(DatabaseHelper.TABLE_NAME, values, DatabaseHelper.COLUMN_ID + "= ?", new String[]{"1"});
        } else {
            // Si no existe, insertar un nuevo registro
            database.insert(DatabaseHelper.TABLE_NAME, null, values);
        }
        // Cerrar el cursor para liberar recursos
        cursor.close();
    }

    /**
     * Método para obtener la dirección IP almacenada en la base de datos.
     * @return La dirección IP almacenada, o una cadena vacía si no hay ninguna.
     */
    public String obtenerDireccionIP() {
        String direccionIP = "";
        // Consultar la tabla para obtener la dirección IP
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.COLUMN_IP_ADDRESS}, DatabaseHelper.COLUMN_ID + "= ?", new String[]{"1"}, null, null, null);
        if (cursor.moveToFirst()) {
            // Si hay un resultado, obtener la dirección IP
            direccionIP = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IP_ADDRESS));
        }
        // Cerrar el cursor para liberar recursos
        cursor.close();
        return direccionIP;
    }

    /**
     * Método para cerrar la base de datos.
     */
    public void cerrar() {
        // Cerrar la conexión con el helper de la base de datos
        dbHelper.close();
    }
}
