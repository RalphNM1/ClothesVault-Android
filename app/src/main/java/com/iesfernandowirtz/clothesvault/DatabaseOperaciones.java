package com.iesfernandowirtz.clothesvault;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseOperaciones {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseOperaciones(Context context) {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void guardarDireccionIP(String direccionIP) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IP_ADDRESS, direccionIP);

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            database.update(DatabaseHelper.TABLE_NAME, values, DatabaseHelper.COLUMN_ID + "= ?", new String[]{"1"});
        } else {
            database.insert(DatabaseHelper.TABLE_NAME, null, values);
        }
        cursor.close();
    }

    public String obtenerDireccionIP() {
        String direccionIP = "";
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.COLUMN_IP_ADDRESS}, DatabaseHelper.COLUMN_ID + "= ?", new String[]{"1"}, null, null, null);
        if (cursor.moveToFirst()) {
            direccionIP = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IP_ADDRESS));
        }
        cursor.close();
        return direccionIP;
    }

    public void cerrar() {
        dbHelper.close();
    }
}
