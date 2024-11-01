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
    private EditText etChildField1, etChildField2, etChildField3, etChildField4, etChildField5;
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
        etChildField4 = findViewById(R.id.etChildField4);
        etChildField5 = findViewById(R.id.etChildField5);
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
                String field4 = cursor.getString(cursor.getColumnIndexOrThrow("field4"));
                String field5 = cursor.getString(cursor.getColumnIndexOrThrow("field5"));
                int idParent = cursor.getInt(cursor.getColumnIndexOrThrow("idParent"));

                Child child = new Child(id, field1, field2, field3, field4, field5, idParent);
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
//                String email = cursor.getString(cursor.getColumnIndexOrThrow("field2"));
//                String diaChi = cursor.getString(cursor.getColumnIndexOrThrow("field3"));
//                String dienThoai = cursor.getString(cursor.getColumnIndexOrThrow("field4"));

                Parent parent = new Parent(idParent, tenParent);
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
        String field4 = etChildField4.getText().toString().trim();
        String field5 = etChildField5.getText().toString().trim();
        int idParent = parentList.get(spinnerParent.getSelectedItemPosition()).getId();

        if (!validateInputs(tenChild, ngayXb, theLoai, field4, field5)) {
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("field1", tenChild);
        values.put("field2", ngayXb);
        values.put("field3", theLoai);
        values.put("field4", field4);
        values.put("field5", field5);
        values.put("idParent", idParent);

        long result = db.insert("Child", null, values);
        if (result != -1) {
            Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show();
            loadChildren();
            etChildField1.setText("");
            etChildField2.setText("");
            etChildField3.setText("");
            etChildField4.setText("");
            etChildField5.setText("");
            spinnerParent.setSelection(0);
        } else {
            Toast.makeText(this, "Failed to add Student", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateChild() {
        String tenChild = etChildField1.getText().toString().trim();
        String ngayXb = etChildField2.getText().toString().trim();
        String theLoai = etChildField3.getText().toString().trim();
        String field4 = etChildField4.getText().toString().trim();
        String field5 = etChildField5.getText().toString().trim();
        int idParent = parentList.get(spinnerParent.getSelectedItemPosition()).getId();

        if (!validateInputs(tenChild, ngayXb, theLoai, field4, field5)) {
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("field1", tenChild);
        values.put("field2", ngayXb);
        values.put("field3", theLoai);
        values.put("field4", field4);
        values.put("field5", field5);
        values.put("idParent", idParent);

        int result = db.update("Child", values, "id=?", new String[]{String.valueOf(selectedChild.getId())});
        if (result > 0) {
            Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
            loadChildren();
            etChildField1.setText("");
            etChildField2.setText("");
            etChildField3.setText("");
            etChildField4.setText("");
            etChildField5.setText("");
            spinnerParent.setSelection(0);
            selectedChild = null;
            btnAddUpdateChild.setText("Add Student");
        } else {
            Toast.makeText(this, "Failed to update Student", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs(String name, String date, String gender, String email, String address) {
        if (name.isEmpty()) {
            Toast.makeText(this, "Tên student không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (date.isEmpty() || !Pattern.matches("\\d{2}/\\d{2}/\\d{4}", date)) {
            Toast.makeText(this, "Date không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (gender.isEmpty() || !Pattern.matches("(?i)^(male|female)$", gender)) {
            Toast.makeText(this, "Gender không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email.isEmpty() || !Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email)) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (address.isEmpty()) {
            Toast.makeText(this, "Address không được trống", Toast.LENGTH_SHORT).show();
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
        etChildField4.setText(child.getField4());
        etChildField5.setText(child.getField5());
        spinnerParent.setSelection(getParentPosition(child.getIdParent()));
        btnAddUpdateChild.setText("Update Student");
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
