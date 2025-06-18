package com.nhatthach.lab4_login_sqlite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nhatthach.lab4_login_sqlite.dao.TaskInfoDAO;
import com.nhatthach.lab4_login_sqlite.model.TaskInfo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TaskInfoDAO dao;
    ArrayList<TaskInfo> listTask;
    String TAG = "//=====";
    Button btnList;
    public String username = "";
    public String password = "";

    public void getDataLogin() {
        SharedPreferences sheredPreferences = getSharedPreferences("MyLogin", MODE_PRIVATE);
        username = sheredPreferences.getString("username", "");
        password = sheredPreferences.getString("password", "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        getDataLogin();
        if (username.equals("") || password.equals("")) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        dao = new TaskInfoDAO(this);
        listTask = dao.getListInfo();
        Log.d(TAG, "onCreate: " + listTask.size());
    }
}