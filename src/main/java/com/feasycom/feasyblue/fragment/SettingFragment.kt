package com.feasycom.feasyblue.fragment

import android.view.View
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.activity.BatchOtaActivity
import com.feasycom.feasyblue.activity.MainActivity
import com.feasycom.feasyblue.activity.OtaSearchDeviceActivity
import com.feasycom.feasyblue.activity.PasswordActivity
import com.feasycom.spp.controler.FscSppCentralApi
import com.feasycom.spp.controler.FscSppCentralApiImp
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment: BaseFragment(), View.OnClickListener {


    override fun getLayoutId() = R.layout.fragment_setting

    private val sFscSppCentralApi: FscSppCentralApi by lazy {
        FscSppCentralApiImp.getInstance(requireContext())
    }

    override fun initView() {
        toolbarTitle.text = getString(R.string.setting)
        parameterDefining.setOnClickListener(this)
        otaButton.setOnClickListener(this)
        otaBatchButton.setOnClickListener(this)
        sdpBatchButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.parameterDefining -> {
                PasswordActivity.activityStart(requireContext())
                requireActivity().finish()
            }
            R.id.otaButton -> {
                OtaSearchDeviceActivity.activityStart(requireContext())
            }
            R.id.otaBatchButton -> {
                BatchOtaActivity.activityStart(requireContext())
            }
            R.id.sdpBatchButton -> {
                if (sFscSppCentralApi.isEnabledSDP){
                    // closeSdpService()
                    // sdpBatchButton.text = getString(R.string.open_sdp)
                }else {
                    openSdpService()
                    sdpBatchButton.visibility = View.GONE
                    // sdpBatchButton.text = getString(R.string.close_sdp)
                }
            }
        }
    }

    private fun openSdpService() {
        activity?.let {
            if(it is MainActivity) {
                sFscSppCentralApi.openSdpService()
            }
        }
    }

    /*private fun closeSdpService() {
        activity?.let {
            if(it is MainActivity) {
                sFscSppCentralApi.closeSdpService()
            }
        }
    }*/
}