package com.vy.charts


import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText


class SheetButton (
    private val sheet: Sheet,
    private val coordinateGridView: CoordinateGridView,
    private val sheetButtons: ArrayList<RVItem>,
    private val chartMediators: ArrayList<RVItem>,
    private val chartAdapter: BRAdapter,
    private val addSheetButton: AppCompatButton
) : RVItem, Executor {

    private lateinit var editText: AppCompatEditText
    private val gestureDetector = GestureDetector(coordinateGridView.context, GestureListener())

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (editText.isClickable) {
                execute()
            }
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            editText.isClickable = false
            KeyboardHelper.show(getContext(), editText)
            return true
        }
    }

    fun setEditText(editText: AppCompatEditText) {
        this.editText = editText
    }

    private fun getEditText(): AppCompatEditText {
        return editText
    }

    fun getSheet(): Sheet {
        return sheet
    }

    fun getContext(): Context {
        return coordinateGridView.context
    }

    fun getGestureDetector(): GestureDetector {
        return gestureDetector
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDelete(position: Int) {
        if (sheetButtons.size == 0) {
            addSheetButton.callOnClick()
        } else if (chartAdapter.getCurrentSheetButton() == this) {
            if (position > 0) {
                (sheetButtons[position - 1] as SheetButton).execute()
            } else {
                (sheetButtons[position] as SheetButton).execute()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun execute() {
        coordinateGridView.reset()
        chartAdapter.getCurrentSheetButton()?.getEditText()
            ?.setBackgroundResource(R.drawable.solid_btn)
        editText.setBackgroundResource(R.drawable.solid_stroke_btn)
        chartAdapter.setCurrentSheetButton(this)

        chartMediators.clear()
        chartAdapter.notifyDataSetChanged()
        val charts = sheet.getCharts()
        for (chart in charts) {
            chartMediators.add(ChartMediator(chart, coordinateGridView))
        }
        chartAdapter.notifyDataSetChanged()
        coordinateGridView.invalidate()
    }
}
