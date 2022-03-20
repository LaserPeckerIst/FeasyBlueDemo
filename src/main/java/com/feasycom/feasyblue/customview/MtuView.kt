package com.feasycom.feasyblue.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.feasycom.feasyblue.R
import kotlinx.android.synthetic.main.mtu_view.view.*


class MtuView  @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): ConstraintLayout(
    context,
    attrs,
    defStyleAttr
) {

    var setMtu: ((mtu: Int) -> Unit)? = null


    init {
        addView(LayoutInflater.from(context).inflate(R.layout.mtu_view, this, false))

        rangeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mtuTextView.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        minusButton.setOnClickListener {
            rangeSeekBar.progress = rangeSeekBar.progress - 1
        }

        plusButton.setOnClickListener {
            rangeSeekBar.progress = rangeSeekBar.progress + 1
        }

        okButton.setOnClickListener {
            setMtu?.invoke(rangeSeekBar.progress)
        }

    }

    fun setProgress(progress: Int){
        rangeSeekBar.progress = progress
    }

}