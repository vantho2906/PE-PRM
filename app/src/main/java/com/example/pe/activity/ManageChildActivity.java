package com.example.pe.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pe.R;
import com.example.pe.adapter.ChildAdapter;
import com.example.pe.helper.DatabaseHelper;
import com.example.pe.model.Child;
import com.example.pe.model.Parent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ManageChildActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private EditText etChildField1, etChildField2, etChildField3;
    private Spinner spinnerParent;
    private Button btnAddUpdateChild;
    private ListView lvChilds;
    private ChildAdapter childAdapter;
    private List<Child> childList;
    private Child selectedChild = null;
    private List<Parent> parentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_child);

        dbHelper = new DatabaseHelper(this);
        etChildField1 = findViewById(R.id.etChildField1);
        etChildField2 = findViewById(R.id.etChildField2);
        etChildField3 = findViewById(R.id.etChildField3);
        spinnerParent = findViewById(R.id.spinnerParent);
        btnAddUpdateChild = findViewById(R.id.btnAddUpdateChild);
        lvChilds = findViewById(R.id.lvChildren);

        childList = new ArrayList<>();
        parentList = new ArrayList<>();

        childAdapter = new ChildAdapter(this, childList, dbHelper);
        lvChilds.setAdapter(childAdapter);

        loadChildren();
        loadParents();

        ArrayAdapter<String> parentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getParentNames());
        parentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParent.setAdapter(parentAdapter);

        btnAddUpdateChild.setOnClickListener(view -> {
            if (selectedChild == null) {
                addChild();
            } else {
                updateChild();
            }
        });
    }

    private void loadChildren() {
        childList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Child", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String field1 = cursor.getString(cursor.getColumnIndexOrThrow("field1"));
                String field2 = cursor.getString(cursor.getColumnIndexOrThrow("field2"));
                String field3 = cursor.getString(cursor.getColumnIndexOrThrow("field3"));
                int idParent = cursor.getInt(cursor.getColumnIndexOrThrow("idParent"));

                Child child = new Child(id, field1, field2, field3, idParent);
                childList.add(child);
            } while (cursor.moveToNext());
        }
        cursor.close();
        childAdapter.notifyDataSetChanged();
    }

    private void loadParents() {
        parentList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Parent", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int idParent = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String tenParent = cursor.getString(cursor.getColumnIndexOrThrow("field1"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("field2"));
                String diaChi = cursor.getString(cursor.getColumnIndexOrThrow("field3"));
                String dienThoai = cursor.getString(cursor.getColumnIndexOrThrow("field4"));

                Parent parent = new Parent(idParent, tenParent, email, diaChi, dienThoai);
                parentList.add(parent);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private List<String> getParentNames() {
        List<String> parentNames = new ArrayList<>();
        for (Parent parent : parentList) {
            parentNames.add(parent.getField1());
        }
        return parentNames;
    }

    private void addChild() {
        String tenChild = etChildField1.getText().toString().trim();
        String ngayXb = etChildField2.getText().toString().trim();
        String theLoai = etChildField3.getText().toString().trim();
        int idParent = parentList.get(spinnerParent.getSelectedItemPosition()).getId();

        if (!validateInputs(tenChild, ngayXb, theLoai)) {
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("field1", tenChild);
        values.put("field2", ngayXb);
        values.put("field3", theLoai);
        values.put("idParent", idParent);

        long result = db.insert("Child", null, values);
        if (result != -1) {
            Toast.makeText(this, "Sách added successfully", Toast.LENGTH_SHORT).show();
            loadChildren();
            etChildField1.setText("");
            etChildField2.setText("");
            etChildField3.setText("");
            spinnerParent.setSelection(0);
        } else {
            Toast.makeText(this, "Failed to add sách", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateChild() {
        String tenChild = etChildField1.getText().toString().trim();
        String ngayXb = etChildField2.getText().toString().trim();
        String theLoai = etChildField3.getText().toString().trim();
        int idParent = parentList.get(spinnerParent.getSelectedItemPosition()).getId();

        if (!validateInputs(tenChild, ngayXb, theLoai)) {
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("field1", tenChild);
        values.put("field2", ngayXb);
        values.put("field3", theLoai);
        values.put("idParent", idParent);

        int result = db.update("Child", values, "idChild=?", new String[]{String.valueOf(selectedChild.getId())});
        if (result > 0) {
            Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
            loadChildren();
            etChildField1.setText("");
            etChildField2.setText("");
            etChildField3.setText("");
            spinnerParent.setSelection(0);
            selectedChild = null;
            btnAddUpdateChild.setText("Add Sách");
        } else {
            Toast.makeText(this, "Failed to update sách", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs(String tenChild, String ngayXb, String theLoai) {
        if (tenChild.isEmpty()) {
            Toast.makeText(this, "Tên sách không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (ngayXb.isEmpty() || !Pattern.matches("\\d{2}/\\d{2}/\\d{4}", ngayXb)) {
            Toast.makeText(this, "Ngày xuất bản không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (theLoai.isEmpty()) {
            Toast.makeText(this, "Thể loại không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void deleteChild(Child child) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Child", "id=?", new String[]{String.valueOf(child.getId())});
        childList.remove(child);
        childAdapter.notifyDataSetChanged();
    }

    public void setSelectedChild(Child child) {
        selectedChild = child;
        etChildField1.setText(child.getField1());
        etChildField2.setText(child.getField2());
        etChildField3.setText(child.getField3());
        spinnerParent.setSelection(getParentPosition(child.getIdParent()));
        btnAddUpdateChild.setText("Update Child");
    }

    private int getParentPosition(int idParent) {
        for (int i = 0; i < parentList.size(); i++) {
            if (parentList.get(i).getId() == idParent) {
                return i;
            }
        }
        return 0;
    }
}
