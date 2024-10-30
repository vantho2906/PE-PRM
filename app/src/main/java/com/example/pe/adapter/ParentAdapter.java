package com.example.pe.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pe.R;
import com.example.pe.activity.ManageParentActivity;
import com.example.pe.activity.MapActivity;
import com.example.pe.helper.DatabaseHelper;
import com.example.pe.model.Parent;

import java.util.List;

public class ParentAdapter extends ArrayAdapter<Parent> {
    private DatabaseHelper dbHelper;

    public ParentAdapter(Context context, List<Parent> parents, DatabaseHelper dbHelper) {
        super(context, 0, parents);
        this.dbHelper = dbHelper;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parents) {
        Parent parent = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.parent_item, parents, false);
        }

        TextView tvTenParent = convertView.findViewById(R.id.tvParentField1);
        TextView tvEmail = convertView.findViewById(R.id.tvParentField2);
        TextView tvDiaChi = convertView.findViewById(R.id.tvParentField3);
        TextView tvDienThoai = convertView.findViewById(R.id.tvParentField4);
        Button btnViewOnMap = convertView.findViewById(R.id.more);

        tvTenParent.setText(parent.getField1());
        tvEmail.setText(parent.getField2());
        tvDiaChi.setText(parent.getField3());
        tvDienThoai.setText(parent.getField4());

        btnViewOnMap.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MapActivity.class);
            intent.putExtra("parent", parent);
            getContext().startActivity(intent);
        });

        convertView.setOnClickListener(v -> {
            ((ManageParentActivity) getContext()).setSelectedParent(parent);
        });

        convertView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete Parent")
                    .setMessage("Are you sure you want to delete this tác giả?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteParent(parent);
                        Toast.makeText(getContext(), "Tác Giả deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });

        return convertView;
    }

    private void deleteParent(Parent parent) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Parent", "id=?", new String[]{String.valueOf(parent.getId())});
        remove(parent);
        notifyDataSetChanged();
    }
}
