package com.isalatapp.ui.camera

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import com.isalatapp.R
import com.isalatapp.databinding.FragmentModeCameraBinding

class ModeCameraFragment : Fragment() {

    private var _binding: FragmentModeCameraBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModeCameraBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("ModeCameraFragment", "onViewCreated called")

        // Set default fragment to ObjectDetectionFragment
        replaceFragment(IsalatModelFragment())
        binding.toggleButtonMode.setOnCheckedChangeListener { _, isChecked ->
            val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.toggle_button_animation)
            binding.toggleButtonMode.startAnimation(animation)
        }

        binding.toggleButtonMode.setOnCheckedChangeListener { _, isChecked ->
            val fragment = if (isChecked) {
                ObjectDetectionFragment()
            } else {
                IsalatModelFragment()
            }

            val currentFragment =
                childFragmentManager.findFragmentById(R.id.modeCameraFragmentContainer)
            currentFragment?.viewLifecycleOwner?.lifecycle?.addObserver(FragmentLifecycleObserver {
                Handler(Looper.getMainLooper()).post {
                    replaceFragment(fragment)
                }
            })

            replaceFragment(fragment)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        Log.d("ModeCameraFragment", "Replacing fragment with: ${fragment::class.java.simpleName}")
        childFragmentManager.beginTransaction()
            .replace(R.id.modeCameraFragmentContainer, fragment)
            .commitAllowingStateLoss()
        childFragmentManager.executePendingTransactions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
