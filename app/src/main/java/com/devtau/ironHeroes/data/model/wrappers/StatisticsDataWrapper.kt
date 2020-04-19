package com.devtau.ironHeroes.data.model.wrappers

import com.github.mikephil.charting.data.LineData
import java.util.*

data class StatisticsDataWrapper(
    val lineData: LineData?, val xLabels: List<Calendar>, val xLabelsCount: Int
)