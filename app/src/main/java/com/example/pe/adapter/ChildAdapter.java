package com.example.pe.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pe.R;
import com.example.pe.activity.ManageChildActivity;
import com.example.pe.helper.DatabaseHelper;
import com.example.pe.model.Child;

import java.util.List;

public class ChildAdapter extends ArrayAdapter<Child> {
    private DatabaseHelper dbHelper;

    public ChildAdapter(Context context, List<Child> children, DatabaseHelper dbHelper) {
        super(context, 0, children);
        this.dbHelper = dbHelper;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Child child = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.child_item, parent, false);
        }

        TextView tvTenChild = convertView.findViewById(R.id.tvChildField1);
        TextView tvNgayXb = convertView.findViewById(R.id.tvChildField2);
        TextView tvTheLoai = convertView.findViewById(R.id.tvChildField3);
        TextView tvParent = convertView.findViewById(R.id.tvChildField4);

        tvTenChild.setText(child.getField1());
        tvNgayXb.setText(child.getField2());
        tvTheLoai.setText(child.getField3());

        String parentName = getParentName(child.getIdParent());
        tvParent.setText(parentName);

        convertView.setOnClickListener(v -> {
            ((ManageChildActivity) getContext()).setSelectedChild(child);
        });

        convertView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete Child")
                    .setMessage("Are you sure you want to delete this sách?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteChild(child);
                        Toast.makeText(getContext(), "Sách deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });

        return convertView;
    }

    private String getParentName(int idParent) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Parent", new String[]{"field1"}, "id=?", new String[]{String.valueOf(idParent)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String parentName = cursor.getString(cursor.getColumnIndexOrThrow("field1"));
            cursor.close();
            return parentName;
        }

        return "Unknown Parent";
    }

    private void deleteChild(Child child) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Child", "id=?", new String[]{String.valueOf(child.getId())});
        remove(child);
        notifyDataSetChanged();
    }
}
