package com.example.closetfrontend

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitInterface {

    @GET("/user/{userId}/check")
    fun getUserCheck(@Path("userId") userId: String): Call<JsonObject>

    @POST("/{userId}/clothes/add")
    fun postAddClothes(
        @Path("userId") userId: String,
        @Field("category") category: String,
        @Field("styles") styles: List<String>,
        @Field("tag") tag: List<String>?,
        @Field("imageUrl") imageUrl: String,
        @Field("link") link: String?,
    ): Call<JsonObject>

    @GET("/{userId}/codi")
    fun getAllCodies(@Path("userId") userId: String): Call<JsonObject>

    @GET("/{userId}/codi/{codiId}/view")
    fun getSelectedCodi(@Path("userId") userId: String, @Path("codiId") codiId: String): Call<JsonObject>

    @GET("/{userId}/codi/liked")
    fun getLikedCodies(@Path("userId") userId: String): Call<JsonObject>
//    // 전 주에 했었던 예시들 보여줄겡
//    // 만약 위에처럼 path에 parameter이 들어가는게 아니라, 그냥 parameter만 전달하는 거라면 이런 식으로!
//    @GET("/myCustom")
//    fun getMyCustom(@Query("userId") parameter: String): Call<List<List<String>>>
//
//    // parameter이 필요없으면 이런 식으로!
//    @GET("/allCustoms")
//    fun getAllCustoms(): Call<List<List<String>>>
//
//    // POST를 원하면 이런 식으로!
//    @FormUrlEncoded
//    @POST("/addCustom")
//    fun addCustom(
//        @Field("name") name: String,
//        @Field("menu") menu: String,
//        @Field("category") category: String,
//        @Field("custom") custom: String,
//        @Field("Description") Description: String,
//        @Field("creator") creator: String,
//        @Field("creatornum") creatornum: String
//    ): Call<Void>

    // 그리고 각 activity 혹은 fragment에서 받으면 돼
    // getUserCheck()에 대한 예시는 MainActivity에 있으니까 참고!
    // GET이든 POST든 Activity나 Fragment에서 사용하는 형식은 모두 같음 (onResponse, onFailure)
    // Log 찍으면서 보면 오류들 다 보이구
    // MainActivity에도 잘 찾아보면 내가 적어놓은 것처럼 서버 연결했다가 받는데에도 시간이 걸리니까
    // 만약 꼬이면 그때는 handler 같은거 사용해서 다음에 실행해야 할 것들 좀 미뤄놔!
    // 원래는 뭐 동기적 비동기적 이런거 써야한다고는 하는데 그건 아직 잘 모르겠어서...


    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        //private const val BASE_URL = "http://172.10.7.47:80"
        //private const val BASE_URL = "http://localhost:8888"
        //private const val BASE_URL = "http://127.0.0.1:8888"

        // 에뮬레이터는 기본적으로 자체 가상 네트워크를 사용함
        // -> 호스트의 127.0.0.1에 직접 접근하는게 불가능
        // -> 에뮬레이터가 호스트와 통신하려면 호스트 IP를 사용해야하며,
        // 일반적으로 10.0.2.2가 호스트의 IP임
        private const val BASE_URL = "http://10.0.2.2:8888/"


        fun create(): RetrofitInterface {
            val gson : Gson =   GsonBuilder().setLenient().create();

            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(RetrofitInterface::class.java)
        }
    }
}