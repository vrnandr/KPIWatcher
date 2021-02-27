package com.example.vrnandr.kpiwatcher.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.vrnandr.kpiwatcher.databinding.DetailFragmentBinding

class DetailFragment : Fragment() {

    private lateinit var binding: DetailFragmentBinding
    private lateinit var viewModel :DetailViewModel// by lazy { ViewModelProvider(this).get(DetailViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DetailFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        binding.viewModel = viewModel
        binding.chart.apply {
            data = viewModel.getData()
            xAxis.valueFormatter = DetailViewModel.MyXAxisFormatter()
            description.isEnabled = false
            invalidate()
        }
        
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = DetailFragment()
    }
}