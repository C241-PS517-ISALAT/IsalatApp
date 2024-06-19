package com.isalatapp.ui

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
import com.isalatapp.ui.auth.LoginFragment
import com.isalatapp.ui.camera.ModeCameraFragment
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

                user.rememberMe -> {
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
                R.id.nav_camera -> loadFragment(ModeCameraFragment())
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
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                .commit()
            true
        } else {
            false
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
