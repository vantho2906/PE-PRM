package com.example.pe.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pe.R;
import com.example.pe.adapter.ParentAdapter;
import com.example.pe.helper.DatabaseHelper;
import com.example.pe.model.Parent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ManageParentActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private EditText etParentField1;
    private Button btnAddUpdateParent;
    private ListView lvParents;
    private ParentAdapter parentAdapter;
    private List<Parent> parentList;
    private Parent selectedParent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_parent);

        dbHelper = new DatabaseHelper(this);
        etParentField1 = findViewById(R.id.etParentField1);
        btnAddUpdateParent = findViewById(R.id.btnAddUpdateParent);
        lvParents = findViewById(R.id.lvParents);

        parentList = new ArrayList<>();
        parentAdapter = new ParentAdapter(this, parentList, dbHelper);
        lvParents.setAdapter(parentAdapter);

        loadParents();

        btnAddUpdateParent.setOnClickListener(view -> {
            if (selectedParent == null) {
                addParent();
            } else {
                updateParent();
            }
        });
    }

    private void addParent() {
        String field1 = etParentField1.getText().toString().trim();

        if (!validateInputs(field1)) {
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("field1", field1);

        long result = db.insert("Parent", null, values);
        if (result != -1) {
            Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show();
            loadParents();
            etParentField1.setText("");
        } else {
            Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateParent() {
        String etParentField1 = this.etParentField1.getText().toString().trim();

        if (!validateInputs(etParentField1)) {
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("field1", etParentField1);

        int result = db.update("Parent", values, "id=?", new String[]{String.valueOf(selectedParent.getId())});
        if (result > 0) {
            Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
            loadParents();
            this.etParentField1.setText("");
            selectedParent = null;
            btnAddUpdateParent.setText("Add Major");
        } else {
            Toast.makeText(this, "Failed to update parent", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs(String tenParent) {
        if (tenParent.isEmpty()) {
            Toast.makeText(this, "Tên Major không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }

//        if (email.isEmpty() || !Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email)) {
//            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
//            return false;
//        }

//        if (diaChi.isEmpty()) {
//            Toast.makeText(this, "Địa chỉ không được để trống", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (dienThoai.isEmpty() || !Pattern.matches("\\d{10,11}", dienThoai)) {
//            Toast.makeText(this, "Điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
//            return false;
//        }

        return true;
    }

    private void loadParents() {
        parentList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Parent", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int idParent = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String field1 = cursor.getString(cursor.getColumnIndexOrThrow("field1"));
//                String field2 = cursor.getString(cursor.getColumnIndexOrThrow("field2"));
//                String field3 = cursor.getString(cursor.getColumnIndexOrThrow("field3"));
//                String field4 = cursor.getString(cursor.getColumnIndexOrThrow("field4"));

                Parent parent = new Parent(idParent, field1);
                parentList.add(parent);
            } while (cursor.moveToNext());
        }
        cursor.close();
        parentAdapter.notifyDataSetChanged();
    }

    private void deleteParent(Parent parent) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Parent", "id=?", new String[]{String.valueOf(parent.getId())});
        parentList.remove(parent);
        parentAdapter.notifyDataSetChanged();
    }

    public void setSelectedParent(Parent parent) {
        selectedParent = parent;
        etParentField1.setText(parent.getField1());
        btnAddUpdateParent.setText("Update Major");
    }

    private void viewOnMap(Parent parent) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("parent", parent);
        startActivity(intent);
    }

    public void onItemClick(Parent parent) {
        setSelectedParent(parent);
        viewOnMap(parent);
    }

    public void onItemLongClick(Parent parent) {
        deleteParent(parent);
    }
}
