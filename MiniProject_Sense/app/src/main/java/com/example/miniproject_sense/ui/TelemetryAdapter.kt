package com.example.miniproject_sense.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.miniproject_sense.db.TelemetryEntity

class TelemetryAdapter(private var items: List<TelemetryEntity>) :
    RecyclerView.Adapter<TelemetryAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tv: TextView = v.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val it = items[position]
        holder.tv.text = "ts=${it.ts} | T=${it.temperature} | H=${it.humidity} | mov=${it.motionValue}"
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<TelemetryEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}