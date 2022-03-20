package com.feasycom.feasyblue.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.utils.putStr


class OtaSearchDeviceActivity: BaseActivity() {

    override fun initView() {
        if (Intent.ACTION_VIEW == intent.action) {
            intent.dataString?.let {
                val parse = Uri.parse(it)
                grantUriPermission(packageName, parse, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    and Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                putStr("fileUri", it)
            }
        }
    }

    override fun getLayout() = R.layout.activity_search_device

    companion object{
        private const val TAG = "OtaSearchDeviceActivity"
        fun activityStart(context: Context){
            val intent = Intent(context, OtaSearchDeviceActivity::class.java)
            context.startActivity(intent)
        }
    }
}