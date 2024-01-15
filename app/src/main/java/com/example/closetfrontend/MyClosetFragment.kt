package com.example.closetfrontend

import android.content.ContentValues
import android.content.Context
import android.content.Intent
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyClosetFragment : BottomSheetDialogFragment() {

    private lateinit var userId: String // userId
    lateinit var binding: FragmentMyClosetBinding // binding

    // recyclerView들
    private lateinit var rvTab1Top: RecyclerView
    private lateinit var rvTab1Bottom: RecyclerView
    private lateinit var rvTab1Outer: RecyclerView
    private lateinit var rvTab1Onepiece: RecyclerView
    private lateinit var rvTab1Shoes: RecyclerView
    private lateinit var rvTab1Bag: RecyclerView

//    // adapter들 - dummy 실험용 아이들
//    private lateinit var topItemAdapter: myClosetAdapter
//    private lateinit var bottomItemAdapter: myClosetAdapter
//    private lateinit var outerItemAdapter: myClosetAdapter
//    private lateinit var onepieceItemAdapter: myClosetAdapter
//    private lateinit var shoesItemAdapter: myClosetAdapter
//    private lateinit var bagItemAdapter: myClosetAdapter
//
//    // itemList들 - dummy 실험용 아이들
//    private var topItemList = ArrayList<myClosetItem>()
//    private var bottomItemList = ArrayList<myClosetItem>()
//    private var outerItemList = ArrayList<myClosetItem>()
//    private var onepieceItemList = ArrayList<myClosetItem>()
//    private var shoeItemList = ArrayList<myClosetItem>()
//    private var bagItemList = ArrayList<myClosetItem>()

    // itemList들 - 실제 서버에 사용될 아이들
    private var topList = ArrayList<Clothes>()
    private var bottomList = ArrayList<Clothes>()
    private var outerList = ArrayList<Clothes>()
    private var onepieceList = ArrayList<Clothes>()
    private var shoeList = ArrayList<Clothes>()
    private var bagList = ArrayList<Clothes>()

    // adapter들 - 실제 서버에 사용될 아이들
    private lateinit var topAdapter: ClothesAdapter
    private lateinit var bottomAdapter: ClothesAdapter
    private lateinit var outerAdapter: ClothesAdapter
    private lateinit var onepieceAdapter: ClothesAdapter
    private lateinit var shoesAdapter: ClothesAdapter
    private lateinit var bagAdapter: ClothesAdapter

    // heart 버튼들
    private lateinit var tab1TopLike: ImageButton
    private lateinit var tab1BottomLike: ImageButton
    private lateinit var tab1OuterLike: ImageButton
    private lateinit var tab1OnepieceLike: ImageButton
    private lateinit var tab1ShoesLike: ImageButton
    private lateinit var tab1BagLike: ImageButton

    // 코디 save 버튼
    private lateinit var codiSaveBtn: ImageButton

    // 코디로 선택한 옷들의 clothId
    private lateinit var clothIdTop: String
    private lateinit var clothIdBottom: String
    private lateinit var clothIdOuter: String
    private lateinit var clothIdOnepiece: String
    private lateinit var clothIdShoes: String
    private lateinit var clothIdBag: String

    // add clothes 버튼
    private lateinit var goAddClothes: FloatingActionButton



    // 서버에서 불러오기
    private val api = RetrofitInterface.create()

    // 카테고리별 분류를 위한 list
    private val category = listOf("상의", "하의", "아우터", "원피스", "신발", "가방")

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
        getClothes() // 서버에서 모든 옷들 가져오기
        showOnlyLikes() // 하트 누르면 like된 애들만 빼오기
        // clickHeart() // 각 항목 하트 누르면 like-unlike 실행 -> 이건 adapter에서 함
        // goTrash() // 길게 누르면 trash 항목으로 이동 -> 이건 adapter에서 함
        // makeLookbook() // Make a New Lookbook 버튼 누르면 각 옷 선택됨 + 같은 카테고리의 다른 옷들 선택 못하게 됨 + lookbook에 사진 띄워짐 -> 이건 adapter에서 함
        addNewCloth() // +버튼 누르면 add하는 activity로 넘어감
        addNewCodi() // save 버튼 누르면 코디 저장됨

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

//        // adapter 매칭 - dummy data용
//        topItemAdapter = myClosetAdapter(topItemList)
//        bottomItemAdapter = myClosetAdapter(bottomItemList)
//        outerItemAdapter = myClosetAdapter(outerItemList)
//        onepieceItemAdapter = myClosetAdapter(onepieceItemList)
//        shoesItemAdapter = myClosetAdapter(shoeItemList)
//        bagItemAdapter = myClosetAdapter(bagItemList)

        // adapter 매칭 - 실제 서버에서 data 가져왔을 때 용
        topAdapter = ClothesAdapter(this, topList)
        bottomAdapter = ClothesAdapter(this, bottomList)
        outerAdapter = ClothesAdapter(this, outerList)
        onepieceAdapter = ClothesAdapter(this, onepieceList)
        shoesAdapter = ClothesAdapter(this, shoeList)
        bagAdapter = ClothesAdapter(this, bagList)

        // recyclerView 가로로 스크롤 하도록
        rvTab1Top.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvTab1Bottom.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvTab1Outer.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvTab1Onepiece.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvTab1Shoes.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvTab1Bag.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

//        // recyclerView와 adapter 연결 - dummy data용
//        rvTab1Top.adapter = topItemAdapter
//        rvTab1Bottom.adapter = bottomItemAdapter
//        rvTab1Outer.adapter = outerItemAdapter
//        rvTab1Onepiece.adapter = onepieceItemAdapter
//        rvTab1Shoes.adapter = shoesItemAdapter
//        rvTab1Bag.adapter = bagItemAdapter

        // recyclerView와 adapter 연결 - 실제 서버에서 data 가져왔을 때 용
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

        // 코디 save 버튼
        codiSaveBtn = binding.saveCodiBtn

        // add Clothes 버튼
        goAddClothes = binding.addFab

        // bottom sheet behavior
        val bottomSheetView = binding.bottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        bottomSheetBehavior.setBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset > 0) {
                    // 화살표 180도 돌아가도록
                    binding.guideline1.rotation = slideOffset * 180F
                    val params = binding.myClosetPadding.layoutParams
                    params.height = resources.getDimension(R.dimen.new_height).toInt() // 원하는 높이로 변경
                    binding.myClosetPadding.layoutParams = params
                } else if (slideOffset.toInt() == 0) {
                    val params = binding.myClosetPadding.layoutParams
                    params.height = resources.getDimension(R.dimen.old_height).toInt() // 원하는 높이로 변경
                    binding.myClosetPadding.layoutParams = params
                }
            }
        })

    }

    private fun getClothes() {
        
//        // dummy
//        val dummy = myClosetItem("1234", "1", "top", listOf("Casual", "Basic"), listOf("like", "wish"), "https://k.kakaocdn.net/dn/iiHzE/btsCnFefcFe/csRhbfOvNWQKsumvxRXkA1/img_640x640.jpg", listOf("https://k.kakaocdn.net/dn/iiHzE/btsCnFefcFe/csRhbfOvNWQKsumvxRXkA1/img_640x640.jpg"))
//        for (i: Int in 1..5) {
//            topItemList.add(dummy)
//            bottomItemList.add(dummy)
//            outerItemList.add(dummy)
//            onepieceItemList.add(dummy)
//            shoeItemList.add(dummy)
//            bagItemList.add(dummy)
//        }
//        topItemAdapter.notifyDataSetChanged()
//        bottomItemAdapter.notifyDataSetChanged()
//        outerItemAdapter.notifyDataSetChanged()
//        onepieceItemAdapter.notifyDataSetChanged()
//        shoesItemAdapter.notifyDataSetChanged()
//        bagItemAdapter.notifyDataSetChanged()
        
        for (category in category) {
            // 서버에서 옷 가져오는 거 해야함
            api.getCategoryClothes(userId, category).enqueue(object: Callback<ClothesResponse> {
                override fun onResponse(call: Call<ClothesResponse>, response: Response<ClothesResponse>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.e(ContentValues.TAG, "result: $result")
                        result?.let{ handleGetClothes(category, it) }
                    } else {
                        // HTTP 요청이 실패한 경우의 처리
                        Log.e(ContentValues.TAG, "HTTP 요청 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ClothesResponse>, t: Throwable) {
                    Log.e(ContentValues.TAG, "네트워크 오류: ${t.message}")
                }
            })
        }
        topList.add(Clothes("1", "상의", listOf("꾸안꾸"), listOf("like"), "https://k.kakaocdn.net/dn/iiHzE/btsCnFefcFe/csRhbfOvNWQKsumvxRXkA1/img_640x640.jpg", null, "송한이", "3283120333"))
        bottomList.add(Clothes("2", "하의", listOf("꾸안꾸"), listOf("like"), "https://k.kakaocdn.net/dn/iiHzE/btsCnFefcFe/csRhbfOvNWQKsumvxRXkA1/img_640x640.jpg", null, "송한이", "3283120333"))
        outerList.add(Clothes("3", "아우터", listOf("꾸안꾸"), listOf("like"), "https://k.kakaocdn.net/dn/iiHzE/btsCnFefcFe/csRhbfOvNWQKsumvxRXkA1/img_640x640.jpg", null, "송한이", "3283120333"))
        onepieceList.add(Clothes("4", "원피스", listOf("꾸안꾸"), listOf("like"), "https://k.kakaocdn.net/dn/iiHzE/btsCnFefcFe/csRhbfOvNWQKsumvxRXkA1/img_640x640.jpg", null, "송한이", "3283120333"))
        shoeList.add(Clothes("5", "신발", listOf("꾸안꾸"), listOf("like"), "https://k.kakaocdn.net/dn/iiHzE/btsCnFefcFe/csRhbfOvNWQKsumvxRXkA1/img_640x640.jpg", null, "송한이", "3283120333"))
        bagList.add(Clothes("6", "가방", listOf("꾸안꾸"), listOf("like"), "https://k.kakaocdn.net/dn/iiHzE/btsCnFefcFe/csRhbfOvNWQKsumvxRXkA1/img_640x640.jpg", null, "송한이", "3283120333"))

        topAdapter.notifyDataSetChanged()
        bottomAdapter.notifyDataSetChanged()
        outerAdapter.notifyDataSetChanged()
        onepieceAdapter.notifyDataSetChanged()
        shoesAdapter.notifyDataSetChanged()
        bagAdapter.notifyDataSetChanged()
    }

    private fun showOnlyLikes() {
        // 하트 누르면 like된 애들만 빼오기
        clickLikeButton(tab1TopLike, "상의")
        clickLikeButton(tab1BottomLike, "하의")
        clickLikeButton(tab1OuterLike, "아우터")
        clickLikeButton(tab1OnepieceLike, "원피스")
        clickLikeButton(tab1ShoesLike, "신발")
        clickLikeButton(tab1BagLike, "가방")
    }

    private fun clickLikeButton(likeButton: ImageButton, category: String) {
        likeButton.setOnClickListener {
            val currentTopLikeImage = likeButton.tag as? Int ?: R.drawable.empty_heart
            val newTopLikeImage = if (currentTopLikeImage == R.drawable.empty_heart) {
                R.drawable.full_heart
            } else { R.drawable.empty_heart }

            likeButton.setImageResource(newTopLikeImage)
            likeButton.tag = newTopLikeImage
            
            // 각 list clear은 handleGetClothes에서 했음

            // 서버에서 옷 가져오는 거 해야함
            api.getTagCategoryClothes(userId, "Like", category).enqueue(object: Callback<ClothesResponse> {
                override fun onResponse(call: Call<ClothesResponse>, response: Response<ClothesResponse>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.e(ContentValues.TAG, "result: $result")
                        result?.let{ handleGetClothes(category, it) }
                    } else {
                        // HTTP 요청이 실패한 경우의 처리
                        Log.e(ContentValues.TAG, "HTTP 요청 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ClothesResponse>, t: Throwable) {
                    Log.e(ContentValues.TAG, "네트워크 오류: ${t.message}")
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
        for (cloth in data.clothes) {
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
    }

    private fun addNewCloth() {
        // +버튼 누르면 add하는 activity로 넘어감
        goAddClothes.setOnClickListener {
            val intent = Intent(context, AddPhotoActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
    }
    
    private fun addNewCodi() {
        // save 버튼 누르면 코디 저장됨
        codiSaveBtn.setOnClickListener {

            // 코디에 사용되는 cloth들의 id
            clothIdTop = binding.textLookbookTop.text.toString()
            clothIdBottom = binding.textLookbookBottom.text.toString()
            clothIdOuter = binding.textLookbookOuter.text.toString()
            clothIdOnepiece = binding.textLookbookOnepiece.text.toString()
            clothIdShoes = binding.textLookbookShoes.text.toString()
            clothIdBag = binding.textLookbookBag.text.toString()
            // 이때 text를 불러와야 데이터 꼬임이 없을 듯!

            Log.e("MyClosetFragment", clothIdTop)
            Log.e("MyClosetFragment", clothIdBottom)

            val codiTop = findWhichCloth(topList, clothIdTop)
            val codiBottom = findWhichCloth(bottomList, clothIdBottom)
            val codiOuter = findWhichCloth(outerList, clothIdOuter)
            val codiOnepiece = findWhichCloth(onepieceList, clothIdOnepiece)
            val codiShoes = findWhichCloth(shoeList, clothIdShoes)
            val codiBag = findWhichCloth(bagList, clothIdBag)

            var stylesArray = ArrayList<String>()
            for (style in codiTop.styles) { stylesArray.add(style) }
            for (style in codiBottom.styles) { stylesArray.add(style) }
            for (style in codiOuter.styles) { stylesArray.add(style) }
            for (style in codiOnepiece.styles) { stylesArray.add(style) }
            for (style in codiShoes.styles) { stylesArray.add(style) }
            for (style in codiBag.styles) { stylesArray.add(style) }
            stylesArray.distinct() // 중복 제거

            val like: String = "none"

            val clothesIdsArray = ArrayList<String>()
            clothesIdsArray.add(clothIdTop)
            clothesIdsArray.add(clothIdBottom)
            clothesIdsArray.add(clothIdOuter)
            clothesIdsArray.add(clothIdOnepiece)
            clothesIdsArray.add(clothIdShoes)
            clothesIdsArray.add(clothIdBag)

            val clothesImagesArray = ArrayList<String>()
            clothesImagesArray.add(codiTop.imageUrl)
            clothesImagesArray.add(codiBottom.imageUrl)
            clothesImagesArray.add(codiOuter.imageUrl)
            clothesImagesArray.add(codiOnepiece.imageUrl)
            clothesImagesArray.add(codiShoes.imageUrl)
            clothesImagesArray.add(codiBag.imageUrl)

            val comment = binding.comment.text.toString()

            val call = api.saveCodi(
                userId,
                stylesArray,
                like,
                clothesIdsArray,
                clothesImagesArray,
                comment)
            call.enqueue(object: Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        val whichUser = result?.get("result")?.asString
                        if (whichUser == "codi has been saved") {
                            Log.e("Lets go", "success!! good!!")
                        } else  { Log.e("Lets go", "what's wrong in response...") }
                    } else { Log.e("Lets go", "what's wrong...") }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("mad..nn", "so sad plz")
                }
            })
            
            // 다 보내고 나서 리셋하는 과정 필요함
            // list들 모두 reset,
            // 이미지 뷰들 모두 reset,
            // comment도 reset
        }
    }

    private fun findWhichCloth(clothesList: ArrayList<Clothes>, clothId: String) : Clothes {
        var position: Int = 0
        for (i: Int in 0..(clothesList.size-1)) {
            if (clothesList[i].id == clothId) {
                position = i
                break
            }
        }
        return clothesList[position]
    }

    companion object {}
}