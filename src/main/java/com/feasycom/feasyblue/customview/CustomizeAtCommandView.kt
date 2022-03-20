package com.feasycom.feasyblue.customview

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.adapter.AtCommandAdapter
import com.feasycom.feasyblue.adapter.ForceAtCommandAdapter
import kotlinx.android.synthetic.main.customize_at_command1.view.*

class CustomizeAtCommandView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): LinearLayout(context, attrs) {

    var atCommandCount = 0
    var atCommandNoParam = 0
    init {
        getAttrs(context, attrs)
        initView(context)
        setEvent()
    }

    private fun setEvent() {
        customizeCommandButton.setOnClickListener {
            customizeAtCommandList.visibility =
                if (customizeAtCommandList.visibility == GONE) VISIBLE else GONE
        }
    }

    private fun initView(context: Context) {
        val inflate = inflate(context, R.layout.customize_at_command1, this)
        val listOf = listOf<String>(
            context.getString(R.string.deviceName),
            context.getString(R.string.pin),
            context.getString(R.string.baud)
        )
        with(recyclerView){
            layoutManager = LinearLayoutManager(context)
            adapter = ForceAtCommandAdapter(listOf)
        }

        with(customizeAtCommandList) {
            layoutManager = LinearLayoutManager(context)
            adapter = AtCommandAdapter(atCommandCount, atCommandNoParam).apply {
                countChangeCallback = {
                    inflate.findViewById<TextView>(R.id.customizeSelectCount).text = it.toString()
                }
            }
        }
    }


    private fun getAttrs(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomizeAtCommandView)
        atCommandCount = a.getInt(R.styleable.CustomizeAtCommandView_atCommandCount, 0)
        atCommandNoParam = a.getInt(R.styleable.CustomizeAtCommandView_atCommandNoParam, 0)
        a.recycle()
    }

    companion object{
        private const val TAG = "CustomizeAtCommandView1"
    }

}