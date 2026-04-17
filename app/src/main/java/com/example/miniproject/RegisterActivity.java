package com.example.miniproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    EditText etRegUser, etRegPass;
    Button btnSubmitRegister, btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etRegUser = findViewById(R.id.etRegUser);
        etRegPass = findViewById(R.id.etRegPass);
        btnSubmitRegister = findViewById(R.id.btnSubmitRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        btnSubmitRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = etRegUser.getText().toString();
                String pass = etRegPass.getText().toString();

                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    if (prefs.contains(user)) {
                        Toast.makeText(RegisterActivity.this, "Username exists!", Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(user, pass);
                        editor.apply();
                        Toast.makeText(RegisterActivity.this, "Success! Please Login", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });

        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}