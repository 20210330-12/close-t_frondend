package com.example.closetfrontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class AddPhotoActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    private ArrayList<String> imagePathArrayList;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        selectedImage = findViewById(R.id.RVImages)
    }


}