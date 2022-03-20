package com.feasycom.feasyblue.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.feasycom.feasyblue.R
import kotlinx.android.synthetic.main.radio_group_view.view.*

class MyGroup @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): LinearLayout(
    context,
    attrs,
    defStyleAttr
) {


    init {
        LayoutInflater.from(context).inflate(R.layout.radio_group_view, this, true)
        radio_bnt1.isChecked = true

        radio_group.setOnCheckedChangeListener { group, checkedId ->
            onClickListener?.let {
                onClickListener?.invoke(when(checkedId) {
                    R.id.radio_bnt1 -> {
                        0
                    }
                    R.id.radio_bnt2 -> {
                        1
                    }
                    R.id.radio_bnt3 -> {
                        2
                    }
                    R.id.radio_bnt4 -> {
                        3
                    }
                    else -> 0
                })
            }
        }
    }

    fun getSelectRadioButton() = when( radio_group.checkedRadioButtonId){
        R.id.radio_bnt1 -> {
            0
        }
        R.id.radio_bnt2 -> {
            1
        }
        R.id.radio_bnt3 -> {
            2
        }
        R.id.radio_bnt4 -> {
            3
        }
        else -> -1
    }

    var onClickListener:((position: Int) -> Unit)? = null

}