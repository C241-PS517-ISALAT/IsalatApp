package com.isalatapp.ui.auth

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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.isalatapp.R
import com.isalatapp.api.ResultState
import com.isalatapp.databinding.FragmentLoginBinding
import com.isalatapp.helper.model.AuthViewModel
import com.isalatapp.helper.response.UserRecord
import com.isalatapp.ui.MainActivity
import com.isalatapp.ui.ViewModelFactory
import com.isalatapp.ui.home.HomeFragment

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupAction() {
        viewModel.logout()
        binding.apply {
            btnLogin.setOnClickListener {
                handleLogin()
            }

            forgotPassword.setOnClickListener {
                handleForgotPassword()
            }

            val builder = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
            builder.setView(R.layout.progress_indicator)
            val dialog: AlertDialog = builder.create()

            observeViewModel(dialog)
        }
    }

    private fun handleLogin() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()
        val rememberMe = binding.cbxRememberMe.isChecked
        val userRecord = UserRecord(email = email, password = password, rememberMe = rememberMe)
        viewModel.submitLogin(userRecord)
    }


    private fun handleForgotPassword() {
        val email = binding.edLoginEmail.text.toString().lowercase()
        if (email.isBlank()) {
            Snackbar.make(
                binding.forgotPassword,
                "Please enter your email to reset password",
                Snackbar.LENGTH_SHORT
            ).show()
        } else {
            viewModel.resetPassword(email)
            Toast.makeText(
                requireContext(),
                "Check your email to reset password",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun observeViewModel(dialog: AlertDialog) {
        viewModel.responseResult.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ResultState.Loading -> dialog.show()
                is ResultState.Error -> {
                    dialog.dismiss()
                    Toast.makeText(requireContext(), response.error, Toast.LENGTH_SHORT).show()
                }

                is ResultState.Success -> {
                    dialog.dismiss()
                    navigateToHome()
                }

                else -> dialog.dismiss()
            }
        }
    }

    private fun navigateToHome() {
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, HomeFragment(), HomeFragment::class.java.simpleName)
            commit()
        }
    }

    private fun setupView() {
        val toRegisterText = getString(R.string.register_text)
        val spannableString = SpannableStringBuilder("Don't Have Account? $toRegisterText")
        val startIndex = spannableString.indexOf(toRegisterText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, RegisterFragment()).commit()
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
    }

    private fun playAnimation() {
        binding.apply {
            val labelHeading =
                ObjectAnimator.ofFloat(labelHeading, View.ALPHA, 0f, 1f).setDuration(200)
            val tvEmail = ObjectAnimator.ofFloat(tvEmail, View.ALPHA, 0f, 1f).setDuration(200)
            val edLoginEmail =
                ObjectAnimator.ofFloat(edlLoginEmail, View.ALPHA, 0f, 1f).setDuration(200)
            val tvPassword = ObjectAnimator.ofFloat(tvPassword, View.ALPHA, 0f, 1f).setDuration(200)
            val edLoginPassword =
                ObjectAnimator.ofFloat(edlLoginPassword, View.ALPHA, 0f, 1f).setDuration(200)
            val cslRememberMe =
                ObjectAnimator.ofFloat(cslRememberMe, View.ALPHA, 0f, 1f).setDuration(200)
            val btnLogin = ObjectAnimator.ofFloat(btnLogin, View.ALPHA, 0f, 1f).setDuration(200)
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