package com.example.closetfrontend

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closetfrontend.databinding.FragmentMyClosetBinding

class MyClosetFragment : Fragment() {

    private lateinit var userId: String // userId
    private lateinit var binding: FragmentMyClosetBinding // binding

    // recyclerView들
    private lateinit var rvTab1Top: RecyclerView
    private lateinit var rvTab1Bottom: RecyclerView
    private lateinit var rvTab1Outer: RecyclerView
    private lateinit var rvTab1Onepiece: RecyclerView
    private lateinit var rvTab1Shoes: RecyclerView
    private lateinit var rvTab1Bag: RecyclerView

    // adapter들
    private lateinit var topAdapter: myClosetAdapter
    private lateinit var bottomAdapter: myClosetAdapter
    private lateinit var outerAdapter: myClosetAdapter
    private lateinit var onepieceAdapter: myClosetAdapter
    private lateinit var shoesAdapter: myClosetAdapter
    private lateinit var bagAdapter: myClosetAdapter

    // itemList들
    private var topItemList = ArrayList<myClosetItem>()
    private var bottomItemList = ArrayList<myClosetItem>()
    private var outerItemList = ArrayList<myClosetItem>()
    private var onepieceItemList = ArrayList<myClosetItem>()
    private var shoeItemList = ArrayList<myClosetItem>()
    private var bagItemList = ArrayList<myClosetItem>()

    // heart 버튼들
    private lateinit var tab1TopLike: ImageButton
    private lateinit var tab1BottomLike: ImageButton
    private lateinit var tab1OuterLike: ImageButton
    private lateinit var tab1OnepieceLike: ImageButton
    private lateinit var tab1ShoesLike: ImageButton
    private lateinit var tab1BagLike: ImageButton

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
        binding = FragmentMyClosetBinding.inflate(inflater, container, false)
        
        initiation() // 모든 view들 정의
        val dummy = myClosetItem("1234", "1", "top", listOf("Casual", "Basic"), listOf("like", "wish"), "https://k.kakaocdn.net/dn/iiHzE/btsCnFefcFe/csRhbfOvNWQKsumvxRXkA1/img_640x640.jpg", listOf("https://k.kakaocdn.net/dn/iiHzE/btsCnFefcFe/csRhbfOvNWQKsumvxRXkA1/img_640x640.jpg"))
        for (i: Int in 1..10) {
            topItemList.add(dummy)
        }
        topAdapter.notifyDataSetChanged()

        return binding.root
    }

    private fun initiation() {

        // recyclerView 매칭
        rvTab1Top = binding.rvTab1Top
        rvTab1Bottom = binding.rvTab1Bottom
        rvTab1Outer = binding.rvTab1Outer
        rvTab1Onepiece = binding.rvTab1Onepiece
        rvTab1Shoes = binding.rvTab1Shoes
        rvTab1Bag = binding.rvTab1Bag

        // adapter 매칭
        topAdapter = myClosetAdapter(topItemList)
        bottomAdapter = myClosetAdapter(bottomItemList)
        outerAdapter = myClosetAdapter(outerItemList)
        onepieceAdapter = myClosetAdapter(onepieceItemList)
        shoesAdapter = myClosetAdapter(shoeItemList)
        bagAdapter = myClosetAdapter(bagItemList)

        // recyclerView 가로로 스크롤 하도록
        rvTab1Top.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvTab1Bottom.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvTab1Outer.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvTab1Onepiece.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvTab1Shoes.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvTab1Bag.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        // recyclerView와 adapter 연결
        rvTab1Top.adapter = topAdapter
        rvTab1Bottom.adapter = bottomAdapter
        rvTab1Outer.adapter = outerAdapter
        rvTab1Onepiece.adapter = onepieceAdapter
        rvTab1Shoes.adapter = shoesAdapter
        rvTab1Bag.adapter = bagAdapter

        // heart 버튼들
        tab1TopLike = binding.tab1TopLike
        tab1BottomLike = binding.tab1BottomLike
        tab1OuterLike = binding.tab1OuterLike
        tab1OnepieceLike = binding.tab1OnepieceLike
        tab1ShoesLike = binding.tab1ShoesLike
        tab1BagLike = binding.tab1BagLike

    }

    companion object {}
}