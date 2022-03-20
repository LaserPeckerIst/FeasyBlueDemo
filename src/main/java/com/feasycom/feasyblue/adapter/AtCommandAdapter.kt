package com.feasycom.feasyblue.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.utils.ToastUtil
import kotlinx.android.synthetic.main.at_command_item.view.*

sealed class AtCommandViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

class CustomizeAtCommandViewHolder(itemView: View): AtCommandViewHolder(itemView)

class CustomizeAtCommandNoParamViewViewHolder(itemView: View): AtCommandViewHolder(itemView)


class AtCommandAdapter(private val atCommandCount: Int, atCommandNoParamCount: Int): RecyclerView.Adapter<AtCommandViewHolder>(){

    private val atCommandTotal = atCommandCount + atCommandNoParamCount
    var countChangeCallback: ((count: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AtCommandViewHolder {
        Log.e(TAG, "onCreateViewHolder: $viewType" )
        return if(viewType == CUSTOMIZE_AT_COMMAND){
            CustomizeAtCommandViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.at_command_item, parent, false)
            )
        }else {
            CustomizeAtCommandNoParamViewViewHolder(
                LayoutInflater.from(
                    parent.context
                ).inflate(R.layout.at_command_item, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: AtCommandViewHolder, position: Int) {
        when(holder){
            is CustomizeAtCommandViewHolder -> {
                Log.e(TAG, "onBindViewHolder: CustomizeAtCommandViewHolder")
                with(holder.itemView){
                    group.visibility = View.VISIBLE
                    checkFlag.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            if (parameter.text.toString().isNotEmpty() && command.text.toString()
                                    .isNotEmpty()
                            ) {
                                parameter.isEnabled = !isChecked
                                command.isEnabled = !isChecked
                                CUSTOMIZE_COMMAND_COUNT++
                                countChangeCallback?.invoke(CUSTOMIZE_COMMAND_COUNT)
                            } else {
                                checkFlag.isChecked = false
                                ToastUtil.show(context, resources.getString(R.string.none))
                            }
                        } else {
                            parameter.isEnabled = !isChecked
                            command.isEnabled = !isChecked
                            CUSTOMIZE_COMMAND_COUNT--
                            countChangeCallback?.invoke(CUSTOMIZE_COMMAND_COUNT)
                        }
                    }
                }
            }
            is CustomizeAtCommandNoParamViewViewHolder -> {
                with(holder.itemView){
                    checkFlag.setOnCheckedChangeListener{ _, isChecked ->
                        if (isChecked) {
                            if (command.text.toString().isNotEmpty()) {
                                command.isEnabled = !isChecked
                                CUSTOMIZE_COMMAND_COUNT++
                                countChangeCallback?.invoke(CUSTOMIZE_COMMAND_COUNT)
                            } else {
                                checkFlag.isChecked = false
                                ToastUtil.show(context, resources.getString(R.string.none))
                            }
                        } else {
                            command.isEnabled = !isChecked
                            CUSTOMIZE_COMMAND_COUNT--
                            countChangeCallback?.invoke(CUSTOMIZE_COMMAND_COUNT)
                        }
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int{
        return if (position <= atCommandCount) {
            CUSTOMIZE_AT_COMMAND
        } else {
            CUSTOMIZE_AT_COMMAND_NO_PARAM
        }
    }

    override fun getItemCount(): Int{
        return atCommandTotal
    }

    companion object{
        private const val TAG = "AtCommandAdapter"
        var CUSTOMIZE_COMMAND_COUNT = 0

        const val CUSTOMIZE_AT_COMMAND = 0
        const val CUSTOMIZE_AT_COMMAND_NO_PARAM = 1
    }

}