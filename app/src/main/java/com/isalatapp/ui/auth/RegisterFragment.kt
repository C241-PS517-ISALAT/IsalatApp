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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.isalatapp.R
import com.isalatapp.api.ResultState
import com.isalatapp.databinding.FragmentRegisterBinding
import com.isalatapp.helper.model.AuthViewModel
import com.isalatapp.ui.MainActivity
import com.isalatapp.ui.ViewModelFactory
import com.isalatapp.ui.home.HomeFragment

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

    private fun setupAction() {
        binding.apply {
            btnRegister.setOnClickListener {
                if (edRegisterName.text?.isNotEmpty()!! && edRegisterEmail.text?.isNotEmpty()!! && edRegisterPassword.text?.length!! >= 8) {
                    viewModel.submitRegister(
                        name = edRegisterName.text.toString(),
                        email = edRegisterEmail.text.toString().lowercase(),
                        password = edRegisterPassword.text.toString()
                    )
                } else {
                    Toast.makeText(
                        requireContext(), "Please fill the form correctly", Toast.LENGTH_SHORT
                    ).show()
                }
            }

            val builder: AlertDialog.Builder =
                AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
            builder.setView(R.layout.progress_indicator)
            val dialog: AlertDialog = builder.create()

            viewModel.responseResult.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ResultState.Loading -> dialog.show()
                    is ResultState.Error -> {
                        dialog.dismiss()
                        Toast.makeText(
                            requireContext(), response.error, Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResultState.Success -> {
                        dialog.dismiss()
                        parentFragmentManager.beginTransaction().apply {
                            replace(
                                R.id.fragment_container,
                                LoginFragment(),
                                LoginFragment::class.java.simpleName
                            )
                            addToBackStack(null)
                            commit()
                        }
                    }

                    else -> dialog.dismiss()
                }
            }


        }
    }

    private fun setupView() {
        binding.apply {
            val text = getString(R.string.bisindo)
            val spannableString1 = SpannableString("Learn $text\nwith us.")
            val startIndex1 = spannableString1.indexOf(text)
            val foregroundColorSpan = ForegroundColorSpan(resources.getColor(R.color.primary, null))
            spannableString1.setSpan(
                foregroundColorSpan,
                startIndex1,
                startIndex1 + text.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            tvTitle.text = spannableString1
            val toLoginText = getString(R.string.to_login)
            val spannableString2 = SpannableStringBuilder("Already have an account? $toLoginText")
            val startIndex2 = spannableString2.indexOf(toLoginText)
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

            spannableString2.setSpan(
                clickableSpan,
                startIndex2,
                startIndex2 + toLoginText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tvToLogin.text = spannableString2
            tvToLogin.movementMethod = LinkMovementMethod.getInstance()
        }
    }

}