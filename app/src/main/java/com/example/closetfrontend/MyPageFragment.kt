package com.example.closetfrontend

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.closetfrontend.databinding.FragmentMyClosetBinding
import com.example.closetfrontend.databinding.FragmentMyPageBinding
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageFragment : Fragment() {

    private lateinit var userId: String // userId
    lateinit var binding: FragmentMyPageBinding // binding
    // 서버에서 불러오기
    private val api = RetrofitInterface.create()

    // 화살표 버튼들
    private lateinit var goTrashBtn: ImageButton
    private lateinit var goLikeBtn: ImageButton
    private lateinit var goWishBtn: ImageButton
    private lateinit var goOotdBtn: ImageButton

    // 프로필 정보 나타낼 것들
    private lateinit var profileImageText: String
    private lateinit var profileImage: ImageView
    private lateinit var profileNameText: String
    private lateinit var profileName: TextView
    private lateinit var profileGenderText: String
    private lateinit var profileGender: ImageView
    private lateinit var profileEmailText: String
    private lateinit var profileEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 여기가 sharedPreference로 userId 받는 부분
        val sharedPref = requireContext().getSharedPreferences("userId", Context.MODE_PRIVATE)
        userId = sharedPref.getString("userId", "")!!
        Log.e("MyClosetFragment", "userId: $userId")

        // Inflate the layout for this fragment
        binding = FragmentMyPageBinding.inflate(inflater, container, false)

        initiation()
        getUser()
        Handler(Looper.getMainLooper()).postDelayed({
            // user 정보 불러오는데 시간 걸릴 것 같아서 이렇게 해줬다
            setProfile()
        }, 1000)
        goActivity()


        return binding.root
    }

    private fun initiation() {
        // 버튼 초기화
        goTrashBtn = binding.TrashBtn
        goLikeBtn = binding.likeClothesBtn
        goWishBtn = binding.wishClothesBtn
        goOotdBtn = binding.goOotdBtn

        // View들 초기화
        profileImage = binding.profilePic
        profileName = binding.profileName
        profileGender = binding.profileGender
        profileEmail = binding.profileEmail
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
    }

    private fun goActivity() {
        goTrashBtn.setOnClickListener {
            val intent1 = Intent(context, TrashActivity::class.java)
            startActivity(intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
        goLikeBtn.setOnClickListener {
            val intent2 = Intent(context, LikeActivity::class.java)
            startActivity(intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
        goWishBtn.setOnClickListener {
            val intent3 = Intent(context, WishActivity::class.java)
            startActivity(intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
        goOotdBtn.setOnClickListener {
            val intent4 = Intent(context, ChatOotdActivity::class.java)
            startActivity(intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
    }

    companion object {    }
}