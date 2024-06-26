package com.isalatapp.ui

import NewsFragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.isalatapp.R
import com.isalatapp.databinding.ActivityMainBinding
import com.isalatapp.helper.model.AuthViewModel
import com.isalatapp.ui.account.AccountFragment
import com.isalatapp.ui.account.EditProfileFragment
import com.isalatapp.ui.auth.LoginFragment
import com.isalatapp.ui.camera.IsalatModelFragment
import com.isalatapp.ui.camera.ModeCameraFragment
import com.isalatapp.ui.camera.ObjectDetectionFragment
import com.isalatapp.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        viewModel.getSession().observe(this) { user ->
            when {
                user == null -> {
                    loadFragment(LoginFragment())
                }

                user.rememberMe == true -> {
                    loadFragment(HomeFragment())
                }

                else -> {
                    loadFragment(LoginFragment())
                }
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_camera -> loadFragment(IsalatModelFragment())
                R.id.nav_account -> loadFragment(AccountFragment())
                else -> false
            }
        }
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        return if (currentFragment?.javaClass != fragment.javaClass) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
            updateBottomNavigationSelection()
            true
        } else {
            false
        }
    }

    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.fragment_container)
    }

    private fun updateBottomNavigationSelection() {
        val currentFragment = getCurrentFragment()
        val itemId = when (currentFragment) {
            is HomeFragment -> R.id.nav_home
            is NewsFragment -> R.id.nav_home
            is ModeCameraFragment -> R.id.nav_camera
            is IsalatModelFragment -> R.id.nav_camera
            is ObjectDetectionFragment -> R.id.nav_camera
            is AccountFragment -> R.id.nav_account
            is EditProfileFragment -> R.id.nav_account
            else -> return
        }
        if (binding.bottomNavigation.selectedItemId != itemId) {
            binding.bottomNavigation.selectedItemId = itemId
        }
    }

    fun hideBottomNavigation() {
        binding.bottomNavigation.visibility = View.GONE
    }

    fun showBottomNavigation() {
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    fun openNewsUrl(view: View) {
        val url = view.getTag(R.id.tvLink) as? String
        url?.let {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }
}
