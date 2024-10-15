package com.az.elib.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.az.elib.R
import com.az.elib.databinding.ActivityHomeBinding

class NavigationMenuIconManager(private val context: Context, private val binding: ActivityHomeBinding) {
    fun updateBottomNavigationIcon(itemSelectedId: Int) : Boolean {
        binding.apply {
            return when (itemSelectedId) {
                R.id.nav_home -> {
                    bnvMain.menu.findItem(R.id.nav_home).icon = ContextCompat.getDrawable(
                        context,
                        R.drawable.icon_home_filled
                    )
                    bnvMain.menu.findItem(R.id.nav_library).icon = ContextCompat.getDrawable(
                        context,
                        R.drawable.icon_book_outlined
                    )
                    true
                }

                R.id.nav_library -> {
                    bnvMain.menu.findItem(R.id.nav_home).icon = ContextCompat.getDrawable(
                        context,
                        R.drawable.icon_home_outlined
                    )
                    bnvMain.menu.findItem(R.id.nav_library).icon = ContextCompat.getDrawable(
                        context,
                        R.drawable.icon_book_filled_open
                    )

                    true
                }

                else -> false
            }
        }
    }
}