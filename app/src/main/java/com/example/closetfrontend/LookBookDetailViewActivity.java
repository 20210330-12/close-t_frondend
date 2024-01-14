package com.example.closetfrontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LookBookDetailViewActivity extends AppCompatActivity {
    private ImageView heartIcon;
    private TextView commentText, firstHashtag, secondHashtag, thirdHashtag;
    private ImageView lookbookTop, lookbookBottom, lookbookOuter, lookbookOnepiece, lookbookShoes, lookbookBag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_book_detail_view);

        String codiId = getIntent().getStringExtra("codiId");
        //맞는지 확인
        heartIcon = findViewById(R.id.idHeartIcon);
        commentText = findViewById(R.id.idCommentText);
        firstHashtag = findViewById(R.id.idFirstHashtag);
        secondHashtag = findViewById(R.id.idSecondHashtag);
        thirdHashtag = findViewById(R.id.idThirdHashtag);

        lookbookTop = findViewById(R.id.lookbookTop);
        lookbookBottom = findViewById(R.id.lookbookBottom);
        lookbookOuter = findViewById(R.id.lookbookOuter);
        lookbookOnepiece = findViewById(R.id.lookbookOnepiece);
        lookbookShoes = findViewById(R.id.lookbookShoes);
        lookbookBag = findViewById(R.id.lookbookBag);

        getSelectedCodi(codiId);
    }

    private void getSelectedCodi(String codiId) {
        SharedPreferences sharedPreferences = getSharedPreferences("userId", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");

        RetrofitInterface retrofitInterface = RetrofitInterface.Companion.create();
        retrofitInterface.getSelectedCodi(userId, codiId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject codiData = response.body();
                    updateUI(codiData);
                } else {
                    Toast.makeText(LookBookDetailViewActivity.this, "Failed to get codi information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(LookBookDetailViewActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(JsonObject codiData) {
        if (codiData != null) {
            updateHashtags(codiData.getAsJsonArray("styles"));

            String comment = codiData.get("comment").getAsString();
            commentText.setText(comment != null ? comment : "");

            String likeType = codiData.get("like").getAsString();
            updateLikeIcon(likeType);

            updateClothesImages(codiData.getAsJsonArray("clothesImages"));
        }
    }

    private void updateHashtags(JsonArray stylesArray) {
        if (stylesArray.size() >= 3) {
            firstHashtag.setText(stylesArray.get(0).getAsString());
            secondHashtag.setText(stylesArray.get(1).getAsString());
            thirdHashtag.setText(stylesArray.get(2).getAsString());
        } else {
            if (stylesArray.size() >= 1) {
                firstHashtag.setText(stylesArray.get(0).getAsString());
                secondHashtag.setVisibility(View.INVISIBLE);
                thirdHashtag.setVisibility(View.INVISIBLE);
            }
            if (stylesArray.size() >= 2) {
                secondHashtag.setText(stylesArray.get(1).getAsString());
                secondHashtag.setVisibility(View.VISIBLE);
                thirdHashtag.setVisibility(View.INVISIBLE);
            }
            if (stylesArray.size() >= 3) {
                thirdHashtag.setText(stylesArray.get(2).getAsString());
                thirdHashtag.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateLikeIcon(String likeType) {
        if ("like".equals(likeType)) {
            heartIcon.setImageResource(R.drawable.heart_filled);
        }
    }

    private void updateClothesImages(JsonArray clothesImagesArray) {
        if (clothesImagesArray.size() >= 6) {
            lookbookTop.setImageBitmap(displayProcessedImage(clothesImagesArray.get(0).getAsString()));
            lookbookBottom.setImageBitmap(displayProcessedImage(clothesImagesArray.get(1).getAsString()));
            lookbookOuter.setImageBitmap(displayProcessedImage(clothesImagesArray.get(2).getAsString()));
            lookbookOnepiece.setImageBitmap(displayProcessedImage(clothesImagesArray.get(3).getAsString()));
            lookbookShoes.setImageBitmap(displayProcessedImage(clothesImagesArray.get(4).getAsString()));
            lookbookBag.setImageBitmap(displayProcessedImage(clothesImagesArray.get(5).getAsString()));
        }
    }

    private Bitmap displayProcessedImage(String base64Image) {
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

        return decodedBitmap;
    }
}