package com.az.elib.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.az.elib.databinding.EstinGalleryItemLayoutBinding


class EstinGalleryRvAdapter (private val imagesList : List<Int>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    inner class ImageViewHolder(private val binding : EstinGalleryItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(imageId : Int){
            binding.imEstinGallery.setImageResource(imageId)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageViewHolder(EstinGalleryItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageViewHolder) holder.bind(imagesList[position])
    }

    fun getSize(): Int {
        return imagesList.size
    }


}