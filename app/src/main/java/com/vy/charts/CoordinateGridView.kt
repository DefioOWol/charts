package com.vy.charts


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import kotlin.math.round


@SuppressLint("ViewConstructor")
class CoordinateGridView(
    context: Context,
    private val chartMediators: ArrayList<RVItem>,
    private val floatStrFormatter: FloatStrFormatter
) : View(context) {

    private val gridPaint = Paint().apply {
        color = Color.LTGRAY
        strokeWidth = 2f
    }

    private val axisPaint = Paint().apply {
        color = Color.DKGRAY
        strokeWidth = 4f
    }

    private val chartPaint = Paint().apply {
        strokeWidth = 6f
    }

    private val textPaint = Paint().apply {
        color = Color.DKGRAY
        textSize = 25f
    }

    private var touchX = 0f
    private var touchY = 0f

    private var offsetX = 0f
    private var offsetY = 0f

    private var startX = 0f
    private var startY = 0f

    private var valueX = 0f
    private var valueY = 0f

    private var basicLineStep = 0f
    private var lineStep = 0f

    private var valueStep = 0f
    private var textOffset = 0f

    private var basicPartWidth = 0f
    private var partWidth = 0f

    private var basicPartHeight = 0f
    private var partHeight = 0f

    private var scaleGestureDetector: ScaleGestureDetector

    init {
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        private var scaleFactor = 0f
        private var offsetXOnBegin = 0f
        private var offsetYOnBegin = 0f
        private var scaleFocusX = 0f
        private var scaleFocusY = 0f

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            scaleFactor = 1f
            offsetXOnBegin = offsetX
            offsetYOnBegin = offsetY
            scaleFocusX = detector.focusX
            scaleFocusY = detector.focusY
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            lineStep *= detector.scaleFactor
            partWidth *= detector.scaleFactor
            partHeight *= detector.scaleFactor

            if (lineStep >= basicLineStep * 2) {
                lineStep = basicLineStep
                partWidth = basicPartWidth
                partHeight = basicPartHeight
                valueStep /= 2
            } else if (lineStep <= basicLineStep) {
                lineStep = basicLineStep * 2
                partWidth = basicPartWidth * 2
                partHeight = basicPartHeight * 2
                valueStep *= 2
            }

            offsetX = offsetXOnBegin * scaleFactor + scaleFocusX * (1 - scaleFactor)
            offsetY = offsetYOnBegin * scaleFactor + scaleFocusY * (1 - scaleFactor)
            return true
        }
    }

    fun reset() {
        offsetX = width.toFloat() / 2
        offsetY = height.toFloat() / 2

        basicLineStep = width.toFloat() / 5
        lineStep = basicLineStep

        valueStep = 5f
        textOffset = width.toFloat() * 0.005f

        basicPartWidth = (width.toFloat() / lineStep).toInt() * lineStep
        partWidth = basicPartWidth

        basicPartHeight = (height.toFloat() / lineStep).toInt() * lineStep
        partHeight = basicPartHeight

        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleGestureDetector.onTouchEvent(event!!)
        if (event.actionMasked == MotionEvent.ACTION_MOVE) {
            offsetX += event.x - touchX
            offsetY += event.y - touchY
            invalidate()
        }
        touchX = event.x
        touchY = event.y
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        startX = offsetX % partWidth + if (offsetX < 0) { partWidth } else { 0f }
        startY = offsetY % partHeight + if (offsetY < 0) { partHeight } else { 0f }

        val counts = drawGrid(canvas, width, height)

        if (offsetX in 0f..width) {
            canvas?.drawLine(offsetX, 0f, offsetX, height, axisPaint)
        }
        if (offsetY in 0f..height) {
            canvas?.drawLine(0f, offsetY, width, offsetY, axisPaint)
        }

        valueX = round((startX - offsetX) / lineStep) * valueStep
        valueY = round((offsetY - startY) / lineStep) * valueStep

        drawCharts(canvas, valueX - valueStep * counts.first,
                   valueX + valueStep * counts.second,
                   valueStep / 250 * basicLineStep / lineStep)

        drawText(canvas, width, height)
    }

    private fun drawGrid(canvas: Canvas?, width: Float, height: Float): Pair<Int, Int> {
        var rightCount = 0
        var curPoint = startX
        while (curPoint <= width) {
            canvas?.drawLine(curPoint, 0f, curPoint, height, gridPaint)
            curPoint += lineStep
            rightCount++
        }

        var leftCount = 1
        curPoint = startX - lineStep
        while (curPoint >= 0) {
            canvas?.drawLine(curPoint, 0f, curPoint, height, gridPaint)
            curPoint -= lineStep
            leftCount++
        }

        curPoint = startY
        while (curPoint <= height) {
            canvas?.drawLine(0f, curPoint, width, curPoint, gridPaint)
            curPoint += lineStep
        }

        curPoint = startY - lineStep
        while (curPoint >= 0) {
            canvas?.drawLine(0f, curPoint, width, curPoint, gridPaint)
            curPoint -= lineStep
        }

        return Pair(leftCount, rightCount)
    }

    private fun translateX(x: Float): Float {
        return startX + (x - valueX) / valueStep * lineStep
    }

    private fun translateY(y: Float): Float {
        return startY - (y - valueY) / valueStep * lineStep
    }

    private fun drawCharts(canvas: Canvas?, start: Float, end: Float, step: Float) {
        for (chartMediator in chartMediators) {
            val chart = (chartMediator as ChartMediator).getChart()
            chartPaint.color = chart.getColor()

            val values = chart.getValuesOnInterval(start, end, step)
            val valuesX = values.first
            val valuesY = values.second

            for (i in 0 until valuesY.size) {
                if (!valuesY[i].isNaN()) {
                    canvas?.drawPoint(translateX(valuesX[i]), translateY(valuesY[i]), chartPaint)
                }
            }
        }
    }

    private fun drawYValue(canvas: Canvas?, value: Float, point: Float) {
        val string = floatStrFormatter.convert(value)
        val measure = textPaint.measureText(string)

        val valueOffset = if (offsetX - measure - 2 * textOffset <= 0) {
            measure + textOffset
        } else if (offsetX >= width) {
            width - textOffset
        } else {
            offsetX - textOffset
        }

        canvas?.drawText(string, valueOffset, point, textPaint)
    }

    private fun drawText(canvas: Canvas?, width: Float, height: Float) {
        var count = 1
        var curPoint = startX + lineStep + textOffset
        textPaint.textAlign = Paint.Align.LEFT

        val valueOffset = if (offsetY <= 0) {
            textPaint.textSize
        } else if (offsetY + textPaint.textSize + textOffset >= height) {
            height - textOffset
        } else {
            offsetY + textPaint.textSize
        }

        while (curPoint <= width) {
            canvas?.drawText(floatStrFormatter.convert(valueX + valueStep * count++),
                             curPoint, valueOffset, textPaint)
            curPoint += lineStep
        }

        count = 0
        curPoint = startX + textOffset
        var value = floatStrFormatter.convert(valueX - valueStep * count++)
        var measure = textPaint.measureText(value)

        while (curPoint + measure >= 0) {
            canvas?.drawText(value, curPoint, valueOffset, textPaint)
            value = floatStrFormatter.convert(valueX - valueStep * count++)
            measure = textPaint.measureText(value)
            curPoint -= lineStep
        }

        count = 0
        curPoint = startY - textOffset
        textPaint.textAlign = Paint.Align.RIGHT

        while (curPoint - textPaint.textSize <= height) {
            drawYValue(canvas, valueY - valueStep * count++, curPoint)
            curPoint += lineStep
        }

        count = 1
        curPoint = startY - lineStep - textOffset

        while (curPoint >= 0) {
            drawYValue(canvas, valueY + valueStep * count++, curPoint)
            curPoint -= lineStep
        }
    }
}
