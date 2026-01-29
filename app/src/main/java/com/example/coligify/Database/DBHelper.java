package com.example.coligify.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "coligify.db";
    private static final int DATABASE_VERSION = 1;

    // Table
    private static final String TABLE_USERS = "users";

    // Columns
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_MOBILE = "mobileno";
    private static final String COL_EMAIL = "email";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_MOBILE + " TEXT UNIQUE NOT NULL, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_USERNAME + " TEXT UNIQUE NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ================= REGISTER USER =================
    public boolean registerUser(String name, String mobileno,
                                String email, String username,
                                String password) {

        if (checkUserExist(mobileno, email, username)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_MOBILE, mobileno);
        values.put(COL_EMAIL, email);
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // ================= CHECK USER EXISTS =================
    private boolean checkUserExist(String mobileno, String email, String username) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_USERS +
                        " WHERE " + COL_MOBILE + "=? OR " +
                        COL_EMAIL + "=? OR " +
                        COL_USERNAME + "=?",
                new String[]{mobileno, email, username}
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // ================= LOGIN VALIDATION =================
    public boolean validateLogin(String username, String password) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_USERS +
                        " WHERE " + COL_USERNAME + "=? AND " +
                        COL_PASSWORD + "=?",
                new String[]{username, password}
        );

        boolean valid = cursor.moveToFirst();
        cursor.close();
        return valid;
    }

    // ================= UPDATE USER PROFILE =================
    public boolean updateUser(String oldUsername, String name,
                              String mobileno, String email,
                              String username) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_MOBILE, mobileno);
        values.put(COL_EMAIL, email);
        values.put(COL_USERNAME, username);

        int result = db.update(
                TABLE_USERS,
                values,
                COL_USERNAME + "=?",
                new String[]{oldUsername}
        );

        return result > 0;
    }

    // ================= DELETE USER =================
    public boolean deleteUser(String username) {

        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(
                TABLE_USERS,
                COL_USERNAME + "=?",
                new String[]{username}
        );

        return result > 0;
    }

    // ================= CHECK USERNAME EXISTS =================
    public boolean isUsernameExists(String username) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_USERS +
                        " WHERE " + COL_USERNAME + "=?",
                new String[]{username}
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // ================= UPDATE PASSWORD BY EMAIL (FORGOT PASSWORD) =================
    public boolean updatePasswordByEmail(String email, String newPassword) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_PASSWORD, newPassword);

        int result = db.update(
                TABLE_USERS,
                values,
                COL_EMAIL + "=?",
                new String[]{email}
        );

        return result > 0;
    }

    // ================= UPDATE PASSWORD BY USERNAME (OPTIONAL) =================
    public boolean updatePasswordByUsername(String username, String newPassword) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_PASSWORD, newPassword);

        int result = db.update(
                TABLE_USERS,
                values,
                COL_USERNAME + "=?",
                new String[]{username}
        );

        return result > 0;
    }
}
