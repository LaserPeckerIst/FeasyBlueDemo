package com.feasycom.feasyblue.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.dialog.InfoDialog
import com.feasycom.feasyblue.utils.FeasyCallbackSDK
import com.feasycom.feasyblue.utils.FeasyCallbackSDK.Companion.FEEDBACK_ADVICE
import com.feasycom.feasyblue.utils.FeasyCallbackSDK.Companion.FEEDBACK_BUG
import com.feasycom.feasyblue.utils.FeasyCallbackSDK.Companion.FEEDBACK_COOPERATION
import com.feasycom.feasyblue.utils.FeasyCallbackSDK.Companion.FEEDBACK_UI
import com.feasycom.feasyblue.utils.getStr
import com.feasycom.feasyblue.utils.putStr
import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FeedbackActivity: BaseActivity(){

    var connectDialog: InfoDialog? = null

    var handler: Handler = Handler(Looper.getMainLooper())

    private var mPreferenceKey = "FEEDBACK_ADVICE"

    private val mNetworkIntentFilter: IntentFilter by lazy {
        IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
    }

    private val mNetworkReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                val wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                //如果无网络连接activeInfo为null
                val activeInfo = manager.activeNetworkInfo

                when {
                    wifiInfo!!.isConnected -> {
                        // wifi 网络
                    }
                    mobileInfo!!.isConnected -> {
                        // 手机网络
                    }
                    null == activeInfo -> {
                        // 没有网络
                        mResultDialog = AlertDialog.Builder(this@FeedbackActivity)
                            .setMessage(getString(R.string.no_network))
                            .setPositiveButton(android.R.string.ok, null)
                            .show()
                        mResultDialog.setCanceledOnTouchOutside(false)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun initView() {
        registerReceiver(mNetworkReceiver, mNetworkIntentFilter)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false)
            it.setDisplayHomeAsUpEnabled(true)
        }
        toolbarTitle.text = getString(R.string.feedback_title)
        deviceData.setText(getStr(mPreferenceKey))
        deviceData.setSelection(deviceData.text.toString().length)
        initEvent()
    }

    private fun initEvent() {
        toolbar.setNavigationOnClickListener { finish() }

        submit.setOnClickListener {
            if (deviceData.text.toString().isNotBlank()) {
                connectDialog = InfoDialog(this, getString(R.string.feedback_status))
                connectDialog?.show()

                handler.postDelayed({
                    connectDialog?.let {
                        if (it.isShowing) {
                            it.dismiss()
                        }
                    }
                }, 10000)

                FeasyCallbackSDK.complete = {
                    runOnUiThread {
                        MainScope().launch {
                            connectDialog?.info =
                                getString(if (it) R.string.feedback_success else R.string.feedback_failure)
                            delay(1000)
                            connectDialog?.dismiss()
                        }
                    }
                }

                FeasyCallbackSDK.feedback(
                    this, "${deviceData.text}", when (radio_group.checkedRadioButtonId) {
                        R.id.proposal -> FEEDBACK_ADVICE
                        R.id.abnormal_function -> FEEDBACK_BUG
                        R.id.interface_abnormality -> FEEDBACK_UI
                        R.id.cooperation -> FEEDBACK_COOPERATION
                        else -> FEEDBACK_ADVICE
                    }
                )
            } else {
                MainScope().launch {
                    connectDialog?.info = getString(R.string.feedback_data_error)
                    delay(1000)
                    connectDialog?.dismiss()
                }
            }
        }

        radio_group.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {

                mPreferenceKey = when (p1) {
                    R.id.proposal -> "FEEDBACK_ADVICE"
                    R.id.abnormal_function -> "FEEDBACK_BUG"
                    R.id.interface_abnormality -> "FEEDBACK_UI"
                    R.id.cooperation -> "FEEDBACK_COOPERATION"
                    else -> "FEEDBACK_ADVICE"
                }

                deviceData.setText(getStr(mPreferenceKey))
                deviceData.setSelection(deviceData.text.toString().length)
            }
        });

        deviceData.addTextChangedListener {
            Log.e(TAG, "initView: ${it.toString()}")
            putStr(mPreferenceKey, it.toString())
        }
    }

    override fun getLayout() = R.layout.activity_feedback

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mNetworkReceiver)
    }


    companion object {
        val TAG = "FeedbackActivity"
        private lateinit var mResultDialog: AlertDialog

        fun activityStart(context: Context){
            val intent = Intent(context, FeedbackActivity::class.java)
            context.startActivity(intent)
        }
    }



}