package com.vy.charts


import android.annotation.SuppressLint
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView


class RRAdapter(private val list: ArrayList<RVItem>)
    : RecyclerView.Adapter<RRAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val editText: AppCompatEditText = itemView.findViewById(R.id.sheetButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RRAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.sheet_button, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RRAdapter.ViewHolder, position: Int) {
        val item = list[position] as SheetButton

        holder.editText.text = SpannableStringBuilder(item.getSheet().getName())
        item.setEditText(holder.editText)
        if (list.size == 1) { item.execute() }

        holder.editText.setOnTouchListener { _, event ->
            item.getGestureDetector().onTouchEvent(event) }

        holder.editText.setOnFocusChangeListener { v, hasFocus -> run {
            if (!hasFocus) {
                KeyboardHelper.hide(item.getContext(), v)
                holder.editText.isClickable = true
            }
        } }

        holder.editText.addTextChangedListener { s ->
            run { item.getSheet().setName(s.toString()) } }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
