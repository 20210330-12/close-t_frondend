package com.example.closetfrontend

import android.Manifest.permission
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closetfrontend.AddClothesActivity

class AddPhotoActivity : AppCompatActivity() {
    private lateinit var imagePathArrayList: ArrayList<String>
    private var myAdapter: GalleryAdapter? = null
    private var capturedImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        val cameraHeader = findViewById<RelativeLayout>(R.id.cameraHeader)
        cameraHeader.setOnClickListener {
            openCamera()
            val resultIntent = Intent()
            resultIntent.putExtra("capturedImageUri", capturedImageUri.toString())
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        val imagesRV = findViewById<RecyclerView>(R.id.RVImages)
        imagePathArrayList = ArrayList()
        requestPermissions()

        myAdapter = GalleryAdapter(this, imagePathArrayList!!)
        val manager = GridLayoutManager(this, 3)
        imagesRV.layoutManager = manager
        imagesRV.adapter = myAdapter
        imagesRV.setHasFixedSize(true)

        myAdapter!!.setOnItemClickListener(object : GalleryAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val i = Intent(this@AddPhotoActivity, AddClothesActivity::class.java)
                i.putExtra("imgPath", imagePathArrayList[position])
                startActivity(i)
            }
        })
        refreshGallery()
    }

    private fun openCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    this,
                    permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    this,
                    permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        permission.READ_EXTERNAL_STORAGE,
                        permission.WRITE_EXTERNAL_STORAGE,
                        permission.CAMERA
                    ),
                    TAKE_PICTURE_REQUEST_CODE
                )
            }
        }
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        capturedImageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST_CODE)
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_PICTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Add the new image to the gallery
            if (capturedImageUri != null) {
                refreshGallery()

                // Set result and finish
                val resultIntent = Intent()
                resultIntent.putExtra("capturedImageUri", capturedImageUri.toString())
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Error: Captured image URI is null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestPermissions() {
        val permissionResult =
            ContextCompat.checkSelfPermission(this, permission.READ_EXTERNAL_STORAGE)
        if (permissionResult != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission.READ_EXTERNAL_STORAGE), 200)
        } else {
            refreshGallery()
        }
    }

    private fun refreshGallery() {
        if (imagePathArrayList == null) {
            imagePathArrayList = ArrayList()
        } else {
            imagePathArrayList?.clear()
        }

        getImagePath()
        runOnUiThread {
            myAdapter?.notifyDataSetChanged()
        }
    }

    private fun getImagePath() {
        val galleryPresent = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        if (galleryPresent) {
            val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
            val orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC"
            val cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns,
                null,
                null,
                orderBy
            )
            cursor?.use {
                val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                while (cursor.moveToNext()) {
                    val imagePath = cursor.getString(dataColumnIndex)
                    imagePath?.let {
                        imagePathArrayList?.add(it)
                    }
                }
            }
            /*
            if (cursor != null && cursor.count > 0) {
                val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                while (cursor.moveToNext()) {
                    imagePathArrayList!!.add(cursor.getString(dataColumnIndex))
                }
                myAdapter!!.notifyDataSetChanged()
            }
            cursor?.close()

             */
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImagePath()
            } else {
                Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val TAKE_PICTURE_REQUEST_CODE = 101
    }
}