package com.az.elib.presentation.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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

    private val pickMultipleMedia = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModelCreatePost.addFiles(this, uris)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        customizeSystemBars(binding.main.id, ContextCompat.getColor(this, R.color.colorPrimaryDark))
        setNavigationBarColor(ContextCompat.getColor(this, R.color.colorSecondaryDark))
        setStatusBarLight(false)
        
        setUiFunc()
        setViewModelObservers()
        observeEditText()
    }

    private fun observeEditText() {
        binding.apply {
            etPostText.addTextChangedListener {
                updateSubmitButtonState()
            }
        }
    }

    private fun updateSubmitButtonState() {
        val hasText = binding.etPostText.text?.toString()?.trim()?.let { it.length >= 10 } ?: false
        val hasAttachments = viewModelCreatePost.attachList.value.isNotEmpty()
        binding.btnSubmitPost.isEnabled = hasText || hasAttachments
    }

    private fun setUiFunc() {
        binding.apply {
            btnClose.setOnClickListener {
                finish()
            }
            btnAddAttachment.setOnClickListener {
                pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            btnAddTag.setOnClickListener {
                showTagsMenu()
            }
            btnAddChannel.setOnClickListener {
                showChannelsMenu()
            }
            btnSubmitPost.setOnClickListener {
                val content = etPostText.text.toString()
                if (content.isNotBlank() || viewModelCreatePost.attachList.value.isNotEmpty()) {
                    loadingBarDialog.showLoadingDialog()
                    viewModelCreatePost.createPostAndNotification(content)
                } else {
                    customSnackBar.launchSnackBar("Post cannot be empty", true)
                }
            }
        }
    }

    private fun setViewModelObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModelCreatePost.attachList.collect { attachList ->
                        updateSubmitButtonState()
                        updatePickedFilesHv(attachList)
                    }
                }
                launch {
                    viewModelCreatePost.postCreatingResult.collect { result ->
                        if (result == null) return@collect
                        loadingBarDialog.hideLoadingDialog()
                        if (result.isSuccess) {
                            viewModelCreatePost.clearData()
                            binding.etPostText.text?.clear()
                        } else {
                            customSnackBar.launchSnackBar("${result.exceptionOrNull()?.message}", true)
                        }
                        viewModelCreatePost.clearPostCreatingResult()
                    }
                }
                launch {
                    viewModelCreatePost.sendNotificationResult.collect { result ->
                        if (result == null) return@collect
                        if (result.isSuccess) {
                            finish()
                        }
                        viewModelCreatePost.clearSendNotificationResult()
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
        attachList.forEach { attach ->
            val attachmentView = AttachmentView(this@CreatePostActivity, null).apply {
                onRemoveClickListener = this@CreatePostActivity
                setText(attach.first)
                setFileUri(attach.second)
            }
            binding.hsvAttachmentsSv.addView(attachmentView)
        }
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onRemoveButtonClickListener(position: Int, textView: TextView) {
        viewModelCreatePost.removeFromAttachList(textView.text.toString())
    }

}