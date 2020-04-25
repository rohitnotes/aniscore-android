package com.example.aniscoreandroid.activity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.model.user.UserResponse;
import com.example.aniscoreandroid.utils.ServerCall;
import com.google.android.material.button.MaterialButton;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SelectImageActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000").
            addConverterFactory(GsonConverterFactory.create()).build();
    private String baseUrl = "http://10.0.2.2:4000/";
    public static final int SELECT_IMAGE = 10;
    private String mode;                                // "avatar" or "background"
    private String userId;
    private Uri imageUri;
    private int screenWidth;
    private final String SUCCESS_UPLOAD_AVATAR = "Successfully upload the avatar";
    private final String SUCCESS_UPLOAD_BACKGROUND = "Successfully upload the background";


    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_select_image);
        MaterialButton selectImage = findViewById(R.id.selectImageButton);
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        mode = intent.getStringExtra("MODE");               // used to determine changing background or avatar
        String currentImage = intent.getStringExtra("CURRENT_IMAGE");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        ImageView preview = findViewById(R.id.preview);
        // get screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        // set current avatar/background into preview
        if (currentImage != null && currentImage.length() > 0) {
            Glide.with(this).load(baseUrl + currentImage).asBitmap().centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).override(screenWidth, screenWidth)
                    .into(preview);
        } else {
            // user has not set avatar/background
            if (mode.equals("background")) {
                preview.setImageResource(R.drawable.default_background);
                preview.setScaleType(ImageView.ScaleType.FIT_XY);
                preview.setAdjustViewBounds(true);
            } else {
                preview.setImageResource(R.drawable.default_avatar);
                preview.setScaleType(ImageView.ScaleType.FIT_XY);
                preview.setAdjustViewBounds(true);
            }
        }
        // click select image button to select image
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_IMAGE);
            }
        });
        findViewById(R.id.uploadImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView image = findViewById(R.id.preview);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                imageUri = data.getData();
                Glide.with(this).load(imageUri).centerCrop().override(screenWidth, screenWidth).into(image);
            }
        }
    }

    /*
     * get file path of the image based on the uri
     */
    private String getPath(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }

    private void uploadImage() {
        ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
        );
        File imageFile = new File(getPath(imageUri));
        ServerCall service = retrofit.create(ServerCall.class);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part imageUpload;
        Call<UserResponse> updateImage;
        if (mode.equals("avatar")) {
            imageUpload = MultipartBody.Part.createFormData("avatar", imageFile.getName(), reqFile);
            updateImage = service.updateAvatarById(userId, imageUpload);
        } else {
            imageUpload = MultipartBody.Part.createFormData("background", imageFile.getName(), reqFile);
            updateImage = service.updateBackgroundById(userId, imageUpload);
        }
        updateImage.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    String message = response.body().getMessage();
                    if (message.equals(SUCCESS_UPLOAD_AVATAR) || message.equals(SUCCESS_UPLOAD_BACKGROUND)) {
                        toUser();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                ((TextView)findViewById(R.id.debug)).setText(t.toString());
            }
        });
    }

    /*
     * direct to user page
     */
    private void toUser() {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("USER_ID", userId);
        // let user activity direct back to main when back arrow clicked
        intent.putExtra("SOURCE", "MAIN");
        startActivity(intent);
    }
}