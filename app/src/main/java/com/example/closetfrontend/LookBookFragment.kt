package com.example.closetfrontend

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LookBookFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LookBookFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var recyclerView: RecyclerView
    private lateinit var lookBookAdapter: LookBookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_look_book, container, false)

        recyclerView = view.findViewById(R.id.idLookBooks)
        recyclerView.layoutManager = GridLayoutManager(context, 3)

        val lookBookDataList: List<MyLookBookDataModel>

        lookBookAdapter = LookBookAdapter(lookBookDataList)
        recyclerView.adapter = lookBookAdapter

        return view
    }

    public fun showDetailViewPopup() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.activity_look_book_detail_view)

        val imageView = dialog.findViewById<ImageView>(R.id.idDetailImage)
        val heartIcon = dialog.findViewById<ImageView>(R.id.idHeartIcon)
        val commentText = dialog.findViewById<TextView>(R.id.idCommentText)
        val hashtagsContainer = dialog.findViewById<LinearLayout>(R.id.idHashTagContainer)

        dialog.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LookBookFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LookBookFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}