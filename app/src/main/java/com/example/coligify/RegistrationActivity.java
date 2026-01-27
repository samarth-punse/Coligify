package com.example.coligify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coligify.Database.DBHelper;

public class RegistrationActivity extends AppCompatActivity {

    EditText registername, registermobileno, registeremail,
            registerusername, registerpassword;
    Button newregister;
    TextView newlogin;
    ImageView togglePassword;

    DBHelper dbHelper;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        // DB
        dbHelper = new DBHelper(this);

        // SharedPreferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        // Views
        registername = findViewById(R.id.etregistername);
        registermobileno = findViewById(R.id.etregistermobileno);
        registeremail = findViewById(R.id.etregisteremail);
        registerusername = findViewById(R.id.etregisterusername);
        registerpassword = findViewById(R.id.etregisterpassword);
        togglePassword = findViewById(R.id.ivToggleRegisterPassword);
        newregister = findViewById(R.id.btnregister);
        newlogin = findViewById(R.id.tvnewlogin);


        togglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                registerpassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                );
                togglePassword.setImageResource(R.drawable.ic_eye_close);
                isPasswordVisible = false;
            } else {
                registerpassword.setInputType(
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );
                togglePassword.setImageResource(R.drawable.ic_eye_open);
                isPasswordVisible = true;
            }
            registerpassword.setSelection(registerpassword.getText().length());
        });

        // Register Button
        newregister.setOnClickListener(v -> {

            String strName = registername.getText().toString().trim();
            String strMobile = registermobileno.getText().toString().trim();
            String strEmail = registeremail.getText().toString().trim();
            String strUsername = registerusername.getText().toString().trim();
            String strPassword = registerpassword.getText().toString().trim();

            // Validations
            if (strName.isEmpty()) {
                registername.setError("Please enter name");
                return;
            }

            if (!strMobile.matches("\\d{10}")) {
                registermobileno.setError("Enter valid 10-digit mobile number");
                return;
            }

            if (!strEmail.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                registeremail.setError("Enter valid email address");
                return;
            }

            if (strUsername.length() < 8) {
                registerusername.setError("Username must be at least 8 characters");
                return;
            }

            if (strPassword.length() < 8) {
                registerpassword.setError("Password must be at least 8 characters");
                return;
            }

            if (!strPassword.matches(".*[A-Z].*")) {
                registerpassword.setError("Use at least 1 uppercase letter");
                return;
            }

            if (!strPassword.matches(".*[a-z].*")) {
                registerpassword.setError("Use at least 1 lowercase letter");
                return;
            }

            if (!strPassword.matches(".*[0-9].*")) {
                registerpassword.setError("Use at least 1 number");
                return;
            }

            if (!strPassword.matches(".*[!@#$%&*].*")) {
                registerpassword.setError("Use at least 1 special character");
                return;
            }

            addUser(strName, strMobile, strEmail, strUsername, strPassword);
        });

        // Login
        newlogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void addUser(String name, String mobile, String email,
                         String username, String password) {

        boolean isInserted = dbHelper.registerUser(
                name, mobile, email, username, password
        );

        if (isInserted) {
            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();

            editor.putString("Name", name);
            editor.putString("MobileNo", mobile);
            editor.putString("EmailId", email);
            editor.putString("Username", username);
            editor.apply();

            startActivity(new Intent(this, LoginActivity.class));
            finish();

        } else {
            Toast.makeText(this, "User already exists!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
