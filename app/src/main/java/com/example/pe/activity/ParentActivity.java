package com.example.pe.activity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pe.R;
import com.example.pe.helper.DatabaseHelper;
import com.example.pe.model.Parent;

import java.util.regex.Pattern;

public class ParentActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private EditText etParentField1, etParentField2, etParentField3, etParentField4;
    private Button btnAddParent;
    private Parent parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        dbHelper = new DatabaseHelper(this);
        etParentField1 = findViewById(R.id.etParentField1);
        etParentField2 = findViewById(R.id.etParentField2);
        etParentField3 = findViewById(R.id.etParentField3);
        etParentField4 = findViewById(R.id.etParentField4);
        btnAddParent = findViewById(R.id.btnAddParent);

        parent = (Parent) getIntent().getSerializableExtra("parent");

        if (parent != null) {
            etParentField1.setText(parent.getField1());
            btnAddParent.setText("Cập Nhật Parent");
        }

        btnAddParent.setOnClickListener(view -> {
            if (parent == null) {
                addParent();
            } else {
                updateParent();
            }
        });
    }

    private void addParent() {
        String parentField1 = etParentField1.getText().toString().trim();
        String parentField2 = etParentField2.getText().toString().trim();
        String parentField3 = etParentField3.getText().toString().trim();
        String parentField4 = etParentField4.getText().toString().trim();

        if (!validateInputs(parentField1, parentField2, parentField3, parentField4)) {
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("field1", parentField1);
        values.put("field2", parentField2);
        values.put("field3", parentField3);
        values.put("field4", parentField4);

        long result = db.insert("Parent", null, values);
        if (result != -1) {
            Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateParent() {
        String field1 = etParentField1.getText().toString().trim();
        String field2 = etParentField2.getText().toString().trim();
        String field3 = etParentField3.getText().toString().trim();
        String field4 = etParentField4.getText().toString().trim();

        if (!validateInputs(field1, field2, field3, field4)) {
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("field1", field1);
        values.put("field2", field2);
        values.put("field3", field3);
        values.put("field4", field4);

        int result = db.update("Parent", values, "id=?", new String[]{String.valueOf(parent.getId())});
        if (result > 0) {
            Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs(String tenTacgia, String email, String diaChi, String dienThoai) {
        if (tenTacgia.isEmpty()) {
            Toast.makeText(this, "Tên tác giả không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email.isEmpty() || !Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email)) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (diaChi.isEmpty()) {
            Toast.makeText(this, "Địa chỉ không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dienThoai.isEmpty() || !Pattern.matches("\\d{10,11}", dienThoai)) {
            Toast.makeText(this, "Điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
