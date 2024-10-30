package com.example.pe.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "abc.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createParentTable = "CREATE TABLE Parent (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "field1 TEXT, field2 TEXT, field3 TEXT, field4 TEXT)";
        db.execSQL(createParentTable);

        String createChildTable = "CREATE TABLE Child (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "field1 TEXT, field2 TEXT, field3 TEXT, idParent INTEGER)";
        db.execSQL(createChildTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Parent");
        db.execSQL("DROP TABLE IF EXISTS Child");
        onCreate(db);
    }
}
