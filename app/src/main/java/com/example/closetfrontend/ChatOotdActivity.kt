package com.example.closetfrontend

import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.closetfrontend.databinding.ActivityChatOotdBinding
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatOotdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatOotdBinding // binding
    private lateinit var haveToEditBtn: ImageButton // 선택 입력 사항 버튼
    private lateinit var parentLayout: ConstraintLayout // 원래 있는 레이아웃
    private lateinit var childLayout: LinearLayout // 버튼 누르면 생길 레이아웃
    private lateinit var backBtn: ImageButton // back button
    private lateinit var saveBtn: ImageButton // user update save button
    private lateinit var goDalleBtn: Button // dalle 실행시키는 버튼

    // 서버에서 불러오기
    private val api = RetrofitInterface.create()
    private lateinit var userId: String // userId

    // 프로필 정보 나타낼 것들
    private lateinit var profileImageText: String
    private lateinit var profileImage: ImageView
    private lateinit var profileNameText: String
    private lateinit var profileName: TextView
    private lateinit var profileGenderText: String
    private lateinit var profileGender: ImageView
    private lateinit var profileEmailText: String
    private lateinit var profileEmail: TextView

    private lateinit var profileAgeText: String
    private lateinit var profileHeightText: String
    private lateinit var profileBodyTypeText: String

    // user UPDATE 할 때 사용할 것들
    private var userAge: Int = 0
    private var userHeight: Int = 0
    private var userBodyType: String = ""
    private lateinit var userAgeEdit: EditText
    private lateinit var userHeightEdit: EditText

    // dalle 보낼 때 사용할 것들
    private var stylePick: String = ""

    // 체형 버튼 눌린거 표기
    private var isNatureImageToggle = false
    private var isWaveImageToggle = false
    private var isHourglassImageToggle = false
    private var isStraightImageToggle = false

    // 체형 버튼
    private lateinit var natureBtn: ImageButton
    private lateinit var waveBtn: ImageButton
    private lateinit var hourglassBtn: ImageButton
    private lateinit var straightBtn: ImageButton

    // 오늘의 룩 스타일 눌린거 표기
    private var isCasualToggle = false
    private var isLovelyToggle = false
    private var isChicToggle = false
    private var isFormalToggle = false
    private var isSportyToggle = false
    private var isBasicToggle = false
    private var isRomanticToggle = false
    private var isNaturalToggle = false

    // 오늘의 룩 스타일 버튼
    private lateinit var casualBtn: ImageButton
    private lateinit var lovelyBtn: ImageButton
    private lateinit var chicBtn: ImageButton
    private lateinit var formalBtn: ImageButton
    private lateinit var sportyBtn: ImageButton
    private lateinit var basicBtn: ImageButton
    private lateinit var romanticBtn: ImageButton
    private lateinit var naturalBtn: ImageButton

    // dalle에게 받은 이미지 url
    private var dalleUrl = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatOotdBinding.inflate(layoutInflater)
//        setContentView(R.layout.activity_chat_ootd)
        setContentView(binding.root)

        // 여기가 sharedPreference로 userId 받는 부분
        val sharedPref = this.getSharedPreferences("userId", Context.MODE_PRIVATE)
        userId = sharedPref.getString("userId", "")!!
        Log.e("MyClosetFragment", "userId: $userId")


        initiation() // init 해주기
        expandParent() // 선택 입력 사항 접혔다가 펼치는 함수
        goBack() // back 버튼 누르면 돌아가기
        getUser() // user 정보 불러오기
        Handler(Looper.getMainLooper()).postDelayed({
            // user 정보 불러오는데 시간 걸릴 것 같아서 이렇게 해줬다
            setProfile() // 프로필 꾸며주기
        }, 1000)
        updateUser() // save 버튼 눌러서 user update
        goDalle() // dalle 버튼 누를 때
        // 그리고 save 누를 때는 isImageToggle = true인 애로 넣으면됨
    }

    private fun initiation() {
        // 버튼 및 접혔다가 펼쳐지는 layout들 초기화
        haveToEditBtn = binding.parentBtn
        parentLayout = binding.parentLayout
        childLayout = binding.expandLayout
        backBtn = binding.backBtn
        saveBtn = binding.gotoActivity
        goDalleBtn = binding.goDalleBtn

        // View들 초기화
        profileImage = binding.profilePic
        profileName = binding.profileName
        profileGender = binding.profileGender
        profileEmail = binding.profileEmail

        // 나이, 키 editText
        userAgeEdit = binding.userAge
        userHeightEdit = binding.userHeight

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

    }

    private fun expandParent() {
        haveToEditBtn.setOnClickListener {
            if (childLayout.visibility == View.VISIBLE) {
                childLayout.visibility = View.GONE
                haveToEditBtn.animate().setDuration(200).rotation(0f)
            } else {
                childLayout.visibility = View.VISIBLE
                haveToEditBtn.animate().setDuration(200).rotation(180f)
            }
        }
    }

    private fun goBack() {
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun getUser() {
        api.getUser(userId).enqueue(object: Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.e(ContentValues.TAG, "result: $result")
                    profileNameText = result?.get("name")?.asString!!
                    profileGenderText = result?.get("gender")?.asString!!
                    profileEmailText = result?.get("email")?.asString!!
                    profileImageText = result?.get("profileImage")?.asString!!
                    profileAgeText = if (result?.get("age") == null) { ""
                    } else { result?.get("age")?.asString!! }
                    profileHeightText = if (result?.get("height") == null) { ""
                    } else { result?.get("height")?.asString!! }
                    profileBodyTypeText = if (result?.get("bodyType") == null) { ""
                    } else { result?.get("bodyType")?.asString!! }
                } else {
                    // HTTP 요청이 실패한 경우의 처리
                    Log.e(ContentValues.TAG, "HTTP 요청 실패: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e(ContentValues.TAG, "네트워크 오류: ${t.message}")
            }
        })

        // 일단 dummy data로
        profileNameText = "송한이"
        profileGenderText = "Female"
        profileEmailText = "hanis@kaist.ac.kr"
        profileImageText = "https://k.kakaocdn.net/dn/iiHzE/btsCnFefcFe/csRhbfOvNWQKsumvxRXkA1/img_640x640.jpg"
        profileAgeText = "22"
        profileHeightText = "165"
        profileBodyTypeText = "모래시계형"
        
    }

    private fun setProfile() {
        // 이미지 세팅
        Picasso.get().load(profileImageText)
            .placeholder(R.drawable.full_heart)
            .error(R.drawable.empty_heart) // 에러 발생 시 로딩될 이미지
            .into(profileImage)

        // 이름 세팅
        profileName.text = profileNameText

        // 성별 세팅
        if (profileGenderText == "Male") {
            profileGender.setImageResource(R.drawable.mars)
        } else if (profileGenderText == "Female") {
            profileGender.setImageResource(R.drawable.femenine)
        }

        // 이메일 세팅
        profileEmail.text = profileEmailText

        // 선택 입력 사항 세팅
        if (!profileAgeText.isEmpty()) {
            // 빈 문자열이 아니면, editText 세팅해주기
            userAgeEdit.setText(profileAgeText)
        }
        if (!profileHeightText.isEmpty()) {
            // 빈 문자열이 아니면, editText 세팅해주기
            userHeightEdit.setText(profileHeightText)
        }

        // 체형 버튼 세팅
        when (profileBodyTypeText) {
            "내추럴" -> {
                natureBtn.setImageResource(R.drawable.natural_selected)
                isNatureImageToggle = true
            }
            "웨이브" -> {
                waveBtn.setImageResource(R.drawable.wave_selected)
                isWaveImageToggle = true
            }
            "모래시계형" -> {
                hourglassBtn.setImageResource(R.drawable.hourglass_selected)
                isHourglassImageToggle = true
            }
            "스트레이트" -> {
                straightBtn.setImageResource(R.drawable.straight_selected)
                isStraightImageToggle = true
            }
        }
    }

    private fun updateUser() {
        saveBtn.setOnClickListener {
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

            // DB에 user post 시키기
            val call = api.addUserInfo(
                userId,
                UserProfileUpdate( if (userAge == 0) { null } else { userAge },
                if (userHeight == 0) { null } else { userHeight },
                if (userBodyType == "") { null } else { userBodyType })
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
            
        }
    }

    private fun goDalle() {
        goDalleBtn.setOnClickListener {
            getTodayStyle()

            // DB에 user post 시키기
            val dalleCall = api.dalle(
                userId,
                if ( stylePick == "") { null } else { stylePick }
            )
            dalleCall.enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
//                        dalleUrl = response.data.data[0].url.toString()
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

        }
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

    private fun getTodayStyle() {
        stylePick = if (isCasualToggle) {
            "캐주얼"
        } else if (isLovelyToggle) {
            "러블리"
        } else if (isChicToggle) {
            "모던시크"
        } else if (isFormalToggle) {
            "포멀/오피스룩"
        } else if (isSportyToggle) {
            "스포티"
        } else if (isBasicToggle) {
            "심플베이직"
        } else if (isRomanticToggle) {
            "로맨틱럭셔리"
        } else if (isNaturalToggle) {
            "꾸안꾸"
        } else {
            ""
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

        // 다른 애들은 모두 선택이 해제된 상태여야함
        isLovelyToggle = false
        isChicToggle = false
        isFormalToggle = false
        isSportyToggle = false
        isBasicToggle = false
        isRomanticToggle = false
        isNaturalToggle = false
        // 그리고 모두 이미지가 다시 돌아와야 하는 상황
        lovelyBtn.setImageResource(R.drawable.tag_lovely_button)
        chicBtn.setImageResource(R.drawable.tag_chic_button)
        formalBtn.setImageResource(R.drawable.tag_formal_button)
        sportyBtn.setImageResource(R.drawable.tag_sporty_button)
        basicBtn.setImageResource(R.drawable.tag_basic_button)
        romanticBtn.setImageResource(R.drawable.tag_romantic_button)
        naturalBtn.setImageResource(R.drawable.tag_natural_button)
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

        // 다른 애들은 모두 선택이 해제된 상태여야함
        isCasualToggle = false
        isChicToggle = false
        isFormalToggle = false
        isSportyToggle = false
        isBasicToggle = false
        isRomanticToggle = false
        isNaturalToggle = false
        // 그리고 모두 이미지가 다시 돌아와야 하는 상황
        casualBtn.setImageResource(R.drawable.tag_casual_button)
        chicBtn.setImageResource(R.drawable.tag_chic_button)
        formalBtn.setImageResource(R.drawable.tag_formal_button)
        sportyBtn.setImageResource(R.drawable.tag_sporty_button)
        basicBtn.setImageResource(R.drawable.tag_basic_button)
        romanticBtn.setImageResource(R.drawable.tag_romantic_button)
        naturalBtn.setImageResource(R.drawable.tag_natural_button)
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

        // 다른 애들은 모두 선택이 해제된 상태여야함
        isCasualToggle = false
        isLovelyToggle = false
        isFormalToggle = false
        isSportyToggle = false
        isBasicToggle = false
        isRomanticToggle = false
        isNaturalToggle = false
        // 그리고 모두 이미지가 다시 돌아와야 하는 상황
        casualBtn.setImageResource(R.drawable.tag_casual_button)
        lovelyBtn.setImageResource(R.drawable.tag_lovely_button)
        formalBtn.setImageResource(R.drawable.tag_formal_button)
        sportyBtn.setImageResource(R.drawable.tag_sporty_button)
        basicBtn.setImageResource(R.drawable.tag_basic_button)
        romanticBtn.setImageResource(R.drawable.tag_romantic_button)
        naturalBtn.setImageResource(R.drawable.tag_natural_button)
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

        // 다른 애들은 모두 선택이 해제된 상태여야함
        isCasualToggle = false
        isLovelyToggle = false
        isChicToggle = false
        isSportyToggle = false
        isBasicToggle = false
        isRomanticToggle = false
        isNaturalToggle = false
        // 그리고 모두 이미지가 다시 돌아와야 하는 상황
        casualBtn.setImageResource(R.drawable.tag_casual_button)
        lovelyBtn.setImageResource(R.drawable.tag_lovely_button)
        chicBtn.setImageResource(R.drawable.tag_chic_button)
        sportyBtn.setImageResource(R.drawable.tag_sporty_button)
        basicBtn.setImageResource(R.drawable.tag_basic_button)
        romanticBtn.setImageResource(R.drawable.tag_romantic_button)
        naturalBtn.setImageResource(R.drawable.tag_natural_button)
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

        // 다른 애들은 모두 선택이 해제된 상태여야함
        isCasualToggle = false
        isLovelyToggle = false
        isChicToggle = false
        isFormalToggle = false
        isBasicToggle = false
        isRomanticToggle = false
        isNaturalToggle = false
        // 그리고 모두 이미지가 다시 돌아와야 하는 상황
        casualBtn.setImageResource(R.drawable.tag_casual_button)
        lovelyBtn.setImageResource(R.drawable.tag_lovely_button)
        chicBtn.setImageResource(R.drawable.tag_chic_button)
        formalBtn.setImageResource(R.drawable.tag_formal_button)
        basicBtn.setImageResource(R.drawable.tag_basic_button)
        romanticBtn.setImageResource(R.drawable.tag_romantic_button)
        naturalBtn.setImageResource(R.drawable.tag_natural_button)
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

        // 다른 애들은 모두 선택이 해제된 상태여야함
        isCasualToggle = false
        isLovelyToggle = false
        isChicToggle = false
        isFormalToggle = false
        isSportyToggle = false
        isRomanticToggle = false
        isNaturalToggle = false
        // 그리고 모두 이미지가 다시 돌아와야 하는 상황
        casualBtn.setImageResource(R.drawable.tag_casual_button)
        lovelyBtn.setImageResource(R.drawable.tag_lovely_button)
        chicBtn.setImageResource(R.drawable.tag_chic_button)
        formalBtn.setImageResource(R.drawable.tag_formal_button)
        sportyBtn.setImageResource(R.drawable.tag_sporty_button)
        romanticBtn.setImageResource(R.drawable.tag_romantic_button)
        naturalBtn.setImageResource(R.drawable.tag_natural_button)
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

        // 다른 애들은 모두 선택이 해제된 상태여야함
        isCasualToggle = false
        isLovelyToggle = false
        isChicToggle = false
        isFormalToggle = false
        isSportyToggle = false
        isBasicToggle = false
        isNaturalToggle = false
        // 그리고 모두 이미지가 다시 돌아와야 하는 상황
        casualBtn.setImageResource(R.drawable.tag_casual_button)
        lovelyBtn.setImageResource(R.drawable.tag_lovely_button)
        chicBtn.setImageResource(R.drawable.tag_chic_button)
        formalBtn.setImageResource(R.drawable.tag_formal_button)
        sportyBtn.setImageResource(R.drawable.tag_sporty_button)
        basicBtn.setImageResource(R.drawable.tag_basic_button)
        naturalBtn.setImageResource(R.drawable.tag_natural_button)
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

        // 다른 애들은 모두 선택이 해제된 상태여야함
        isCasualToggle = false
        isLovelyToggle = false
        isChicToggle = false
        isFormalToggle = false
        isSportyToggle = false
        isBasicToggle = false
        isRomanticToggle = false
        // 그리고 모두 이미지가 다시 돌아와야 하는 상황
        casualBtn.setImageResource(R.drawable.tag_casual_button)
        lovelyBtn.setImageResource(R.drawable.tag_lovely_button)
        chicBtn.setImageResource(R.drawable.tag_chic_button)
        formalBtn.setImageResource(R.drawable.tag_formal_button)
        sportyBtn.setImageResource(R.drawable.tag_sporty_button)
        basicBtn.setImageResource(R.drawable.tag_basic_button)
        romanticBtn.setImageResource(R.drawable.tag_romantic_button)
    }

}