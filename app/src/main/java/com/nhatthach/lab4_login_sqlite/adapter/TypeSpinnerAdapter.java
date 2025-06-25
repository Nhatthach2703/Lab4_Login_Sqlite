package com.nhatthach.lab4_login_sqlite.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nhatthach.lab4_login_sqlite.R;

public class TypeSpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] types;

    public TypeSpinnerAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.types = objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent, true);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent, false);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent, boolean isDropDown) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View row = inflater.inflate(
                isDropDown ? R.layout.spinner_dropdown_item : R.layout.spinner_type_item,
                parent,
                false
        );

        TextView label = row.findViewById(R.id.tvTypeName);
        View colorIndicator = row.findViewById(R.id.colorIndicator);

        label.setText(types[position]);

        // Đặt màu sắc phù hợp dựa vào text của item thay vì vị trí
        String type = types[position];
        if (type.equalsIgnoreCase("All")) {
            // "All" không có màu (trong suốt)
            colorIndicator.setBackgroundColor(Color.TRANSPARENT);
        } else if (type.equalsIgnoreCase("easy")) {
            // "easy" màu xanh lá
            colorIndicator.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else if (type.equalsIgnoreCase("medium")) {
            // "medium" màu vàng
            colorIndicator.setBackgroundColor(Color.parseColor("#FFC107"));
        } else if (type.equalsIgnoreCase("hard")) {
            // "hard" màu đỏ
            colorIndicator.setBackgroundColor(Color.parseColor("#F44336"));
        }

        return row;
    }
}
