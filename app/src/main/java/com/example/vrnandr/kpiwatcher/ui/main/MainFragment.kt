package com.example.vrnandr.kpiwatcher.ui.main

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.vrnandr.kpiwatcher.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    private lateinit var binding: MainFragmentBinding
    private var callbacks: Callbacks? = null
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    companion object {
        fun newInstance() = MainFragment()
    }
    interface Callbacks {
        fun showDetail()
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = MainFragmentBinding.inflate(inflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.showErrorToast.observe(viewLifecycleOwner, {
            showToast(it)
        })

        viewModel.currentKpi.observe(viewLifecycleOwner, { kpi ->
            Log.d("my", "KPI: $kpi")
            kpi?.let{
                if(binding.viewModel is MainViewModel){
                    val parsedKPI = viewModel.convertKPI(it.kpi)
                    binding.message.text = kpiToColoredText(parsedKPI)
                }
            }
        })

        return binding.root
    }

    private fun kpiToColoredText(parsedKPI: List<MainViewModel.ParsedKPI>):CharSequence{
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
        binding.message.setOnClickListener { callbacks?.showDetail() }
    }

    private fun showToast(msg:String){
        Toast.makeText(activity,msg, Toast.LENGTH_SHORT).show()
    }

}