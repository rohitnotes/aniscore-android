package com.example.aniscoreandroid;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000").
            addConverterFactory(GsonConverterFactory.create()).build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getName();
    }

    private void getName() {
        ServerCall service = retrofit.create(ServerCall.class);
        Call<BangumiResponse> call = service.test1("39597");
        call.enqueue(new Callback<BangumiResponse>() {
            @Override
            public void onResponse(Call<BangumiResponse> call, Response<BangumiResponse> response) {
                Data data = response.body().getData();
                Bangumi bangumi = data.getBangumi();
                ((TextView)findViewById(R.id.test)).setText(bangumi.getTitle());
            }

            @Override
            public void onFailure(Call<BangumiResponse> call, Throwable t) {
                ((TextView)findViewById(R.id.test)).setText(t.toString());
            }
        });
    }
}
