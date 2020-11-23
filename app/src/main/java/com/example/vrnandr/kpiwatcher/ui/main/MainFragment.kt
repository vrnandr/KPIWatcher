package com.example.vrnandr.kpiwatcher.ui.main

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.vrnandr.kpiwatcher.database.KpiDatabase
import com.example.vrnandr.kpiwatcher.databinding.MainFragmentBinding

import com.example.vrnandr.kpiwatcher.R

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    //private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val application = requireNotNull(this.activity).application
        val dataSource = KpiDatabase.getInstance(application).kpiDao
        val viewModelFactory = MainModelFactory(dataSource, application)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        val binding = MainFragmentBinding.inflate(inflater)

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

                    var ss: SpannableString
                    //var s = ""
                    val rs = SpannableStringBuilder("")
                    for( i in parsedKPI){
                        ss = SpannableString("${i.value}      ${i.text}\n")
                        var color = Color.GREEN
                        when(i.color){
                            "orange" -> color = Color.parseColor("#FFA500")
                            "red" -> color = Color.RED
                        }
                        ss.setSpan(ForegroundColorSpan(color),0,i.value.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        rs.append(ss)
                        //s += i.value + " " + i.text + "\n"
                    }

                    binding.message.text = rs.dropLast(1)
                }

            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    private fun showToast(msg:String){
        Toast.makeText(activity,msg, Toast.LENGTH_SHORT).show()
    }

}