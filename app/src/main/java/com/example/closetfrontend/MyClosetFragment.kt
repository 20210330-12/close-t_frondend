package com.example.closetfrontend

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.closetfrontend.databinding.FragmentMyClosetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class MyClosetFragment : BottomSheetDialogFragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var userId: String // userId
    lateinit var binding: FragmentMyClosetBinding // binding
    // 스와이프해서 새로고침 구현
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

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

    // TrashItemList들 - 이 아이들을 실제 itemList에서 제외시키면 됨
    private var topTrashList = ArrayList<Clothes>()
    private var bottomTrashList = ArrayList<Clothes>()
    private var outerTrashList = ArrayList<Clothes>()
    private var onepieceTrashList = ArrayList<Clothes>()
    private var shoeTrashList = ArrayList<Clothes>()
    private var bagTrashList = ArrayList<Clothes>()

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

    // heart 버튼들 눌렸는지 판단하는 boolean -> true면 눌려있는, false면 안 눌려있는
    private var ifTopLike = false
    private var ifBottomLike = false
    private var ifOuterLike = false
    private var ifOnepieceLike = false
    private var ifShoesLike = false
    private var ifBagLike = false

    // 코디 save 버튼
    private lateinit var codiSaveBtn: ImageButton

    // 코디로 선택된 옷들의 실제 cloth
    private lateinit var clothTop: ImageView
    private lateinit var clothBottom: ImageView
    private lateinit var clothOuter: ImageView
    private lateinit var clothOnepiece: ImageView
    private lateinit var clothShoes: ImageView
    private lateinit var clothBag: ImageView

    // 코디로 선택한 옷들의 clothId
    private lateinit var clothIdTop: String
    private lateinit var clothIdBottom: String
    private lateinit var clothIdOuter: String
    private lateinit var clothIdOnepiece: String
    private lateinit var clothIdShoes: String
    private lateinit var clothIdBag: String

    // add clothes 버튼
    private lateinit var goAddClothes: FloatingActionButton

//    // context
//    private lateinit var context: Context

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

//        // context
//        context = requireContext()

        // 새로고침
        swipeRefreshLayout = binding.tab1SwipeLayout
        swipeRefreshLayout.setOnRefreshListener(this)

        initiation() // 모든 view들 정의
        getClothes() // 서버에서 모든 옷들 가져오기


        // 지금 버그가, 처음에 실행시키고 바로 한 번 누르면 그때는 작동이 안 된다. 그 뒤부터 작동됨.
        tab1TopLike.setOnClickListener { topLikeButton(it) }
        tab1BottomLike.setOnClickListener { bottomLikeButton(it) }
        tab1OuterLike.setOnClickListener { outerLikeButton(it) }
        tab1OnepieceLike.setOnClickListener { onepieceLikeButton(it) }
        tab1ShoesLike.setOnClickListener { shoesLikeButton(it) }
        tab1BagLike.setOnClickListener { bagLikeButton(it) }

//        showOnlyLikes() // 하트 누르면 like된 애들만 빼오기
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

        // heart 버튼들 모두 맨 처음에는 empty heart로
        tab1TopLike.setImageResource(R.drawable.empty_heart)
        tab1BottomLike.setImageResource(R.drawable.empty_heart)
        tab1OuterLike.setImageResource(R.drawable.empty_heart)
        tab1OnepieceLike.setImageResource(R.drawable.empty_heart)
        tab1ShoesLike.setImageResource(R.drawable.empty_heart)
        tab1BagLike.setImageResource(R.drawable.empty_heart)

        // 룩북 사진들
        clothTop = binding.lookbookTop
        clothBottom = binding.lookbookBottom
        clothOuter = binding.lookbookOuter
        clothOnepiece = binding.lookbookOnepiece
        clothShoes = binding.lookbookShoes
        clothBag = binding.lookbookBag

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
        
        // 한 번 쭉 비워줘야 하긴 함 다른 탭 갔다가 다시돌아오니까 계속 생기네
        topList.clear()
        bottomList.clear()
        outerList.clear()
        onepieceList.clear()
        shoeList.clear()
        bagList.clear()


        for (category in category) {
            // 서버에서 옷 가져오는 거 해야함
            Log.e(ContentValues.TAG, "$category")
//            val encodedCategory = URLEncoder.encode(category, StandardCharsets.UTF_8.toString())
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
        // 근데 여기서 보관함에 있는 옷은 불러오면 안됨. 근데 그 로직이 없으니까 그냥 내가 제외하도록 하겠음
        getTrashList()
        // 이렇게 하면 TrashList 얻어진거니까
        // 약간의 시간차를 둔 다음에, remove하면 됨
        Handler(Looper.getMainLooper()).postDelayed({
            for (topTrash in topTrashList) { topList.remove(topTrash) }
            for (bottomTrash in bottomTrashList) { bottomList.remove(bottomTrash) }
            for (outerTrash in outerTrashList) { outerList.remove(outerTrash) }
            for (onepieceTrash in onepieceTrashList) { onepieceList.remove(onepieceTrash) }
            for (shoeTrash in shoeTrashList) { shoeList.remove(shoeTrash) }
            for (bagTrash in bagTrashList) { bagList.remove(bagTrash) }

            topAdapter.notifyDataSetChanged()
            bottomAdapter.notifyDataSetChanged()
            outerAdapter.notifyDataSetChanged()
            onepieceAdapter.notifyDataSetChanged()
            shoesAdapter.notifyDataSetChanged()
            bagAdapter.notifyDataSetChanged()
        }, 1000)
        
        
//        topList.add(Clothes("1", "상의", listOf("꾸안꾸"), listOf("like"), "https://k.kakaocdn.net/dn/iiHzE/btsCnFefcFe/csRhbfOvNWQKsumvxRXkA1/img_640x640.jpg", null, "송한이", "3283120333"))
//        bottomList.add(Clothes("2", "하의", listOf("꾸안꾸"), listOf("like"), "https://k.kakaocdn.net/dn/iiHzE/btsCnFefcFe/csRhbfOvNWQKsumvxRXkA1/img_640x640.jpg", null, "송한이", "3283120333"))
//        outerList.add(Clothes("3", "아우터", listOf("꾸안꾸"), listOf("like"), "https://k.kakaocdn.net/dn/iiHzE/btsCnFefcFe/csRhbfOvNWQKsumvxRXkA1/img_640x640.jpg", null, "송한이", "3283120333"))
//        onepieceList.add(Clothes("4", "원피스", listOf("꾸안꾸"), listOf("like"), "https://k.kakaocdn.net/dn/iiHzE/btsCnFefcFe/csRhbfOvNWQKsumvxRXkA1/img_640x640.jpg", null, "송한이", "3283120333"))
//        shoeList.add(Clothes("5", "신발", listOf("꾸안꾸"), listOf("like"), "https://k.kakaocdn.net/dn/iiHzE/btsCnFefcFe/csRhbfOvNWQKsumvxRXkA1/img_640x640.jpg", null, "송한이", "3283120333"))
//        bagList.add(Clothes("6", "가방", listOf("꾸안꾸"), listOf("like"), "https://k.kakaocdn.net/dn/iiHzE/btsCnFefcFe/csRhbfOvNWQKsumvxRXkA1/img_640x640.jpg", null, "송한이", "3283120333"))
    }

    private fun getTrashList() {
        for (category in category) {
            // 서버에서 옷 가져오는 거 해야함
            Log.e(ContentValues.TAG, "$category")
//            val encodedCategory = URLEncoder.encode(category, StandardCharsets.UTF_8.toString())
            api.getTrash(userId, category).enqueue(object: Callback<ClothesResponse> {
                override fun onResponse(call: Call<ClothesResponse>, response: Response<ClothesResponse>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.e(ContentValues.TAG, "result: $result")
                        result?.let{
                            when (category) {
                                "상의" -> { topTrashList.clear() }
                                "하의" -> { bottomTrashList.clear() }
                                "아우터" -> { outerTrashList.clear() }
                                "원피스" -> { onepieceTrashList.clear() }
                                "신발" -> { shoeTrashList.clear() }
                                "가방" -> { bagTrashList.clear() }
                            }
                            for (cloth in result.clothes) {
                                when (category) {
                                    "상의" -> { topTrashList.add(cloth) }
                                    "하의" -> { bottomTrashList.add(cloth) }
                                    "아우터" -> { outerTrashList.add(cloth) }
                                    "원피스" -> { onepieceTrashList.add(cloth) }
                                    "신발" -> { shoeTrashList.add(cloth) }
                                    "가방" -> { bagTrashList.add(cloth) }
                                }
                            }
                        }
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

    private fun showOnlyLikes() {
        // 하트 누르면 like된 애들만 빼오기
//        ifTopLike = clickLikeButton(ifTopLike, tab1TopLike, "상의")
//        ifBottomLike = clickLikeButton(ifBottomLike, tab1BottomLike, "하의")
//        ifOuterLike = clickLikeButton(ifOuterLike, tab1OuterLike, "아우터")
//        ifOnepieceLike = clickLikeButton(ifOnepieceLike, tab1OnepieceLike, "원피스")
//        ifShoesLike = clickLikeButton(ifShoesLike, tab1ShoesLike, "신발")
//        ifBagLike = clickLikeButton(ifBagLike, tab1BagLike, "가방")
    }

    private fun clickLikeButton(ifLike: Boolean, likeButton: ImageButton, category: String) {
        // 각 list clear은 handleGetClothes에서 했음
        if (!ifLike) {
            // 이미 눌려있는 상황이면, 다시 눌렀을 때 false가 되는거지
//            likeButton.setImageResource(R.drawable.empty_heart)
            api.getCategoryClothes(userId, category).enqueue(object: Callback<ClothesResponse> {
                override fun onResponse(call: Call<ClothesResponse>, response: Response<ClothesResponse>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.e(ContentValues.TAG, "result: $result")
                        result?.let{ handleGetClothes(category, it) }
                        //likeButton.setImageResource(R.drawable.empty_heart)
                    } else {
                        // HTTP 요청이 실패한 경우의 처리
                        Log.e(ContentValues.TAG, "HTTP 요청 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ClothesResponse>, t: Throwable) {
                    Log.e(ContentValues.TAG, "네트워크 오류: ${t.message}")
                }
            })
        } else {
//            likeButton.setImageResource(R.drawable.full_heart)
            // 서버에서 옷 가져오는 거 해야함
            api.getLike(userId, category).enqueue(object: Callback<ClothesResponse> {
                override fun onResponse(call: Call<ClothesResponse>, response: Response<ClothesResponse>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.e(ContentValues.TAG, "result: $result")
                        result?.let{ handleGetClothes(category, it) }
                        //likeButton.setImageResource(R.drawable.full_heart)
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

    fun handleGetClothes(category: String, data: ClothesResponse) {
        // 처음에 topList를 Reset하는게 필요하지 않을까?
        when (category) {
            "상의" -> { topList.clear() }
            "하의" -> { bottomList.clear() }
            "아우터" -> { outerList.clear() }
            "원피스" -> { onepieceList.clear() }
            "신발" -> { shoeList.clear() }
            "가방" -> { bagList.clear() }
        }
        Handler(Looper.getMainLooper()).postDelayed({
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
        }, 400)
    }

    private fun addNewCloth() {
        // +버튼 누르면 add하는 activity로 넘어감
        goAddClothes.setOnClickListener {
            val intent = Intent(requireContext(), AddPhotoActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            //finish()
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

            val clothesIdsArray = ArrayList<UUID>()
            clothesIdsArray.add(UUID.fromString(clothIdTop))
            clothesIdsArray.add(UUID.fromString(clothIdBottom))
            clothesIdsArray.add(UUID.fromString(clothIdOuter))
            clothesIdsArray.add(UUID.fromString(clothIdOnepiece))
            clothesIdsArray.add(UUID.fromString(clothIdShoes))
            clothesIdsArray.add(UUID.fromString(clothIdBag))

            Log.e("MyClosetFragment", clothesIdsArray.toString())

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
            // list들 모두 reset, -> 은 save 버튼 누를때마다 reset되니까 ㅇㅇ 상관없
            // 이미지 뷰들 모두 reset,
            clothTop.setImageResource(android.R.color.transparent)
            clothBottom.setImageResource(android.R.color.transparent)
            clothOuter.setImageResource(android.R.color.transparent)
            clothOnepiece.setImageResource(android.R.color.transparent)
            clothShoes.setImageResource(android.R.color.transparent)
            clothBag.setImageResource(android.R.color.transparent)
            // text 뷰들도 모두 reset,
            binding.textLookbookTop.text = ""
            binding.textLookbookBottom.text = ""
            binding.textLookbookOuter.text = ""
            binding.textLookbookOnepiece.text = ""
            binding.textLookbookShoes.text = ""
            binding.textLookbookBag.text = ""
            // comment도 reset
            binding.comment.setText("")
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

    private fun topLikeButton(view: View) {
        val topLikeButton = view as ImageButton
        // 이미지 토글
        if (ifTopLike) {
            // true였으면, 눌렸을 때 false가 되는거고, empty heart가 되는거지.
            topLikeButton.setImageResource(R.drawable.empty_heart)
        } else {
            topLikeButton.setImageResource(R.drawable.full_heart)
        }
        ifTopLike = !ifTopLike

        clickLikeButton(ifTopLike, topLikeButton, "상의")
    }


    private fun bottomLikeButton(view: View) {
        val bottomLikeButton = view as ImageButton
        // 이미지 토글
        if (ifBottomLike) {
            // true였으면, 눌렸을 때 false가 되는거고, empty heart가 되는거지.
            bottomLikeButton.setImageResource(R.drawable.empty_heart)
        } else {
            bottomLikeButton.setImageResource(R.drawable.full_heart)
        }
        ifBottomLike = !ifBottomLike

        clickLikeButton(ifBottomLike, bottomLikeButton, "하의")
    }
    
    private fun outerLikeButton(view: View) {
        val outerLikeButton = view as ImageButton
        // 이미지 토글
        if (ifOuterLike) {
            // true였으면, 눌렸을 때 false가 되는거고, empty heart가 되는거지.
            outerLikeButton.setImageResource(R.drawable.empty_heart)
        } else {
            outerLikeButton.setImageResource(R.drawable.full_heart)
        }
        ifOuterLike = !ifOuterLike

        clickLikeButton(ifOuterLike, outerLikeButton, "아우터")
    }

    private fun onepieceLikeButton(view: View) {
        val onepieceLikeButton = view as ImageButton
        // 이미지 토글
        if (ifOnepieceLike) {
            // true였으면, 눌렸을 때 false가 되는거고, empty heart가 되는거지.
            onepieceLikeButton.setImageResource(R.drawable.empty_heart)
        } else {
            onepieceLikeButton.setImageResource(R.drawable.full_heart)
        }
        ifOnepieceLike = !ifOnepieceLike

        clickLikeButton(ifOnepieceLike, onepieceLikeButton, "원피스")
    }

    private fun shoesLikeButton(view: View) {
        val shoesLikeButton = view as ImageButton
        // 이미지 토글
        if (ifShoesLike) {
            // true였으면, 눌렸을 때 false가 되는거고, empty heart가 되는거지.
            shoesLikeButton.setImageResource(R.drawable.empty_heart)
        } else {
            shoesLikeButton.setImageResource(R.drawable.full_heart)
        }
        ifShoesLike = !ifShoesLike

        clickLikeButton(ifShoesLike, shoesLikeButton, "신발")
    }

    private fun bagLikeButton(view: View) {
        val bagLikeButton = view as ImageButton
        // 이미지 토글
        if (ifBagLike) {
            // true였으면, 눌렸을 때 false가 되는거고, empty heart가 되는거지.
            bagLikeButton.setImageResource(R.drawable.empty_heart)
        } else {
            bagLikeButton.setImageResource(R.drawable.full_heart)
        }
        ifBagLike = !ifBagLike

        clickLikeButton(ifBagLike, bagLikeButton, "가방")
    }




    // 새로고침 로직
    override fun onRefresh() {

        // list 모두 클리어
        topList.clear()
        bottomList.clear()
        outerList.clear()
        onepieceList.clear()
        shoeList.clear()
        bagList.clear()

        // lookbook도 모두 클리어
        // 이미지 뷰들 모두 reset,
        clothTop.setImageResource(android.R.color.transparent)
        clothBottom.setImageResource(android.R.color.transparent)
        clothOuter.setImageResource(android.R.color.transparent)
        clothOnepiece.setImageResource(android.R.color.transparent)
        clothShoes.setImageResource(android.R.color.transparent)
        clothBag.setImageResource(android.R.color.transparent)
        // text 뷰들도 모두 reset,
        binding.textLookbookTop.text = ""
        binding.textLookbookBottom.text = ""
        binding.textLookbookOuter.text = ""
        binding.textLookbookOnepiece.text = ""
        binding.textLookbookShoes.text = ""
        binding.textLookbookBag.text = ""
        // comment도 reset
        binding.comment.setText("")

        initiation() // 모든 view들 정의
        getClothes() // 서버에서 모든 옷들 가져오기
        showOnlyLikes() // 하트 누르면 like된 애들만 빼오기
        // clickHeart() // 각 항목 하트 누르면 like-unlike 실행 -> 이건 adapter에서 함
        // goTrash() // 길게 누르면 trash 항목으로 이동 -> 이건 adapter에서 함
        // makeLookbook() // Make a New Lookbook 버튼 누르면 각 옷 선택됨 + 같은 카테고리의 다른 옷들 선택 못하게 됨 + lookbook에 사진 띄워짐 -> 이건 adapter에서 함
        addNewCloth() // +버튼 누르면 add하는 activity로 넘어감
        addNewCodi() // save 버튼 누르면 코디 저장됨
        swipeRefreshLayout.isRefreshing = false
    }

    companion object {}
}