package com.isalatapp.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.isalatapp.R
import com.isalatapp.databinding.FragmentRegisterBinding
import com.isalatapp.ui.home.HomeFragment
import com.isalatapp.ui.MainActivity

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnRegister.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        val loginText = getString(R.string.login_text)
        val spannableString = SpannableStringBuilder("Already have an account, $loginText")
        val startIndex = spannableString.indexOf(loginText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LoginFragment())
                    .commit()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireContext(), R.color.blue)
            }
        }

        spannableString.setSpan(clickableSpan, startIndex, startIndex + loginText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvLogin.text = spannableString
        binding.tvLogin.movementMethod = LinkMovementMethod.getInstance()

        playAnimation()
    }

    private fun playAnimation() {
        binding.nameEditText.alpha = 0f
        binding.emailEditText.alpha = 0f
        binding.passwordEditText.alpha = 0f
        binding.btnRegister.alpha = 0f
        binding.tvLogin.alpha = 0f

        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -50f, 50f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val edLoginName = ObjectAnimator.ofFloat(binding.nameEditText, View.ALPHA, 0f, 1f).setDuration(200)
        val edLoginEmail = ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 0f, 1f).setDuration(200)
        val edLoginPassword = ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 0f, 1f).setDuration(200)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 0f, 1f).setDuration(200)
        val tvRegister = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 0f, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(
                edLoginName,
                edLoginEmail,
                edLoginPassword,
                btnLogin,
                tvRegister
            )
            startDelay = 200
        }.start()
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