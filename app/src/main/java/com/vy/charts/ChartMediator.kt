package com.vy.charts


import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder


class ChartMediator(
    private val chart: Chart,
    private val coordinateGridView: CoordinateGridView
) : RVItem, Executor {

    private lateinit var editText: AppCompatEditText
    private lateinit var colorButton: AppCompatButton
    private lateinit var sheetButton: SheetButton

    fun setEditText(editText: AppCompatEditText) {
        this.editText = editText
    }

    fun setSheetButton(sheetButton: SheetButton) {
        this.sheetButton = sheetButton
    }

    fun setColorButton(colorButton: AppCompatButton) {
        this.colorButton = colorButton
    }

    fun getChart(): Chart {
        return chart
    }

    fun invalidateCoordinateGrid() {
        coordinateGridView.invalidate()
    }

    override fun onDelete(position: Int) {
        sheetButton.getSheet().delChart(chart)
        coordinateGridView.invalidate()
    }

    override fun execute() {
        ColorPickerDialogBuilder.with(coordinateGridView.context)
            .initialColor(chart.getColor()).density(13)
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .setNegativeButton("Cancel") { _, _ -> run {} }
            .setPositiveButton("Select") {
                _, selectedColor, _ -> run {
                    chart.setColor(selectedColor)
                    colorButton.setBackgroundColor(selectedColor)
                    coordinateGridView.invalidate()
                }
            }.build().show()
    }
}
