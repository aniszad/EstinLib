package com.az.elib.presentation.adapters

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.widget.ViewPager2
import com.az.elib.R
import com.az.elib.databinding.LayoutLoadMorePostsBinding
import com.az.elib.databinding.LoadingLayoutBinding
import com.az.elib.databinding.PostLayoutBinding
import com.az.elib.domain.interfaces.OnDeletePostClickListener
import com.az.elib.domain.interfaces.OnLoadMorePostsClickListener
import com.az.elib.domain.interfaces.OnPostCommentClickedListener
import com.az.elib.domain.interfaces.OnPostImageLongClick
import com.az.elib.domain.interfaces.OnPostReplyClickListener
import com.az.elib.domain.interfaces.OnReactClickListener
import com.az.elib.domain.models.Post
import com.az.elib.presentation.ui.custom.InitialsAvatarView
import com.az.elib.presentation.ui.custom.LikeButton
import com.az.elib.presentation.ui.fragments.FeedFragment
import com.az.elib.presentation.util.OnDoubleClickListener
import com.az.elib.util.Constants
import com.az.elib.util.ReactionCountFormatter
import com.az.elib.util.TimeFormatter
import com.az.estinlib.presentation.adapters.PostImageAdapter
import com.google.android.material.card.MaterialCardView
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator


class FeedAdapter(
    private val context: Context,
    private val currentUserId: String,
    var posts: MutableList<Post>
) :
    RecyclerView.Adapter<ViewHolder>() {
    private var onPostReplyClickListener: OnPostReplyClickListener? = null
    private var onLoadMorePostsClickListener: OnLoadMorePostsClickListener? = null
    private var onDeletePostClickListener: OnDeletePostClickListener? = null
    private var onReactClickListener: OnReactClickListener? = null
    private var onPostCommentClickedListener: OnPostCommentClickedListener? = null
    private var onPostImageLongClick: OnPostImageLongClick? = null
    private var showLoadMoreButton = true
    private val timeFormatter = TimeFormatter()
    private var isLoading = false
    private lateinit var attachmentImagesAdapter: PostImageAdapter
    private lateinit var popupMenu: PopupMenu

    companion object {
        private const val POSTS_VIEW_HOLDER = 0
        private const val LOAD_MORE_BUTTON_VIEW_HOLDER = 2
        private const val LOADING_BAR_VIEW_HOLDER = 3
    }

    inner class PostViewHolder(postLayoutBinding: PostLayoutBinding) :
        ViewHolder(postLayoutBinding.root) {
        val cardView = postLayoutBinding.cardView
        val tvFullName = postLayoutBinding.tvFullName
        val tvAboutUser = postLayoutBinding.tvAboutUser
        val tvPostTag = postLayoutBinding.tvPostTag
        val tvPostContent = postLayoutBinding.tvPostContent
        val viewPagerImagesAttachments = postLayoutBinding.viewPagerImagesAttachments
        val dotsIndicator = postLayoutBinding.dotsIndicator
        val btnReply = postLayoutBinding.btnReply
        val btnMore = postLayoutBinding.btnMore
        val btnReact = postLayoutBinding.btnReact as LikeButton
        val tvReactCount = postLayoutBinding.tvReactCount
        val btnComment = postLayoutBinding.btnComment
        val llLike = postLayoutBinding.llLike
        val imUser = postLayoutBinding.imUserImage as InitialsAvatarView
    }

    inner class LoadMoreButtonViewHolder(loadMoreBinding: LayoutLoadMorePostsBinding) :
        ViewHolder(loadMoreBinding.root) {
        val btnLoadMorePosts = loadMoreBinding.btnLoadMore
    }

    inner class LoadingViewHolder(binding: LoadingLayoutBinding) : ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        if (isLoading) {
            return LOADING_BAR_VIEW_HOLDER
        }
        return when (position) {
            posts.size -> LOAD_MORE_BUTTON_VIEW_HOLDER
            else -> POSTS_VIEW_HOLDER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            POSTS_VIEW_HOLDER -> {
                PostViewHolder(
                    PostLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            LOAD_MORE_BUTTON_VIEW_HOLDER -> {
                LoadMoreButtonViewHolder(
                    LayoutLoadMorePostsBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }

            LOADING_BAR_VIEW_HOLDER -> {
                LoadingViewHolder(
                    LoadingLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun getItemCount(): Int {
        return if (showLoadMoreButton) posts.size + 1 else posts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is PostViewHolder -> {
                val currentPost = posts[position]
                holder.apply {
                    imUser.setInitialsAndColor(
                        "${currentPost.ownerFirstName} ${currentPost.ownerLastName}",
                        currentPost.ownerImage ?: "#000000"
                    )
                    
                    tvReactCount.text = ReactionCountFormatter.formatReactionCount(currentPost.postReactCount)
                    val isLikedByMe = currentPost.postLikesIds.contains(currentUserId)
                    btnReact.setLiked(isLikedByMe, false)
                    tvReactCount.setTextColor(ContextCompat.getColor(context, if (isLikedByMe) R.color.colorPrimaryAccent else R.color.colorTextGray))
                    
                    llLike?.setOnClickListener {
                        btnReact.toggle()
                    }

                    btnReact.onLikeChanged = { liked ->
                        val isNowLiked = !liked // liked param is the OLD state in toggle()
                        if (isNowLiked) {
                            tvReactCount.text = ReactionCountFormatter.increment(tvReactCount.text.toString())
                            tvReactCount.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryAccent))
                            onReactClickListener?.addReaction(currentPost.id, holder.bindingAdapterPosition)
                        } else {
                            tvReactCount.text = ReactionCountFormatter.decrement(tvReactCount.text.toString())
                            tvReactCount.setTextColor(ContextCompat.getColor(context, R.color.colorTextGray))
                            onReactClickListener?.removeReaction(currentPost.id, holder.bindingAdapterPosition)
                        }
                    }

                    btnComment?.setOnClickListener {
                        onPostCommentClickedListener?.onPostCommentClicked(currentPost.id)
                    }

                    btnReply?.setOnClickListener {
                        onPostReplyClickListener?.onReplyClicked(currentPost.ownerEmail, currentPost.tag ?: "")
                    }

                    tvFullName.text = "${currentPost.ownerFirstName} ${currentPost.ownerLastName}"
                    
                    val timeAgo = timeFormatter.formatTimestamp(currentPost.timestamp)
                    tvAboutUser.text = "${currentPost.ownerYear} • $timeAgo"
                    
                    tvPostTag.text = currentPost.tag?.uppercase()
                    tvPostTag.visibility = if (currentPost.tag.isNullOrEmpty()) View.GONE else View.VISIBLE
                    
                    if (currentPost.postContent.isNotEmpty()) {
                        tvPostContent.visibility = View.VISIBLE
                        tvPostContent.text = currentPost.postContent
                    } else {
                        tvPostContent.visibility = View.GONE
                    }

                    if (currentPost.postImageAttachments.isNotEmpty()) {
                        viewPagerImagesAttachments.visibility = View.VISIBLE
                        dotsIndicator.visibility = View.VISIBLE
                        setViewPagerImages(viewPagerImagesAttachments, currentPost.postImageAttachments, dotsIndicator, btnReact)
                    } else {
                        viewPagerImagesAttachments.visibility = View.GONE
                        dotsIndicator.visibility = View.GONE
                    }

                    btnMore?.visibility = if (currentUserId == currentPost.ownerId) View.VISIBLE else View.GONE
                    btnMore?.setOnClickListener {
                        showPostOptionsPopupMenu(it, currentPost.id)
                    }

                    if (currentPost.tag == Constants.EVENT) animate(cardView)
                }
            }

            is LoadMoreButtonViewHolder -> {
                holder.btnLoadMorePosts.setOnClickListener {
                    onLoadMorePostsClickListener?.onLoadMorePostsClicked()
                }
            }
        }
    }

    private fun showPostOptionsPopupMenu(btnMore: View, postId: String) {
        popupMenu = PopupMenu(context, btnMore)
        popupMenu.menuInflater.inflate(R.menu.menu_post_options, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { _ ->
            onDeletePostClickListener?.OnDeletePostClick(postId = postId)
            true
        }
        popupMenu.show()
    }

    /*private fun openGmailMessagingApp(emailAddress: String) {
        // Create an intent to open the messaging app
        val messageIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
            putExtra(Intent.CATEGORY_APP_MESSAGING, arrayOf(emailAddress))
        }

        // Check if there is a messaging app that can handle the intent
        if (messageIntent.resolveActivity(context.packageManager) != null) {
        } else {
            // If no messaging app is available, display an error message or handle it in another way
            Toast.makeText(context, "No messaging app found", Toast.LENGTH_SHORT).show()
        }
    }

     */
    private fun setViewPagerImages(
        tvPostImagesViewPager: ViewPager2,
        postImageAttachments: List<String>,
        dotsIndicator: DotsIndicator,
        btnReact: LikeButton
    ) {
        val viewPager2: ViewPager2 = tvPostImagesViewPager
        attachmentImagesAdapter = PostImageAdapter(context, postImageAttachments)
        attachmentImagesAdapter.setOnDoubleClickListenerListener(OnDoubleClickListener {
            btnReact.toggle()
        })
        viewPager2.adapter = attachmentImagesAdapter
        dotsIndicator.attachTo(tvPostImagesViewPager)
    }


    // setting the adapter interfaces
    fun setOnPostReplyClickListener(onPostReplyClickListener: OnPostReplyClickListener) {
        this.onPostReplyClickListener = onPostReplyClickListener
    }

    fun setOnLoadMorePostsClickListener(onLoadMorePostsClickListener: OnLoadMorePostsClickListener) {
        this.onLoadMorePostsClickListener = onLoadMorePostsClickListener
    }

    fun setOnReactClickListener(onReactClickListener: OnReactClickListener) {
        this.onReactClickListener = onReactClickListener
    }

    fun setOnPostCommentClickedListener(onPostCommentClickedListener: OnPostCommentClickedListener) {
        this.onPostCommentClickedListener = onPostCommentClickedListener
    }

    fun setOnPostDeleteClickedListener(onDeletePostClickListener: FeedFragment) {
        this.onDeletePostClickListener = onDeletePostClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun appendAsFirstItem(post: Post) {
        posts.add(0, post)
        notifyDataSetChanged()
    }

    private fun animate(materialCardView: MaterialCardView, reverse: Boolean = false) {
        val colorTo =
            if (!reverse) ContextCompat.getColor(context, R.color.colorPurple) else Color.WHITE

        ObjectAnimator.ofArgb(materialCardView, "strokeColor", colorTo).apply {
            duration = 2000
            // this introduces a sudden change of colors- to be removed
//            addUpdateListener {
//                materialCardView.invalidate()
//            }

            doOnEnd {
                this@FeedAdapter.animate(materialCardView, !reverse)
            }
            start()
        }
    }

    fun getPositionFromId(postIdFromNotification: String?): Int {
        return this.posts.indexOfFirst { it.id == postIdFromNotification }
    }

    fun updatePostRemoved(postId: String?) {
        if (postId == null) return
        val position = this.posts.indexOfFirst { it.id == postId }
        if (position != -1) {
            this.posts.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(posts: List<Post>) {
        this.posts = posts.toMutableList()
        showLoadMorePostsButton()
        this.isLoading = false
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setLoading(isLoading: Boolean){
        this.isLoading = isLoading
        posts.clear()
        notifyDataSetChanged()
    }

    fun canFetchMore(): Boolean {
        return showLoadMoreButton
    }

    fun hideLoadMorePostsButton() {
        showLoadMoreButton = false
        notifyItemChanged(posts.size)
    }

    fun showLoadMorePostsButton() {
        showLoadMoreButton = true
        notifyItemChanged(posts.size)
    }


}
