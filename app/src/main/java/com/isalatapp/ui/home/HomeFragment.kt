package com.isalatapp.ui.home

import NewsFragment
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.isalatapp.R
import com.isalatapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // Load the MenuFragment into the FragmentContainerView
        childFragmentManager.beginTransaction().apply {
            replace(R.id.newsFragmentContainer, MenuFragment())
            commit()
        }

        setupView()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupView() {
        binding.apply {
            val text = "John Doe"
            val spannableString = SpannableString("Welcome, $text")
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
