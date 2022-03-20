package com.feasycom.feasyblue.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.widget.addTextChangedListener
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.utils.*
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.activity_filter.toolbar
import kotlinx.android.synthetic.main.activity_filter.toolbarTitle

class FilterActivity: BaseActivity() {
    @SuppressLint("SetTextI18n")
    override fun initView() {

        setSupportActionBar(toolbar)

        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false)
            it.setDisplayHomeAsUpEnabled(true)
        }
        toolbarTitle.text = getString(R.string.filter)
        toolbar.setNavigationOnClickListener { finish() }

        getBoolean("rssiSwitch",
            false).let {
            rssiSwitch.isChecked = it
            rssiGroup.visibility = if (it){
                View.VISIBLE
            }else {
                View.GONE
            }
        }


        rssiSeekBar.progress =getInt( "rssiValue", -100) + 100

        rssiValueTextView.text = ":  ${getInt("rssiValue", -100)} dB"

        getBoolean(
            "nameSwitch",
            false
        ).let {
            nameSwitch.isChecked = it
            nameEditText.visibility = if (it){
                View.VISIBLE
            }else {
                View.GONE
            }
        }
        nameEditText.setText(getStr(
            "nameValue",
            ""))

        nameEditText.addTextChangedListener {
            putStr("nameValue", it.toString())
        }

        rssiSeekBar.setOnSeekBarChangeListener(
            object : OnSeekBarChangeListener {
                //监听进度条
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    rssiValueTextView.text = ":  ${progress - 100} dB"
                    putInt("rssiValue",progress - 100)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    val start = seekBar.progress
                    rssiValueTextView.text = "${start - 100} dB"
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    val end = seekBar.progress
                    rssiValueTextView.text = "${end - 100} dB"
                }
            })

        rssiSwitch.setOnCheckedChangeListener { _, isChecked ->
            rssiGroup.visibility = if (isChecked){
                View.VISIBLE
            }else {
                View.GONE
            }
            putBoolean("rssiSwitch", isChecked)
        }

        nameSwitch.setOnCheckedChangeListener { _, isChecked ->
            nameEditText.visibility = if (isChecked) View.VISIBLE else View.GONE
            putBoolean("nameSwitch", isChecked)
        }
    }

    override fun getLayout() = R.layout.activity_filter

    companion object{
        fun activityStart(context: Context){
            val intent = Intent(context, FilterActivity::class.java)
            context.startActivity(intent)
        }
    }
}