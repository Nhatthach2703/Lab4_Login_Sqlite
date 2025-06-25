package com.nhatthach.lab4_login_sqlite.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhatthach.lab4_login_sqlite.R;
import com.nhatthach.lab4_login_sqlite.dao.TaskInfoDAO;
import com.nhatthach.lab4_login_sqlite.model.TaskInfo;

import java.util.ArrayList;

public class TaskInfoAdapter extends RecyclerView.Adapter<TaskInfoAdapter.ViewHolderInfo> {
    Context context;
    ArrayList<TaskInfo> list;
    TaskInfoDAO taskInfoDAO;

    public TaskInfoAdapter(Context context, ArrayList<TaskInfo> list) {
        this.context = context;
        this.list = list;
        taskInfoDAO = new TaskInfoDAO(context);
    }

    @NonNull
    @Override
    public ViewHolderInfo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_info, parent, false);
        return new ViewHolderInfo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderInfo holder, int position) {
        TaskInfo currentTask = list.get(position);
        holder.tvContent.setText(currentTask.getContent());
        holder.tvDate.setText(currentTask.getDate());

        // Remove the listener before setting checked state to avoid triggering it during binding
        holder.chkTask.setOnCheckedChangeListener(null);

        if (currentTask.getStatus() == 1) {
            holder.chkTask.setChecked(true);
            holder.tvContent.setPaintFlags(holder.tvContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.chkTask.setChecked(false);
            holder.tvContent.setPaintFlags(holder.tvContent.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Set the listener after configuring the checkbox state
        holder.chkTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    int pos = holder.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && pos < list.size()) {
                        TaskInfo task = list.get(pos);
                        int id = task.getId();

                        // Update the database
                        boolean checkRS = taskInfoDAO.updateTypeInfo(id, isChecked);

                        if (checkRS) {
                            // Update the model
                            task.setStatus(isChecked ? 1 : 0);

                            // Update the UI
                            if (isChecked) {
                                holder.tvContent.setPaintFlags(holder.tvContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            } else {
                                holder.tvContent.setPaintFlags(holder.tvContent.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                            }

                            Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show();
                        } else {
                            // Revert checkbox if update failed
                            holder.chkTask.setOnCheckedChangeListener(null);
                            holder.chkTask.setChecked(!isChecked);
                            holder.chkTask.setOnCheckedChangeListener(this);

                            Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    // Log the exception and prevent app crash
                    e.printStackTrace();
                    Toast.makeText(context, "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle update button click
        holder.imgUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDialog(currentTask);
            }
        });

        // Handle delete button click
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(currentTask.getId());
            }
        });
    }

    private void showUpdateDialog(TaskInfo task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Task");

        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_task, null);
        builder.setView(dialogView);

        // Find dialog views
        EditText edTitle = dialogView.findViewById(R.id.edTitleUpdate);
        EditText edContent = dialogView.findViewById(R.id.edContentUpdate);
        EditText edDate = dialogView.findViewById(R.id.edDateUpdate);
        Spinner spinnerType = dialogView.findViewById(R.id.spinnerTypeUpdate);

        // Set up spinner with type options và sử dụng TypeSpinnerAdapter tùy chỉnh
        String[] typeOptions = {"easy", "medium", "hard"};
        TypeSpinnerAdapter typeAdapter = new TypeSpinnerAdapter(context,
                R.layout.spinner_type_item, typeOptions);
        spinnerType.setAdapter(typeAdapter);

        // Set current task values
        edTitle.setText(task.getTitle());
        edContent.setText(task.getContent());
        edDate.setText(task.getDate());

        // Set the spinner selection based on the task's type
        String currentType = task.getType();
        int typePosition = 0; // default to "easy"

        for (int i = 0; i < typeOptions.length; i++) {
            if (typeOptions[i].equals(currentType)) {
                typePosition = i;
                break;
            }
        }
        spinnerType.setSelection(typePosition);

        // Set up date picker dialog for the date field
        edDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(edDate);
            }
        });

        // Add action buttons
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Check if fields are not empty
                String title = edTitle.getText().toString().trim();
                String content = edContent.getText().toString().trim();
                String date = edDate.getText().toString().trim();
                String type = spinnerType.getSelectedItem().toString();

                if (title.isEmpty() || content.isEmpty() || date.isEmpty()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update task with new values
                TaskInfo updatedTask = new TaskInfo(
                        task.getId(),
                        title,
                        content,
                        date,
                        type,
                        task.getStatus()
                );

                long result = taskInfoDAO.updateInfo(updatedTask);

                if (result > 0) {
                    Toast.makeText(context, "Task updated successfully", Toast.LENGTH_SHORT).show();
                    // Cập nhật danh sách một cách an toàn
                    ArrayList<TaskInfo> newList = taskInfoDAO.getListInfo();
                    updateData(newList);
                } else {
                    Toast.makeText(context, "Failed to update task", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDatePickerDialog(final EditText dateField) {
        // Use the current date as the default date in the picker
        final java.util.Calendar calendar = java.util.Calendar.getInstance();
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH);
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        // If there's an existing date in the field, parse it and use it as the default
        String currentDate = dateField.getText().toString().trim();
        if (!currentDate.isEmpty()) {
            try {
                String[] parts = currentDate.split("/");
                if (parts.length == 3) {
                    day = Integer.parseInt(parts[0]);
                    month = Integer.parseInt(parts[1]) - 1; // Month is 0-based in Calendar
                    year = Integer.parseInt(parts[2]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Create a new instance of DatePickerDialog and return it
        android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(
                context,
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

    // Interface callback để MainActivity biết khi xóa thành công
    public interface OnTaskChangedListener {
        void onTaskDeleted();
    }
    private OnTaskChangedListener onTaskChangedListener;
    public void setOnTaskChangedListener(OnTaskChangedListener listener) {
        this.onTaskChangedListener = listener;
    }

    private void showDeleteConfirmationDialog(int taskId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Task");
        builder.setMessage("Are you sure you want to delete this task?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean result = taskInfoDAO.removeInfo(taskId);

                if (result) {
                    Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                    if (onTaskChangedListener != null) {
                        onTaskChangedListener.onTaskDeleted();
                    }
                } else {
                    Toast.makeText(context, "Failed to delete task", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Thêm phương thức cập nhật danh sách an toàn
    public void updateData(ArrayList<TaskInfo> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolderInfo extends RecyclerView.ViewHolder {
        TextView tvContent, tvDate;
        CheckBox chkTask;
        ImageView imgUpdate, imgDelete;

        public ViewHolderInfo(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvDate = itemView.findViewById(R.id.tvDate);
            chkTask = itemView.findViewById(R.id.chkTask);
            imgUpdate = itemView.findViewById(R.id.imgUpdate);
            imgDelete = itemView.findViewById(R.id.imgDelete);
        }
    }
}
