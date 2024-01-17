package com.example.closetfrontend

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.example.closetfrontend.databinding.ActivityWishItemViewBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.selects.select

fun showWishItemViewActivity(context: Context, selectedItem: Clothes) {
    // AlertDialog 생성
    val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
    val bindingPopup = ActivityWishItemViewBinding.inflate(LayoutInflater.from(context))
    val view: View = bindingPopup.layoutPopup

    builder.setView(view)
    val alertDialog: AlertDialog = builder.create()

    view.findViewById<ImageButton>(R.id.popupCloseBtn).setOnClickListener {
        alertDialog.dismiss()
    }

    if (selectedItem.like == "Like") {
        view.findViewById<ImageView>(R.id.popupLike).setImageResource(R.drawable.full_heart)
    }

    view.findViewById<TextView>(R.id.popupLink).text = selectedItem.link.toString()
    Picasso.get().load("http://172.10.7.44:80/images/${selectedItem.imageUrl}").into(view.findViewById<ImageView>(R.id.popupCircle))

    val link: TextView = view.findViewById<TextView>(R.id.popupLink)
    link.setOnClickListener {
        val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("${selectedItem.link.toString()}"))
        context.startActivity(intent)
    }

    // AlertDialog 보이기
    alertDialog.show()
}