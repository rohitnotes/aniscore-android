package com.example.aniscoreandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.activity.LoginActivity;
import com.example.aniscoreandroid.model.user.AuthResponse;
import com.example.aniscoreandroid.signupView.AccountCreateWindow;
import com.example.aniscoreandroid.utils.ServerCall;
import com.example.aniscoreandroid.utils.TextChangeListener;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity {
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create()).build();
    private TextChangeListener usernameListener;
    private TextChangeListener emailListener;
    private TextChangeListener passwordListener;
    private TextChangeListener confirmPasswordListener;
    private String USERNAME_EXISTS = "Username already exists";
    private String EMAIL_EXISTS = "Email already exists";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // add text change listener for username box
        EditText enterUsername = findViewById(R.id.enter_username);
        usernameListener = new TextChangeListener();
        enterUsername.addTextChangedListener(usernameListener);
        // add text change listener for email address box
        EditText enterEmail = findViewById(R.id.enter_email);
        emailListener = new TextChangeListener();
        enterEmail.addTextChangedListener(emailListener);
        // add text change listener for password box
        EditText enterPassword = findViewById(R.id.enter_password);
        passwordListener = new TextChangeListener();
        enterPassword.addTextChangedListener(passwordListener);
        // add text change listener for confirm password box
        EditText confirmPassword = findViewById(R.id.confirm_password);
        confirmPasswordListener = new TextChangeListener();
        confirmPassword.addTextChangedListener(confirmPasswordListener);
        // set click listener for create account button
        findViewById(R.id.create_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        // set listener for cancel button
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toLogin();
            }
        });
    }

    private void signup() {
        ServerCall service = retrofit.create(ServerCall.class);
        final TextView errorMessage = findViewById(R.id.signup_error);
        String username = usernameListener.getQuery();
        String email = emailListener.getQuery();
        String password = passwordListener.getQuery();
        String confirmPassword = confirmPasswordListener.getQuery();
        // judge whether all information are correct in format
        if (username.length() == 0) {
            // username not entered
            errorMessage.setText(("Please enter username"));
            return;
        } else if (email.length() == 0) {
            errorMessage.setText(("Please enter email"));
            return;
        } else if (!email.contains("@")) {
            // email format error
            errorMessage.setText(("Incorrect email address format"));
            return;
        } else if (password.length() < 8) {
            // password length less than 8
            errorMessage.setText(("Password must be at least 8 characters"));
            return;
        } else if (!password.equals(confirmPassword)) {
            // password and confirm password are not the same
            errorMessage.setText(("Passwords not the same"));
            return;
        }
        HashMap<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("email", email);
        body.put("password", password);
        // sign up call in server
        Call<AuthResponse> signupCall = service.signup(body);
        signupCall.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.body().getMessage().equals(USERNAME_EXISTS)) {
                    errorMessage.setText(("Username already exists"));
                } else if (response.body().getMessage().equals(EMAIL_EXISTS)) {
                    errorMessage.setText(("Email already exists"));
                } else {
                    // inform user account created
                    showDialog();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }

    /*
     * a dialog showing account created information
     */
    private void showDialog() {
        AccountCreateWindow accountCreateWindow = new AccountCreateWindow();
        accountCreateWindow.show(getSupportFragmentManager(), "Account created");
    }

    /*
     * direct user to login page
     */
    private void toLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}