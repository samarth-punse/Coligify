package com.example.coligify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coligify.Database.DBHelper;

public class Forgot_Password_Activity extends AppCompatActivity {

    EditText etNewPassword, etConfirmPassword;
    ImageView ivToggleNewPassword, ivToggleConfirmPassword;
    Button btnConfirmReset;

    boolean isNewVisible = false;
    boolean isConfirmVisible = false;

    DBHelper dbHelper;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // DB & SharedPreferences
        dbHelper = new DBHelper(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get username (saved during login / forgot flow)
        String username = preferences.getString("username", null);

        // Views
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivToggleNewPassword = findViewById(R.id.ivToggleNewPassword);
        ivToggleConfirmPassword = findViewById(R.id.ivToggleConfirmPassword);
        btnConfirmReset = findViewById(R.id.btnConfirmReset);

        // 👁️ Toggle New Password
        ivToggleNewPassword.setOnClickListener(v -> {
            if (isNewVisible) {
                etNewPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                );
                ivToggleNewPassword.setImageResource(R.drawable.ic_eye_close);
                isNewVisible = false;
            } else {
                etNewPassword.setInputType(
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );
                ivToggleNewPassword.setImageResource(R.drawable.ic_eye_open);
                isNewVisible = true;
            }
            etNewPassword.setSelection(etNewPassword.getText().length());
        });

        // 👁️ Toggle Confirm Password
        ivToggleConfirmPassword.setOnClickListener(v -> {
            if (isConfirmVisible) {
                etConfirmPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                );
                ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye_close);
                isConfirmVisible = false;
            } else {
                etConfirmPassword.setInputType(
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );
                ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye_open);
                isConfirmVisible = true;
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        // RESET PASSWORD
        btnConfirmReset.setOnClickListener(v -> {

            String newPass = etNewPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            if (newPass.isEmpty()) {
                etNewPassword.setError("Enter new password");
                return;
            }

            if (newPass.length() < 8) {
                etNewPassword.setError("Minimum 8 characters");
                return;
            }

            if (!newPass.matches(".*[A-Z].*") ||
                    !newPass.matches(".*[a-z].*") ||
                    !newPass.matches(".*[0-9].*") ||
                    !newPass.matches(".*[!@#$%&*].*")) {
                etNewPassword.setError("Use upper, lower, number & symbol");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                etConfirmPassword.setError("Passwords do not match");
                return;
            }

            if (username == null) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                return;
            }

            // UPDATE PASSWORD IN DB
            boolean updated = dbHelper.updatePasswordByUsername(username, newPass);

            if (updated) {
                Toast.makeText(this, "Password reset successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Failed to reset password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
