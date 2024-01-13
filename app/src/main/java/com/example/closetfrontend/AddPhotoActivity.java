package com.example.closetfrontend;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AddPhotoActivity extends AppCompatActivity {
    private static final int TAKE_PICTURE_REQUEST_CODE = 101;

    private ArrayList<String> imagePathArrayList;
    private GalleryAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        RelativeLayout cameraHeader = findViewById(R.id.cameraHeader);
        cameraHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        RecyclerView imagesRV = findViewById(R.id.RVImages);
        imagePathArrayList = new ArrayList<>();
        requestPermissions();

        myAdapter = new GalleryAdapter(this, imagePathArrayList);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        imagesRV.setLayoutManager(manager);
        imagesRV.setAdapter(myAdapter);
        imagesRV.setHasFixedSize(false);

        myAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent i = new Intent(AddPhotoActivity.this, AddClothesActivity.class);
                i.putExtra("imgPath", imagePathArrayList);
                startActivity(i);
            }
        });
    }

    private void requestPermissions() {
        int permissionResult = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        if (permissionResult != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, 200);
        } else {
            refreshGallery();
        }
    }

    private void refreshGallery() {
        imagePathArrayList.clear();
        getImagePath();
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }
    }

    public void getImagePath() {
        boolean galleryPresent = android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

        if (galleryPresent) {
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC";

            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);

            if (cursor != null && cursor.getCount() > 0) {
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                while (cursor.moveToNext()) {
                    imagePathArrayList.add(cursor.getString(dataColumnIndex));
                }

                myAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImagePath();
            }
            else {
                Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}