package com.nhatthach.lab4_login_sqlite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhatthach.lab4_login_sqlite.adapter.TaskInfoAdapter;
import com.nhatthach.lab4_login_sqlite.dao.TaskInfoDAO;
import com.nhatthach.lab4_login_sqlite.model.TaskInfo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TaskInfoDAO dao;
    ArrayList<TaskInfo> listTask;
    String TAG = "//=====";
    RecyclerView rcvTask;
    TaskInfoAdapter adapter;
    EditText edId, edTitle, edContent, edDate, edType;
    Button btnAdd;
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

        initGUI();

        dao = new TaskInfoDAO(this);
        listTask = dao.getListInfo();
        Log.d(TAG, "onCreate: " + listTask.size());

        adapter = new TaskInfoAdapter(MainActivity.this, listTask);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvTask.setLayoutManager(linearLayoutManager);
        rcvTask.setAdapter(adapter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = edTitle.getText().toString().trim();
                String content = edContent.getText().toString().trim();
                String date = edDate.getText().toString().trim();
                String type = edType.getText().toString().trim();

                if (title.isEmpty() || content.isEmpty() || date.isEmpty() || type.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please input data", Toast.LENGTH_SHORT).show();
                    if (title.isEmpty()) {
                        edTitle.setError("Please enter title");
                    }
                    if (content.isEmpty()) {
                        edContent.setError("Please enter content");
                    }
                    if (date.isEmpty()) {
                        edDate.setError("Please enter date");
                    }
                    if (type.isEmpty()) {
                        edType.setError("Please enter type");
                    }
                } else {
                    TaskInfo info = new TaskInfo(1, title, content, date, type, 0);
                    long check = dao.addInfo(info);
                    if (check < 0) {
                        Toast.makeText(MainActivity.this, "ERROR: Not insert data", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Insert successful", Toast.LENGTH_SHORT).show();
                    }
                    listTask = dao.getListInfo();
                    adapter = new TaskInfoAdapter(MainActivity.this, listTask);
                    rcvTask.setLayoutManager(linearLayoutManager);
                    rcvTask.setAdapter(adapter);
                    reset();
                }
            }
        });
    }

    public void initGUI() {
        rcvTask = findViewById(R.id.rcvTask);

        edId = findViewById(R.id.edId);
        edTitle = findViewById(R.id.edTitle);
        edContent = findViewById(R.id.edContent);
        edDate = findViewById(R.id.edDate);
        edType = findViewById(R.id.edType);
        btnAdd = findViewById(R.id.btnAdd);
    }

    public void reset() {
        edId.setText("");
        edTitle.setText("");
        edContent.setText("");
        edDate.setText("");
        edType.setText("");
    }
}