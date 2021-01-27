package com.example.vrnandr.kpiwatcher.ui.main

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import com.example.vrnandr.kpiwatcher.R
import com.example.vrnandr.kpiwatcher.databinding.MainFragmentBinding
import com.example.vrnandr.kpiwatcher.utility.*
import timber.log.Timber

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
        fun logout()
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)

        binding = MainFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.useLogFile.isChecked = viewModel.useLogFile

        viewModel.showErrorToast.observe(viewLifecycleOwner, {
            showToast(it)
        })

        viewModel.messageToShow.observe(viewLifecycleOwner,{ showToast(it)})

        viewModel.currentKpi.observe(viewLifecycleOwner, { kpi ->
            Timber.d("KPI: $kpi")
            kpi?.let{
                if(binding.viewModel is MainViewModel){
                    val parsedKPI = convertKPI(it.kpi)
                    binding.message.text = kpiToColoredText(parsedKPI)
                }
            }
        })


        viewModel.time.observe(viewLifecycleOwner, {
            WorkManager.getInstance(requireContext()).cancelAllWork()
            binding.stopWorkerButton.visibility = View.GONE
        })

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
        //TODO показ детализации пока не реализован
        binding.message.setOnClickListener { callbacks?.showDetail() }
        binding.useLogFile.setOnClickListener { viewModel.onClick(binding.useLogFile) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_fragment,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.logout -> {
                callbacks?.logout()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showToast(msg:String){
        Toast.makeText(activity,msg, Toast.LENGTH_SHORT).show()
    }

}