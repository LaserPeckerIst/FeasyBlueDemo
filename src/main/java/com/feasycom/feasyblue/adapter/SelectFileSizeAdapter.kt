package com.feasycom.feasyblue.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feasycom.feasyblue.R
import kotlinx.android.synthetic.main.file_size_item.view.*

class SelectFileSizeAdapter: RecyclerView.Adapter<SelectFileSizeAdapter.ViewHolder>() {

    private val items = listOf("10K", "1M", "50K", "2MB", "100K", "5MB", "200KB", "10MB", "500KB", "20MB")

    var onClickListener: ((size: Int, data: String) -> Unit)? = null

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.file_size_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.text.text = items[position]
        holder.itemView.setOnClickListener {
            onClickListener?.invoke(when(position){
                0 -> {
                    10000
                }
                1 -> {
                    1000000
                }
                2 -> {
                    50000
                }
                3 -> {
                    2000000
                }
                4 -> {
                    100000
                }
                5 -> {
                    5000000
                }
                6 -> {
                    200000
                }
                7 -> {
                    10000000
                }
                8 -> {
                    500000
                }
                9 -> {
                    20000000
                }
                else -> {
                    0
                }
            }, items[position])
        }
    }

    override fun getItemCount() = items.size

    companion object{
        private const val TAG = "SelectFileSizeAdapter"
    }
}