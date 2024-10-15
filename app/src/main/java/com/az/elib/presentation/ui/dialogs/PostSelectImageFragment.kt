package com.az.elib.presentation.ui.dialogs

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.az.elib.databinding.FragmentPostSelectImageBinding
import com.az.elib.domain.interfaces.OnBrowseImagesClicked
import com.az.elib.domain.interfaces.OnImageSelectedListener
import com.az.elib.presentation.adapters.ImageSelectAdapter
import com.az.elib.presentation.viewmodels.ViewModelCreatePost
import com.az.elib.util.FileUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PostSelectImageFragment : BottomSheetDialogFragment(), OnImageSelectedListener,
    OnBrowseImagesClicked {
    private lateinit var binding: FragmentPostSelectImageBinding
    private val viewModelCreatePost: ViewModelCreatePost by activityViewModels()
    private var selectedImages = mutableListOf<Uri>()
    private lateinit var imagesFromDevice: List<Uri>
    private var canStillSelect = true
    private lateinit var imageSelectAdapter: ImageSelectAdapter


    private var filePickerLauncher: ActivityResultLauncher<Intent>? =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val uri: Uri? = data?.data
                if (uri != null) {
                    val actualImage = FileUtil.from(requireContext(), data.data)
                    // compress selected file
                    viewModelCreatePost.compressAndAddFile(requireContext(), actualImage)
                }
            }
        }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setupImagesSelectionRv()
        }else{

        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
        fun newInstance() = PostSelectImageFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostSelectImageBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUIFun()
        setupImagesSelectionRv()

    }

    private fun setUIFun() {
        binding.btnDone.setOnClickListener {
            for (image in selectedImages) {
                val imageFile = FileUtil.from(requireContext(), image)
                viewModelCreatePost.compressAndAddFile(requireContext(), imageFile)
            }
            this@PostSelectImageFragment.dismiss()
        }
    }

    private fun setupImagesSelectionRv() {
        binding.rvImages.layoutManager = GridLayoutManager(context, 3)
        imagesFromDevice = loadImagesFromStorage()
        imageSelectAdapter = ImageSelectAdapter(requireContext(), imagesFromDevice)
        imageSelectAdapter.setImageSelectedListener(this@PostSelectImageFragment)
        imageSelectAdapter.setOnBrowseImagesClickedListener(this@PostSelectImageFragment)
        binding.rvImages.adapter = imageSelectAdapter
    }

    override fun onImageSelected(imageUri: Uri) {
        if (canStillSelect) {
            this@PostSelectImageFragment.selectedImages.add(imageUri)
            updateSize()
        }
    }

    override fun onImageUnselected(imageUri: Uri) {
        this@PostSelectImageFragment.selectedImages.remove(imageUri)
        updateSize()
    }

    private fun updateSize() {
        var size = 0L
        selectedImages.forEach { uri ->
            val fileDescriptor = requireContext().contentResolver.openFileDescriptor(uri, "r")
            if (fileDescriptor != null) {
                size += fileDescriptor.statSize
                fileDescriptor.close()
            }
        }
        if (size > 15000000) {
            Toast.makeText(requireContext(), "Maximum size of images is 15MB", Toast.LENGTH_SHORT)
                .show()
            canStillSelect = false
            imageSelectAdapter.updateSelectionAbility(false)
        } else {
            Toast.makeText(requireContext(), "$size", Toast.LENGTH_SHORT).show()

            canStillSelect = true
            imageSelectAdapter.updateSelectionAbility(true)
        }
    }

    private fun loadImagesFromStorage(): List<Uri> {
        val images = mutableListOf<Uri>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        context?.contentResolver?.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                images.add(contentUri)
            }
        }
        return images
    }

    override fun onBrowseImagesClicked() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher?.launch(intent)
    }

}