package com.example.pomelipo;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ConectorDB {
    static final String DB_NAME = "DataBase";
    final private WorkSessionSQLiteHelper dbHelper;
    private SQLiteDatabase db;

    public ConectorDB(Context ctx) {
        dbHelper = new WorkSessionSQLiteHelper(ctx, DB_NAME, null, 1);
    }

    public ConectorDB open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (db != null) db.close();
    }

    public void createWorkSession(String sessionName, int workTime, int chillTime, int repetitions) {
        db.execSQL("INSERT INTO WorkSessions VALUES('"+sessionName+"',"+workTime+","+chillTime+","+repetitions+")");
    }

    public Cursor readAllWorkSessions() {
        return db.rawQuery("SELECT * FROM WorkSessions", null);
    }

    public Cursor readWorkSession(String sessionName){
        return db.rawQuery("SELECT * FROM WorkSessions WHERE sessionName='"+sessionName+"'", null);
    }

    public void deleteWorkSession(String sessionName){
        db.execSQL("DELETE FROM WorkSessions WHERE sessionName='"+sessionName+"'");
    }

}
