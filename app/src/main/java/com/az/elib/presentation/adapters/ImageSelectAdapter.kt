package com.az.elib.presentation.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.az.elib.R
import com.az.elib.databinding.BrowsePicturesItemLayoutBinding
import com.az.elib.databinding.ImageSelectionLayoutBinding
import com.az.elib.domain.interfaces.OnBrowseImagesClicked
import com.az.elib.domain.interfaces.OnImageSelectedListener
import com.bumptech.glide.Glide

class ImageSelectAdapter(private val context: Context, private var imageUris: List<Uri>) :
    RecyclerView.Adapter<ViewHolder>() {
    private lateinit var onImageSelectedListener: OnImageSelectedListener
    private lateinit var onBrowseImagesClicked: OnBrowseImagesClicked
    private var canStillSelect = true
    private var selectedImages: List<Uri> = listOf()

    companion object {
        const val BROWSE_BUTTON_VIEW_TYPE = 0
        const val IMAGE_VIEW_TYPE = 1
    }

    inner class ImageBrowseViewHolder(private val binding: BrowsePicturesItemLayoutBinding) :
        ViewHolder(binding.root) {
        fun bind() {
            binding.main.setOnClickListener {
                onBrowseImagesClicked.onBrowseImagesClicked()
            }
        }
    }

    inner class ImageSelectionViewHolder(private val binding: ImageSelectionLayoutBinding) :
        ViewHolder(binding.root) {

        fun bind(imageUri: Uri) {
            Glide.with(context)
                .load(imageUri)
                .into(binding.imGallery)

            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels - 20
            val itemWidth = screenWidth / 3

            // Set the item's width and height
            binding.cardViewMain.layoutParams.width = itemWidth
            binding.cardViewMain.layoutParams.height = itemWidth
            if (imageUri !in selectedImages) {
                binding.cardViewMain.strokeColor =
                    ContextCompat.getColor(context, android.R.color.transparent)
            } else {
                binding.cardViewMain.strokeColor =
                    ContextCompat.getColor(context, R.color.colorGreen)
            }
            binding.cardViewMain.setOnClickListener {
                if (imageUri in selectedImages) {
                    selectedImages = selectedImages.filter { it != imageUri }
                    binding.cardViewMain.strokeColor =
                        ContextCompat.getColor(context, android.R.color.transparent)
                    this@ImageSelectAdapter.onImageSelectedListener.onImageUnselected(imageUri)
                } else {
                    if (!canStillSelect) return@setOnClickListener
                    selectedImages = selectedImages + imageUri
                    binding.cardViewMain.strokeColor =
                        ContextCompat.getColor(context, R.color.colorGreen)
                    this@ImageSelectAdapter.onImageSelectedListener.onImageSelected(imageUri)
                }
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            BROWSE_BUTTON_VIEW_TYPE
        } else {
            IMAGE_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            BROWSE_BUTTON_VIEW_TYPE -> {
                ImageBrowseViewHolder(
                    BrowsePicturesItemLayoutBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }

            IMAGE_VIEW_TYPE -> {
                val binding = ImageSelectionLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ImageSelectionViewHolder(binding)
            }

            else -> {
                val binding = ImageSelectionLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ImageSelectionViewHolder(binding)
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ImageSelectionViewHolder) {
            holder.bind(imageUris[position])
        } else if (holder is ImageBrowseViewHolder) {
            holder.bind()
        }
    }


    override fun getItemCount(): Int = imageUris.size

    fun setImageSelectedListener(onImageSelectedListener: OnImageSelectedListener) {
        this@ImageSelectAdapter.onImageSelectedListener = onImageSelectedListener
    }

    fun setOnBrowseImagesClickedListener(onBrowseImagesClicked: OnBrowseImagesClicked) {
        this.onBrowseImagesClicked = onBrowseImagesClicked
    }

    fun updateSelectionAbility(canStillSelect: Boolean) {
        this@ImageSelectAdapter.canStillSelect = canStillSelect
    }

    fun clearData() {
        imageUris = emptyList()
    }


}