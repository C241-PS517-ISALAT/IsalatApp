package com.isalatapp.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.isalatapp.R
import com.isalatapp.api.ResultState
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
        val builder =
            AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
        builder.setView(R.layout.progress_indicator)
        val dialog: AlertDialog = builder.create()
        observeViewModel(dialog)
    }

    private fun saveProfile() {
        val name = binding.edEditName.text.toString()
        val phone = binding.edEditPhone.text.toString()
        val dob = binding.edEditBirthday.text.toString()

        val updatedProfile = UserRecord(
            name = name,
            phone = phone,
            dob = dob
        )
        viewModel.updateProfile(updatedProfile)
        parentFragmentManager.beginTransaction().apply {
            replace(
                R.id.fragment_container,
                AccountFragment(),
                AccountFragment::class.java.simpleName
            )
            commit()
        }
    }

    private fun observeViewModel(dialog: AlertDialog) {
        viewModel.userProfile.observe(viewLifecycleOwner) { authResponse ->
            authResponse.let { userRecord ->
                binding.edEditName.setText(userRecord.name)
                binding.edEditPhone.setText(userRecord.phone)
                binding.edEditBirthday.setText(userRecord.dob)
            }
        }
        viewModel.responseResult.observe(viewLifecycleOwner) { resultState ->
            when (resultState) {
                is ResultState.Loading -> {
                    dialog.show()
                }

                is ResultState.Success -> {
                    dialog.dismiss()
                }

                is ResultState.Error -> {
                    dialog.dismiss()
                    Toast.makeText(requireContext(), resultState.error, Toast.LENGTH_SHORT).show()
                }
            }
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
