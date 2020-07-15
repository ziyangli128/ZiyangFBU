package com.example.outfit.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.outfit.R;
import com.example.outfit.databinding.ActivitySignupBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SignupActivity";

    ActivitySignupBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        binding.btnSignupSubmit.setOnClickListener(new View.OnClickListener() {
            ParseUser user = new ParseUser();
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: Signup submit button");
                user.setEmail(binding.etSignupEmail.getText().toString());
                user.setUsername(binding.etSignupUsername.getText().toString());
                user.setPassword(binding.etSignupPassword.getText().toString());
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue with signup", e);
                            Toast.makeText(SignupActivity.this,
                                    R.string.signup_fail_toast, Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                });
            }
        });
    }
}
