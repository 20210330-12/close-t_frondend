package com.example.closetfrontend

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.example.closetfrontend.RetrofitInterface.Companion.create
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.Call
import okhttp3.Callback
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class AddClothesActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var heartButton: ImageView
    private lateinit var trashButton: ImageView
    private lateinit var noneButton: ImageView
    private lateinit var heartText: TextView
    private lateinit var trashText: TextView
    private lateinit var noneText: TextView
    private lateinit var wishToggle: ToggleButton
    private lateinit var urlEditText: EditText
    private var imageUrl: String? = null
    private val selectedTags = ArrayList<String>()
    private val selectedStyles = ArrayList<String>()
    private lateinit var selectedCategoryKeyword: TextView
    private lateinit var retrofitInterface: RetrofitInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_clothes)

        val imagePath = intent.getStringExtra("imgPath")
        if (imagePath != null) {
            val imageFile = File(imagePath)

            if (imageFile.exists()) {
                clipdropBg(imageFile)
            } else {
                Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Image path is null", Toast.LENGTH_SHORT).show()
        }
        imageView = findViewById(R.id.idIVImage)


        val backIcon = findViewById<ImageView>(R.id.backIcon)
        backIcon.setOnClickListener { navigateBackToPhotosActivity() }

        heartButton = findViewById(R.id.heartIcon)
        trashButton = findViewById(R.id.trashIcon)
        noneButton = findViewById(R.id.noneButton)
        heartText = findViewById(R.id.heartText)
        trashText = findViewById(R.id.trashText)
        noneText = findViewById(R.id.noneText)

        heartButton.setOnClickListener(View.OnClickListener { handleButtonClick("heart") })
        trashButton.setOnClickListener(View.OnClickListener { handleButtonClick("trash") })
        noneButton.setOnClickListener(View.OnClickListener { handleButtonClick("none") })

        wishToggle = findViewById(R.id.idWishToggle)
        urlEditText = findViewById(R.id.urlEditText)
        wishToggle.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                wishToggle.setBackgroundResource(R.drawable.toggle_on)
                selectedTags.add("wish")
                urlEditText.isEnabled = isChecked
            } else {
                wishToggle.setBackgroundResource(R.drawable.toggle_off)
            }
        })

        val casualStyleTextView = findViewById<TextView>(R.id.idCasualStyle)
        val lovelyStyleTextView = findViewById<TextView>(R.id.idLovelyStyle)
        val modernChicStyleTextView = findViewById<TextView>(R.id.idModernChicStyle)
        val formalStyleTextView = findViewById<TextView>(R.id.idFormalStyle)
        val sportyStyleTextView = findViewById<TextView>(R.id.idSportyStyle)
        val simpleBasicStyleTextView = findViewById<TextView>(R.id.idSimpleBasicStyle)
        val romanticStyleTextView = findViewById<TextView>(R.id.idRomanticStyle)
        val comfortableStyleTextView = findViewById<TextView>(R.id.idComfortableStyle)

        setStyleClickListener(casualStyleTextView, "캐주얼")
        setStyleClickListener(lovelyStyleTextView, "러블리")
        setStyleClickListener(modernChicStyleTextView, "모던시크")
        setStyleClickListener(formalStyleTextView, "포멀/오피스룩")
        setStyleClickListener(sportyStyleTextView, "스포티")
        setStyleClickListener(simpleBasicStyleTextView, "심플베이직")
        setStyleClickListener(romanticStyleTextView, "로맨틱럭셔리")
        setStyleClickListener(comfortableStyleTextView, "꾸안꾸")

        selectedCategoryKeyword = findViewById(R.id.idTopCategory)
        val topCategoryTextView = findViewById<TextView>(R.id.idTopCategory)
        val bottomCategoryTextView = findViewById<TextView>(R.id.idBottomCategory)
        val outerCategoryTextView = findViewById<TextView>(R.id.idOuterCategory)
        val onePieceCategoryTextView = findViewById<TextView>(R.id.idOnePieceCategory)
        val shoesCategoryTextView = findViewById<TextView>(R.id.idShoesCategory)
        val bagCategoryTextView = findViewById<TextView>(R.id.idBagCategory)

        setCategoryClickListener(topCategoryTextView, "상의")
        setCategoryClickListener(bottomCategoryTextView, "하의")
        setCategoryClickListener(outerCategoryTextView, "아우터")
        setCategoryClickListener(onePieceCategoryTextView, "원피스")
        setCategoryClickListener(shoesCategoryTextView, "신발")
        setCategoryClickListener(bagCategoryTextView, "가방")

        retrofitInterface = create()
        findViewById<View>(R.id.saveButton).setOnClickListener { addClothes() }
    }

    private fun addClothes() {
        val sharedPreferences = getSharedPreferences("userId", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")
        val category = selectedCategoryKeyword.text.toString() //지금 카테고리 셀렉하는 부분을 구현 안해놨다 (추가해야함)
        val enteredLink = urlEditText.text.toString() //위시일때 link받아오기 추가해야함.

        retrofitInterface.postAddClothes(
            userId!!,
            category,
            selectedStyles,
            selectedTags,
            imageUrl!!,
            enteredLink
        )
            .enqueue(object : retrofit2.Callback<JsonObject> {
                override fun onResponse(call: retrofit2.Call<JsonObject?>, response: retrofit2.Response<JsonObject?>) {
                    if (response.isSuccessful) {
                        val jsonObject = response.body()
                        Log.d(jsonObject.toString(), "clothes added")
                    } else {
                        Toast.makeText(
                            this@AddClothesActivity,
                            "Error: " + response.code(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<JsonObject?>, t: Throwable) {
                    Toast.makeText(
                        this@AddClothesActivity,
                        "Network error: " + t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun setCategoryClickListener(clickedTextView: TextView, keyword: String) {
        clickedTextView.setOnClickListener {
            if (selectedCategoryKeyword != null) {
                when (selectedCategoryKeyword.text.toString()) {
                    "상의", "하의", "신발", "가방" -> {
                        selectedCategoryKeyword.setBackgroundResource(R.drawable.short_category_rectangle)
                        selectedCategoryKeyword.setTypeface(null, Typeface.NORMAL)
                    }
                    "아우터", "원피스" -> {
                        selectedCategoryKeyword.setBackgroundResource(R.drawable.long_catgory_rectangle)
                        selectedCategoryKeyword.setTypeface(null, Typeface.NORMAL)
                    }
                }
            }
            when (clickedTextView.text.toString()) {
                "상의", "하의", "신발", "가방" -> {
                    selectedCategoryKeyword.setBackgroundResource(R.drawable.short_category_rectangle_gray)
                    selectedCategoryKeyword.setTypeface(null, Typeface.BOLD)
                }
                "아우터", "원피스" -> {
                    selectedCategoryKeyword.setBackgroundResource(R.drawable.long_category_rectangle_gray)
                    selectedCategoryKeyword.setTypeface(null, Typeface.BOLD)
                }
            }
            selectedCategoryKeyword = clickedTextView
        }
    }

    private fun setStyleClickListener(styleTextView: TextView, styleKeyword: String) {
        styleTextView.setOnClickListener {
            if (selectedStyles.contains(styleKeyword)) {
                selectedStyles.remove(styleKeyword)
                when (styleKeyword) {
                    "모던시크" -> {
                        styleTextView.setBackgroundResource(R.drawable.modern_chic_rectangle)
                        styleTextView.setTypeface(null, Typeface.NORMAL)
                    }
                    "포멀/오피스룩" -> {
                        styleTextView.setBackgroundResource(R.drawable.formal_style_rectangle)
                        styleTextView.setTypeface(null, Typeface.NORMAL)
                    }
                    "심플베이직" -> {
                        styleTextView.setBackgroundResource(R.drawable.simple_basic_style)
                        styleTextView.setTypeface(null, Typeface.NORMAL)
                    }
                    "로맨틱럭셔리" -> {
                        styleTextView.setBackgroundResource(R.drawable.romantic_luxury_style)
                        styleTextView.setTypeface(null, Typeface.NORMAL)
                    }
                }
                styleTextView.setBackgroundResource(R.drawable.styles_rectangle)
                styleTextView.setTypeface(null, Typeface.NORMAL)
            } else {
                selectedStyles.add(styleKeyword)
                when (styleKeyword) {
                    "모던시크" -> {
                        styleTextView.setBackgroundResource(R.drawable.modern_chic_rectangle_gray)
                        styleTextView.setTypeface(null, Typeface.BOLD)
                    }
                    "포멀/오피스룩" -> {
                        styleTextView.setBackgroundResource(R.drawable.formal_style_rectangle_gray)
                        styleTextView.setTypeface(null, Typeface.BOLD)
                    }
                    "심플베이직" -> {
                        styleTextView.setBackgroundResource(R.drawable.simple_basic_style_gray)
                        styleTextView.setTypeface(null, Typeface.BOLD)
                    }
                    "로맨틱럭셔리" -> {
                        styleTextView.setBackgroundResource(R.drawable.romantic_luxury_style_gray)
                        styleTextView.setTypeface(null, Typeface.BOLD)
                    }
                }
                styleTextView.setBackgroundResource(R.drawable.styles_rectangle_gray)
                styleTextView.setTypeface(null, Typeface.BOLD)
            }
        }
    }

    private fun handleButtonClick(tag: String) {
        resetButtonColors()
        when (tag) {
            "heart" -> {
                heartButton.setImageResource(R.drawable.heart_icon)
                heartText.setTextColor(Color.BLACK)
                selectedTags.add("like")
            }

            "trash" -> {
                trashButton.setImageResource(R.drawable.trash_icon_black)
                trashText.setTextColor(Color.BLACK)
                selectedTags.add("trash")
            }

            "none" -> {
                noneButton.setImageResource(R.drawable.exit_icon)
                noneText.setTextColor(Color.BLACK)
            }
        }
    }

    private fun resetButtonColors() {
        heartButton.setImageResource(R.drawable.heart_icon_gray)
        trashButton.setImageResource(R.drawable.trash_icon_gray)
        noneButton.setImageResource(R.drawable.exit_icon_gray)
        heartText.setTextColor(Color.parseColor("#9B9B9B"))
        trashText.setTextColor(Color.parseColor("#9B9B9B"))
        noneText.setTextColor(Color.parseColor("#9B9B9B"))
    }

    private fun navigateBackToPhotosActivity() {
        val i = Intent(this, AddPhotoActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun clipdropBg(imageFile: File) {
        val client = OkHttpClient()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image_file", "image.jpg", imageFile.asRequestBody("image/jpeg".toMediaType()))
            .build()

        val request = Request.Builder().header("x-api-key", "97838e421cdfc4d7fd8f6f13065711b89a24bafbce5b0ccde618266696155d0b2598e9866be5433a8e8b4e6137a0a4cf")
            .url("https://clipdrop-api.co/remove-background/v1").post(requestBody).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val byteArray = response.body?.bytes()

                    runOnUiThread {
                        byteArray?.let {
                            displayProcessedImage(it)
                            imageUrl = saveImageAndGetUrl(it)
                        }
                    }
                } else {
                    Log.e("Clipdrop", "Unexpected code ${response.code}")
                }
            }
        })

        /*
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            else {
                val byteArray = response.body?.bytes()
            }
        }

         */
    }
    private fun removeBg(imageFile: File) {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image_file", "image.jpg", imageFile.asRequestBody("image/jpeg".toMediaType()))
            .addFormDataPart("size", "auto")
            .build()

        val request = Request.Builder()
            .url("https://api.remove.bg/v1.0/removebg")
            .header("X-API-Key", "")
            .post(requestBody)
            .build()

        /*
        val client = OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

         */

        val client = OkHttpClient()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            else {
                val byteResultImage = response.body?.bytes()

            }
        }

        /*
        retrofitInterface.removeBackground(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val contentType = response.headers.get("Content-Type")
                    if (contentType != null && contentType.startsWith("application/json")) {
                        val responseData = response.body?.string()
                        try {
                            val resultData = GsonBuilder().setLenient().create().fromJson(responseData, JsonObject::class.java)
                        } catch (e: JsonSyntaxException) {
                            Log.e(ContentValues.TAG, "Error parsing JSON: $responseData")
                        }
                    } else if (contentType != null && contentType.startsWith("image/")) {
                        val imageBytes = response.body?.bytes()
                        if (imageBytes != null) {
                            displayProcessedImage(imageBytes)
                            imageUrl = saveImageAndGetUrl(imageBytes)
                        } else {
                            Log.e(ContentValues.TAG, "Image bytes are null")
                        }
                    } else {
                        Log.e(ContentValues.TAG, "Unknown content type: $contentType")
                    }
                    /*
                    val responseData = response.body()?.string()
                    try {
                        val resultData = GsonBuilder().setLenient().create().fromJson(responseData, JsonObject::class.java)
                        if (resultData != null && resultData.has("data")) {
                            val resultObj = resultData.getAsJsonObject("data")
                            if (resultObj != null && resultObj.has("result_b64")) {
                                val base64Image = resultObj.getAsJsonPrimitive("result_b64").asString
                                displayProcessedImage(Base64.decode(base64Image, Base64.DEFAULT))
                                imageUrl = saveImageAndGetUrl(Base64.decode(base64Image, Base64.DEFAULT))
                                Log.e(ContentValues.TAG, "result: $resultObj")
                            } else {
                                Log.e(ContentValues.TAG, "Response does not have result_b64")
                            }
                        } else {
                            Log.e(ContentValues.TAG, "Response does not have data")
                        }
                    } catch (e: JsonSyntaxException) {
                        Log.e(ContentValues.TAG, "Error parsing JSON: $responseData")
                    }

                     */
                } else {
                    Log.e(ContentValues.TAG, "HTTP request failed with code: ${response.code}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e(ContentValues.TAG, "RemoveBG HTTP request failed: ${e.message}")
            }
        })

         */

    }

    private fun displayProcessedImage(decodedBytes: ByteArray) {
        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        imageView.setImageBitmap(decodedBitmap)
        /*
        if (inputStream != null) {
            val decodedBitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(decodedBitmap)
        } else {
            Log.e(ContentValues.TAG, "InputStream is null")
        }

         */
        //val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
        //val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    private fun saveImageAndGetUrl(decodedBytes: ByteArray): String? {
        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        return saveImageToFile(decodedBitmap)
        /*
        if (inputStream != null) {
            val decodedBitmap = BitmapFactory.decodeStream(inputStream)
            return saveImageToFile(decodedBitmap)
        } else {
            Log.e(ContentValues.TAG, "Input stream is null")
            return null
        }

         */
    }

    private fun saveImageToFile(bitmap: Bitmap): String? {
        val file = File(filesDir, "processed_image.jpg")
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    /*
    interface RemoveBGService {
        @POST("removebg")
        fun removeBackground(
            @Query("api_key") apiKey: String,
            @Body body: RequestBody
        ): Call
    }

    companion object {
        private const val API_KEY = "smSA3YYqq9zN9XymdrriWFyE"
    }

     */
}