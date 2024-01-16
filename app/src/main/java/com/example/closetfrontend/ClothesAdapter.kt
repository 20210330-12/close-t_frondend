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
//            val currentTopLikeImage = holder.myClosetLikeBtn.tag as? Int ?: R.drawable.empty_heart
//            val newTopLikeImage = if (currentTopLikeImage == R.drawable.empty_heart) {
//                R.drawable.full_heart
//            } else { R.drawable.empty_heart }
//
//            Log.e("ClothesAdapter", "current: $currentTopLikeImage")
//            Log.e("ClothesAdapter", "new: $newTopLikeImage")
//            Log.e("ClothesAdapter", "empty_heart: ${R.drawable.empty_heart}")
//            Log.e("ClothesAdapter", "full_heart: ${R.drawable.full_heart}")
//            holder.myClosetLikeBtn.setImageResource(newTopLikeImage)
//            holder.myClosetLikeBtn.tag = newTopLikeImage

            if (ifLike) {
                // true 였던 걸 누른 거니까 false가 되고, like는 none이 되어야 하는거지.
                // ifLike = false
                holder.myClosetLikeBtn.setImageResource(R.drawable.empty_heart)
            } else {
                // false였던걸 누른 거니까 true가 되고, 하트는 칠해져야 하는거지.
                // ifLike = true
                holder.myClosetLikeBtn.setImageResource(R.drawable.full_heart)
            }
            ifLike = !ifLike

            // 서버에 like 여부 저장
            // 이건 그냥 보내만 두면 -> like 되어있으면 해제해주고, 안 되어있으면 like해줌
            api.changeLike(userId, clothesList[position].id).enqueue(object:
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

//            notifyDataSetChanged()
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

            val byteArrayString = "iVBORw0KGgoAAAANSUhEUgAABDgAAAjoCAYAAAA5jhrUAAABBmlDQ1BJQ0MgUHJvZmlsZQAAeJxjYGCSYAACJgEGhty8kqIgdyeFiMgoBQYkkJhcXMCAGzAyMHy7BiIZGC7r4lGHC3CmpBYnA+kPQFxSBLQcaGQKkC2SDmFXgNhJEHYPiF0UEuQMZC8AsjXSkdhJSOzykoISIPsESH1yQRGIfQfItsnNKU1GuJuBJzUvNBhIRwCxDEMxQxCDO4MTGX7ACxDhmb+IgcHiKwMD8wSEWNJMBobtrQwMErcQYipAP/C3MDBsO1+QWJQIFmIBYqa0NAaGT8sZGHgjGRiELzAwcEVj2oGICxx+VQD71Z0hHwjTGXIYUoEingx5DMkMekCWEYMBgyGDGQBMpUCRBqmilgAAAAlwSFlzAAALEwAACxMBAJqcGAABAABJREFUeF7s3duCG7mStucvQFaV1D3LPvDYt+sL8LFv9PeMuiUVET4IBIBEZpIsSd2z1sz7qKtJ5gabAHIHJkkJAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"

            // Convert the string representation to an actual byte array
            val byteArray = Base64.decode(byteArrayString, Base64.DEFAULT)

            // Create a Bitmap from the byte array
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            // val bitmap = "android.graphics.Bitmap@ca6faba"
            // Now you can use 'bitmap' to display the image
            Log.d("bitmap 확인", bitmap.toString())
            myClosetPic.setImageBitmap(bitmap)

//            itemView.context.openFileInput(imageUrl).use { inputStream ->
//                val byteArray = inputStream.readBytes()
//                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//                myClosetPic.setImageBitmap(bitmap)
//            }

        }

    }
}