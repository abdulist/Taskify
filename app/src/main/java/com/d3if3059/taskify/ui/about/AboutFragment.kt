package com.d3if3059.taskify.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.d3if3059.taskify.R
import com.d3if3059.taskify.data.About
import com.d3if3059.taskify.databinding.FragmentAboutBinding
import com.d3if3059.taskify.network.AboutApi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : Fragment(R.layout.fragment_about) {
    private lateinit var aboutViewModel: AboutViewModel
    private lateinit var _binding: FragmentAboutBinding
    private val binding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        aboutViewModel = ViewModelProvider(this).get(AboutViewModel::class.java)

        aboutViewModel.getData().observe(viewLifecycleOwner) { aboutList ->
            if (aboutList.isNotEmpty()) {
                val about = aboutList[0]
                bind(about)
            }
        }
    }
    private fun bind(about: About) {
        binding.apply {
            author.text = about.author
            appName.text = about.appName
            appVersion.text = about.appVersion
            visitURL.setOnClickListener {
                launchBrowser(about.visitURL)
            }
            desc.text = about.desc
            license.text = about.license
            Glide.with(logo.context)
                .load(AboutApi.getAboutUrl(about.imageId))
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(logo)
        }
    }

    private fun launchBrowser(url: String) = Intent(Intent.ACTION_VIEW, Uri.parse(url)).also {
        startActivity(it)
    }
}
