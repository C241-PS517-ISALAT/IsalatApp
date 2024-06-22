package com.isalatapp.ui.home

import NewsFragment
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.isalatapp.R
import com.isalatapp.databinding.FragmentHomeBinding
import com.isalatapp.helper.model.AuthViewModel
import com.isalatapp.ui.ViewModelFactory
import com.isalatapp.ui.camera.IsalatModelFragment
import com.isalatapp.ui.camera.ObjectDetectionFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeSession()

        binding.apply {
            btnNews.setOnClickListener {
                childFragmentManager.beginTransaction().apply {
                    replace(R.id.newsFragmentContainer, NewsFragment())
                    commit()
                }
            }
            btnTranslate.setOnClickListener {
                childFragmentManager.beginTransaction().apply {
                    replace(R.id.newsFragmentContainer, IsalatModelFragment())
                    commit()
                }
            }
            btnObjectDetect.setOnClickListener {
                childFragmentManager.beginTransaction().apply {
                    replace(R.id.newsFragmentContainer, ObjectDetectionFragment())
                    commit()
                }
            }
            btnDictionary.setOnClickListener {
                childFragmentManager.beginTransaction().apply {
                    replace(R.id.newsFragmentContainer, DictionaryFragment())
                    commit()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeSession() {
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            user?.let {
                val text = it.name
                val spannableString = SpannableString("Welcome, $text")
                val startIndex = spannableString.indexOf(text)
                val foregroundColorSpan =
                    ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary))
                spannableString.setSpan(
                    foregroundColorSpan,
                    startIndex,
                    startIndex + text.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.tvTitle.text = spannableString
            }
        }
    }
}
