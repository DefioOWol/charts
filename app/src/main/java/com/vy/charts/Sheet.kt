package com.vy.charts


data class Sheet(private var name: String) {
    private val charts = ArrayList<Chart>()

    fun setName(name: String) {
        this.name = name
    }

    fun getName(): String {
        return name
    }

    fun addChart(chart: Chart) {
        charts.add(chart)
    }

    fun delChart(chart: Chart) {
        charts.remove(chart)
    }

    fun getCharts(): ArrayList<Chart> {
        return charts
    }
}
