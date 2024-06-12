package com.isalatapp.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.isalatapp.R
import com.isalatapp.databinding.FragmentRegisterBinding
import com.isalatapp.ui.MainActivity
import com.isalatapp.ui.home.HomeFragment


class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnRegister.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment()).commit()
            }

            val toLoginText = getString(R.string.to_login)
            val spannableString = SpannableStringBuilder("Already have an account? $toLoginText")
            val startIndex = spannableString.indexOf(toLoginText)
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, LoginFragment()).commit()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(requireContext(), R.color.primary)
                }
            }

            spannableString.setSpan(
                clickableSpan,
                startIndex,
                startIndex + toLoginText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            tvToLogin.text = spannableString
            tvToLogin.movementMethod = LinkMovementMethod.getInstance()
            setupView()
            playAnimation()
        }
    }

    private fun playAnimation() {
        binding.apply {
            val labelHeading =
                ObjectAnimator.ofFloat(labelHeading, View.ALPHA, 0f, 1f).setDuration(200)
            val tvName = ObjectAnimator.ofFloat(tvName, View.ALPHA, 0f, 1f).setDuration(200)
            val edLoginName =
                ObjectAnimator.ofFloat(edlRegisterName, View.ALPHA, 0f, 1f).setDuration(200)
            val tvEmail = ObjectAnimator.ofFloat(tvEmail, View.ALPHA, 0f, 1f).setDuration(200)
            val edLoginEmail =
                ObjectAnimator.ofFloat(edlRegisterEmail, View.ALPHA, 0f, 1f).setDuration(200)
            val tvPassword = ObjectAnimator.ofFloat(tvPassword, View.ALPHA, 0f, 1f).setDuration(200)
            val edLoginPassword =
                ObjectAnimator.ofFloat(edlRegisterPassword, View.ALPHA, 0f, 1f).setDuration(200)
            val btnRegister =
                ObjectAnimator.ofFloat(btnRegister, View.ALPHA, 0f, 1f).setDuration(200)
            val tvLogin = ObjectAnimator.ofFloat(tvToLogin, View.ALPHA, 0f, 1f).setDuration(200)
            val tvPrivacyPolicy =
                ObjectAnimator.ofFloat(tvPrivacyPolicy, View.ALPHA, 0f, 1f).setDuration(200)

            AnimatorSet().apply {
                playSequentially(
                    labelHeading,
                    tvName,
                    edLoginName,
                    tvEmail,
                    edLoginEmail,
                    tvPassword,
                    edLoginPassword,
                    btnRegister,
                    tvLogin,
                    tvPrivacyPolicy
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

    private fun setupView() {
        binding.apply {
            val text = getString(R.string.bisindo)
            val spannableString = SpannableString("Learn $text\nwith us.")
            val startIndex = spannableString.indexOf(text)
            val foregroundColorSpan = ForegroundColorSpan(resources.getColor(R.color.primary, null))
            spannableString.setSpan(
                foregroundColorSpan,
                startIndex,
                startIndex + text.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tvTitle.text = spannableString
        }
    }

}