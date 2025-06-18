package com.nhatthach.lab4_login_sqlite.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
        holder.tvContent.setText(list.get(position).getContent());
        holder.tvDate.setText(list.get(position).getDate());

        if (list.get(position).getStatus() == 1) {
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
