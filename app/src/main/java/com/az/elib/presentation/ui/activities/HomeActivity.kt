package com.az.elib.presentation.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.az.elib.R
import com.az.elib.databinding.ActivityHomeBinding
import com.az.elib.presentation.adapters.MyPagerAdapter
import com.az.elib.presentation.ui.fragments.FeedFragment
import com.az.elib.presentation.ui.fragments.LibraryFragment
import com.az.elib.presentation.viewmodels.ViewModelFeed
import com.az.elib.util.Constants
import com.az.elib.util.CustomSnackBar
import com.az.elib.util.NavigationMenuIconManager
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val navigationMenuIconManager by lazy{
         NavigationMenuIconManager(this@HomeActivity, binding)
    }
    private lateinit var pagerAdapter: MyPagerAdapter
    private lateinit var feedFragment: FeedFragment
    private lateinit var libraryFragment: LibraryFragment
    private lateinit var customSnackBar: CustomSnackBar
    private val viewModelFeed: ViewModelFeed by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customizeSystemBars(binding.main.id, ContextCompat.getColor(this@HomeActivity, R.color.colorPrimaryDark))
        setNavigationBarColor(ContextCompat.getColor(this@HomeActivity, R.color.colorSecondaryDark))
        customSnackBar = CustomSnackBar(binding.root, this@HomeActivity)
        setStatusBarLight(false)
        setBottomNavigationFunctionality()
        setUiFunc()
        setViewPager()
    }



    private fun handleNotificationClick() {
        val postId = intent.getStringExtra(Constants.POST_ID_FROM_NOTIFICATION)
        Log.e("Notification", "postId in home : ${intent.extras}")
        postId?.let {
            viewModelFeed.scrollToPostFromNotification(postId)
        }
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationClick()
    }

    override fun onResume() {
        super.onResume()
        handleNotificationClick()
    }

    private fun setUiFunc() {
    }

    private fun setViewPager() {
        feedFragment = FeedFragment()
        libraryFragment = LibraryFragment()
        pagerAdapter = MyPagerAdapter(this@HomeActivity, listOf(feedFragment, libraryFragment))
        binding.viewPagerMain.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Toast.makeText(this@HomeActivity, "Page $position", Toast.LENGTH_SHORT).show()
                navigationMenuIconManager.updateBottomNavigationIcon(
                    binding.bnvMain.menu.getItem(position).itemId)
            }

        })
        binding.viewPagerMain.adapter = pagerAdapter
        binding.viewPagerMain.isUserInputEnabled=false
    }

    private fun setBottomNavigationFunctionality() {
        binding.bnvMain.setOnItemSelectedListener {
            navigationMenuIconManager.updateBottomNavigationIcon(it.itemId)
            when (it.itemId) {
                R.id.nav_home -> {
                    binding.viewPagerMain.currentItem = 0
                    true
                }
                R.id.nav_library -> {
                    binding.viewPagerMain.currentItem = 1
                    true
                }
                else -> false
            }
        }

    }



}