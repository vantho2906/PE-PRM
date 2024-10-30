package com.example.pe.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pe.R;
import com.example.pe.activity.ManageChildActivity;
import com.example.pe.activity.MapActivity;
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

        TextView field1 = convertView.findViewById(R.id.tvChildField1);
        TextView field2 = convertView.findViewById(R.id.tvChildField2);
        TextView field3 = convertView.findViewById(R.id.tvChildField3);
        TextView field4 = convertView.findViewById(R.id.tvChildField4);
        TextView field5 = convertView.findViewById(R.id.tvChildField5);
        TextView parentFieldName = convertView.findViewById(R.id.parentFieldName);

        field1.setText(child.getField1());
        field2.setText(child.getField2());
        field3.setText(child.getField3());
        field4.setText(child.getField4());
        field5.setText(child.getField5());

        String parentName = getParentName(child.getIdParent());
        parentFieldName.setText(parentName);
        Button btnViewOnMap = convertView.findViewById(R.id.more);

        convertView.setOnClickListener(v -> {
            ((ManageChildActivity) getContext()).setSelectedChild(child);
        });
        btnViewOnMap.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MapActivity.class);
            intent.putExtra("child", child);
            getContext().startActivity(intent);
        });

        convertView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete")
                    .setMessage("Are you sure you want to delete?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteChild(child);
                        Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
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

        return "Unknown Major";
    }

    private void deleteChild(Child child) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Child", "id=?", new String[]{String.valueOf(child.getId())});
        remove(child);
        notifyDataSetChanged();
    }
}
