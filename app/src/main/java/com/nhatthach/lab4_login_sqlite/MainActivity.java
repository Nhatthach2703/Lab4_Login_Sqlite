package com.nhatthach.lab4_login_sqlite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhatthach.lab4_login_sqlite.adapter.TaskInfoAdapter;
import com.nhatthach.lab4_login_sqlite.adapter.TypeSpinnerAdapter;
import com.nhatthach.lab4_login_sqlite.dao.TaskInfoDAO;
import com.nhatthach.lab4_login_sqlite.model.TaskInfo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TaskInfoDAO dao;
    ArrayList<TaskInfo> listTask;
    ArrayList<TaskInfo> filteredList; // Danh sách đã được lọc
    String TAG = "//=====";
    RecyclerView rcvTask;
    TaskInfoAdapter adapter;
    EditText edId, edTitle, edContent, edDate;
    Spinner spinnerType, spinnerFilter; // Thêm spinnerFilter
    Button btnAdd;
    public String username = "";
    public String password = "";
    private String[] typeOptions = {"easy", "medium", "hard"};
    private String[] filterOptions = {"All", "easy", "medium", "hard"}; // Thêm option "All" cho bộ lọc

    public void getDataLogin() {
        SharedPreferences sheredPreferences = getSharedPreferences("MyLogin", MODE_PRIVATE);
        username = sheredPreferences.getString("username", "");
        password = sheredPreferences.getString("password", "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getDataLogin();
        if (username.equals("") || password.equals("")) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        initGUI();

        View rootView = findViewById(R.id.main);
        rootView.setOnApplyWindowInsetsListener((v, insets) -> {
            int topInset = insets.getSystemWindowInsetTop();
            int bottomInset = insets.getSystemWindowInsetBottom();
            v.setPadding(v.getPaddingLeft(), topInset, v.getPaddingRight(), bottomInset);
            return insets.consumeSystemWindowInsets();
        });

        // Sử dụng TypeSpinnerAdapter tùy chỉnh thay vì ArrayAdapter mặc định
        TypeSpinnerAdapter typeAdapter = new TypeSpinnerAdapter(this,
                R.layout.spinner_type_item, typeOptions);
        spinnerType.setAdapter(typeAdapter);

        // Sử dụng TypeSpinnerAdapter cho spinnerFilter
        TypeSpinnerAdapter filterAdapter = new TypeSpinnerAdapter(this,
                R.layout.spinner_type_item, filterOptions);
        spinnerFilter.setAdapter(filterAdapter);

        dao = new TaskInfoDAO(this);
        listTask = dao.getListInfo();
        Log.d(TAG, "onCreate: " + listTask.size());

        adapter = new TaskInfoAdapter(MainActivity.this, new ArrayList<>());
        adapter.setOnTaskChangedListener(new TaskInfoAdapter.OnTaskChangedListener() {
            @Override
            public void onTaskDeleted() {
                listTask = dao.getListInfo();
                String selectedFilter = filterOptions[spinnerFilter.getSelectedItemPosition()];
                filterList(selectedFilter);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvTask.setLayoutManager(linearLayoutManager);
        rcvTask.setAdapter(adapter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = edTitle.getText().toString().trim();
                String content = edContent.getText().toString().trim();
                String date = edDate.getText().toString().trim();
                String type = spinnerType.getSelectedItem().toString();

                if (title.isEmpty() || content.isEmpty() || date.isEmpty()) {
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
                } else {
                    TaskInfo info = new TaskInfo(1, title, content, date, type, 0);
                    long check = dao.addInfo(info);
                    if (check < 0) {
                        Toast.makeText(MainActivity.this, "ERROR: Not insert data", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Insert successful", Toast.LENGTH_SHORT).show();
                    }
                    listTask = dao.getListInfo();
                    String selectedFilter = filterOptions[spinnerFilter.getSelectedItemPosition()];
                    filterList(selectedFilter);
                    reset();
                }
            }
        });

        // Lọc danh sách khi chọn mục trong spinnerFilter
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = filterOptions[position];
                filterList(selectedFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Hiển thị toàn bộ danh sách nếu không có bộ lọc nào được chọn
                adapter = new TaskInfoAdapter(MainActivity.this, listTask);
                rcvTask.setAdapter(adapter);
            }
        });

        listTask = dao.getListInfo();
        filterList(filterOptions[0]); // Hiển thị All khi khởi động
    }

    public void initGUI() {
        rcvTask = findViewById(R.id.rcvTask);

        edTitle = findViewById(R.id.edTitle);
        edContent = findViewById(R.id.edContent);
        edDate = findViewById(R.id.edDate);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerFilter = findViewById(R.id.spinnerFilter); // Khởi tạo spinnerFilter
        btnAdd = findViewById(R.id.btnAdd);

        // Set up date picker dialog for date field
        edDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(edDate);
            }
        });
    }

    private void showDatePickerDialog(final EditText dateField) {
        // Use the current date as the default date in the picker
        final java.util.Calendar calendar = java.util.Calendar.getInstance();
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH);
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(
                MainActivity.this,
                new android.app.DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                        // Format the date as dd/mm/yyyy and set it to the EditText
                        String formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        dateField.setText(formattedDate);
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }

    public void reset() {
        edTitle.setText("");
        edContent.setText("");
        edDate.setText("");
        spinnerType.setSelection(0); // Reset spinner to first item
        spinnerFilter.setSelection(0); // Reset spinnerFilter to first item
    }

    private void filterList(String type) {
        filteredList = new ArrayList<>();
        if (type.equals("All")) {
            filteredList.addAll(listTask);
        } else {
            for (TaskInfo task : listTask) {
                if (task.getType().equals(type)) {
                    filteredList.add(task);
                }
            }
        }
        adapter.updateData(filteredList);
    }

    public void refreshTaskList() {
        listTask = dao.getListInfo();
        filteredList = new ArrayList<>();
        filteredList.addAll(listTask); // Always show all tasks after refresh
        adapter.updateData(filteredList);
        spinnerFilter.post(new Runnable() {
            @Override
            public void run() {
                spinnerFilter.setSelection(0); // Reset spinner to "All" immediately
                spinnerFilter.invalidate(); // Force UI refresh to ensure spinner updates visually
                spinnerFilter.requestLayout(); // Ensure layout recalculates
            }
        });
    }

    public void updateTask(TaskInfo updatedTask) {
        long result = dao.updateInfo(updatedTask);
        if (result > 0) {
            refreshTaskList();
            Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show();
        }
    }
}