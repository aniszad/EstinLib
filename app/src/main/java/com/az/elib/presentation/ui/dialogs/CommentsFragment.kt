package com.az.elib.presentation.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.az.elib.R
import com.az.elib.databinding.FragmentCommentsBinding
import com.az.elib.domain.interfaces.OnDeleteCommentClickListener
import com.az.elib.domain.models.Comment
import com.az.elib.domain.models.CommentsBatchState
import com.az.elib.domain.models.CommentsUpdateType
import com.az.elib.presentation.adapters.CommentsAdapter
import com.az.elib.presentation.viewmodels.ViewModelComments
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class CommentsFragment : BottomSheetDialogFragment(), OnDeleteCommentClickListener {

    private lateinit var binding: FragmentCommentsBinding
    private val viewModelComments: ViewModelComments by activityViewModels()
    private var commentsAdapter: CommentsAdapter? = null
    private lateinit var postId: String
    private lateinit var userId: String

    private var commentsJob: Job? = null

    private val confirmDialog: ConfirmDialog by lazy {
        ConfirmDialog(requireContext())
    }

    companion object {
        private const val ARG_POST_ID = "postId"
        private const val ARG_USER_ID = "userId"

        fun newInstance(postId: String, userId: String): CommentsFragment {
            return CommentsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_POST_ID, postId)
                    putString(ARG_USER_ID, userId)
                }
            }
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {
            dialog.window?.decorView?.background = ColorDrawable(Color.TRANSPARENT)
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { view ->
                val behaviour = BottomSheetBehavior.from(view)
                behaviour.isDraggable = false
            }
        }

        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postId = it.getString(ARG_POST_ID).toString()
            userId = it.getString(ARG_USER_ID).toString()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCommentsBinding.inflate(inflater, container, false)

        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUiFunc()
        setObservables()
        getComments()
    }

    private fun setUiFunc() {
        with(binding) {
            etComment.addTextChangedListener { editable ->
                if (editable.toString().isNotBlank()) {
                    with(tilComment) {
                        endIconDrawable =
                            (ContextCompat.getDrawable(requireContext(), R.drawable.ic_send))
                        endIconMode = TextInputLayout.END_ICON_CUSTOM
                        setEndIconOnClickListener {
                            isEndIconVisible = false
                            val comment = etComment.text.toString()
                            if (comment.isNotBlank()) viewModelComments.addComment(comment)
                        }
                    }
                } else {
                    tilComment.endIconDrawable = null
                    tilComment.setEndIconOnClickListener(null)
                }
            }

        }
    }

    private fun dismissKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun setObservables() {
        commentsJob = viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModelComments.commentsBatch.collect { commentBatchState ->
                        Log.e("CommentsBatchState", commentBatchState.toString())
                        when (commentBatchState) {
                            is CommentsBatchState.Loading -> {
                                setCommentsRv(mutableListOf())
                            }

                            is CommentsBatchState.Success -> {
                                when (commentBatchState.updateType) {
                                    is CommentsUpdateType.Initialized -> {
                                        setCommentsRv(commentBatchState.comments.toMutableList())
                                    }

                                    is CommentsUpdateType.NextBatch -> {
                                        commentsAdapter?.addNextBatch(commentBatchState.comments)
                                    }

                                    is CommentsUpdateType.Added -> {
                                        commentsAdapter?.addComment(commentBatchState.comments)
                                        binding.rvComments.smoothScrollToPosition(0)
                                        binding.etComment.text.clear()
                                        binding.etComment.clearFocus()
                                        binding.tilComment.isEndIconVisible = true
                                        dismissKeyboard()

                                    }

                                    is CommentsUpdateType.Deleted -> {
                                        commentsAdapter?.deleteComment(commentBatchState.comments)
                                        binding.etComment.text.clear()
                                        binding.etComment.clearFocus()
                                        dismissKeyboard()
                                    }
                                }
                            }

                            is CommentsBatchState.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Something went wrong!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getComments() {
        if (::postId.isInitialized) {
            if (postId == viewModelComments.currentCommentsPostId.value) {
                setCommentsRv(
                    (viewModelComments.commentsBatch.value as? CommentsBatchState.Success)?.comments?.toMutableList()
                        ?: mutableListOf()
                )
            } else {
                viewModelComments.getNextCommentsBatch()
            }
        }
        viewModelComments.setPostIdForComments(postId)
    }

    private fun setCommentsRv(comments: MutableList<Comment>) {
        if (commentsAdapter == null && ::userId.isInitialized) {
            commentsAdapter = CommentsAdapter(comments, userId)
            commentsAdapter!!.setOnCommentDeleteListener(this@CommentsFragment)
            with(binding) {
                rvComments.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                rvComments.adapter = commentsAdapter
            }
        } else {
            commentsAdapter?.initialize(comments)
        }
    }

    override fun onDeleteCommentClickListener(comment: Comment) {
        confirmDialog.showConfirmDialog(
            "Are you sure you want to delete your comment?",
            R.drawable.ic_delete
        ) { viewModelComments.deleteComment(postId, comment) }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        commentsJob?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        commentsJob?.cancel()
    }
}