package com.isalatapp.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.isalatapp.R

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.isalatapp.adapter.DictionaryAdapter
import com.isalatapp.api.GitHubApiHelper
import com.isalatapp.databinding.FragmentDictionaryBinding
import com.isalatapp.databinding.ItemDictionaryBinding

class DictionaryFragment : Fragment() {

    private lateinit var binding: FragmentDictionaryBinding
    private lateinit var adapter: DictionaryAdapter
    private val bisindoAlphabet = mutableListOf<Pair<String, String>>()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dictionaryRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = DictionaryAdapter(bisindoAlphabet) { letter, url ->
            Toast.makeText(context, "Clicked on $letter", Toast.LENGTH_SHORT).show()
        }
        binding.dictionaryRecyclerView.adapter = adapter

        binding.progressBar.visibility = View.VISIBLE

        GitHubApiHelper.getGithubFiles { files ->
            handler.post {
                bisindoAlphabet.clear()
                files.forEachIndexed { index, fileUrl ->
                    bisindoAlphabet.add((65 + index % 26).toChar().toString() to fileUrl)
                }
                adapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}