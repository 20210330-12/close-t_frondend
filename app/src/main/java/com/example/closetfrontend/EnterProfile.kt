package com.example.closetfrontend

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import com.example.closetfrontend.databinding.ActivityChatOotdBinding
import com.example.closetfrontend.databinding.ActivityEnterProfileBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class EnterProfile : AppCompatActivity() {

    // 서버에서 불러오기
    val api = RetrofitInterface.create()
    // binding
    private lateinit var binding: ActivityEnterProfileBinding

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

    // user 정보 나타내는 text나 버튼들
    private lateinit var userNameText: TextView // user 이름
    private lateinit var userGenderToggle: ToggleButton // user 성별
    private lateinit var userAgeEdit: EditText // user 나이
    private lateinit var userHeightEdit: EditText // user 키
    // user 바디타입
    private lateinit var natureBtn: ImageButton
    private lateinit var waveBtn: ImageButton
    private lateinit var hourglassBtn: ImageButton
    private lateinit var straightBtn: ImageButton
    // user 좋아하는 스타일
    private lateinit var casualBtn: ImageButton
    private lateinit var lovelyBtn: ImageButton
    private lateinit var chicBtn: ImageButton
    private lateinit var formalBtn: ImageButton
    private lateinit var sportyBtn: ImageButton
    private lateinit var basicBtn: ImageButton
    private lateinit var romanticBtn: ImageButton
    private lateinit var naturalBtn: ImageButton

    // 체형 버튼 눌린거 표기
    private var isNatureImageToggle = false
    private var isWaveImageToggle = false
    private var isHourglassImageToggle = false
    private var isStraightImageToggle = false
    // 오늘의 룩 스타일 눌린거 표기
    private var isCasualToggle = false
    private var isLovelyToggle = false
    private var isChicToggle = false
    private var isFormalToggle = false
    private var isSportyToggle = false
    private var isBasicToggle = false
    private var isRomanticToggle = false
    private var isNaturalToggle = false

    // save 버튼
    private lateinit var saveButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // user 정보 불러오기
        val sharedPref = this.getSharedPreferences("userId", Context.MODE_PRIVATE)
        userId = sharedPref.getString("userId", "")!!
        userName = sharedPref.getString("userName", "")!!
        userEmail = sharedPref.getString("userEmail", "")!!
        userProfileImage = sharedPref.getString("userPic", "")!!
        Log.d("EnterProfile", "userId: $userId")
        Log.d("EnterProfile", "userName: $userName")
        Log.d("EnterProfile", "userEmail: $userEmail")


        initiation()
        // 이름 세팅하기
        userNameText.text = userName
        // 토글 꺼지면 남 - 켜지면 여
        userGenderToggle.setOnCheckedChangeListener { _, isChecked ->
            userGender = if (isChecked) { "Female" } else { "Male" }
        }
        saveUserProfile()
    }

    private fun initiation() {
        userNameText = binding.userName // user이름 넣는 곳
        userGenderToggle = binding.genderToggle // user 성별 토글
        userAgeEdit = binding.userAge // user 나이
        userHeightEdit = binding.userHeight // user 키

        // 체형 버튼
        natureBtn = binding.natureBtn
        waveBtn = binding.waveBtn
        hourglassBtn = binding.hourglassBtn
        straightBtn = binding.straightBtn
        // 체형 버튼 그림 초기화
        natureBtn.setImageResource(R.drawable.natural)
        waveBtn.setImageResource(R.drawable.wave)
        hourglassBtn.setImageResource(R.drawable.hourglass)
        straightBtn.setImageResource(R.drawable.straight)

        // 오늘의 룩 버튼
        casualBtn = binding.casualBtn
        lovelyBtn = binding.lovelyBtn
        chicBtn = binding.chicBtn
        formalBtn = binding.formalBtn
        sportyBtn = binding.sportyBtn
        basicBtn = binding.basicBtn
        romanticBtn = binding.romanticBtn
        naturalBtn = binding.naturalBtn
        // 오늘의 룩 버튼 그림 초기화
        casualBtn.setImageResource(R.drawable.tag_casual_button)
        lovelyBtn.setImageResource(R.drawable.tag_lovely_button)
        chicBtn.setImageResource(R.drawable.tag_chic_button)
        formalBtn.setImageResource(R.drawable.tag_formal_button)
        sportyBtn.setImageResource(R.drawable.tag_sporty_button)
        basicBtn.setImageResource(R.drawable.tag_basic_button)
        romanticBtn.setImageResource(R.drawable.tag_romantic_button)
        naturalBtn.setImageResource(R.drawable.tag_natural_button)

        // save 버튼
        saveButton = binding.gotoActivity
    }

    private fun getUserBodyType() {
        userBodyType = if (isNatureImageToggle) {
            "내추럴"
        } else if (isWaveImageToggle) {
            "웨이브"
        } else if (isHourglassImageToggle) {
            "모래시계형"
        } else if (isStraightImageToggle) {
            "스트레이트"
        } else {
            ""
        }
    }

    private fun getUserStyles() {
        userStyles.clear() // 이거 안 해주면, save 버튼 한 번 누르고 바꾼 다음에 다시 누르면 취소한 것도 이미 add 되어있음
        if (isCasualToggle) { userStyles.add("캐주얼") }
        if (isLovelyToggle) { userStyles.add("러블리") }
        if (isChicToggle) { userStyles.add("모던시크") }
        if (isFormalToggle) { userStyles.add("포멀/오피스룩") }
        if (isSportyToggle) { userStyles.add("스포티") }
        if (isBasicToggle) { userStyles.add("심플베이직") }
        if (isRomanticToggle) { userStyles.add("로맨틱럭셔리") }
        if (isNaturalToggle) { userStyles.add("꾸안꾸") }
    }

    private fun saveUserProfile() {

        saveButton.setOnClickListener {
            // 밖에서 하니까 ""라서 에러가 뜬다 ㅋㅋ
            // 나이 지정
            userAge = if (userAgeEdit.text.toString().isEmpty()) {
                0
            } else {
                userAgeEdit.text.toString().toInt()
            }
            // 키 지정
            userHeight = if (userHeightEdit.text.toString().isEmpty()) {
                0
            } else {
                userHeightEdit.text.toString().toInt()
            }

            // 여기서 해줘야 최신의 T/F가 저장됨
            getUserBodyType()
            getUserStyles()
            
            // style은 입력할거면 2개 이상 무조건 입력해야함
            if (userStyles.size == 1) {
                Toast.makeText(this, "좋아하는 스타일은 두 개 이상 선택해 주세요", Toast.LENGTH_SHORT).show()
            } else {
                // DB에 user post 시키기
                val call = api.createUser(
                    userId,
                    userName,
                    userGender,
                    userEmail,
                    userProfileImage,
                    if (userAge == 0) {
                        null
                    } else {
                        userAge
                    },
                    if (userHeight == 0) {
                        null
                    } else {
                        userHeight
                    },
                    if (userBodyType == "") {
                        null
                    } else {
                        userBodyType
                    },
                    if (userStyles.size == 0 || userStyles.size == 1) {
                        null
                    } else {
                        userStyles
                    }
                )
                call.enqueue(object : Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            Log.e("Lets go", "$result")
                            Log.e("Lets go", "success!! good!!")
                        } else {
                            Log.e("Lets go", "what's wrong...")
                        }
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



    fun natureImageButton(view: View) {
        val natureButton = view as ImageButton
        // 이미지 토글
        if (isNatureImageToggle) {
            natureButton.setImageResource(R.drawable.natural)
        } else {
            natureButton.setImageResource(R.drawable.natural_selected)
        }

        isNatureImageToggle = !isNatureImageToggle

        // 다른 애들은 모두 선택이 해제된 상태여야함
        isWaveImageToggle = false
        isHourglassImageToggle = false
        isStraightImageToggle = false
        // 그리고 모두 이미지가 다시 돌아와야 하는 상황
        waveBtn.setImageResource(R.drawable.wave)
        hourglassBtn.setImageResource(R.drawable.hourglass)
        straightBtn.setImageResource(R.drawable.straight)
    }

    fun waveImageButton(view: View) {
        val waveButton = view as ImageButton
        // 이미지 토글
        if (isWaveImageToggle) {
            waveButton.setImageResource(R.drawable.wave)
        } else {
            waveButton.setImageResource(R.drawable.wave_selected)
        }

        isWaveImageToggle = !isWaveImageToggle

        // 다른 애들은 모두 선택이 해제된 상태여야함
        isNatureImageToggle = false
        isHourglassImageToggle = false
        isStraightImageToggle = false
        // 그리고 모두 이미지가 다시 돌아와야 하는 상황
        natureBtn.setImageResource(R.drawable.natural)
        hourglassBtn.setImageResource(R.drawable.hourglass)
        straightBtn.setImageResource(R.drawable.straight)
    }

    fun hourglassImageButton(view: View) {
        val hourglassButton = view as ImageButton
        // 이미지 토글
        if (isHourglassImageToggle) {
            hourglassButton.setImageResource(R.drawable.hourglass)
        } else {
            hourglassButton.setImageResource(R.drawable.hourglass_selected)
        }

        isHourglassImageToggle = !isHourglassImageToggle

        // 다른 애들은 모두 선택이 해제된 상태여야함
        isWaveImageToggle = false
        isNatureImageToggle = false
        isStraightImageToggle = false
        // 그리고 모두 이미지가 다시 돌아와야 하는 상황
        waveBtn.setImageResource(R.drawable.wave)
        natureBtn.setImageResource(R.drawable.natural)
        straightBtn.setImageResource(R.drawable.straight)
    }

    fun straightImageButton(view: View) {
        val straightButton = view as ImageButton
        // 이미지 토글
        if (isStraightImageToggle) {
            straightButton.setImageResource(R.drawable.straight)
        } else {
            straightButton.setImageResource(R.drawable.straight_selected)
        }

        isStraightImageToggle = !isStraightImageToggle

        // 다른 애들은 모두 선택이 해제된 상태여야함
        isWaveImageToggle = false
        isHourglassImageToggle = false
        isNatureImageToggle = false
        // 그리고 모두 이미지가 다시 돌아와야 하는 상황
        waveBtn.setImageResource(R.drawable.wave)
        hourglassBtn.setImageResource(R.drawable.hourglass)
        natureBtn.setImageResource(R.drawable.natural)
    }

    fun casualImageButton(view: View) {
        val casualButton = view as ImageButton
        // 이미지 토글
        if (isCasualToggle) {
            casualButton.setImageResource(R.drawable.tag_casual_button)
        } else {
            casualButton.setImageResource(R.drawable.tag_casual_button_selected)
        }

        isCasualToggle = !isCasualToggle
    }

    fun lovelyImageButton(view: View) {
        val lovelyButton = view as ImageButton
        // 이미지 토글
        if (isLovelyToggle) {
            lovelyButton.setImageResource(R.drawable.tag_lovely_button)
        } else {
            lovelyButton.setImageResource(R.drawable.tag_lovely_button_selected)
        }

        isLovelyToggle = !isLovelyToggle
    }

    fun chicImageButton(view: View) {
        val chicButton = view as ImageButton
        // 이미지 토글
        if (isChicToggle) {
            chicButton.setImageResource(R.drawable.tag_chic_button)
        } else {
            chicButton.setImageResource(R.drawable.tag_chic_button_selected)
        }

        isChicToggle = !isChicToggle
    }

    fun formalImageButton(view: View) {
        val formalButton = view as ImageButton
        // 이미지 토글
        if (isFormalToggle) {
            formalButton.setImageResource(R.drawable.tag_formal_button)
        } else {
            formalButton.setImageResource(R.drawable.tag_formal_button_selected)
        }

        isFormalToggle = !isFormalToggle
    }

    fun sportyImageButton(view: View) {
        val sportyButton = view as ImageButton
        // 이미지 토글
        if (isSportyToggle) {
            sportyButton.setImageResource(R.drawable.tag_sporty_button)
        } else {
            sportyButton.setImageResource(R.drawable.tag_sporty_button_selected)
        }

        isSportyToggle = !isSportyToggle
    }

    fun basicImageButton(view: View) {
        val basicButton = view as ImageButton
        // 이미지 토글
        if (isBasicToggle) {
            basicButton.setImageResource(R.drawable.tag_basic_button)
        } else {
            basicButton.setImageResource(R.drawable.tag_basic_button_selected)
        }

        isBasicToggle = !isBasicToggle
    }

    fun romanticImageButton(view: View) {
        val romanticButton = view as ImageButton
        // 이미지 토글
        if (isRomanticToggle) {
            romanticButton.setImageResource(R.drawable.tag_romantic_button)
        } else {
            romanticButton.setImageResource(R.drawable.tag_romantic_button_selected)
        }

        isRomanticToggle = !isRomanticToggle
    }

    fun naturalImageButton(view: View) {
        val naturalButton = view as ImageButton
        // 이미지 토글
        if (isNaturalToggle) {
            naturalButton.setImageResource(R.drawable.tag_natural_button)
        } else {
            naturalButton.setImageResource(R.drawable.tag_natural_button_selected)
        }

        isNaturalToggle = !isNaturalToggle
    }
}