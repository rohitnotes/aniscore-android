package com.example.aniscoreandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aniscoreandroid.model.user.AuthResponse;
import com.example.aniscoreandroid.utils.ServerCall;
import com.example.aniscoreandroid.utils.TextChangeListener;
import com.google.android.material.button.MaterialButton;

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
    private String password= "";
    private EditText enterEmail;
    private EditText enterPassword;
    private TextChangeListener emailListener;
    private TextChangeListener passwordListener;
    public static final String CURRENT_USER_NAME = "CURRENT_USER_NAME";
    public static final String CURRENT_USER_AVATAR = "CURRENT_USER_AVATAR";
    public static final String CURRENT_USER_ID = "CURRENT_USER_ID";
    public static final String CURRENT_USER_EMAIL = "CURRENT_USER_EMAIL";
    private String username = "";
    private String avatar = "";
    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        enterEmail = findViewById(R.id.email);
        emailListener = new TextChangeListener();
        passwordListener = new TextChangeListener();
        enterEmail.addTextChangedListener(emailListener);
        enterPassword = findViewById(R.id.password);
        enterPassword.addTextChangedListener(passwordListener);
        MaterialButton login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        email = emailListener.getQuery();
        password = passwordListener.getQuery();
        ServerCall service = retrofit.create(ServerCall.class);
        HashMap<String, String> input = new HashMap<>();
        input.put("email", email);
        input.put("password", password);
        final Call<AuthResponse> loginCall = service.login(input);
        loginCall.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getMessage().equals("Successfully Login")) {            // successfully login
                        ((TextView)findViewById(R.id.test)).setText(response.body().getUser().getAvatar());
                        username = response.body().getUser().getUsername();
                        avatar = response.body().getUser().getAvatar();
                        userId = response.body().getUser().getUserId();
                        toHome();
                    } else {
                        ((TextView)findViewById(R.id.test)).setText(("password error"));
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                System.out.println("fail");
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
}