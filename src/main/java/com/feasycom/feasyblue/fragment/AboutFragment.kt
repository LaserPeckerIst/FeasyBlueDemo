package com.feasycom.feasyblue.fragment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.activity.AgreementActivity
import com.feasycom.feasyblue.activity.FeedbackActivity
import com.feasycom.feasyblue.activity.QRCodeActivity
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AboutFragment: BaseFragment(),View.OnClickListener {
    override fun getLayoutId() = R.layout.fragment_about

    override fun initView() {
        toolbarTitle.text = getString(R.string.about)
        toolbarFeedback.setOnClickListener(this)
        wxImage.setOnClickListener(this)
        privacyText.setOnClickListener(this)
        userText.setOnClickListener(this)

        version.text = packageCode(requireContext())
    }

    private fun packageCode(context: Context): String? {
        val manager = context.packageManager
        var code: String? = null
        try {
            val info = manager.getPackageInfo(context.packageName, 0)
            code = info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return code
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.toolbarFeedback -> {
                FeedbackActivity.activityStart(requireContext())
            }
            R.id.wxImage -> {
                QRCodeActivity.activityStart(requireContext())
            }
            R.id.privacyText -> {
                val intent = Intent(requireContext(), AgreementActivity::class.java)
                intent.putExtra("type", 2)
                requireContext().startActivity(intent)
            }
            R.id.userText -> {
                val intent = Intent(requireContext(), AgreementActivity::class.java)
                intent.putExtra("type", 1)
                requireContext().startActivity(intent)
            }
        }
    }


}