package com.example.closetfrontend

import android.content.Context
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
import com.google.gson.JsonElement
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
import okhttp3.RequestBody.Companion.toRequestBody
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
    private lateinit var userId: String
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
    private lateinit var like: String
    private lateinit var trash: String
    private lateinit var wish: String
    //private val selectedTags = ArrayList<String>()
    private val selectedStyles = ArrayList<String>()
    private lateinit var selectedCategoryKeyword: TextView
    private lateinit var topCategoryTextView: TextView
    private lateinit var bottomCategoryTextView: TextView
    private lateinit var outerCategoryTextView: TextView
    private lateinit var onePieceCategoryTextView: TextView
    private lateinit var shoesCategoryTextView: TextView
    private lateinit var bagCategoryTextView: TextView

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

        like = "None"
        trash = "None"
        heartButton.setOnClickListener(View.OnClickListener { handleButtonClick("heart") })
        trashButton.setOnClickListener(View.OnClickListener { handleButtonClick("trash") })
        noneButton.setOnClickListener(View.OnClickListener { handleButtonClick("none") })

        wishToggle = findViewById(R.id.idWishToggle)
        urlEditText = findViewById(R.id.urlEditText)
        wish = "None"
        wishToggle.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                wishToggle.setBackgroundResource(R.drawable.toggle_on)
                wish = "Wish"
                urlEditText.isEnabled = isChecked
            } else {
                wishToggle.setBackgroundResource(R.drawable.toggle_off)
                wish = "None"
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
        topCategoryTextView = findViewById<TextView>(R.id.idTopCategory)
        bottomCategoryTextView = findViewById<TextView>(R.id.idBottomCategory)
        outerCategoryTextView = findViewById<TextView>(R.id.idOuterCategory)
        onePieceCategoryTextView = findViewById<TextView>(R.id.idOnePieceCategory)
        shoesCategoryTextView = findViewById<TextView>(R.id.idShoesCategory)
        bagCategoryTextView = findViewById<TextView>(R.id.idBagCategory)

        setCategoryClickListener(topCategoryTextView, "상의")
        setCategoryClickListener(bottomCategoryTextView, "하의")
        setCategoryClickListener(outerCategoryTextView, "아우터")
        setCategoryClickListener(onePieceCategoryTextView, "원피스")
        setCategoryClickListener(shoesCategoryTextView, "신발")
        setCategoryClickListener(bagCategoryTextView, "가방")

        retrofitInterface = create()
        findViewById<View>(R.id.saveButton).setOnClickListener {
            Log.d("AddClothesActivity", "Selected styles: ${selectedStyles.size}")
            if (selectedStyles.size == 1) {
                Toast.makeText(this@AddClothesActivity,
                    "태그를 두개 이상 선택해주세요",
                    Toast.LENGTH_SHORT).show()
            } else {
                addClothes()
            }
        }
    }

    private fun addClothes() {
        val sharedPreferences = getSharedPreferences("userId", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", "")!!
        val category = selectedCategoryKeyword.text.toString() //지금 카테고리 셀렉하는 부분을 구현 안해놨다 (추가해야함)
        val enteredLink = urlEditText.text.toString() //위시일때 link받아오기 추가해야함.

        Log.d("AddClothesActivity", "UserId: ${userId}")
        Log.d("AddClothesActivity", "Category: $category")
        Log.d("AddClothesActivity", "Selected Styles: $selectedStyles")
        //Log.d("AddClothesActivity", "Selected Tags: $selectedTags")
        Log.d("AddClothesActivity", "ImageUrl: $imageUrl")
        Log.d("AddClothesActivity", "EnteredLink: $enteredLink")

        retrofitInterface.postAddClothes(
            userId,
            category,
            selectedStyles,
            like,
            trash,
            wish,
            imageUrl!!,
            enteredLink
        )
            .enqueue(object : retrofit2.Callback<JsonObject> {
                override fun onResponse(call: retrofit2.Call<JsonObject?>, response: retrofit2.Response<JsonObject?>) {
                    if (response.isSuccessful) {
                        val jsonObject = response.body()
                        Log.d("AddClothesActivity", jsonObject.toString())
                        Log.d(jsonObject.toString(), "clothes added")

                        val intent = Intent(this@AddClothesActivity, AfterLoginActivity::class.java)
                        intent.putExtra("userId", userId)
                        startActivity(intent)
                        finish()
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
                    "상의" -> {
                        selectedCategoryKeyword.setBackgroundResource(R.drawable.short_category_rectangle)
                        selectedCategoryKeyword.setTypeface(null, Typeface.NORMAL)
                    }
                    "하의" -> {
                        selectedCategoryKeyword.setBackgroundResource(R.drawable.short_category_rectangle)
                        selectedCategoryKeyword.setTypeface(null, Typeface.NORMAL)
                    }
                    "신발" -> {
                        selectedCategoryKeyword.setBackgroundResource(R.drawable.short_category_rectangle)
                        selectedCategoryKeyword.setTypeface(null, Typeface.NORMAL)
                    }
                    "가방" -> {
                        selectedCategoryKeyword.setBackgroundResource(R.drawable.short_category_rectangle)
                        selectedCategoryKeyword.setTypeface(null, Typeface.NORMAL)
                    }
                    "아우터" -> {
                        selectedCategoryKeyword.setBackgroundResource(R.drawable.long_catgory_rectangle)
                        selectedCategoryKeyword.setTypeface(null, Typeface.NORMAL)
                    }
                    "원피스" -> {
                        selectedCategoryKeyword.setBackgroundResource(R.drawable.long_catgory_rectangle)
                        selectedCategoryKeyword.setTypeface(null, Typeface.NORMAL)
                    }
                }
            }
            selectedCategoryKeyword = clickedTextView
            when (clickedTextView.text.toString()) {
                "상의" -> {
                    selectedCategoryKeyword.setBackgroundResource(R.drawable.short_category_rectangle_gray)
                    selectedCategoryKeyword.setTypeface(null, Typeface.BOLD)
                }
                "하의" -> {
                    selectedCategoryKeyword.setBackgroundResource(R.drawable.short_category_rectangle_gray)
                    selectedCategoryKeyword.setTypeface(null, Typeface.BOLD)
                }
                "신발" -> {
                    selectedCategoryKeyword.setBackgroundResource(R.drawable.short_category_rectangle_gray)
                    selectedCategoryKeyword.setTypeface(null, Typeface.BOLD)
                }
                "가방" -> {
                    selectedCategoryKeyword.setBackgroundResource(R.drawable.short_category_rectangle_gray)
                    selectedCategoryKeyword.setTypeface(null, Typeface.BOLD)
                }
                "아우터" -> {
                    selectedCategoryKeyword.setBackgroundResource(R.drawable.long_category_rectangle_gray)
                    selectedCategoryKeyword.setTypeface(null, Typeface.BOLD)
                }
                "원피스" -> {
                    selectedCategoryKeyword.setBackgroundResource(R.drawable.long_category_rectangle_gray)
                    selectedCategoryKeyword.setTypeface(null, Typeface.BOLD)
                }
            }
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
                    "캐주얼", "러블리", "스포티", "꾸안꾸" -> {
                        styleTextView.setBackgroundResource(R.drawable.styles_rectangle)
                        styleTextView.setTypeface(null, Typeface.NORMAL)
                    }
                }
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
                    "캐주얼", "러블리", "스포티", "꾸안꾸" -> {
                        styleTextView.setBackgroundResource(R.drawable.styles_rectangle_gray)
                        styleTextView.setTypeface(null, Typeface.BOLD)
                    }
                }
            }
        }
    }

    private fun handleButtonClick(tag: String) {
        resetButtonColors()
        like = "None"
        trash = "None"
        wish = "None"
        when (tag) {
            "heart" -> {
                heartButton.setImageResource(R.drawable.heart_icon)
                heartText.setTextColor(Color.BLACK)
                like = "Like"
            }


            "trash" -> {
                trashButton.setImageResource(R.drawable.trash_icon_black)
                trashText.setTextColor(Color.BLACK)
                trash = "Trash"
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

        val request = Request.Builder().header("x-api-key", "8fa4f1ef03e050deb24ea41db892f951d4866b41a8936e63d42f45beb50df888b51ac1560d0c16bdfe4d50d6dbd1ed67")
            .url("https://clipdrop-api.co/remove-background/v1").post(requestBody).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val byteArray = response.body?.bytes()
                    //Log.d("response", response.body.toString())
                    runOnUiThread {
                        byteArray?.let {
                            displayProcessedImage(it)
                            uploadImageToServer(it)
                        }
                    }
                } else {
                    Log.e("Clipdrop", "Unexpected code ${response.code}")
                }
            }
        })
    }

    private fun uploadImageToServer(byteArray: ByteArray) {
        val requestFile: RequestBody = byteArray.toRequestBody("image/jpeg".toMediaType())
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("image", "image.jpg", requestFile)

        retrofitInterface.uploadImage(body).enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onResponse(
                call: retrofit2.Call<JsonObject>,
                response: retrofit2.Response<JsonObject>
            ) {
                if (response.isSuccessful) {
                    val jsonObject: JsonObject? = response.body()

                    if (jsonObject != null && jsonObject.has("imagePath")) {
                        val imagePath: JsonElement = jsonObject.getAsJsonPrimitive("imagePath")

                        // Check if the "imagePath" is a string
                        if (imagePath.isJsonPrimitive) {
                            imageUrl = imagePath.asString
                            Log.d("Image Upload", "Image URL: $imageUrl")
                        } else {
                            Log.e("Image Upload", "Unexpected 'imagePath' format")
                        }
                    }
                    //imageUrl = response.body().toString()

                    //return imageUrl
                } else {
                    Log.e("Image Upload", "failed")
                }
            }

            override fun onFailure(call: retrofit2.Call<JsonObject>, t: Throwable) {
                Log.e("Image upload", "Unexpected result")
            }
        })
    }

    private fun generateUniqueFileName(): String {
        return "processed_image_${System.currentTimeMillis()}.jpg"
    }

    private fun displayProcessedImage(decodedBytes: ByteArray) {
        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        Log.d("display", decodedBitmap.toString())
        imageView.setImageBitmap(decodedBitmap)
    }

    private fun saveImageAndGetUrl(decodedBytes: ByteArray, fileName: String): String? {
        //val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        //return saveImageToFile(decodedBytes, fileName)
        val encodedString: String = Base64.encodeToString(decodedBytes, Base64.DEFAULT)
        //Log.d("display base 64", "${encodedString}");
        return decodedBytes.toString();
    }

    private fun saveImageToFile(decodedBytes: ByteArray, fileName: String): String? {
        openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(decodedBytes)
        }

        val filePath = getFileStreamPath(fileName).absolutePath

        val server =

        return fileName;
        /*
        file.parentFile?.mkdirs()
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

         */
    }

}