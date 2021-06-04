package com.example.pomelipo;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WorkSessionSQLiteHelper extends SQLiteOpenHelper {

    String createTable = "CREATE TABLE WorkSessions(sessionName TEXT, workTime INTEGER, chillTime INTEGER, repetitions INTEGER)";

    public WorkSessionSQLiteHelper(Context contexto, String nombreBD, SQLiteDatabase.CursorFactory factory, int versionBD) {
        super(contexto, nombreBD, factory, versionBD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(createTable);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        try {
            db.execSQL("DROP TABLE IF EXISTS WorkSessions");
            db.execSQL(createTable);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

}
