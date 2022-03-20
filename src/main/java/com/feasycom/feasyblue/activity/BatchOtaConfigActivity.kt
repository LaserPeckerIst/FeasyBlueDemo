package com.feasycom.feasyblue.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.core.widget.addTextChangedListener
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.utils.*
import kotlinx.android.synthetic.main.activity_ota_config.*

class BatchOtaConfigActivity: BaseActivity() {

    @SuppressLint("SetTextI18n")
    override fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false)
            it.setDisplayHomeAsUpEnabled(true)
        }
        toolbarTitle.text = getString(R.string.filter)
        toolbar.setNavigationOnClickListener { finish() }
        getBoolean("otaRssiSwitch",
            false).let {
            rssiSwitch.isChecked = it
            rssiGroup.visibility = if (it){
                View.VISIBLE
            }else {
                View.GONE
            }
        }

        rssiSeekBar.progress = getInt( "otaRssiValue", -70) + 70

        rssiValueTextView.text = ":  ${getInt("otaRssiValue", -70)} dB"

        getBoolean(
            "otaNameSwitch",
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
            "otaNameValue",
            ""))

        nameEditText.addTextChangedListener {
            putStr("otaNameValue", it.toString())
        }

        rssiSeekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                //监听进度条
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    rssiValueTextView.text = ":  ${progress - 70} dB"

                    Log.e("TAG", "onProgressChanged: ${progress}" )
                    putInt("otaRssiValue",progress - 70)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    val start = seekBar.progress
                    rssiValueTextView.text = "${start - 70} dB"
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    val end = seekBar.progress
                    rssiValueTextView.text = "${end - 70} dB"
                }
            })

        rssiSwitch.setOnCheckedChangeListener { _, isChecked ->
            rssiGroup.visibility = if (isChecked){
                View.VISIBLE
            }else {
                View.GONE
            }
            putBoolean("otaRssiSwitch", isChecked)
        }

        nameSwitch.setOnCheckedChangeListener { _, isChecked ->
            nameEditText.visibility = if (isChecked) View.VISIBLE else View.GONE
            putBoolean("otaNameSwitch", isChecked)
        }

        button.setOnClickListener {

            finish()
        }

    }

    override fun getLayout() = R.layout.activity_ota_config



    companion object{
        fun activityStart(context: Context){
            val intent = Intent(context, BatchOtaConfigActivity::class.java)
            context.startActivity(intent)
        }
    }
}