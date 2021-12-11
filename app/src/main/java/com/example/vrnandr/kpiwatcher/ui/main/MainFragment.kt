package com.example.vrnandr.kpiwatcher.ui.main

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vrnandr.kpiwatcher.R
import com.example.vrnandr.kpiwatcher.databinding.MainFragmentBinding
import com.example.vrnandr.kpiwatcher.utility.ParsedKPI
import com.example.vrnandr.kpiwatcher.utility.convertKPI
import timber.log.Timber

class MainFragment : Fragment() {

    private lateinit var binding: MainFragmentBinding
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)

        binding = MainFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.currentKpi.observe(viewLifecycleOwner, { kpi ->
            Timber.d("KPI: $kpi")
            if (kpi !=null) {
                if(binding.viewModel is MainViewModel){
                    val parsedKPI = convertKPI(kpi.kpi)
                    binding.message.text = kpiToColoredText(parsedKPI)
                }
            } else
                binding.message.text = ""
        })
        viewModel.successKPIRequestEvent.observe(viewLifecycleOwner, { hideProgressBar() })

        viewModel.showToastEvent.observe(viewLifecycleOwner, { hideProgressBar()})

        viewModel.showErrorToastEvent.observe(viewLifecycleOwner, { hideProgressBar()})

        return binding.root
    }

    private fun kpiToColoredText(parsedKPI: List<ParsedKPI>):CharSequence{
        val rs = SpannableStringBuilder("")
        for (i in parsedKPI) {
            val ss = SpannableString("${i.value}      ${i.text}\n")
            var color = Color.GREEN
            when (i.color) {
                "orange" -> color = Color.parseColor("#FFA500")
                "red" -> color = Color.RED
            }
            ss.setSpan(ForegroundColorSpan(color), 0, i.value.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            rs.append(ss)
        }
        return rs.dropLast(1)
    }

    override fun onStart() {
        super.onStart()
        binding.updateButton.setOnClickListener {
            showProgressBar()
            viewModel.onKPIButtonClick()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_fragment,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.logout -> {
                viewModel.deleteCredentials()
                binding.message.text = ""
                binding.about.text = ""
                findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showProgressBar(){
        binding.progressBar.visibility = View.VISIBLE
        binding.updateButton.isEnabled = false
    }

    private fun hideProgressBar(){
        binding.progressBar.visibility = View.GONE
        binding.updateButton.isEnabled = true
    }

}