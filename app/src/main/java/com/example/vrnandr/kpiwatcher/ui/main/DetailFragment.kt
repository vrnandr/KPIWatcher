package com.example.vrnandr.kpiwatcher.ui.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.vrnandr.kpiwatcher.R
import com.example.vrnandr.kpiwatcher.databinding.DetailFragmentBinding

const val ALL_KPI_LIST = "all_kpi_list"
const val VISIBLE_KPI_LIST = "visible_kpi_list"
private const val ONEDAY_MS = 24*60*60*1000f

class DetailFragment : Fragment(),DetailSettingsFragment.DetailSettingsListener {

    private lateinit var binding: DetailFragmentBinding
    private lateinit var viewModel :DetailViewModel// by lazy { ViewModelProvider(this).get(DetailViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        binding = DetailFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        binding.viewModel = viewModel
        binding.chart.apply {
            data = viewModel.getData()
            xAxis.valueFormatter = DetailViewModel.MyXAxisFormatter()
            description.isEnabled = false
            setVisibleXRangeMaximum(5*ONEDAY_MS)
            moveViewToX(System.currentTimeMillis() - 5*ONEDAY_MS)
            setVisibleXRangeMaximum(System.currentTimeMillis().toFloat())
            invalidate()
        }
        
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_fragment_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.detail_fragment_settings -> {
                val settingsDialog = DetailSettingsFragment()
                val bundle = Bundle()
                bundle.putStringArray(ALL_KPI_LIST,viewModel.kpisTitle.toTypedArray())
                bundle.putString(VISIBLE_KPI_LIST,viewModel.visibleKPI)
                settingsDialog.arguments = bundle
                settingsDialog.show(childFragmentManager, "settings_dialog")
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onItemCLick(item: String, isChecked: Boolean) {
        binding.chart.apply {
            data.getDataSetByLabel(item,false).isVisible = isChecked
            invalidate()
        }
    }

    override fun onOkClick() {
        var visibleKPIList =""
        for (ds in binding.chart.data.dataSets)
            if (ds.isVisible) visibleKPIList += ds.label
        viewModel.saveChartKPI(visibleKPIList)
    }
}