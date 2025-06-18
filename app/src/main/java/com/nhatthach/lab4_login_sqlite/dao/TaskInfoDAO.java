package com.nhatthach.lab4_login_sqlite.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nhatthach.lab4_login_sqlite.database.DBHelper;
import com.nhatthach.lab4_login_sqlite.model.TaskInfo;

import java.util.ArrayList;

public class TaskInfoDAO {
    private DBHelper dbHelper;
    private SQLiteDatabase database;

    public TaskInfoDAO(Context context) {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public long addInfo(TaskInfo info) {
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TITLE", info.getTitle());
        values.put("CONTENT", info.getContent());
        values.put("DATE", info.getDate());
        values.put("TYPE", info.getType());
//        values.put("STATUS", info.getStatus());
        long check = database.insert("TASKS", null, values);

        if (check <= 0) {
            return -1; // Insertion failed
        }
        return 1; // Return the row ID of the newly inserted row
    }

    public long updateInfo(TaskInfo info) {
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ID", info.getId());
        values.put("TITLE", info.getTitle());
        values.put("CONTENT", info.getContent());
        values.put("DATE", info.getDate());
        values.put("TYPE", info.getType());
        values.put("STATUS", info.getStatus());


        long check = database.update("TASKS", values, "ID = ?", new String[]{String.valueOf(info.getId())});

        if (check <= 0) {
            return -1; // Update failed
        }
        return 1; // Return the number of rows affected
    }

    public ArrayList<TaskInfo> getListInfo() {
        ArrayList<TaskInfo> list = new ArrayList<>();
        database = dbHelper.getReadableDatabase();

        try {
            Cursor cursor = database.rawQuery("SELECT * FROM TASKS", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    list.add(new TaskInfo(cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getInt(5)
                    ));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return list;
    }

    public boolean removeInfo(int id) {
        int row = database.delete("TASKS", "ID = ?", new String[]{String.valueOf(id)});
        return row != -1;
    }

    public boolean updateTypeInfo(int id, boolean check) {
        int status = check ? 1 : 0;
        ContentValues values = new ContentValues();
        values.put("STATUS", status);
        long row = database.update("TASKS", values, "ID = ?", new String[]{String.valueOf(id)});
        return row != -1;
    }

}
