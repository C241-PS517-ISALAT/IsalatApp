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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.isalatapp.R
import com.isalatapp.databinding.FragmentRegisterBinding
import com.isalatapp.helper.model.AuthViewModel
import com.isalatapp.helper.response.UserRecord
import com.isalatapp.ui.MainActivity
import com.isalatapp.ui.ViewModelFactory

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupAction() {
        binding.apply {
            btnRegister.setOnClickListener {
                val name = edRegisterName.text.toString()
                val email = edRegisterEmail.text.toString().lowercase()
                val password = edRegisterPassword.text.toString()
                val phone = edRegisterPhone.text.toString()
                val dob = edRegisterDob.text.toString()
                if (name.isNotEmpty() && email.isNotEmpty() && password.length >= 8) {
                    viewModel.submitRegister(
                        userRecord = UserRecord(name, phone, dob, email, password)
                    )
                } else {
                    Toast.makeText(
                        requireContext(), "Please fill the form correctly", Toast.LENGTH_SHORT
                    ).show()
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LoginFragment()).commit()
            }
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
            val tvPhone = ObjectAnimator.ofFloat(tvPhone, View.ALPHA, 0f, 1f).setDuration(200)
            val edLoginPhone =
                ObjectAnimator.ofFloat(edlRegisterPhone, View.ALPHA, 0f, 1f).setDuration(200)
            val tvDob = ObjectAnimator.ofFloat(tvDob, View.ALPHA, 0f, 1f).setDuration(200)
            val edLoginDob =
                ObjectAnimator.ofFloat(edlRegisterDob, View.ALPHA, 0f, 1f).setDuration(200)
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
                    tvPhone,
                    edLoginPhone,
                    tvDob,
                    edLoginDob,
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
            val spannableStringBisindo = SpannableString("Learn $text\nwith us.")
            val startIndexBisindo = spannableStringBisindo.indexOf(text)
            val foregroundColorSpan = ForegroundColorSpan(resources.getColor(R.color.primary, null))
            spannableStringBisindo.setSpan(
                foregroundColorSpan,
                startIndexBisindo,
                startIndexBisindo + text.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tvTitle.text = spannableStringBisindo

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
        }
    }

}