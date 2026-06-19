package com.example.bessmertnyy_3_2_assignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//SQLite helper for user accounts + profile phone numbers.
public class UserDatabaseHelper extends SQLiteOpenHelper {

    //DB file name + current schema version.
    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 2;

    //Users table + columns.
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_FIRSTNAME = "first_name";
    private static final String COLUMN_LASTNAME = "last_name";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";

    //Required constructor for SQLiteOpenHelper.
    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    //Create users table on first app run.
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, "
                + COLUMN_PASSWORD + " TEXT NOT NULL, "
                + COLUMN_FIRSTNAME + " TEXT, "
                + COLUMN_LASTNAME + " TEXT, "
                + COLUMN_PHONE_NUMBER + " TEXT)");
    }

    @Override
    //Add phone column when upgrading from older DB versions.
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_PHONE_NUMBER + " TEXT");
        }
    }

    //Insert a new user if username isnt already taken.
    public boolean addUser(String username, String password, String firstName, String lastName) {
        if (userExists(username)) return false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_FIRSTNAME, firstName);
        values.put(COLUMN_LASTNAME, lastName);
        return db.insert(TABLE_USERS, null, values) != -1;
    }

    //Return true if username/password combo exists.
    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password});
        boolean found = cursor.moveToFirst();
        cursor.close();
        return found;
    }

    //Return true if username already exists.
    public boolean userExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?",
                new String[]{username});
        boolean found = cursor.moveToFirst();
        cursor.close();
        return found;
    }

    //Return first name, last name, and username for profile screen.
    public String[] getUserProfile(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_FIRSTNAME + ", " + COLUMN_LASTNAME + ", " + COLUMN_USERNAME
                        + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?",
                new String[]{username});

        String[] profile = null;
        if (cursor.moveToFirst()) {
            profile = new String[]{
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2)
            };
        }
        cursor.close();
        return profile;
    }

    //Update saved alert phone number for one user.
    public boolean updateUserPhoneNumber(String username, String phoneNumber) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE_NUMBER, phoneNumber);
        int rowsUpdated = db.update(TABLE_USERS, values, COLUMN_USERNAME + "=?", new String[]{username});
        return rowsUpdated > 0;
    }

    //Read saved alert phone number for one user.
    public String getUserPhoneNumber(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_PHONE_NUMBER + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?",
                new String[]{username});

        String phoneNumber = "";
        if (cursor.moveToFirst()) {
            phoneNumber = cursor.getString(0);
            if (phoneNumber == null) {
                phoneNumber = "";
            }
        }
        cursor.close();
        return phoneNumber;
    }
}
