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

        // Đặt màu sắc khác nhau cho từng mức độ ưu tiên
        switch (position) {
            case 0: // easy
                colorIndicator.setBackgroundColor(Color.parseColor("#4CAF50")); // Màu xanh lá
                break;
            case 1: // medium
                colorIndicator.setBackgroundColor(Color.parseColor("#FFC107")); // Màu vàng
                break;
            case 2: // hard
                colorIndicator.setBackgroundColor(Color.parseColor("#F44336")); // Màu đỏ
                break;
        }

        return row;
    }
}
