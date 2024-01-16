package com.example.closetfrontend

import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.closetfrontend.databinding.ActivityTrashBinding
import com.example.closetfrontend.databinding.FragmentMyClosetBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrashActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var userId: String // userId
    // 스와이프해서 새로고침 구현
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var binding: ActivityTrashBinding

    // recyclerView들
    private lateinit var rvTab1Top: RecyclerView
    private lateinit var rvTab1Bottom: RecyclerView
    private lateinit var rvTab1Outer: RecyclerView
    private lateinit var rvTab1Onepiece: RecyclerView
    private lateinit var rvTab1Shoes: RecyclerView
    private lateinit var rvTab1Bag: RecyclerView

    // itemList들 - 실제 서버에 사용될 아이들
    private var topList = ArrayList<Clothes>()
    private var bottomList = ArrayList<Clothes>()
    private var outerList = ArrayList<Clothes>()
    private var onepieceList = ArrayList<Clothes>()
    private var shoeList = ArrayList<Clothes>()
    private var bagList = ArrayList<Clothes>()

    // adapter들 - 실제 서버에 사용될 아이들
    private lateinit var topAdapter: TrashAdapter
    private lateinit var bottomAdapter: TrashAdapter
    private lateinit var outerAdapter: TrashAdapter
    private lateinit var onepieceAdapter: TrashAdapter
    private lateinit var shoesAdapter: TrashAdapter
    private lateinit var bagAdapter: TrashAdapter

    // backbutton
    private lateinit var backButton: ImageButton

    // 서버에서 불러오기
    private val api = RetrofitInterface.create()

    // 카테고리별 분류를 위한 list
    private val category = listOf("상의", "하의", "아우터", "원피스", "신발", "가방")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTrashBinding.inflate(layoutInflater)
        // 새로고침
        swipeRefreshLayout = binding.trashSwipeLayout
        swipeRefreshLayout.setOnRefreshListener(this)

        setContentView(binding.root)

        // 여기가 sharedPreference로 userId 받는 부분
        val sharedPref = this.getSharedPreferences("userId", Context.MODE_PRIVATE)
        userId = sharedPref.getString("userId", "")!!
        Log.e("MyClosetFragment", "userId: $userId")

        initiation()
        goBack() // back button도 만들자

    }

    private fun initiation() {
        // recyclerView 매칭
        rvTab1Top = findViewById(R.id.rvTab1Top)
        rvTab1Bottom = findViewById(R.id.rvTab1Bottom)
        rvTab1Outer = findViewById(R.id.rvTab1Outer)
        rvTab1Onepiece = findViewById(R.id.rvTab1Onepiece)
        rvTab1Shoes = findViewById(R.id.rvTab1Shoes)
        rvTab1Bag = findViewById(R.id.rvTab1Bag)

        // adapter 매칭 - 실제 서버에서 data 가져왔을 때 용
        topAdapter = TrashAdapter(topList)
        bottomAdapter = TrashAdapter(bottomList)
        outerAdapter = TrashAdapter(outerList)
        onepieceAdapter = TrashAdapter(onepieceList)
        shoesAdapter = TrashAdapter(shoeList)
        bagAdapter = TrashAdapter(bagList)

        // recyclerView 가로로 스크롤 하도록
        rvTab1Top.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvTab1Bottom.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvTab1Outer.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvTab1Onepiece.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvTab1Shoes.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvTab1Bag.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        // recyclerView와 adapter 연결 - 실제 서버에서 data 가져왔을 때 용
        rvTab1Top.adapter = topAdapter
        rvTab1Bottom.adapter = bottomAdapter
        rvTab1Outer.adapter = outerAdapter
        rvTab1Onepiece.adapter = onepieceAdapter
        rvTab1Shoes.adapter = shoesAdapter
        rvTab1Bag.adapter = bagAdapter

        // backButton
        backButton = findViewById(R.id.backBtn)

        // 서버 DB에서 각 리스트에 배당해주기
        // 서버에서 옷 가져오는 거 해야함
        for (category in category) {
            api.getTrash(userId, category).enqueue(object :
                Callback<ClothesResponse> {
                override fun onResponse(
                    call: Call<ClothesResponse>,
                    response: Response<ClothesResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.e(ContentValues.TAG, "result: $result")
                        result?.let { handleGetClothes(category, it) }
                        swipeRefreshLayout.isRefreshing = false
                    } else {
                        // HTTP 요청이 실패한 경우의 처리
                        Log.e(ContentValues.TAG, "HTTP 요청 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ClothesResponse>, t: Throwable) {
                    Log.e(ContentValues.TAG, "네트워크 오류: ${t.message}")
                    swipeRefreshLayout.isRefreshing = false
                }
            })
        }
    }

    private fun handleGetClothes(category: String, data: ClothesResponse) {
        // 처음에 topList를 Reset하는게 필요하지 않을까?
        when (category) {
            "상의" -> { topList.clear() }
            "하의" -> { topList.clear() }
            "아우터" -> { outerList.clear() }
            "원피스" -> { onepieceList.clear() }
            "신발" -> { shoeList.clear() }
            "가방" -> { bagList.clear() }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            for (cloth in data.clothes) {
                Log.d("TrashActivity", "$cloth")
                when (category) {
                    "상의" -> { topList.add(cloth) }
                    "하의" -> { bottomList.add(cloth) }
                    "아우터" -> { outerList.add(cloth) }
                    "원피스" -> { onepieceList.add(cloth) }
                    "신발" -> { shoeList.add(cloth) }
                    "가방" -> { bagList.add(cloth) }
                }
            }
            topAdapter.notifyDataSetChanged()
            bottomAdapter.notifyDataSetChanged()
            outerAdapter.notifyDataSetChanged()
            onepieceAdapter.notifyDataSetChanged()
            shoesAdapter.notifyDataSetChanged()
            bagAdapter.notifyDataSetChanged()
        }, 400)
    }


    private fun goBack() {
        backButton.setOnClickListener {
            finish()
        }
    }

    override fun onRefresh() {
        // list 모두 클리어
        topList.clear()
        bottomList.clear()
        outerList.clear()
        onepieceList.clear()
        shoeList.clear()
        bagList.clear()

        initiation()
        goBack()
        swipeRefreshLayout.isRefreshing = false
    }

}