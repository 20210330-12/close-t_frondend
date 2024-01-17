package com.example.closetfrontend

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.closetfrontend.databinding.ActivityMainBinding
import com.google.gson.JsonObject
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.Constants
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.UserApiClient.Companion.instance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var context: Context
    private lateinit var kakaoLoginButton : ImageButton
    // 토큰 아이디
    private lateinit var userId: String
    // 유저 이름
    private lateinit var userName: String
    // 로딩뷰
    private lateinit var loadingView: ImageView
    private lateinit var logo: ImageView

    // 서버에서 불러오기
    val api = RetrofitInterface.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadingView = findViewById(R.id.gif_image)
        logo = findViewById(R.id.logo)

        kakaoLoginButton = findViewById(R.id.kakaotalkBtn)
        context = this
        kakaoLoginButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    // 로딩 화면
                    Glide.with(context).load(R.drawable.splash_activity_1by1).into(loadingView)
                    loadingView.visibility = View.VISIBLE
                    kakaoLoginButton.visibility = View.INVISIBLE
                    logo.visibility = View.INVISIBLE
                    // 서비스 코드에서는 간단하게 로그인 요청하고 oAuthToken을 받아올 수 있다.
                    val oAuthToken = UserApiClient.loginWithKakao(context)
                    Log.d("MainActivity", "beanbean > $oAuthToken")
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e(Constants.TAG, "사용자 정보 요청 실패 $error")
                        } else if (user != null) {
                            Log.e(Constants.TAG, "로그인 성공")
                            userId = user.id.toString()
                            val userName = user.properties!!["nickname"].toString()
                            val userPic = user.properties!!["profile_image"].toString()
                            val userEmail = user.kakaoAccount!!.email.toString()
                            Log.e(Constants.TAG, "userid: $userId")
                            Log.e(Constants.TAG, "userName: $userName")
                            Log.e(Constants.TAG, "userPic: $userPic")
                            Log.e(Constants.TAG, "userEmail: $userEmail")

                            // SharedPreferences 객체 가져오기
                            val sharedPref = getSharedPreferences("userId", Context.MODE_PRIVATE)
                            val editor = sharedPref.edit()
                            // userId 데이터 저장 (다른 activity나 fragment에서도 사용할 수 있도록 함)
                            editor.putString("userId", "$userId")
                            editor.putString("userName", "$userName")
                            editor.putString("userPic", "$userPic")
                            editor.putString("userEmail", "$userEmail")
                            editor.apply() // 변경 사항을 저장
                            // 이걸 내가 MyClosetFragment에서 받아볼테니 한번 봐봥
                            // 코드 수정은 하지 말고 똑같은 방법으로 다른 곳에다가 쓰면 돼!
                        }
                    }
                    // 로그인 하는데 시간이 걸림 -> userId를 lateinit var로 해놔서,
                    // 값이 들어가지 못한 채 getUserCheck()가 실행됨을 방지
                    Handler(Looper.getMainLooper()).postDelayed({
                        getUserCheck()
                    }, 2500)
                } catch (error: Throwable) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        Log.d("MainActivity", "사용자가 명시적으로 취소")
                    } else {
                        Log.e("MainActivity", "인증 에러 발생", error)
                    }
                }
            }
        }
    }

    private fun getUserCheck() {
        api.getUserCheck(userId).enqueue(object: Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.e(ContentValues.TAG, "result: $result")
                    result?.let{ handleGetUserCheck(it) }
                } else {
                    // HTTP 요청이 실패한 경우의 처리
                    Log.e(ContentValues.TAG, "HTTP 요청 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e(ContentValues.TAG, "네트워크 오류: ${t.message}")
            }
        })
    }
    private fun handleGetUserCheck(data: JsonObject) {
        val whichUser = data?.get("result")?.asString
        //var oldUser: Boolean = false
        if (whichUser == "returning user") {
            // 이미 저장된 user이면 fragment로 바로 ㄱㄱ
            //oldUser = true
            val intent = Intent(context, AfterLoginActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        } else  {
            // 신규 user이면 정보 입력 activity로 ㄱㄱ
            val intent = Intent(context, EnterProfile::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
//        loadingView.visibility = View.GONE
//        kakaoLoginButton.visibility = View.VISIBLE
//        logo.visibility = View.VISIBLE

        //Log.e(ContentValues.TAG, "기존 유저인가요?: $oldUser")
    }

}