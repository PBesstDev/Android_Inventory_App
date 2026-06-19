package com.example.bessmertnyy_3_2_assignment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//Profile screen showing user info, logout, and SMS alert setup.
public class UserProfileActivity extends AppCompatActivity {

    //Request code used when asking Android for SEND_SMS permission.
    private static final int REQUEST_SMS_PERMISSION = 1001;
    //DB helper for reading/updating user profile fields.
    private UserDatabaseHelper userDatabaseHelper;
    //Username for currently active account.
    private String activeUsername;
    //Dialog object used to collect phone number.
    private AlertDialog phoneDialog;
    //Textbox in dialog where phone number is typed.
    private EditText phoneInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        TextView tvFirstNameValue = findViewById(R.id.tvFirstNameValue);
        TextView tvLastNameValue = findViewById(R.id.tvLastNameValue);
        TextView tvUsernameValue = findViewById(R.id.tvUsernameValue);
        Button btnSendSms = findViewById(R.id.btnSendSms);
        Button btnReturn = findViewById(R.id.btnReturn);
        Button btnLogout = findViewById(R.id.btnLogout);

        activeUsername = getIntent().getStringExtra("username");
        if (activeUsername == null || activeUsername.trim().isEmpty()) {
            activeUsername = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                    .getString("logged_in_username", "");
        }

        userDatabaseHelper = new UserDatabaseHelper(this);
        String[] profile = userDatabaseHelper.getUserProfile(activeUsername);

        if (profile != null) {
            tvFirstNameValue.setText(profile[0]);
            tvLastNameValue.setText(profile[1]);
            tvUsernameValue.setText(profile[2]);
        } else {
            tvFirstNameValue.setText(getString(R.string.profile_value_unavailable));
            tvLastNameValue.setText(getString(R.string.profile_value_unavailable));
            tvUsernameValue.setText(getString(R.string.profile_value_unavailable));
        }

        btnSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhoneNumberDialog();
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    //Open dialog so user can enter/update SMS phone number.
    private void showPhoneNumberDialog() {
        phoneInput = new EditText(this);
        phoneInput.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneInput.setHint(getString(R.string.profile_sms_phone_hint));

        String existingNumber = userDatabaseHelper.getUserPhoneNumber(activeUsername);
        if (existingNumber == null || existingNumber.trim().isEmpty()) {
            existingNumber = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("sms_number", "");
        }
        if (existingNumber != null && !existingNumber.trim().isEmpty()) {
            phoneInput.setText(existingNumber.trim());
            phoneInput.setSelection(phoneInput.getText().length());
        }

        phoneDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.profile_sms_phone_title)
                .setMessage(R.string.profile_sms_phone_message)
                .setView(phoneInput)
                .setPositiveButton(R.string.profile_sms_save_and_enable, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        phoneDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = phoneDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handlePhoneSaveAndPermission();
                    }
                });
            }
        });

        phoneDialog.show();
    }

    //Validate number, save it, then ask for SMS permission if needed.
    private void handlePhoneSaveAndPermission() {
        String phoneNumber = phoneInput.getText().toString().trim();

        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, R.string.profile_sms_phone_required, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            Toast.makeText(this, R.string.profile_sms_phone_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!savePhoneNumberForUser(phoneNumber)) {
            Toast.makeText(this, R.string.profile_sms_phone_save_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.profile_sms_permission_already_granted, Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
        }

        if (phoneDialog != null) {
            phoneDialog.dismiss();
        }
    }

    //Basic phone check: allow common symbols and require 10-15 digits.
    private boolean isValidPhoneNumber(String phoneNumber) {
        int digitCount = 0;

        for (int i = 0; i < phoneNumber.length(); i++) {
            char ch = phoneNumber.charAt(i);
            if (Character.isDigit(ch)) {
                digitCount++;
            } else if (ch == ' ' || ch == '+' || ch == '-' || ch == '(' || ch == ')') {
            } else {
                return false;
            }
        }

        return digitCount >= 10 && digitCount <= 15;
    }

    //Save phone number to active user profile + legacy preference.
    private boolean savePhoneNumberForUser(String phoneNumber) {
        if (activeUsername == null || activeUsername.trim().isEmpty()) {
            return false;
        }

        boolean profileSaved = userDatabaseHelper.updateUserPhoneNumber(activeUsername, phoneNumber);
        if (profileSaved) {
            getSharedPreferences("AppPrefs", MODE_PRIVATE)
                    .edit()
                    .putString("sms_number", phoneNumber)
                    .apply();
        }
        return profileSaved;
    }

    @Override
    //Handle user response from SMS permission request dialog.
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.profile_sms_permission_enabled, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.profile_sms_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Clear login session and send user back to login screen.
    private void logoutUser() {
        getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .edit()
                .remove("logged_in_username")
                .apply();

        Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

