package com.example.closetfrontend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LookBookFragment extends Fragment {
    private RecyclerView recyclerView;
    private LookBookAdapter lookBookAdapter;
    private List<String> codiIds;
    private List<String> likes;
    private List<List<String>> clothesImageUrls;

    public LookBookFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_look_book, container, false);

        recyclerView = view.findViewById(R.id.idLookBooks);
        codiIds = new ArrayList<>();
        likes = new ArrayList<>();
        clothesImageUrls = new ArrayList<>();
        lookBookAdapter = new LookBookAdapter(requireContext(), codiIds, likes, clothesImageUrls, new LookBookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                onCodiItemClick(position);
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        recyclerView.setAdapter(lookBookAdapter);

        getAllCodies();

        return view;
    }

    private void getAllCodies() {
        //getContext() 맞는지 확인
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("userId", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");

        RetrofitInterface retrofitInterface = RetrofitInterface.Companion.create();
        retrofitInterface.getAllCodies(userId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    parseResponse(response.body());
                } else {
                    Toast.makeText(requireContext(), "Failed to get codi information", Toast.LENGTH_SHORT).show();                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseResponse(JsonObject response) {
        JsonArray codiIdsArray = response.getAsJsonArray("codiIds");
        JsonArray likesArray = response.getAsJsonArray("likes");
        JsonArray clothesImageUrlsArray = response.getAsJsonArray("clothesImageUrls");

        for (int i = 0; i < codiIdsArray.size(); i++) {
            codiIds.add(codiIdsArray.get(i).getAsString());
            likes.add(likesArray.get(i).getAsString());

            List<String> clothesImages = new ArrayList<>();
            JsonArray clothesImagesArray = clothesImageUrlsArray.get(i).getAsJsonArray();
            for (int j = 0; j < clothesImagesArray.size(); j++) {
                clothesImages.add(clothesImagesArray.get(j).getAsString());
            }
            clothesImageUrls.add(clothesImages);
        }

        lookBookAdapter.notifyDataSetChanged();
    }

    private void onCodiItemClick(int position) {
        String selectedCodiId = codiIds.get(position);

        Intent intent = new Intent(requireContext(), LookBookDetailViewActivity.class);
        intent.putExtra("codiId", selectedCodiId);
        startActivity(intent);
    }

}
