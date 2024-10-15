package com.az.elib.presentation.ui.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.az.elib.databinding.FragmentFeedBinding
import com.az.elib.domain.interfaces.OnDeletePostClickListener
import com.az.elib.domain.interfaces.OnLoadMorePostsClickListener
import com.az.elib.domain.interfaces.OnPostCommentClickedListener
import com.az.elib.domain.interfaces.OnPostReplyClickListener
import com.az.elib.domain.interfaces.OnReactClickListener
import com.az.elib.domain.models.Post
import com.az.elib.domain.models.PostsBatchState
import com.az.elib.domain.models.PostsUpdateType
import com.az.elib.presentation.adapters.FeedAdapter
import com.az.elib.presentation.util.SystemBarsHeight
import com.az.elib.presentation.viewmodels.ViewModelCreatePost
import com.az.elib.presentation.viewmodels.ViewModelFeed
import com.az.elib.presentation.ui.activities.SettingsActivity
import com.az.elib.presentation.ui.activities.CreatePostActivity
import com.az.elib.presentation.ui.dialogs.CommentsFragment
import com.az.elib.util.CustomSnackBar
import kotlinx.coroutines.launch

class FeedFragment : Fragment(), OnPostReplyClickListener, OnLoadMorePostsClickListener,
    OnReactClickListener,
    OnPostCommentClickedListener, OnDeletePostClickListener {
    private lateinit var binding: FragmentFeedBinding
    private val viewModelCreatePost: ViewModelCreatePost by activityViewModels()
    private val viewModelFeed: ViewModelFeed by activityViewModels()
    private val customSnackBar: CustomSnackBar by lazy {
        CustomSnackBar(binding.root, requireActivity())
    }
    private val systemBarsHeight: SystemBarsHeight by lazy {
        SystemBarsHeight(requireActivity())
    }
    private var feedAdapter: FeedAdapter? = null
    private var toolbarIsShowing = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getMostRecentPostsBatch()
        observeViewModelData()
        setToolbarFunctionality()
        rvScrollStateListener()
        setupFeedAdapter(mutableListOf())
    }

    private fun rvScrollStateListener() {
        binding.recyclerViewFeed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                when {
                    dy >= 3 -> if (toolbarIsShowing) hideToolbar()
                    dy <= -2 -> if (!toolbarIsShowing) showToolbar()
                }
            }
        })
    }

    private fun hideToolbar() {
        val toolbarHeight = binding.toolbar.height
        binding.toolbar.animate()
            .translationY(-toolbarHeight.toFloat())
            .setDuration(200) // Set your desired animation duration here
            .start()
        toolbarIsShowing = false
    }

    private fun showToolbar() {
        binding.toolbar.animate()
            .translationY(0f)
            .setDuration(200) // Set your desired animation duration here
            .start()
        toolbarIsShowing = true
    }

    private fun setToolbarFunctionality() {
        binding.apply {
            btnSettings.setOnClickListener {
                startActivity(Intent(requireActivity(), SettingsActivity::class.java))
            }
            view?.viewTreeObserver?.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val windowInsets =
                        WindowInsetsCompat.toWindowInsetsCompat(view!!.rootWindowInsets)
                    val statusBarHeight =
                        windowInsets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                    binding.clToolbar.updatePadding(top = statusBarHeight)
                    view?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                }
            })
            btnAddPost.setOnClickListener {
                startActivity(Intent(requireActivity(), CreatePostActivity::class.java))
            }
        }
    }

    private fun getMostRecentPostsBatch() {
        viewModelFeed.getNextPostsBatch()
    }

    private fun observeViewModelData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModelFeed.postsBatch.collect { postsBatchState ->
                        when (postsBatchState) {
                            is PostsBatchState.Loading -> {
                                feedAdapter?.setLoading(true)
                            }
                            is PostsBatchState.Success -> {
                                Log.e("update_type", "${postsBatchState.updateType}")
                                when (postsBatchState.updateType) {
                                    PostsUpdateType.Initialized -> {
                                        feedAdapter?.updateData(postsBatchState.posts)
                                    }

                                    PostsUpdateType.NextBatch -> {
                                        if (feedAdapter?.canFetchMore() == false) return@collect
                                        if (postsBatchState.posts == feedAdapter?.posts) {
                                            customSnackBar.launchSnackBar(
                                                "No more posts to show",
                                                false
                                            )
                                            feedAdapter?.hideLoadMorePostsButton()
                                        } else {
                                            feedAdapter?.updateData(postsBatchState.posts)
                                        }
                                    }
                                }
                            }

                            is PostsBatchState.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    "something went wrong!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
                launch {
                    viewModelCreatePost.postCreatingResult.collect { result ->
                        if (result == null) return@collect
                        if (result.isSuccess) {
                            customSnackBar.launchSnackBar("Post created successfully", false)
                            updateFeedAfterCreate(result.getOrNull())
                        } else {
                            customSnackBar.launchSnackBar("Post creation failed", true)
                        }
                        viewModelCreatePost.clearPostCreatingResult()
                    }
                }
                launch {
                    viewModelCreatePost.sendNotificationResult.collect { result ->
                        if (result == null) return@collect
                        if (result.isFailure) {
                            customSnackBar.launchSnackBar("Failed to send notification", true)
                        }
                        viewModelCreatePost.clearSendNotificationResult()
                    }
                }
                launch {
                    viewModelFeed.addReactionResult.collect { result ->
                        if (result == null) return@collect
                        if (result.isFailure) customSnackBar.launchSnackBar("Failed to react", true)
                        viewModelFeed.clearAddReactionResult()
                    }
                }
                launch {
                    viewModelFeed.removeReactionResult.collect { result ->
                        if (result == null) return@collect
                        if (result.isFailure) customSnackBar.launchSnackBar(
                            "Failed to remove reaction",
                            true
                        )
                        viewModelFeed.clearRemoveReactionResult()

                    }
                }
                launch {
                    viewModelFeed.postFromNotification.collect { postIdFromNotification ->
                        if (postIdFromNotification == null || feedAdapter == null) return@collect
                        if (postIdFromNotification.isNotBlank()) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                val position = feedAdapter?.getPositionFromId(postIdFromNotification) ?: 0
                                binding.recyclerViewFeed.smoothScrollToPosition(position)
                            }, 1500)
                        }
                    }
                }
                launch {
                    viewModelFeed.deletePostResult.collect { result ->
                        if (result == null) return@collect
                        if (result.isSuccess) {
                            customSnackBar.launchSnackBar("Post deleted successfully", false)
                            feedAdapter?.updatePostRemoved(result.getOrNull())
                        } else {
                            if (result.isFailure) customSnackBar.launchSnackBar(
                                "Failed to delete post",
                                true
                            )
                        }
                        viewModelFeed.clearDeletePostResult()
                    }
                }

            }
        }
    }

    private fun setupFeedAdapter(postsList: MutableList<Post>) {
        feedAdapter = FeedAdapter(
            requireContext(),
            viewModelFeed.getUserId(),
            postsList,
            systemBarsHeight.getSystemBarsHeight()
        )
        feedAdapter?.setOnPostReplyClickListener(this@FeedFragment)
        feedAdapter?.setOnLoadMorePostsClickListener(this@FeedFragment)
        feedAdapter?.setOnReactClickListener(this@FeedFragment)
        feedAdapter?.setOnPostCommentClickedListener(this@FeedFragment)
        feedAdapter?.setOnPostDeleteClickedListener(this@FeedFragment)
        val mLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.apply {
            recyclerViewFeed.layoutManager = mLayoutManager
            recyclerViewFeed.adapter = feedAdapter
        }

    }


    private fun updateFeedAfterCreate(post: Post?) {
        if (post != null) {
            feedAdapter?.appendAsFirstItem(post)
        }
    }

    override fun onReplyClicked(emailAddress: String, subject: String) {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, "")
        }
        emailIntent.`package` = "com.google.android.gm"
        try {
            ContextCompat.startActivity((context as Activity), emailIntent, null)

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Gmail app not found", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error sending email: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onLoadMorePostsClicked() {
        viewModelFeed.getNextPostsBatch()
    }

    override fun addReaction(postId: String, postPosition: Int) {
        Log.e("reacta", postId.toString())
        viewModelFeed.addReaction(postId)
    }

    override fun removeReaction(postId: String, postPosition: Int) {
        Log.e("reactr", postId.toString())
        viewModelFeed.removeReaction(postId)

    }


    override fun onPostCommentClicked(postId: String) {
        val commentsFragment = CommentsFragment.newInstance(postId, viewModelFeed.getUserId())
        commentsFragment.show(parentFragmentManager, "CommentsFragment")
    }

    override fun OnDeletePostClick(postId: String) {
        viewModelFeed.deletePost(postId)
    }


}