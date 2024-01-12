package com.example.closetfrontend

import android.app.Application
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.Constants.TAG
import com.kakao.sdk.user.UserApiClient

class InitKakaoSdk : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "3b702648604a0823ed034de028a00406")
    }
}