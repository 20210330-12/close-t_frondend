package com.example.closetfrontend

import android.content.ContentValues
import android.content.Intent
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
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.File

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
        imageView = findViewById(R.id.idIVImage)
        val imageFile = File(imagePath)

        if (imageFile.exists()) {
            removeBg(imageFile)
        } else {
            Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show()
        }

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
                urlEditText.setEnabled(isChecked)
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
            .enqueue(object : Callback<JsonObject?> {
                override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
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

                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
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

    private fun removeBg(imageFile: File) {
        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        clientBuilder.addInterceptor(loggingInterceptor)

        val client: OkHttpClient = clientBuilder.build()

        val retrofit = Retrofit.Builder().baseUrl("https://api.remove.bg/v1.0")
            .addConverterFactory(GsonConverterFactory.create()).client(client).build()

        val service = retrofit.create(RemoveBGService::class.java)

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image_file", "image.jpg", RequestBody.create("image/*".toMediaType(), imageFile))
            .addFormDataPart("size", "auto")
            .build()

        val call = service.removeBackground(API_KEY, requestBody)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val result = response.body()!!.getAsJsonObject("data")
                    val base64Image = result["result_b64"].asString
                    imageUrl = base64Image
                    displayProcessedImage(base64Image)
                    Log.e(ContentValues.TAG, "Result: $imageUrl")
                } else {
                    Log.e(ContentValues.TAG, "HTTP 요청 실패: " + response.code())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e(ContentValues.TAG, "네트워크 오류: " + t.message)
            }
        })
    }

    private fun displayProcessedImage(base64Image: String) {
        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        imageView.setImageBitmap(decodedBitmap)
    }

    interface RemoveBGService {
        @POST("removebg")
        fun removeBackground(
            @Query("api_key") apiKey: String,
            @Body body: RequestBody
        ): Call<JsonObject>
    }

    companion object {
        private const val API_KEY = "smSA3YYqq9zN9XymdrriWFyE"
    }
}