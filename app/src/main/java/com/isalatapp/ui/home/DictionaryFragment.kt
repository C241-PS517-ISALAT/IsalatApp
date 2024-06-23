package com.isalatapp.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.isalatapp.R
import com.isalatapp.adapter.DictionaryAdapter
import com.isalatapp.api.GitHubApiHelper
import com.isalatapp.databinding.FragmentDictionaryBinding

class DictionaryFragment : Fragment() {

    private lateinit var binding: FragmentDictionaryBinding
    private lateinit var adapter: DictionaryAdapter
    private val bisindoAlphabet = mutableListOf<Pair<String, List<String>>>()
    private val handler = Handler(Looper.getMainLooper())
    private val currentIndexMap = mutableMapOf<String, Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dictionaryRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = DictionaryAdapter(bisindoAlphabet) { letter, urls ->
            showNextImage(letter, urls)
        }
        binding.dictionaryRecyclerView.adapter = adapter

        binding.progressBar.visibility = View.VISIBLE

        GitHubApiHelper.getGithubFiles { files ->
            handler.post {
                bisindoAlphabet.clear()
                bisindoAlphabet.addAll(files)
                adapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showNextImage(letter: String, urls: List<String>) {
        val currentIndex = currentIndexMap[letter] ?: 0
        val nextIndex = (currentIndex + 1) % urls.size
        currentIndexMap[letter] = nextIndex

        val nextImageUrl = urls[nextIndex]

        val imageView = view?.findViewById<ImageView>(R.id.alphabetImageView)
        if (imageView != null) {
            Glide.with(this)
                .load(nextImageUrl)
                .into(imageView)
        }
    }
}
