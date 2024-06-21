package com.isalatapp.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.isalatapp.databinding.FragmentEditProfileBinding
import com.isalatapp.helper.model.AuthViewModel
import com.isalatapp.helper.response.UserRecord
import com.isalatapp.ui.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.edEditBirthday.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            saveProfile()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.userRecord.observe(viewLifecycleOwner) { userRecord ->
            userRecord?.let {
                binding.edEditName.setText(it.name)
                binding.edEditPhone.setText(it.phone)
                binding.edEditBirthday.setText(it.dob)
            }
        }
    }

    private fun saveProfile() {
        val name = binding.edEditName.text.toString().trim()
        val phone = binding.edEditPhone.text.toString().trim()
        val dob = binding.edEditBirthday.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty() || dob.isEmpty()) {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val currentUser = viewModel.userRecord.value
        if (currentUser != null) {
            val updatedUser = UserRecord(
                email = currentUser.email,
                name = name,
                phone = phone,
                dob = dob
            )

            binding.progressBar.visibility = View.VISIBLE // Show loading indicator

            viewModel.editProfile(updatedUser).observe(viewLifecycleOwner) { result ->
                binding.progressBar.visibility = View.GONE // Hide loading indicator

                if (result) {
                    Toast.makeText(requireContext(), "Profile Updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Failed to get user details", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            // Convert the selected date to a readable format
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            val date = Date(selection)
            val formattedDate = sdf.format(date)

            // Set the selected date to the EditText
            binding.edEditBirthday.setText(formattedDate)
        }

        datePicker.show(parentFragmentManager, "datePicker")
    }
}
