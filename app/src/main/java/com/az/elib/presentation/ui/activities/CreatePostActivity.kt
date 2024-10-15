package com.az.elib.presentation.ui.activities

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.az.elib.R
import com.az.elib.databinding.FragmentCreatePostBinding
import com.az.elib.presentation.viewmodels.ViewModelCreatePost
import com.az.elib.presentation.ui.custom.AttachmentView
import com.az.elib.presentation.ui.dialogs.LoadingBarDialog
import com.az.elib.presentation.ui.dialogs.PostSelectImageFragment
import com.az.elib.util.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CreatePostActivity : BaseActivity(),
    AttachmentView.RemoveButtonClickListener {
    private lateinit var binding: FragmentCreatePostBinding
    private val viewModelCreatePost: ViewModelCreatePost by viewModels()
    private lateinit var popupMenu: PopupMenu
    private val loadingBarDialog: LoadingBarDialog by lazy { LoadingBarDialog(this@CreatePostActivity) }
    private val customSnackBar: CustomSnackBar by lazy {
        CustomSnackBar(binding.main, this@CreatePostActivity)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
        } else {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        binding = FragmentCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSystemBarsColors(
            ContextCompat.getColor(
                this@CreatePostActivity,
                R.color.colorPrimaryDark
            )
        )
        setUiFunc()
        setViewModelObservers()
        observeEditText()
    }

    private fun observeEditText() {
        binding.apply {
            etPostText.addTextChangedListener { text ->
                btnSubmitPost.isEnabled = text.toString().length >= 10
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                this@CreatePostActivity,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val postSelectImageFragment = PostSelectImageFragment()
            postSelectImageFragment.show(supportFragmentManager, postSelectImageFragment.tag)
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun setUiFunc() {
        binding.apply {
            btnAddAttachment.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this@CreatePostActivity, Manifest.permission.READ_MEDIA_IMAGES)) {
                        Toast.makeText(
                            this@CreatePostActivity,
                            "Permission permanently denied, please grant access to it from app settings",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this@CreatePostActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Toast.makeText(
                            this@CreatePostActivity,
                            "Permission permanently denied, please grant access to it from app settings",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                checkAndRequestPermissions()
            }
            btnAddTag.setOnClickListener {
                showTagsMenu()
            }
            btnAddChannel.setOnClickListener {
                showChannelsMenu()
            }
            btnSubmitPost.setOnClickListener {
                loadingBarDialog.showLoadingDialog()
                viewModelCreatePost.createPostAndNotification(etPostText)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                btnAddChannel.setOnApplyWindowInsetsListener { view, insets ->
                    val imeVisible = insets.isVisible(WindowInsets.Type.ime())
                    val imeInsets = insets.getInsets(WindowInsets.Type.ime())
                    if (imeVisible) {
                        view.translationY = -imeInsets.bottom.toFloat()
                    } else {
                        view.translationY = 0f
                    }
                    insets
                }
            }
        }
    }

    private fun setViewModelObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModelCreatePost.attachList.collect { attachHashMap ->
                        binding.btnSubmitPost.isEnabled = attachHashMap.isNotEmpty()
                        updatePickedFilesHv(attachHashMap.toList())
                    }
                }
                launch {
                    viewModelCreatePost.postCreatingResult.collect { result ->
                        if (result == null) {
                            return@collect
                        }
                        loadingBarDialog.hideLoadingDialog()
                        viewModelCreatePost.attachList.value.clear()
                        if (!result.isSuccess) {
                            customSnackBar.launchSnackBar("${result.exceptionOrNull()}", true)
                        }
                    }
                }
                launch {
                    viewModelCreatePost.sendNotificationResult.collect { result ->
                        if (result == null) {
                            return@collect
                        }
                        if (result.isSuccess) {
                            finish()
                        }
                    }
                }

            }
        }
    }

    private fun showTagsMenu() {
        popupMenu = PopupMenu(this@CreatePostActivity, binding.btnAddTag)
        popupMenu.menuInflater.inflate(R.menu.menu_tags, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            binding.tvSelectedTag.text = item.title.toString()
            viewModelCreatePost.setTag(item.title.toString())
            true
        }
        popupMenu.show()
    }

    private fun showChannelsMenu() {
        popupMenu = PopupMenu(this@CreatePostActivity, binding.btnAddChannel)
        popupMenu.menuInflater.inflate(R.menu.channels_tags, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            binding.tvSelectedChannel.text = item.title.toString()
            viewModelCreatePost.setChannel(item.title.toString())
            true
        }
        popupMenu.show()
    }

    private fun updatePickedFilesHv(attachList: List<Pair<String, Uri>>) {
        binding.hsvAttachmentsSv.removeAllViews()
        for (attach in attachList) {
            val attachmentView = AttachmentView(this@CreatePostActivity, null)
            attachmentView.onRemoveClickListener = this
            attachmentView.setText(attach.first)
            attachmentView.setFileUri(attach.second)
            binding.hsvAttachmentsSv.addView(attachmentView)
        }
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onRemoveButtonClickListener(position: Int, textView: TextView) {
        binding.hsvAttachmentsSv.removeViewAt(position)
        viewModelCreatePost.removeFromAttachList(textView.text.toString())
    }

}