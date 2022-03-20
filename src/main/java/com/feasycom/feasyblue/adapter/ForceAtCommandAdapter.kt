package com.feasycom.feasyblue.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.utils.ToastUtil
import kotlinx.android.synthetic.main.force_at_command_item.view.*

class ForceAtCommandAdapter(private val labelList: List<String>): RecyclerView.Adapter<ForceAtCommandAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.force_at_command_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView){
            label.text = labelList[position]
            checkFlag.setOnCheckedChangeListener { buttonView, isChecked ->
                if (contextEditText.text.toString().isNotEmpty()) {
                    contextEditText.isEnabled = !isChecked
                } else {
                    checkFlag.isChecked = false
                    ToastUtil.show(context, resources.getString(R.string.none))
                }
            }
        }

    }

    override fun getItemCount() = labelList.size

}