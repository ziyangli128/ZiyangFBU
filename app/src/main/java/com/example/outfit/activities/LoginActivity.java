package com.example.outfit.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.outfit.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import permissions.dispatcher.PermissionUtils;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private static final String[] PERMISSION_GETMYLOCATION =
            new String[] {"android.permission.ACCESS_FINE_LOCATION","android.permission.ACCESS_COARSE_LOCATION"};
    private static final int REQUEST_GETMYLOCATION = 0;
    private static final int REQUEST_CODE = 20;

    private Button btnLogin;
    private EditText etUsername;
    private EditText etPassword;
    private TextView tvSignup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            LoginActivity.this,
                            PERMISSION_GETMYLOCATION,
                            REQUEST_GETMYLOCATION);
        }
        else {
            // if a user its already logged in, go to main directly
            if (ParseUser.getCurrentUser() != null) {
                goMainActivity();
            }

            btnLogin = findViewById(R.id.btnLogin);
            etUsername = findViewById(R.id.etUsername);
            etPassword = findViewById(R.id.etPassword);
            tvSignup = findViewById(R.id.tvSignup);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick login button");
                    String username = etUsername.getText().toString();
                    String password = etPassword.getText().toString();
                    loginUser(username, password);
                }
            });

            tvSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: Sign up button");
                    goSignupActivity();
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == REQUEST_GETMYLOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // if a user its already logged in, go to main directly
                if (ParseUser.getCurrentUser() != null) {
                    goMainActivity();
                }

                btnLogin = findViewById(R.id.btnLogin);
                etUsername = findViewById(R.id.etUsername);
                etPassword = findViewById(R.id.etPassword);
                tvSignup = findViewById(R.id.tvSignup);

                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i(TAG, "onClick login button");
                        String username = etUsername.getText().toString();
                        String password = etPassword.getText().toString();
                        loginUser(username, password);
                    }
                });

                tvSignup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i(TAG, "onClick: Sign up button");
                        goSignupActivity();
                    }
                });
            } else {
                ActivityCompat
                        .requestPermissions(
                                LoginActivity.this,
                                PERMISSION_GETMYLOCATION,
                                REQUEST_GETMYLOCATION);
            }
        }
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "loginUser: " + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(LoginActivity.this,
                            R.string.login_fail_toast, Toast.LENGTH_LONG).show();
                    return;
                }
                goMainActivity();
                Toast.makeText(LoginActivity.this, R.string.login_success_toast,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void goSignupActivity() {
        Intent i = new Intent(this, SignupActivity.class);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, R.string.signup_success_toast, Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
