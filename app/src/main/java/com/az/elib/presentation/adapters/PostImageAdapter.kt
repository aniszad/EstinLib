package com.az.estinlib.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.az.elib.databinding.LayoutAttachmentImageBinding
import com.az.elib.presentation.util.OnDoubleClickListener
import com.bumptech.glide.Glide

class PostImageAdapter(private val context: Context, private val imageUrls: List<String>) : RecyclerView.Adapter<PostImageAdapter.ImageViewHolder>() {

    private var onDoubleClickListener : OnDoubleClickListener? = null
    inner class ImageViewHolder(private val binding: LayoutAttachmentImageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            binding.imAttachment.setOnClickListener(this@PostImageAdapter.onDoubleClickListener)
            Glide.with(context)
                .load(imageUrl)
                .into(binding.imAttachment)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = LayoutAttachmentImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount(): Int = imageUrls.size

    fun setOnDoubleClickListenerListener(listener: OnDoubleClickListener) {
        onDoubleClickListener = listener
    }

}