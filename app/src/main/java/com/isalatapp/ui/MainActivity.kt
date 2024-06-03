package com.isalatapp.ui

import CameraFragment
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.isalatapp.R
import com.isalatapp.auth.LoginFragment
import com.isalatapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(LoginFragment())

        binding.bottomNavigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_camera -> {
                    loadFragment(CameraFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_account -> {
                    loadFragment(AccountFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

//    private fun playToolbarAnimation() {
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        ObjectAnimator.ofFloat(toolbar, View.TRANSLATION_Y, -toolbar.height.toFloat(), 0f).apply {
//            duration = 1000
//            start()
//        }
//
//        val fragmentContainer = findViewById<View>(R.id.fragment_container)
//        ObjectAnimator.ofFloat(fragmentContainer, View.ALPHA, 0f, 1f).apply {
//            duration = 1000
//            start()
//        }
//    }
//
//    private fun replaceFragmentWithAnimation(fragment: Fragment) {
//        val fragmentContainer = findViewById<View>(R.id.fragment_container)
//        val fadeOut = ObjectAnimator.ofFloat(fragmentContainer, View.ALPHA, 1f, 0f).apply {
//            duration = 500
//        }
//        val fadeIn = ObjectAnimator.ofFloat(fragmentContainer, View.ALPHA, 0f, 1f).apply {
//            duration = 500
//        }
//
//        fadeOut.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator) {
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.fragment_container, fragment)
//                    .commit()
//                fadeIn.start()
//            }
//        })
//        fadeOut.start()
//    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun hideBottomNavigation() {
        binding.bottomNavigation.visibility = View.GONE
    }

    fun showBottomNavigation() {
        binding.bottomNavigation.visibility = View.VISIBLE
    }


}