package com.example.closetfrontend

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.closetfrontend.RetrofitInterface.Companion.create
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LookBookDetailViewActivity : AppCompatActivity() {
    private lateinit var heartIcon: ImageView
    private lateinit var commentText: TextView
    private lateinit var firstHashtag: TextView
    private lateinit var secondHashtag: TextView
    private lateinit var thirdHashtag: TextView
    private lateinit var lookbookTop: ImageView
    private lateinit var lookbookBottom: ImageView
    private lateinit var lookbookOuter: ImageView
    private lateinit var lookbookOnepiece: ImageView
    private lateinit var lookbookShoes: ImageView
    private lateinit var lookbookBag: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_look_book_detail_view)
        val codiId = intent.getStringExtra("codiId")
        //맞는지 확인
        heartIcon = findViewById(R.id.idHeartIcon)
        commentText = findViewById(R.id.idCommentText)
        firstHashtag = findViewById(R.id.idFirstHashtag)
        secondHashtag = findViewById(R.id.idSecondHashtag)
        thirdHashtag = findViewById(R.id.idThirdHashtag)

        lookbookTop = findViewById(R.id.lookbookTop)
        lookbookBottom = findViewById(R.id.lookbookBottom)
        lookbookOuter = findViewById(R.id.lookbookOuter)
        lookbookOnepiece = findViewById(R.id.lookbookOnepiece)
        lookbookShoes = findViewById(R.id.lookbookShoes)
        lookbookBag = findViewById(R.id.lookbookBag)

        getSelectedCodi(codiId)
    }

    private fun getSelectedCodi(codiId: String?) {
        val sharedPreferences = getSharedPreferences("userId", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")
        val retrofitInterface = create()
        retrofitInterface.getSelectedCodi(userId!!, codiId!!)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val codiData = response.body()
                        updateUI(codiData)
                    } else {
                        Toast.makeText(
                            this@LookBookDetailViewActivity,
                            "Failed to get codi information",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                    Toast.makeText(
                        this@LookBookDetailViewActivity,
                        "Network error. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun updateUI(codiData: JsonObject?) {
        if (codiData != null) {
            updateHashtags(codiData.getAsJsonArray("styles"))

            val comment: String = codiData.get("comment").asString
            commentText.text = comment

            val likeType: String = codiData.get("like").asString
            updateLikeIcon(likeType)

            updateClothesImages(codiData.getAsJsonArray("clothesImages"))
        }
    }

    private fun updateHashtags(stylesArray: JsonArray) {
        if (stylesArray.size() >= 3) {
            firstHashtag.text = stylesArray[0].asString
            secondHashtag.text = stylesArray[1].asString
            thirdHashtag.text = stylesArray[2].asString
        } else {
            if (stylesArray.size() >= 1) {
                firstHashtag.text = stylesArray[0].asString
                secondHashtag.visibility = View.INVISIBLE
                thirdHashtag.visibility = View.INVISIBLE
            }
            if (stylesArray.size() >= 2) {
                secondHashtag.text = stylesArray[1].asString
                secondHashtag.visibility = View.VISIBLE
                thirdHashtag.visibility = View.INVISIBLE
            }
            if (stylesArray.size() >= 3) {
                thirdHashtag.text = stylesArray[2].asString
                thirdHashtag.visibility = View.VISIBLE
            }
        }
    }

    private fun updateLikeIcon(likeType: String) {
        if ("like" == likeType) {
            heartIcon.setImageResource(R.drawable.heart_filled)
        }
    }

    private fun updateClothesImages(clothesImagesArray: JsonArray) {
        if (clothesImagesArray.size() >= 6) {
            lookbookTop.setImageBitmap(displayProcessedImage(clothesImagesArray[0].asString))
            lookbookBottom.setImageBitmap(displayProcessedImage(clothesImagesArray[1].asString))
            lookbookOuter.setImageBitmap(displayProcessedImage(clothesImagesArray[2].asString))
            lookbookOnepiece.setImageBitmap(displayProcessedImage(clothesImagesArray[3].asString))
            lookbookShoes.setImageBitmap(displayProcessedImage(clothesImagesArray[4].asString))
            lookbookBag.setImageBitmap(displayProcessedImage(clothesImagesArray[5].asString))
        }
    }

    private fun displayProcessedImage(base64Image: String): Bitmap {
        val decodedBytes =
            Base64.decode(base64Image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}