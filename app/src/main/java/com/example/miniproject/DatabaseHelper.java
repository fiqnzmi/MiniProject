package com.example.miniproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "event.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE events (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)");
        db.execSQL("CREATE TABLE participants (id INTEGER PRIMARY KEY AUTOINCREMENT, event_id INTEGER, student_name TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS events");
        db.execSQL("DROP TABLE IF EXISTS participants");
        onCreate(db);
    }

    public void insertEvent(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insert("events", null, values);
    }

    public Cursor getAllEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM events", null);
    }

    public void updateEvent(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.update("events", values, "id=?", new String[]{String.valueOf(id)});
    }

    public void deleteEvent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("events", "id=?", new String[]{String.valueOf(id)});
    }

    public void insertParticipant(int eventId, String studentName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("event_id", eventId);
        values.put("student_name", studentName);
        db.insert("participants", null, values);
    }

    public Cursor getParticipantsCount(int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM participants WHERE event_id=" + eventId, null);
    }

    public boolean isAlreadyRegistered(int eventId, String studentName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM participants WHERE event_id=? AND student_name=?",
                new String[]{String.valueOf(eventId), studentName});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}