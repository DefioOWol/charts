package com.vy.charts


import android.graphics.Color
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView


class BRAdapter(private val list: ArrayList<RVItem>)
    : RecyclerView.Adapter<BRAdapter.ViewHolder>() {

    private var currentSheetButton: SheetButton? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val formulaField: AppCompatEditText = itemView.findViewById(R.id.formulaField)
        val colorButton: AppCompatButton = itemView.findViewById(R.id.colorButton)
    }

    fun setCurrentSheetButton(currentSheetButton: SheetButton?) {
        this.currentSheetButton = currentSheetButton
    }

    fun getCurrentSheetButton(): SheetButton? {
        return currentSheetButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BRAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chart_form, parent, false)
        return ViewHolder(itemView)
    }

    private fun changeFormulaField(formulaField: AppCompatEditText, isCorrect: Boolean) {
        if (!isCorrect) {
            formulaField.setBackgroundColor(
                Color.argb(100, 255, 0, 0)
            )
        } else {
            formulaField.setBackgroundColor(
                Color.argb(0, 0, 0, 0)
            )
        }
    }

    override fun onBindViewHolder(holder: BRAdapter.ViewHolder, position: Int) {
        val item = list[position] as ChartMediator

        item.setEditText(holder.formulaField)
        item.setColorButton(holder.colorButton)
        item.setSheetButton(currentSheetButton!!)

        holder.formulaField.text = SpannableStringBuilder(item.getChart().getFormula())
        holder.colorButton.setBackgroundColor(item.getChart().getColor())
        holder.colorButton.setOnClickListener { item.execute() }
        changeFormulaField(holder.formulaField, item.getChart().getIsFormulaCorrect())

        holder.formulaField.addTextChangedListener { s -> run {
            item.getChart().setFormula(s.toString())
            changeFormulaField(holder.formulaField, item.getChart().getIsFormulaCorrect())
            item.invalidateCoordinateGrid()
        } }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
