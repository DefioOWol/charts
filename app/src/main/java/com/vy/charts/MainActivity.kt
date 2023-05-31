package com.vy.charts


import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import java.util.Random


class MainActivity : AppCompatActivity() {

    private lateinit var rightPanel: RecyclerView
    private lateinit var rightPanelAdapter: RRAdapter
    private lateinit var rightPanelList: ArrayList<RVItem>

    private lateinit var bottomPanel: RecyclerView
    private lateinit var bottomPanelAdapter: BRAdapter
    private lateinit var bottomPanelList: ArrayList<RVItem>

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rightPanel = findViewById(R.id.rightPanel)
        rightPanelList = ArrayList()
        rightPanelAdapter = RRAdapter(rightPanelList)
        rightPanel.adapter = rightPanelAdapter

        TouchHelper(rightPanel, rightPanelList, rightPanelAdapter)

        bottomPanel = findViewById(R.id.bottomPanel)
        bottomPanelList = ArrayList()
        bottomPanelAdapter = BRAdapter(bottomPanelList)
        bottomPanel.adapter = bottomPanelAdapter

        TouchHelper(bottomPanel, bottomPanelList, bottomPanelAdapter)

        val coordinateGridView = CoordinateGridView(this, bottomPanelList) { value ->
            value.toString().dropLastWhile { it == '0' }.dropLastWhile { it == '.' } }

        coordinateGridView.viewTreeObserver.addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    coordinateGridView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    coordinateGridView.reset()
                }
            })

        findViewById<LinearLayout>(R.id.mainPanel).addView(coordinateGridView)

        val addSheetButton = findViewById<AppCompatButton>(R.id.addSheetButton)
        addSheetButton.setOnClickListener {
            rightPanelList.add(SheetButton(
                Sheet("New sheet"), coordinateGridView, rightPanelList,
                bottomPanelList, bottomPanelAdapter, addSheetButton)
            )
            rightPanelAdapter.notifyItemInserted(rightPanelList.size)
        }
        addSheetButton.callOnClick()

        val random = Random()
        findViewById<AppCompatButton>(R.id.addChartButton).setOnClickListener {
            val chart = Chart("", Color.rgb(random.nextInt(256),
                              random.nextInt(256), random.nextInt(256)))
            bottomPanelAdapter.getCurrentSheetButton()!!.getSheet().addChart(chart)
            bottomPanelList.add(ChartMediator(chart, coordinateGridView))
            bottomPanelAdapter.notifyItemInserted(bottomPanelList.size)
        }
    }
}
