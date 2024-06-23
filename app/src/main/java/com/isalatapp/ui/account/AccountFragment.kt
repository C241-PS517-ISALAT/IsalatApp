package com.isalatapp.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.isalatapp.R
import com.isalatapp.databinding.FragmentAccountBinding
import com.isalatapp.helper.model.AuthViewModel
import com.isalatapp.ui.ViewModelFactory
import com.isalatapp.ui.auth.LoginFragment

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSession().observe(viewLifecycleOwner) { userRecord ->
            if (userRecord != null) {
                binding.tvName.text = userRecord.name
                binding.tvEmail.text = userRecord.email
            }
        }

        binding.llEditProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EditProfileFragment()).commit()
        }

        binding.llChangePassword.setOnClickListener {
            val currentUser = viewModel.getSession().value
            if (currentUser != null) {
                val updatedEmail = currentUser.email
                viewModel.resetPassword(updatedEmail)
                Toast.makeText(
                    requireContext(),
                    "Check your email to reset password",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                Toast.makeText(requireContext(), "Failed to get user details", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.llPrivacyPolicy.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PolicyFragment()).commit()
        }

        binding.llAboutUs.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AboutUsFragment()).commit()
        }

        binding.llSignOut.setOnClickListener {
            viewModel.logout()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment()).commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}