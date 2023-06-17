package com.d3if3059.taskify.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.d3if3059.taskify.R
import com.d3if3059.taskify.data.About
import com.d3if3059.taskify.databinding.FragmentAboutBinding
import com.d3if3059.taskify.network.AboutApi
import com.d3if3059.taskify.network.ApiStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : Fragment(R.layout.fragment_about) {
    private lateinit var aboutViewModel: AboutViewModel
    private lateinit var _binding: FragmentAboutBinding
    private val viewModel: AboutViewModel by viewModels()
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
        viewModel.getStatus().observe(viewLifecycleOwner) {
            updateProgress(it)
        }
    }
    private fun updateProgress(status: ApiStatus) {
        when (status) {
            ApiStatus.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.logo.visibility = View.GONE
                binding.appName.visibility = View.GONE
                binding.appVersion.visibility = View.GONE
                binding.licenseTitle.visibility = View.GONE
                binding.desc.visibility = View.GONE
                binding.visitTitle.visibility = View.GONE
                binding.visitURL.visibility = View.GONE
                binding.license.visibility = View.GONE
                binding.author.visibility = View.GONE
            }
            ApiStatus.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
                binding.logo.visibility = View.VISIBLE
                binding.appName.visibility = View.VISIBLE
                binding.appVersion.visibility = View.VISIBLE
                binding.licenseTitle.visibility = View.VISIBLE
                binding.desc.visibility = View.VISIBLE
                binding.visitTitle.visibility = View.VISIBLE
                binding.visitURL.visibility = View.VISIBLE
                binding.license.visibility = View.VISIBLE
                binding.author.visibility = View.VISIBLE
            }
            ApiStatus.FAILED -> {
                binding.progressBar.visibility = View.GONE
                binding.networkError.visibility = View.VISIBLE
            }
        }
    }

    private fun bind(about: About) {
        binding.apply {
            binding.author.text = about.author
            binding.appName.text = about.appName
            binding.appVersion.text = about.appVersion
            binding.visitURL.setOnClickListener {
                launchBrowser(about.visitURL)
            }
            binding.desc.text = about.desc
            binding.license.text = about.license
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
