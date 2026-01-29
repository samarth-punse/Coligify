package com.example.coligify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coligify.Database.DBHelper;

public class LoginActivity extends AppCompatActivity {

    EditText loginusername, loginpassword;
    Button loginBtn;
    TextView loginnewmember, tvForgotPassword;
    ImageView togglePassword;

    DBHelper dbHelper;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    boolean isPasswordVisible = false;
    boolean doubleTap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Init
        dbHelper = new DBHelper(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        if (preferences.getBoolean("islogin",false)){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Views
        loginusername = findViewById(R.id.etloginusername);
        loginpassword = findViewById(R.id.etloginpassword);
        loginBtn = findViewById(R.id.btnlogin);
        loginnewmember = findViewById(R.id.btnloginnewmember);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        togglePassword = findViewById(R.id.ivTogglePassword);

        // 👁️ SHOW / HIDE PASSWORD
        togglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                loginpassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                );
                togglePassword.setImageResource(R.drawable.ic_eye_close);
                isPasswordVisible = false;
            } else {
                loginpassword.setInputType(
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );
                togglePassword.setImageResource(R.drawable.ic_eye_open);
                isPasswordVisible = true;
            }
            loginpassword.setSelection(loginpassword.getText().length());
        });

        // LOGIN BUTTON
        loginBtn.setOnClickListener(v -> {

            String username = loginusername.getText().toString().trim();
            String password = loginpassword.getText().toString().trim();

            if (username.isEmpty()) {
                loginusername.setError("Username is required");
                return;
            }

            if (username.length() < 8) {
                loginusername.setError("Username must be at least 8 characters");
                return;
            }

            if (password.isEmpty()) {
                loginpassword.setError("Password is required");
                return;
            }

            if (password.length() < 8) {
                loginpassword.setError("Password must be at least 8 characters");
                return;
            }

            if (!password.matches(".*[A-Z].*")) {
                loginpassword.setError("Use at least 1 uppercase letter");
                return;
            }

            if (!password.matches(".*[a-z].*")) {
                loginpassword.setError("Use at least 1 lowercase letter");
                return;
            }

            if (!password.matches(".*[0-9].*")) {
                loginpassword.setError("Use at least 1 number");
                return;
            }

            if (!password.matches(".*[!@#$%&*].*")) {
                loginpassword.setError("Use at least 1 special symbol");
                return;
            }

            validateLogin(username, password);
        });


        loginnewmember.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistrationActivity.class));
            finish();
        });


        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, Forgot_Password_Activity.class));
        });
    }

    private void validateLogin(String username, String password) {

        if (dbHelper.validateLogin(username, password)) {

            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

            editor.putBoolean("islogin", true);
            editor.putString("username", username);
            editor.apply();

            startActivity(new Intent(this, HomeActivity.class));
            finish();

        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (doubleTap) {
            finishAffinity();
        } else {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            doubleTap = true;
            new Handler().postDelayed(() -> doubleTap = false, 2000);
        }
    }
}
