

package com.example.bessmertnyy_3_2_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


//Login screen that authenticates users with local SQLite data.
public class LoginActivity extends AppCompatActivity {

    //Username + password input fields.
    private EditText myUsername;
    private EditText myPassword;
    //Helper for user account DB queries.
    private UserDatabaseHelper userDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDatabaseHelper = new UserDatabaseHelper(this);

        myUsername = findViewById(R.id.etUsername);
        myPassword = findViewById(R.id.etPassword);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, UserCreateActivity.class));
            }
        });
    }

    //Validates credentials and opens dashboard on success.
    private void performLogin() {
        String inputUsername = myUsername.getText().toString().trim();
        String inputPassword = myPassword.getText().toString().trim();

        if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userDatabaseHelper.validateUser(inputUsername, inputPassword)) {
            getSharedPreferences("AppPrefs", MODE_PRIVATE)
                    .edit()
                    .putString("logged_in_username", inputUsername)
                    .apply();

            Intent intent = new Intent(LoginActivity.this, MainViewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
        }
    }
}

