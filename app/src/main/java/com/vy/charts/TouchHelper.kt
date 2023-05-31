package com.vy.charts


import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class TouchHelper(
    panel: RecyclerView,
    list: ArrayList<RVItem>,
    adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>
) {
    init {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean { return false }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val delItem = list[position]
                list.removeAt(position)
                delItem.onDelete(position)
                adapter.notifyItemRemoved(position)
            }
        }).attachToRecyclerView(panel)
    }
}
