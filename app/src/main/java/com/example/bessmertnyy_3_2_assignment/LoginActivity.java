

package com.example.bessmertnyy_3_2_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


//Activity for handling user login and creating account
public class LoginActivity extends AppCompatActivity {

    // UI element variables
    private EditText myUsername;
    private EditText myPassword;
    private Button btnLogin;
    private Button btnCreateAccount;
    
    // Database helper for user authentication
    private UserDatabaseHelper userDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize the database helper
        userDatabaseHelper = new UserDatabaseHelper(this);

        // 1. Link Java variables to UI components defined in XML
        myUsername = findViewById(R.id.etUsername);
        myPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        // 2. Set up the Login Button Listener to process credentials
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        // 3. Set up the Create Account button listener to register new users
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    //Validate credentials and log into the Dashboard if successful.
    private void performLogin() {
        String inputUsername = myUsername.getText().toString().trim();
        String inputPassword = myPassword.getText().toString().trim();

        // Check if both fields are filled out
        if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate against the local SQLite database
        if (userDatabaseHelper.validateUser(inputUsername, inputPassword)) {
            // Redirect to the Main Dashboard on success
            Intent intent = new Intent(LoginActivity.this, MainViewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            // Alert user of invalid credentials
            Toast.makeText(LoginActivity.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
        }
    }

    //Add a new user to database if username is unique
    //TODO: Make a fancy separate screen for creating account. Possible require email verification
    private void createAccount() {
        String inputUsername = myUsername.getText().toString().trim();
        String inputPassword = myPassword.getText().toString().trim();

        // Ensure fields are not empty
        if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
            Toast.makeText(this, "Enter a username and password first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Attempt to add user to database
        boolean added = userDatabaseHelper.addUser(inputUsername, inputPassword);
        if (added) {
            Toast.makeText(this, "Account created. You can now log in.", Toast.LENGTH_SHORT).show();
        } else {
            // Username already exists in the system
            Toast.makeText(this, "That username already exists.", Toast.LENGTH_SHORT).show();
        }
    }
}
