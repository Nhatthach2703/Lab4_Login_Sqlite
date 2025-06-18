package com.nhatthach.lab4_login_sqlite.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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

        if (currentTask.getStatus() == 1) {
            holder.chkTask.setChecked(true);
            holder.tvContent.setPaintFlags(holder.tvContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.chkTask.setChecked(false);
            holder.tvContent.setPaintFlags(holder.tvContent.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.chkTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int id = list.get(holder.getAdapterPosition()).getId();
                boolean checkRS = taskInfoDAO.updateTypeInfo(id, holder.chkTask.isChecked());

                if (checkRS) {
                    Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                }
                list.clear();
                list = taskInfoDAO.getListInfo();
                notifyDataSetChanged();
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
        EditText edType = dialogView.findViewById(R.id.edTypeUpdate);

        // Set current task values
        edTitle.setText(task.getTitle());
        edContent.setText(task.getContent());
        edDate.setText(task.getDate());
        edType.setText(task.getType());

        // Add action buttons
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Check if fields are not empty
                String title = edTitle.getText().toString().trim();
                String content = edContent.getText().toString().trim();
                String date = edDate.getText().toString().trim();
                String type = edType.getText().toString().trim();

                if (title.isEmpty() || content.isEmpty() || date.isEmpty() || type.isEmpty()) {
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
                    // Refresh the list
                    list.clear();
                    list.addAll(taskInfoDAO.getListInfo());
                    notifyDataSetChanged();
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
                    // Refresh the list
                    list.clear();
                    list.addAll(taskInfoDAO.getListInfo());
                    notifyDataSetChanged();
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
