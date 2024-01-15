package com.example.closetfrontend

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ToggleButton
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class EnterProfile : AppCompatActivity() {

    // 서버에서 불러오기
    val api = RetrofitInterface.create()
    // user 정보 불러오기
    private lateinit var userId: String
    private lateinit var userName: String
    private lateinit var userEmail: String
    private lateinit var userProfileImage: String
    private var userGender: String = "Male"


    private var userAge: Int = 0
    private var userHeight: Int = 0
    private var userBodyType: String = ""
    private var userStyles = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_profile)

        // user 정보 불러오기
        val sharedPref = this.getSharedPreferences("userId", Context.MODE_PRIVATE)
        userId = sharedPref.getString("userId", "")!!
        userName = sharedPref.getString("userName", "")!!
        userEmail = sharedPref.getString("userEmail", "")!!
        userProfileImage = sharedPref.getString("userPic", "")!!
        Log.d("EnterProfile", "userId: $userId")
        Log.d("EnterProfile", "userName: $userName")
        Log.d("EnterProfile", "userEmail: $userEmail")

        // 이름 세팅하기
        findViewById<TextView>(R.id.userName).setText(userName)

        // 토글 꺼지면 남 - 켜지면 여
        val gender_toggle = findViewById<ToggleButton>(R.id.gender_toggle)
        gender_toggle.setOnCheckedChangeListener { _, isChecked ->
            userGender = if (isChecked) { "Female" } else { "Male" }
        }

//        // 나이 지정
//        userAge = findViewById<EditText>(R.id.userAge).text.toString().toInt()
//        // 키 지정
//        userHeight = findViewById<EditText>(R.id.userHeight).text.toString().toInt()

        // bodyType 지정
        val natureBtn = findViewById<ImageButton>(R.id.natureBtn)
        val waveBtn = findViewById<ImageButton>(R.id.waveBtn)
        val hourglassBtn = findViewById<ImageButton>(R.id.hourglassBtn)
        val straightBtn = findViewById<ImageButton>(R.id.straightBtn)

        natureBtn.setOnClickListener { userBodyType = "내추럴" }
        waveBtn.setOnClickListener { userBodyType = "웨이브" }
        hourglassBtn.setOnClickListener { userBodyType = "모래시계형" }
        straightBtn.setOnClickListener { userBodyType = "스트레이트" }
        
        // user의 원하는 style 지정
        val casualBtn = findViewById<ImageButton>(R.id.casualBtn)
        val lovelyBtn = findViewById<ImageButton>(R.id.lovelyBtn)
        val chicBtn = findViewById<ImageButton>(R.id.chicBtn)
        val formalBtn = findViewById<ImageButton>(R.id.formalBtn)
        val sportyBtn = findViewById<ImageButton>(R.id.sportyBtn)
        val basicBtn = findViewById<ImageButton>(R.id.basicBtn)
        val romanticBtn = findViewById<ImageButton>(R.id.romanticBtn)
        val naturalBtn = findViewById<ImageButton>(R.id.naturalBtn)
        casualBtn.isEnabled = true
        lovelyBtn.isEnabled = true
        chicBtn.isEnabled = true
        formalBtn.isEnabled = true
        sportyBtn.isEnabled = true
        basicBtn.isEnabled = true
        romanticBtn.isEnabled = true
        naturalBtn.isEnabled = true

        casualBtn.setOnClickListener {
            userStyles.add("캐주얼")
            casualBtn.isEnabled = false
        }
        lovelyBtn.setOnClickListener {
            userStyles.add("러블리")
            lovelyBtn.isEnabled = false
        }
        chicBtn.setOnClickListener {
            userStyles.add("모던시크")
            chicBtn.isEnabled = false
        }
        formalBtn.setOnClickListener {
            userStyles.add("포멀/오피스룩")
            formalBtn.isEnabled = false
        }
        sportyBtn.setOnClickListener {
            userStyles.add("스포티")
            sportyBtn.isEnabled = false
        }
        basicBtn.setOnClickListener {
            userStyles.add("심플베이직")
            basicBtn.isEnabled = false
        }
        romanticBtn.setOnClickListener {
            userStyles.add("로맨틱럭셔리")
            romanticBtn.isEnabled = false
        }
        naturalBtn.setOnClickListener {
            userStyles.add("꾸안꾸")
            naturalBtn.isEnabled = false
        }

        findViewById<ImageButton>(R.id.gotoActivity).setOnClickListener {
            // 밖에서 하니까 ""라서 에러가 뜬다 ㅋㅋ
            // 나이 지정
            userAge = if (findViewById<EditText>(R.id.userAge).text.toString() == "") {
                0
            } else {
                findViewById<EditText>(R.id.userAge).text.toString().toInt()
            }
            // 키 지정
            userHeight = if (findViewById<EditText>(R.id.userHeight).text.toString() == "") {
                0
            } else {
                findViewById<EditText>(R.id.userHeight).text.toString().toInt()
            }

            // DB에 user post 시키기
            val call = api.createUser(
                userId,
                userName,
                userGender,
                userEmail,
                userProfileImage,
                if (userAge == 0) { null } else { userAge },
                if (userHeight == 0) { null } else { userHeight },
//                userAge,
//                userHeight,
                if (userBodyType == "") { null } else { userBodyType },
                if (userStyles.size == 0) {null} else { userStyles })
            call.enqueue(object: Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.e("Lets go", "$result")
                        Log.e("Lets go", "success!! good!!")
                    } else { Log.e("Lets go", "what's wrong...") }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("mad..nn", "so sad plz")
                }
            })

            Log.d("EnterProfile", "userId: $userId")
            Log.d("EnterProfile", "userName: $userName")
            Log.d("EnterProfile", "userGender: $userGender")
            Log.d("EnterProfile", "userEmail: $userEmail")
            Log.d("EnterProfile", "userProfileImage: $userProfileImage")
            Log.d("EnterProfile", "userAge: $userAge")
            Log.d("EnterProfile", "userAge: ${userAge::class}")
            Log.d("EnterProfile", "userHeight: $userHeight")
            Log.d("EnterProfile", "userBodyType: $userBodyType")
            Log.d("EnterProfile", "userStyles: $userStyles")

            val intent = Intent(this, AfterLoginActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
    }
}