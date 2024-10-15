package com.az.elib.presentation.ui.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.az.elib.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.card.MaterialCardView

class AttachmentView(context : Context, attrs : AttributeSet?) : MaterialCardView(context, attrs) {

    private var textView : TextView
    private var btnRemoveFile : ImageView
    private var imAttachIcon : ImageView
    var onRemoveClickListener : RemoveButtonClickListener? = null
    private var isPreviewing : Boolean = false
    private lateinit var attachUri : Uri
    private var rootView : ViewGroup
    interface RemoveButtonClickListener{
        fun onRemoveButtonClickListener(position: Int, textView: TextView)
    }
    init {
        LayoutInflater.from(context).inflate(R.layout.attachment_item_layout, this, true)
        textView = findViewById(R.id.attachment_title)
        imAttachIcon = findViewById(R.id.im_attach_icon)
        btnRemoveFile = findViewById(R.id.btn_remove_file)
        rootView = findViewById(R.id.main)
        btnRemoveFile.setOnClickListener {
            onRemoveClickListener?.onRemoveButtonClickListener(getPosition(), textView)
        }
        rootView.setOnClickListener {

            if (isPreviewing) {
                Toast.makeText(context, "hahaha", Toast.LENGTH_SHORT).show()
                hidePreview()
            }else {
                Toast.makeText(context, "kaa", Toast.LENGTH_SHORT).show()
                showPreview()
            }
        }

        setMargins(0,0,0,0)
        setBackgroundColor(Color.TRANSPARENT)
    }


    fun setText(text : String){
        this.textView.text = text
        imAttachIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_image))
    }
    private fun showPreview() {
        attachUri?.let { uri ->
            val requestOptions = RequestOptions().override(MAX_WIDTH, MAX_HEIGHT)

            Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(requestOptions)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        imAttachIcon.setImageBitmap(resource)
                        isPreviewing = true
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Handle resource cleared (optional)
                    }
                })
        }
    }

    companion object {
        private const val MAX_WIDTH = 1000 // Set your desired maximum width here
        private const val MAX_HEIGHT = 1000 // Set your desired maximum height here
    }


    private fun hidePreview(){
        imAttachIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_image))
        this.isPreviewing = false
    }

    fun setFileUri(uri:Uri){
        this.attachUri = uri
    }

    private fun getPosition(): Int {
        val parent = parent as? ViewGroup
        return parent?.indexOfChild(this) ?: -1
    }

    private fun setMargins(left: Int, top: Int, right: Int, bottom: Int) {
        val layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(left, top, right, bottom)
        this.layoutParams = layoutParams
    }


}