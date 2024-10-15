package com.az.elib.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.az.elib.databinding.LayoutCommentItemViewBinding
import com.az.elib.databinding.LayoutNoDataItemViewBinding
import com.az.elib.domain.interfaces.OnDeleteCommentClickListener
import com.az.elib.domain.models.Comment
import com.az.elib.util.TimeFormatter


class CommentsAdapter(
    private var commentsList: MutableList<Comment>,
    private val currentUserId : String) :
    RecyclerView.Adapter<ViewHolder>() {
    private var onDeleteCommentClickListener: OnDeleteCommentClickListener? = null
    companion object {
        private const val EMPTY_COMMENTS = 0
        private const val NON_EMPTY_COMMENTS = 1
    }

    inner class CommentViewHolder(private val binding: LayoutCommentItemViewBinding) :
        ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            with(binding){
                tvCommentOwnerFullName.text = comment.ownerFullName
                tvCommentContent.text = comment.content
                tvCommentDate.text = TimeFormatter().formatTimestamp(comment.timestamp)
                Log.e("comment delete vis", currentUserId)
                if (currentUserId == comment.ownerId){
                    btnDeleteComment.visibility = View.VISIBLE
                    btnDeleteComment.setOnClickListener {
                        onDeleteCommentClickListener?.onDeleteCommentClickListener(comment = comment)
                    }
                }else{
                    btnDeleteComment.visibility = View.GONE
                    btnDeleteComment.setOnClickListener(null)
                }
            }

        }
    }

    inner class EmptyCommentsViewHolder(binding: LayoutNoDataItemViewBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            EMPTY_COMMENTS -> EmptyCommentsViewHolder(
                LayoutNoDataItemViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            NON_EMPTY_COMMENTS -> CommentViewHolder(
                LayoutCommentItemViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> CommentViewHolder(
                LayoutCommentItemViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (commentsList.isNotEmpty()) NON_EMPTY_COMMENTS else EMPTY_COMMENTS
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is CommentViewHolder) holder.bind(commentsList[position])
    }

    override fun getItemCount(): Int {
        return if (commentsList.isNotEmpty()) commentsList.size else 1
    }

    private fun updateCommentAdded(comment: Comment) {
        val updatedList = mutableListOf<Comment>()
        updatedList.addAll(commentsList)
        updatedList.add(0, comment)
        commentsList = updatedList
        notifyItemInserted(0)
    }

    fun setOnCommentDeleteListener(onDeleteCommentClickListener: OnDeleteCommentClickListener){
        this.onDeleteCommentClickListener = onDeleteCommentClickListener
    }

    fun updateCommentRemoved(commentId: String?) {
        if (commentId == null) return
        val updatedList = mutableListOf<Comment>()
        updatedList.addAll(commentsList)
        val indexToDelete = updatedList.indexOfFirst { comment -> comment.id == commentId }
        updatedList.removeAt(indexToDelete)
        commentsList = updatedList
        notifyItemRemoved(indexToDelete)
    }

    fun addComment(updatedComments: List<Comment>) {
        commentsList = updatedComments.toMutableList()
        if (commentsList.size>1){
            notifyItemInserted(0)
        }else{
            notifyDataSetChanged()
        }
    }

    fun deleteComment(updatedComments: List<Comment>) {
        val deletedComment = commentsList.find { it !in updatedComments }
        deletedComment?.let {
            val position = commentsList.indexOf(deletedComment)
            commentsList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun addNextBatch(nextBatchComments: List<Comment>) {
        commentsList.addAll(nextBatchComments)
        notifyDataSetChanged()
    }

    fun initialize(comments: List<Comment>) {
        Log.e("CommentsBatchState", comments.toString())
        commentsList = comments.toMutableList()
        notifyDataSetChanged()
    }

}