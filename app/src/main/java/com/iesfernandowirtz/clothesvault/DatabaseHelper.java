package com.iesfernandowirtz.clothesvault;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper es una clase que extiende SQLiteOpenHelper para gestionar la creación y actualización de la base de datos SQLite.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Nombre de la base de datos
    private static final String DATABASE_NAME = "config.db";
    // Versión de la base de datos
    private static final int DATABASE_VERSION = 1;

    // Nombre de la tabla
    public static final String TABLE_NAME = "configuracion";
    // Nombre de las columnas
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_IP_ADDRESS = "direccion_ip";

    // SQL para la creación de la tabla
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_IP_ADDRESS + " TEXT NOT NULL);";

    /**
     * Constructor de DatabaseHelper
     * @param context Contexto de la aplicación
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Método llamado cuando se crea la base de datos por primera vez
     * @param db Instancia de SQLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Ejecuta el SQL para crear la tabla
        db.execSQL(TABLE_CREATE);
    }

    /**
     * Método llamado cuando se actualiza la versión de la base de datos
     * @param db Instancia de SQLiteDatabase
     * @param oldVersion Versión anterior de la base de datos
     * @param newVersion Nueva versión de la base de datos
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Elimina la tabla si ya existe
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Crea la tabla de nuevo
        onCreate(db);
    }
}
