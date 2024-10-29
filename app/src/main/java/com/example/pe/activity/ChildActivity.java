package com.example.pe.activity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pe.R;
import com.example.pe.helper.DatabaseHelper;
import com.example.pe.model.Child;
import com.example.pe.model.Parent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChildActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private EditText etChildField1, etChildField2, etChildField3;
    private Spinner spinnerParent;
    private Button btnAddChild;
    private List<Parent> parentList;
    private ArrayAdapter<String> parentAdapter;
    private Child child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        dbHelper = new DatabaseHelper(this);
        etChildField1 = findViewById(R.id.etChildField1);
        etChildField2 = findViewById(R.id.etChildField2);
        etChildField3 = findViewById(R.id.etChildField3);
        spinnerParent = findViewById(R.id.spinnerParent);
        btnAddChild = findViewById(R.id.btnAddChild);

        etChildField2.setOnClickListener(view -> showDatePickerDialog());

        parentList = new ArrayList<>();
        loadParents();

        parentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getParentNames());
        parentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParent.setAdapter(parentAdapter);

        child = (Child) getIntent().getSerializableExtra("child");

        if (child != null) {
            etChildField1.setText(child.getField1());
            etChildField2.setText(child.getField2());
            etChildField3.setText(child.getField3());
            spinnerParent.setSelection(getParentPosition(child.getIdParent()));
            btnAddChild.setText("Cập Nhật Child");
        }

        btnAddChild.setOnClickListener(view -> {
            if (child == null) {
                addChild();
            } else {
                updateChild();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etChildField2.setText(date);
                }, year, month, day);
        datePickerDialog.show();
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

    private int getParentPosition(int idParent) {
        for (int i = 0; i < parentList.size(); i++) {
            if (parentList.get(i).getId() == idParent) {
                return i;
            }
        }
        return 0;
    }

    private void addChild() {
        String childField1 = etChildField1.getText().toString().trim();
        String childField2 = etChildField2.getText().toString().trim();
        String childField3 = etChildField3.getText().toString().trim();
        int idParent = parentList.get(spinnerParent.getSelectedItemPosition()).getId();

        if (childField1.isEmpty() || childField2.isEmpty() || childField3.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("field1", childField1);
        values.put("field2", childField2);
        values.put("field3", childField3);
        values.put("idParent", idParent);

        long result = db.insert("Child", null, values);
        if (result != -1) {
            Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateChild() {
        String childField1 = etChildField1.getText().toString().trim();
        String childField2 = etChildField2.getText().toString().trim();
        String childField3 = etChildField3.getText().toString().trim();
        int idParent = parentList.get(spinnerParent.getSelectedItemPosition()).getId();

        if (childField1.isEmpty() || childField2.isEmpty() || childField3.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("field1", childField1);
        values.put("field2", childField2);
        values.put("field3", childField3);
        values.put("idParent", idParent);

        int result = db.update("Child", values, "id=?", new String[]{String.valueOf(child.getId())});
        if (result > 0) {
            Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show();
        }
    }
}
