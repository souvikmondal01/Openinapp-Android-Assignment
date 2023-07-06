package com.kivous.openinapp_assignment.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.kivous.openinapp_assignment.R
import com.kivous.openinapp_assignment.adapters.LinkInfoAdapter
import com.kivous.openinapp_assignment.databinding.FragmentHomeBinding
import com.kivous.openinapp_assignment.models.LinkInfo
import com.kivous.openinapp_assignment.utils.Resource
import com.kivous.openinapp_assignment.utils.TokenManager
import com.kivous.openinapp_assignment.utils.Utils.convertObjectToHashmap
import com.kivous.openinapp_assignment.utils.Utils.convertTimestampToDateString
import com.kivous.openinapp_assignment.utils.Utils.sortDates
import com.kivous.openinapp_assignment.utils.Utils.toast
import com.kivous.openinapp_assignment.viewmodels.LinkViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: LinkInfoAdapter
    private val viewModel: LinkViewModel by viewModels()

    @Inject
    lateinit var tokenManager: TokenManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tokenManager.saveToken("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjU5MjcsImlhdCI6MTY3NDU1MDQ1MH0.dCkW0ox8tbjJA2GgUx2UEwNlbTZ7Rr38PVFJevYcXFI")

        viewModel.displayGreeting()
        viewModel.fetchLinkInfo()

        viewModel.linkInfo.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.pb.visibility = View.GONE
                    response.data?.let { response ->
                        val topLinkInfo = response.data.top_links
                        val recentLinkInfo = response.data.recent_links
                        val topLink = arrayListOf<LinkInfo>()
                        val recentLink = arrayListOf<LinkInfo>()
                        for (i in 0..3) {

                            val topLinkData = topLinkInfo[i]
                            val recentLinkData = recentLinkInfo[i]
                            topLink.add(
                                LinkInfo(
                                    topLinkData.app,
                                    convertTimestampToDateString(topLinkData.created_at),
                                    topLinkData.original_image,
                                    topLinkData.smart_link,
                                    topLinkData.total_clicks
                                )
                            )
                            recentLink.add(
                                LinkInfo(
                                    recentLinkData.app,
                                    convertTimestampToDateString(recentLinkData.created_at),
                                    recentLinkData.original_image,
                                    recentLinkData.smart_link,
                                    recentLinkData.total_clicks
                                )
                            )
                        }

                        adapter = LinkInfoAdapter(requireContext(), topLink)
                        binding.recyclerView.adapter = adapter
                        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

                        binding.btnRecentLinks.setOnClickListener {
                            binding.btnRecentLinks.setBackgroundColor(resources.getColor(R.color.blue))
                            binding.btnRecentLinks.setTextColor(resources.getColor(R.color.white))

                            binding.btnTopLinks.setBackgroundColor(resources.getColor(R.color.light_grey))
                            binding.btnTopLinks.setTextColor(resources.getColor(R.color.grey))

                            adapter = LinkInfoAdapter(requireContext(), recentLink)
                            binding.recyclerView.adapter = adapter

                        }

                        binding.btnTopLinks.setOnClickListener {
                            binding.btnTopLinks.setBackgroundColor(resources.getColor(R.color.blue))
                            binding.btnTopLinks.setTextColor(resources.getColor(R.color.white))

                            binding.btnRecentLinks.setBackgroundColor(resources.getColor(R.color.light_grey))
                            binding.btnRecentLinks.setTextColor(resources.getColor(R.color.grey))

                            adapter = LinkInfoAdapter(requireContext(), topLink)
                            binding.recyclerView.adapter = adapter
                        }


                        val chartValueMap =
                            convertObjectToHashmap(response.data.overall_url_chart.toString())
                        val entries = mutableListOf<Entry>()
                        val lineChart = binding.lineChart

                        var index = 0
                        for ((date, value) in chartValueMap) {
                            entries.add(Entry(index.toFloat(), value.toFloat()))
                            index++
                        }

                        val dataSet = LineDataSet(entries, "Chart Label")
                        dataSet.color = resources.getColor(R.color.blue)
                        dataSet.lineWidth = 2f
                        dataSet.circleRadius = 0f
                        dataSet.setCircleColor(resources.getColor(R.color.blue))
                        dataSet.setDrawCircleHole(false)
                        dataSet.setDrawCircles(false)
                        dataSet.valueTextColor = Color.BLACK
                        dataSet.setDrawValues(false)
                        dataSet.setDrawFilled(true)

                        // Create a drawable with the desired gradient colors
                        val startColor = Color.parseColor("#6EA8FF")
                        val endColor = Color.TRANSPARENT
                        val gradientDrawable = GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            intArrayOf(startColor, endColor)
                        )
                        dataSet.fillDrawable = gradientDrawable

                        val lineData = LineData(dataSet)
                        lineData.setValueTextSize(10f)

                        lineChart.data = lineData

                        // Customize chart appearance
                        lineChart.setNoDataText("No data available")
                        lineChart.setNoDataTextColor(Color.GRAY)
                        lineChart.setBackgroundColor(Color.WHITE)
                        lineChart.setTouchEnabled(false)

                        val description = Description()
                        description.text = ""
                        lineChart.description = description

                        lineChart.animateX(2000, Easing.EaseInOutQuart)
                        lineChart.animateY(1000, Easing.EaseInOutQuart)

                        val legend = lineChart.legend
                        legend.isEnabled = false

                        val xAxis = lineChart.xAxis
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.textColor = Color.GRAY
                        xAxis.textSize = 10f
                        xAxis.granularity = 1f
                        xAxis.setDrawAxisLine(true)
                        xAxis.setDrawGridLines(true)
                        xAxis.enableGridDashedLine(5f, 5f, 0f)
                        val sortedList = sortDates(chartValueMap.keys.toList())
                        xAxis.gridColor = Color.GRAY
                        lineChart.xAxis.valueFormatter =
                            IndexAxisValueFormatter(sortedList)

                        binding.tvDuration.text = "${sortedList.first()} - ${sortedList.last()}"

                        val leftAxis = lineChart.axisLeft
                        leftAxis.textColor = Color.GRAY
                        leftAxis.textSize = 10f
                        leftAxis.axisMinimum = 0f
                        leftAxis.enableGridDashedLine(5f, 5f, 0f)
                        leftAxis.setDrawGridLines(true)
                        leftAxis.setDrawAxisLine(true)
                        leftAxis.gridColor = Color.GRAY

                        val rightAxis = lineChart.axisRight
                        rightAxis.isEnabled = false

                        lineChart.invalidate()
                    }

                }

                is Resource.Error -> {
                    binding.pb.visibility = View.GONE
                    response.message?.let {
                        toast(it)
                    }
                }

                is Resource.Loading -> {
                    binding.pb.visibility = View.VISIBLE
                }

            }


        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}