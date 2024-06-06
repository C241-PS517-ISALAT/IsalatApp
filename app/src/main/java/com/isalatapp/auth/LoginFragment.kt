package com.isalatapp.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.isalatapp.R
import com.isalatapp.databinding.FragmentLoginBinding
import com.isalatapp.ui.MainActivity
import com.isalatapp.ui.home.HomeFragment

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()

//            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                Toast.makeText(requireContext(), R.string.email_reminder, Toast.LENGTH_SHORT)
//                    .show()
//                return@setOnClickListener
//            }
//
//            lifecycleScope.launch {
//                try {
//                    binding.progressBarlogin.visibility = View.VISIBLE
//                    val response =
//                        ApiClient.instance.login(mapOf("email" to email, "password" to password))
//
//                    if (!response.error) {
//                        with(preferences.edit()) {
//                            putString("token", response.loginResult.token)
//                            putString("userId", response.loginResult.userId)
//                            putString("name", response.loginResult.name)
//                            apply()
//                        }
//                        Toast.makeText(requireContext(),
//                            getString(R.string.login_successful), Toast.LENGTH_SHORT)
//                            .show()
//                        parentFragmentManager.beginTransaction()
//                            .replace(R.id.fragment_container, StoryListFragment())
//                            .commit()
//                    } else {
//                        Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                } catch (e: Exception) {
//                    Toast.makeText(
//                        requireContext(),
//                        getString(R.string.failed_login),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                } finally {
//                    binding.progressBarlogin.visibility = View.GONE
//                }
//            }
        }
        val toRegisterText = getString(R.string.register_text)
        val spannableString = SpannableStringBuilder("Don't Have Account? $toRegisterText")
        val startIndex = spannableString.indexOf(toRegisterText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, RegisterFragment())
                    .commit()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireContext(), R.color.blue)
            }
        }
        spannableString.setSpan(
            clickableSpan,
            startIndex,
            startIndex + toRegisterText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvToRegister.text = spannableString
        binding.tvToRegister.movementMethod = LinkMovementMethod.getInstance()
        playAnimation()
    }

    private fun playAnimation() {
        binding.apply {
            val labelHeading =
                ObjectAnimator.ofFloat(labelHeading, View.ALPHA, 0f, 1f).setDuration(200)
            val tvEmail =
                ObjectAnimator.ofFloat(tvEmail, View.ALPHA, 0f, 1f).setDuration(200)
            val edLoginEmail =
                ObjectAnimator.ofFloat(edlLoginEmail, View.ALPHA, 0f, 1f).setDuration(200)
            val tvPassword =
                ObjectAnimator.ofFloat(tvPassword, View.ALPHA, 0f, 1f).setDuration(200)
            val edLoginPassword =
                ObjectAnimator.ofFloat(edlLoginPassword, View.ALPHA, 0f, 1f).setDuration(200)
            val cslRememberMe =
                ObjectAnimator.ofFloat(cslRememberMe, View.ALPHA, 0f, 1f).setDuration(200)
            val btnLogin =
                ObjectAnimator.ofFloat(btnLogin, View.ALPHA, 0f, 1f).setDuration(200)
            val tvRegister =
                ObjectAnimator.ofFloat(tvToRegister, View.ALPHA, 0f, 1f).setDuration(200)

            AnimatorSet().apply {
                playSequentially(
                    labelHeading,
                    tvEmail,
                    edLoginEmail,
                    tvPassword,
                    edLoginPassword,
                    cslRememberMe,
                    btnLogin,
                    tvRegister
                )
                startDelay = 200
            }.start()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideBottomNavigation()
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).showBottomNavigation()
    }

}