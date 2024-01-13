package com.example.closetfrontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class AddClothesActivity extends AppCompatActivity {
    private ImageView imageView;
    private static final String API_KEY = "smSA3YYqq9zN9XymdrriWFyE";
    private ImageView heartButton;
    private ImageView trashButton;
    private ImageView noneButton;

    private TextView heartText;
    private TextView trashText;
    private TextView noneText;
    private ToggleButton wishToggle;

    private String imageUrl;
    private ArrayList<String> selectedTags = new ArrayList<>();
    private ArrayList<String> selectedStyles = new ArrayList<>();

    private RetrofitInterface retrofitInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clothes);

        String imagePath = getIntent().getStringExtra("imgPath");
        imageView = findViewById(R.id.idIVImage);
        File imageFile = new File(imagePath);

        if (imageFile.exists()) {
            removeBg(imageFile);
        } else {
            Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show();
        }

        ImageView backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBackToPhotosActivity();
            }
        });

        heartButton = findViewById(R.id.heartIcon);
        trashButton = findViewById(R.id.trashIcon);
        noneButton = findViewById(R.id.noneButton);
        heartText = findViewById(R.id.heartText);
        trashText = findViewById(R.id.trashText);
        noneText = findViewById(R.id.noneText);

        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick("heart");
            }
        });

        trashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick("trash");
            }
        });

        noneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick("none");
            }
        });

        wishToggle = findViewById(R.id.idWishToggle);
        wishToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wishToggle.setBackgroundResource(R.drawable.toggle_on);
                    selectedTags.add("wish");
                } else {
                    wishToggle.setBackgroundResource(R.drawable.toggle_off);
                }
            }
        });

        TextView casualStyleTextView = findViewById(R.id.idCasualStyle);
        TextView lovelyStyleTextView = findViewById(R.id.idLovelyStyle);
        TextView modernChicStyleTextView = findViewById(R.id.idModernChicStyle);
        TextView formalStyleTextView = findViewById(R.id.idFormalStyle);
        TextView sportyStyleTextView = findViewById(R.id.idSportyStyle);
        TextView simpleBasicStyleTextView = findViewById(R.id.idSimpleBasicStyle);
        TextView romanticStyleTextView = findViewById(R.id.idRomanticStyle);
        TextView comfortableStyleTextView = findViewById(R.id.idComfortableStyle);

        setStyleClickListener(casualStyleTextView, "캐주얼");
        setStyleClickListener(lovelyStyleTextView, "러블리");
        setStyleClickListener(modernChicStyleTextView, "모던시크");
        setStyleClickListener(formalStyleTextView, "포멀/오피스룩");
        setStyleClickListener(sportyStyleTextView, "스포티");
        setStyleClickListener(simpleBasicStyleTextView, "심플베이직");
        setStyleClickListener(romanticStyleTextView, "로맨틱럭셔리");
        setStyleClickListener(comfortableStyleTextView, "꾸안꾸");

        retrofitInterface = RetrofitInterface.Companion.create();

        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClothes();
            }
        });
    }

    private void addClothes() {
        SharedPreferences sharedPreferences = getSharedPreferences("userId", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        String category = ''    //지금 카테고리 셀렉하는 부분을 구현 안해놨다 (추가해야함)
        String link = ''    //위시일때 link받아오기 추가해야함.

        retrofitInterface.postAddClothes(userId, category, selectedStyles, selectedTags, imageUrl, link)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            JsonObject jsonObject = response.body();
                        } else {
                            Toast.makeText(AddClothesActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(AddClothesActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setStyleClickListener(final TextView styleTextView, String styleKeyword) {
        styleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedStyles.contains(styleKeyword)) {
                    selectedStyles.remove(styleKeyword);
                    switch (styleKeyword) {
                        case "모던시크":
                            styleTextView.setBackgroundResource(R.drawable.modern_chic_rectangle);
                            styleTextView.setTypeface(null, Typeface.NORMAL);
                            break;
                        case "포멀/오피스룩":
                            styleTextView.setBackgroundResource(R.drawable.formal_style_rectangle);
                            styleTextView.setTypeface(null, Typeface.NORMAL);
                            break;
                        case "심플베이직":
                            styleTextView.setBackgroundResource(R.drawable.simple_basic_style);
                            styleTextView.setTypeface(null, Typeface.NORMAL);
                            break;
                        case "로맨틱럭셔리":
                            styleTextView.setBackgroundResource(R.drawable.romantic_luxury_style);
                            styleTextView.setTypeface(null, Typeface.NORMAL);
                            break;
                    }
                    styleTextView.setBackgroundResource(R.drawable.styles_rectangle);
                    styleTextView.setTypeface(null, Typeface.NORMAL);
                } else {
                    selectedStyles.add(styleKeyword);
                    switch (styleKeyword) {
                        case "모던시크":
                            styleTextView.setBackgroundResource(R.drawable.modern_chic_rectangle_gray);
                            styleTextView.setTypeface(null, Typeface.BOLD);
                            break;
                        case "포멀/오피스룩":
                            styleTextView.setBackgroundResource(R.drawable.formal_style_rectangle_gray);
                            styleTextView.setTypeface(null, Typeface.BOLD);
                            break;
                        case "심플베이직":
                            styleTextView.setBackgroundResource(R.drawable.simple_basic_style_gray);
                            styleTextView.setTypeface(null, Typeface.BOLD);
                            break;
                        case "로맨틱럭셔리":
                            styleTextView.setBackgroundResource(R.drawable.romantic_luxury_style_gray);
                            styleTextView.setTypeface(null, Typeface.BOLD);
                            break;
                    }
                    styleTextView.setBackgroundResource(R.drawable.styles_rectangle_gray);
                    styleTextView.setTypeface(null, Typeface.BOLD);
                }
            }
        });
    }

    private void handleButtonClick(String tag) {
        resetButtonColors();

        switch (tag) {
            case "heart":
                heartButton.setImageResource(R.drawable.heart_icon);
                heartText.setTextColor(Color.BLACK);
                selectedTags.add("like");
                break;
            case "trash":
                trashButton.setImageResource(R.drawable.trash_icon_black);
                trashText.setTextColor(Color.BLACK);
                selectedTags.add("trash");
                break;
            case "none":
                noneButton.setImageResource(R.drawable.exit_icon);
                noneText.setTextColor(Color.BLACK);
                break;
        }
    }

    private void resetButtonColors() {
        heartButton.setImageResource(R.drawable.heart_icon_gray);
        trashButton.setImageResource(R.drawable.trash_icon_gray);
        noneButton.setImageResource(R.drawable.exit_icon_gray);

        heartText.setTextColor(Color.parseColor("#9B9B9B"));
        trashText.setTextColor(Color.parseColor("#9B9B9B"));
        noneText.setTextColor(Color.parseColor("#9B9B9B"));
    }

    private void navigateBackToPhotosActivity() {
        Intent i = new Intent(this, AddPhotoActivity.class);
        startActivity(i);
        finish();
    }

    private void removeBg(File imageFile) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(loggingInterceptor);

        OkHttpClient client = clientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.remove.bg/v1.0")
                .addConverterFactory(GsonConverterFactory.create()).client(client).build();

        RemoveBGService service = retrofit.create(RemoveBGService.class);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image_file", "image.jpg", RequestBody.create(MediaType.parse("image/*"), imageFile))
                .addFormDataPart("size", "auto")
                .build();

        Call<JsonObject> call = service.removeBackground(API_KEY, requestBody);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject result = response.body().getAsJsonObject("data");
                    String base64Image = result.get("result_b64").getAsString();
                    imageUrl = base64Image;
                    displayProcessedImage(base64Image);
                    Log.e(ContentValues.TAG, "Result: " + imageUrl);
                } else {
                    Log.e(ContentValues.TAG, "HTTP 요청 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(ContentValues.TAG, "네트워크 오류: " + t.getMessage());
            }
        });
    }

    private void displayProcessedImage(String base64Image) {
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        imageView.setImageBitmap(decodedBitmap);
    }

    public interface RemoveBGService {
        @POST("removebg")
        Call<JsonObject> removeBackground(
                @Query("api_key") String apiKey,
                @Body RequestBody body
        );
    }
}