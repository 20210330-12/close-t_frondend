package com.example.closetfrontend

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClothesAdapter (private val myClosetFragment: MyClosetFragment, private var clothesList: ArrayList<Clothes>) :
        RecyclerView.Adapter<ClothesAdapter.clothesViewHolder>() {

    // 서버에서 불러오기
    val api = RetrofitInterface.create()
    // userId 불러오기
    private lateinit var userId: String
    // 이미지 url 저장하는 애
    private var imageUrl = ""

    // like 여부 저장하는 boolean
    private var ifLike = false

    // myClosetFragment의 binding 불러오기
    private val bindingMyCloset = myClosetFragment.binding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesAdapter.clothesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_closet_recyclerview, parent, false)
        return clothesViewHolder(view)
    }

    //@SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ClothesAdapter.clothesViewHolder, position: Int) {
        holder.bind(clothesList[position])
        val sharedPref = holder.itemView.context.getSharedPreferences("userId", Context.MODE_PRIVATE)
        userId = sharedPref.getString("userId", "")!!

//        Log.e("ClothesAdapter", "${bindingMyCloset == null}")

        // 사진을 길게 누르는 것만 ㄱㄱ하고 짧게 누르는 건 막아두기
        // 이 코드에서 ACTION_DOWN은 뷰를 누르는 순간을 나타내고,
        // ACTION_UP 및 ACTION_CANCEL은 뷰에서 손을 뗀 순간을 나타냅니다.
        // 따라서 길게 누르는 경우 ACTION_DOWN에서 isClickable을 true로 설정하고,
        // 짧게 누르거나 손을 뗀 경우 ACTION_UP 또는 ACTION_CANCEL에서 isClickable을 false로 설정합니다.
//        holder.myClosetPic.setOnTouchListener { view, motionEvent ->
//            when (motionEvent.action) {
//                MotionEvent.ACTION_DOWN -> { view.isClickable = true }
//                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { view.isClickable = false }
//            }
//            true
//            // 길게 누르는 경우
//        }



        // 하트 버튼 누르면 like tag 추가 / 삭제
        holder.myClosetLikeBtn.setOnClickListener {
//            if (ifLike) {
//                // true 였던 걸 누른 거니까 false가 되고, like는 none이 되어야 하는거지.
//                // ifLike = false
//                clothesList[position].like = "None"
//                holder.myClosetLikeBtn.setImageResource(R.drawable.empty_heart)
//            } else {
//                // false였던걸 누른 거니까 true가 되고, 하트는 칠해져야 하는거지.
//                // ifLike = true
//                clothesList[position].like = "Like"
//                holder.myClosetLikeBtn.setImageResource(R.drawable.full_heart)
//            }
//            ifLike = !ifLike
//            notifyItemChanged(position)

            if (clothesList[position].like.contains("Like")) {
                // 이때는 이제 none이 되는거니까
                clothesList[position].like = "None"
                holder.myClosetLikeBtn.setImageResource(R.drawable.empty_heart)
            } else {
                // false였던걸 누른 거니까 true가 되고, 하트는 칠해져야 하는거지.
                clothesList[position].like = "Like"
                holder.myClosetLikeBtn.setImageResource(R.drawable.full_heart)
            }
            notifyItemChanged(position)

            // 서버에 like 여부 저장
            // 이건 그냥 보내만 두면 -> like 되어있으면 해제해주고, 안 되어있으면 like해줌
            api.changeLike(userId, clothesList[position].id).enqueue(object:
                Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.e(ContentValues.TAG, "result: $result")
//                        if (result?.get("result")?.toString() == "Like has been added") {
//                            holder.myClosetLikeBtn.setImageResource(R.drawable.full_heart)
//                            clothesList[position].like = "Like"
//                        } else if (result?.get("result")?.toString() == "Like has been removed") {
//                            holder.myClosetLikeBtn.setImageResource(R.drawable.empty_heart)
//                            clothesList[position].like = "None"
//                        }
                        notifyItemChanged(position)
                    } else {
                        // HTTP 요청이 실패한 경우의 처리
                        Log.e(ContentValues.TAG, "HTTP 요청 실패: ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e(ContentValues.TAG, "네트워크 오류: ${t.message}")
                }
            })

            //notifyDataSetChanged()
        }
        
        
        
        // 길게 누르면 trash tag 추가 및 보이는 곳에서 삭제
        holder.myClosetPic.setOnLongClickListener {
            
            // trash된다는 이미지를 0.5초간 보여주기
            holder.myClosetPic.setImageResource(R.drawable.go_trash)
            
            // Trash tag 붙이기
            api.changeTrash(userId, clothesList[position].id).enqueue(object:
                Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.e(ContentValues.TAG, "result: $result")
                    } else {
                        // HTTP 요청이 실패한 경우의 처리
                        Log.e(ContentValues.TAG, "HTTP 요청 실패: ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e(ContentValues.TAG, "네트워크 오류: ${t.message}")
                }
            })
            
            // 0.5초 뒤에 list에서 완전히 제거함
            Handler(Looper.getMainLooper()).postDelayed({
                removeMyClosetItem(position)
                notifyDataSetChanged()
                true
            }, 500)
        }



        // 그리고, 각 버튼을 누르면 그 이미지가 Lookbook에 나타나야함
        holder.myClosetPic.setOnClickListener {
//            val clothImage = clothesList[position].imageUrl // 해당 이미지
            imageUrl = clothesList[position].imageUrl
            val clothTag = clothesList[position].category // 해당 이미지 카테고리
            val clothId = clothesList[position].id // 해당 이미지 id

            Log.e("imageURL", "$imageUrl")
//            Picasso.get().load(clothImage)
//                .placeholder(R.drawable.full_heart)
//                .error(R.drawable.empty_heart) // 에러 발생 시 로딩될 이미지
//                .into(bindingMyCloset.lookbookTop)

            when (clothTag) {
                "상의" -> {
                    // displayProcessedImage(bindingMyCloset.lookbookTop, clothImage)
//                    Picasso.get().load(clothImage)
//                        .placeholder(R.drawable.full_heart)
//                        .error(R.drawable.empty_heart) // 에러 발생 시 로딩될 이미지
//                        .into(bindingMyCloset.lookbookTop)
                    Glide.with(myClosetFragment)
                        .load(imageUrl)
                        .into(bindingMyCloset.lookbookTop)
                    bindingMyCloset.textLookbookTop.text = clothId
                }
                "하의" -> {
                    // displayProcessedImage(bindingMyCloset.lookbookBottom, clothImage)
//                    Picasso.get().load(clothImage)
//                        .placeholder(R.drawable.full_heart)
//                        .error(R.drawable.empty_heart) // 에러 발생 시 로딩될 이미지
//                        .into(bindingMyCloset.lookbookBottom)
                    Glide.with(myClosetFragment)
                        .load(imageUrl)
                        .into(bindingMyCloset.lookbookBottom)
                    bindingMyCloset.textLookbookBottom.text = clothId
                }
                "아우터" -> {
                    // displayProcessedImage(bindingMyCloset.lookbookOuter, clothImage)
//                    Picasso.get().load(clothImage)
//                        .placeholder(R.drawable.full_heart)
//                        .error(R.drawable.empty_heart) // 에러 발생 시 로딩될 이미지
//                        .into(bindingMyCloset.lookbookOuter)
                    Glide.with(myClosetFragment)
                        .load(imageUrl)
                        .into(bindingMyCloset.lookbookOuter)
                    bindingMyCloset.textLookbookOuter.text = clothId
                }
                "원피스" -> {
                    // displayProcessedImage(bindingMyCloset.lookbookOnepiece, clothImage)
//                    Picasso.get().load(clothImage)
//                        .placeholder(R.drawable.full_heart)
//                        .error(R.drawable.empty_heart) // 에러 발생 시 로딩될 이미지
//                        .into(bindingMyCloset.lookbookOnepiece)
                    Glide.with(myClosetFragment)
                        .load(imageUrl)
                        .into(bindingMyCloset.lookbookOnepiece)
                    bindingMyCloset.textLookbookOnepiece.text = clothId
                }
                "신발" -> {
                    // displayProcessedImage(bindingMyCloset.lookbookShoes, clothImage)
//                    Picasso.get().load(clothImage)
//                        .placeholder(R.drawable.full_heart)
//                        .error(R.drawable.empty_heart) // 에러 발생 시 로딩될 이미지
//                        .into(bindingMyCloset.lookbookShoes)
                    Glide.with(myClosetFragment)
                        .load(imageUrl)
                        .into(bindingMyCloset.lookbookShoes)
                    bindingMyCloset.textLookbookShoes.text = clothId
                }
                "가방" -> {
                    // displayProcessedImage(bindingMyCloset.lookbookBag, clothImage)
//                    Picasso.get().load(clothImage)
//                        .placeholder(R.drawable.full_heart)
//                        .error(R.drawable.empty_heart) // 에러 발생 시 로딩될 이미지
//                        .into(bindingMyCloset.lookbookBag)
                    Glide.with(myClosetFragment)
                        .load(imageUrl)
                        .into(bindingMyCloset.lookbookBag)
                    bindingMyCloset.textLookbookBag.text = clothId
                }
            }
        }
        
        // 상세정보 뜨는건 할 필요가 없지 않을까

    }

    override fun getItemCount(): Int {
        return clothesList.size
    }

    private fun removeMyClosetItem(position: Int) {
        clothesList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }


    inner class clothesViewHolder(myClosetItemView: View) : RecyclerView.ViewHolder(myClosetItemView) {
        val myClosetLikeBtn = myClosetItemView.findViewById<ImageButton>(R.id.myClosetlikeBtn)
        val myClosetPic = myClosetItemView.findViewById<ImageView>(R.id.myClosetPic)

        fun bind(item: Clothes) {
            
            // tag에 like가 있으면 좋아요 눌러놓기
            if(item.like.contains("Like")) {
                ifLike = true
                myClosetLikeBtn.setImageResource(R.drawable.full_heart)
            } else {
                ifLike = false
                myClosetLikeBtn.setImageResource(R.drawable.empty_heart)
            }


            // 이미지 URL이 있는지 확인하고 Picasso를 사용하여 ImageView에 로드합니다.
            val imageUrl = item.imageUrl // 실제 이미지 URL을 저장하는 myClosetItem 클래스의 필드로 교체하세요.
            //Log.e("imageURL", "$imageUrl")
//            Picasso.get().load(imageUrl)
//                .placeholder(R.drawable.full_heart)
//                .error(R.drawable.empty_heart) // 에러 발생 시 로딩될 이미지
//                .into(myClosetPic)
//            Glide.with(itemView.context)
//                .load(imageUrl)
//                .into(myClosetPic)

            val byteArrayString = "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"
            // Convert the string representation to an actual byte array
            val byteArray = Base64.decode(byteArrayString, Base64.DEFAULT)
            Log.e("byteArray가 null이냐?", "${byteArray == null}")

            // Create a Bitmap from the byte array
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            // val bitmap = "android.graphics.Bitmap@ca6faba"
            // Now you can use 'bitmap' to display the image
            Log.d("bitmap 확인", bitmap.toString())
            myClosetPic.setImageBitmap(bitmap)
//            Picasso.get()
//                .load(bitmap)
//                .into(myClosetPic)
            Glide.with(itemView.context)
                .load(bitmap)
                .into(myClosetPic)

//            itemView.context.openFileInput(imageUrl).use { inputStream ->
//                val byteArray = inputStream.readBytes()
//                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//                myClosetPic.setImageBitmap(bitmap)
//            }

        }

    }
}