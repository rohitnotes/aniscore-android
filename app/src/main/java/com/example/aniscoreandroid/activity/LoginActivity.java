package com.example.aniscoreandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.model.user.AuthResponse;
import com.example.aniscoreandroid.utils.ServerCall;
import com.example.aniscoreandroid.utils.TextChangeListener;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000").
            addConverterFactory(GsonConverterFactory.create()).build();
    private String email = "";
    private TextChangeListener emailListener;
    private TextChangeListener passwordListener;
    public static final String CURRENT_USER_NAME = "CURRENT_USER_NAME";
    public static final String CURRENT_USER_AVATAR = "CURRENT_USER_AVATAR";
    public static final String CURRENT_USER_ID = "CURRENT_USER_ID";
    public static final String CURRENT_USER_EMAIL = "CURRENT_USER_EMAIL";
    private String username = "";
    private String avatar = "";
    private String userId = "";
    private String LOGIN_SUCCESS = "Successfully Login";
    private String USER_NOT_FOUND = "Could not find user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // add text change listener for email box
        EditText enterEmail = findViewById(R.id.email);
        emailListener = new TextChangeListener();
        enterEmail.addTextChangedListener(emailListener);
        // add text change listener for password box
        EditText enterPassword = findViewById(R.id.password);
        passwordListener = new TextChangeListener();
        enterPassword.addTextChangedListener(passwordListener);
        // set click listener for login button
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        // set click listener for sign up button
        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSignUp();
            }
        });
    }

    private void login() {
        ServerCall service = retrofit.create(ServerCall.class);
        final TextView errorMessage = findViewById(R.id.login_error);
        email = emailListener.getQuery();
        String password = passwordListener.getQuery();
        // judge whether user input is in correct format
        if (email.length() == 0) {
            errorMessage.setText(("Please enter email address"));
            return;
        } else if (!email.contains("@")) {
            errorMessage.setText(("Incorrect email address format"));
            return;
        } else if (password.length() == 0) {
            errorMessage.setText(("Please enter password"));
            return;
        }
        // login call in server
        HashMap<String, String> input = new HashMap<>();
        input.put("email", email);
        input.put("password", password);
        final Call<AuthResponse> loginCall = service.login(input);
        loginCall.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    String message = response.body().getMessage();
                    if (message.equals(LOGIN_SUCCESS)) {
                        // successfully login
                        username = response.body().getUser().getUsername();
                        avatar = response.body().getUser().getAvatar();
                        userId = response.body().getUser().getUserId();
                        toHome();
                    } else if (message.equals(USER_NOT_FOUND)){
                        // user not found or incorrect password
                        errorMessage.setText(("Incorrect account or password"));
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }

    private void toHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(CURRENT_USER_NAME, username);
        intent.putExtra(CURRENT_USER_AVATAR, avatar);
        intent.putExtra(CURRENT_USER_EMAIL, email);
        intent.putExtra(CURRENT_USER_ID, userId);
        startActivity(intent);
    }

    private void toSignUp() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}