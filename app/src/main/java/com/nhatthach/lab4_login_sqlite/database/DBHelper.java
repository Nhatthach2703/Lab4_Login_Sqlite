package com.nhatthach.lab4_login_sqlite.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper  extends SQLiteOpenHelper {

    public DBHelper(@Nullable Context context) {
        super(context, "TodoDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sSQL = "Create TABLE TASKS(ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, CONTENT TEXT, DATE TEXT, TYPE TEXT, STATUS INTEGER)";
        db.execSQL(sSQL);
        String sSQL_insert = "INSERT INTO TASKS (ID,TITLE,CONTENT,DATE,TYPE,STATUS) VALUES " +
                "( '1', 'Android', 'học lập trình android', '18/06/2025', 'hard', '1'), " +
                "( '2', 'node', 'học lập trình node', '18/06/2025', 'medium', '0'), " +
                "( '3', 'c', 'học lập trình c', '18/06/2025', 'easy', '0'), " +
                "( '4', 'python', 'học lập trình python', '18/06/2025', 'medium', '1')," +
                "( '5', 'java', 'học lập trình java', '20/06/2025', 'medium', '0')," +
                "( '6', 'c++', 'học lập trình c++', '21/06/2025', 'hard', '0')," +
                "( '7', 'javascript', 'học lập trình javascript', '22/06/2025', 'medium', '0')," +
                "( '8', 'AI', 'học train model AI', '23/06/2025', 'hard', '0')";

        db.execSQL(sSQL_insert);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS TASKS");
            onCreate(db);
        }

    }
}
