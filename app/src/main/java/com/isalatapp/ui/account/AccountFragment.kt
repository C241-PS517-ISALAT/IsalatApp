package com.isalatapp.ui.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.isalatapp.R
import com.isalatapp.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.llEditProfile.setOnClickListener {
            Toast.makeText(context, "Edit Profile clicked", Toast.LENGTH_SHORT).show()
            // Handle Edit Profile action
        }

        binding.llChangePassword.setOnClickListener {
            Toast.makeText(context, "Change Password clicked", Toast.LENGTH_SHORT).show()
            // Handle Change Password action
        }

        binding.llPrivacyPolicy.setOnClickListener {
            Toast.makeText(context, "Privacy Policy clicked", Toast.LENGTH_SHORT).show()
            // Handle Privacy Policy action
        }

        binding.llAboutUs.setOnClickListener {
            Toast.makeText(context, "About Us clicked", Toast.LENGTH_SHORT).show()
            // Handle About Us action
        }

        binding.llSignOut.setOnClickListener {
            Toast.makeText(context, "Sign Out clicked", Toast.LENGTH_SHORT).show()
            // Handle Sign Out action
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}