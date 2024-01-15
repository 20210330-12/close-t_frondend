package com.example.closetfrontend

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closetfrontend.databinding.FragmentMyClosetBinding

class TrashActivity : AppCompatActivity() {

    private lateinit var userId: String // userId

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

    // 서버에서 불러오기
    private val api = RetrofitInterface.create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trash)

        // 여기가 sharedPreference로 userId 받는 부분
        val sharedPref = this.getSharedPreferences("userId", Context.MODE_PRIVATE)
        userId = sharedPref.getString("userId", "")!!
        Log.e("MyClosetFragment", "userId: $userId")

        initiation()
        // 그 뒤에, 이제 각 List에 DB 정보 받아서 입력해주면 됨
        // back button도 만들자

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
    }
}