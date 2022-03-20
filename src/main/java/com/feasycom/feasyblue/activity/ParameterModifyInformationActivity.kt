package com.feasycom.feasyblue.activity

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import com.feasycom.ble.controler.FscBleCentralApi
import com.feasycom.ble.controler.FscBleCentralApiImp
import com.feasycom.ble.controler.FscBleCentralCallbacksImp
import com.feasycom.common.bean.ConnectType
import com.feasycom.common.bean.FscDevice
import com.feasycom.common.utils.Constant
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.utils.getStrSet
import com.feasycom.spp.controler.FscSppCentralApi
import com.feasycom.spp.controler.FscSppCentralApiImp
import com.feasycom.spp.controler.FscSppCentralCallbacksImp
import kotlinx.android.synthetic.main.activity_parameter_modify_information.*
import kotlinx.android.synthetic.main.activity_parameter_modify_information.toolbar
import kotlinx.android.synthetic.main.activity_parameter_modify_information.toolbarTitle
import kotlinx.android.synthetic.main.activity_throughput.*
import java.util.*

class ParameterModifyInformationActivity: BaseActivity() {
    private lateinit var commandSet: HashSet<String>
    private lateinit var commandNoParamSet: HashSet<String>
    private lateinit var bleApi: FscBleCentralApi
    private lateinit var mSppCentralApi: FscSppCentralApi
    private lateinit var mFscDevice: FscDevice

    private var deviceMode = "BLE"

    private val modifyInformation = StringBuffer()

    override fun initView() {

        intent?.getParcelableExtra<FscDevice>("FscDevice")?.let {
            mFscDevice = it
        }
        if (!::mFscDevice.isInitialized){
            finish()
        }
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false)
            it.setDisplayHomeAsUpEnabled(true)
        }
        toolbarTitle.text = getString(R.string.parameterDefining)
        toolbar.setNavigationOnClickListener {
            Log.e(TAG, "initView: disconnect  " )
            disconnect()
            finish()
        }

        toolbarTitle.text = mFscDevice.device.name
        deviceMode = mFscDevice.mode
        sendButton?.isEnabled = false
        sendFileButton?.isEnabled = false
        commandSet = getStrSet("commandSet",HashSet<String>())

        commandNoParamSet = getStrSet("commandNoParamSet", HashSet<String>())

        if (commandNoParamSet.isNotEmpty()) {
            for (string in commandNoParamSet) {
                commandSet.add(string)
            }
        }
        if (commandSet.isEmpty()){
            finish()
        }

        if (deviceMode == Constant.BLE_MODE){
            bleApi = FscBleCentralApiImp.getInstance()
            bleApi.setCallbacks(object : FscBleCentralCallbacksImp() {

                override fun blePeripheralConnected(
                    gatt: BluetoothGatt?,
                    address: String,
                    connectType: ConnectType
                ) {
                    bleApi.sendATCommand(mFscDevice.address, commandSet)
                }

                override fun blePeripheralDisconnected(
                    gatt: BluetoothGatt?,
                    address: String,
                    status: Int
                ) {
                    if(!(modifyInformationTextView.text.contains(getString(R.string.disconnected)))){
                        addState(getString(R.string.disconnected))
                    }
                }


                override fun atCommandCallBack(
                    command: String?,
                    param: String?,
                    type: Int,
                    status: Int
                ) {
                    uiATCommand(status, type, command, param)
                }

                override fun endATCommand() {
                    disconnect()
                }

                override fun startATCommand() {
                    addState(getString(R.string.openEngineSuccess) + "\r\n")
                }
            })

            bleApi.connectToModify(mFscDevice.address)
        }else {
            mSppCentralApi = FscSppCentralApiImp.getInstance()
            mSppCentralApi.setCallbacks(object : FscSppCentralCallbacksImp() {
                override fun sppPeripheralConnected(device: BluetoothDevice, connectType: ConnectType) {
                    super.sppPeripheralConnected(device, connectType)
                    mSppCentralApi.sendATCommand(mFscDevice.address, commandSet)
                }

                override fun sppPeripheralDisconnected(address: String?) {
                    super.sppPeripheralDisconnected(address)
                    if(!(modifyInformationTextView.text.contains(getString(R.string.disconnected)))){
                        addState(getString(R.string.disconnected))
                    }
                }

                override fun atCommandCallBack(
                    command: String?,
                    param: String?,
                    type: Int,
                    status: Int
                ) {
                    uiATCommand(status, type, command, param)
                }

                override fun endATCommand() {
                    disconnect()
                }

                override fun startATCommand() {
                    addState(getString(R.string.openEngineSuccess) + "\r\n")
                }

            })
            if (::mFscDevice.isInitialized){
                mSppCentralApi.connectToModify(mFscDevice.address)
            }
        }

        modifyInformation.append("${resources.getString(R.string.name)} ${mFscDevice.device.name}\r\n")
            .append("${resources.getString(R.string.addr)} ${mFscDevice.device.address}\r\n\r\n")

        runOnUiThread { modifyInformationTextView.text = modifyInformation }
    }

    private fun uiATCommand(
        status: Int,
        type: Int,
        command: String?,
        param: String?
    ) {
        when (status) {
            Constant.COMMAND_SUCCESSFUL -> {
                // 成功
                if (type == Constant.COMMAND_STATE_QUERY) {
                    addState("${getString(R.string.read)} $command ${getString(R.string.success)}\r\n")
                    addState("$param \r\n")
                } else if (type == Constant.COMMAND_STATE_SET) {
                    addState("${getString(R.string.modify)} $command ${getString(R.string.success)}\r\n")
                }
            }
            Constant.COMMAND_FAILED -> {
                // 失败
                if (type == Constant.COMMAND_STATE_QUERY) {
                    addState("${getString(R.string.read)} $command ${getString(R.string.failed)}\r\n")

                } else if (type == Constant.COMMAND_STATE_SET) {
                    addState("${getString(R.string.modify)} $command ${getString(R.string.failed)}\r\n")
                }
                runOnUiThread {
                    modifyInformationTextView.setTextColor(Color.rgb(255, 0, 0))
                }
            }
            Constant.COMMAND_TIME_OUT -> {
                // 超时
                if (type == Constant.COMMAND_STATE_QUERY) {
                    addState(
                        "${getString(R.string.read)} $command ${getString(R.string.timeout)}\r\n"
                    )
                    addState("$param \r\n")
                } else if (type == Constant.COMMAND_STATE_SET) {
                    addState("${getString(R.string.modify)} $command ${getString(R.string.timeout)}\r\n")
                }
            }
        }
    }

    fun disconnect(){
        if (mFscDevice.mode == Constant.BLE_MODE){
            bleApi.disconnect(mFscDevice.address)
        }else {
            mSppCentralApi.disconnect(mFscDevice.address)
        }
    }

    fun addState(string: String) {
        val c = Calendar.getInstance()
        val hour = c[Calendar.HOUR_OF_DAY]
        val minute = c[Calendar.MINUTE]
        val second = c[Calendar.SECOND]
        val msecond = c[Calendar.MILLISECOND]
        val strTime = String.format("【%02d:%02d:%02d.%03d】", hour, minute, second, msecond)
        modifyInformation.append(
            "$strTime$string"/*.trimIndent()*/
        )
        runOnUiThread { modifyInformationTextView.text = modifyInformation }
    }


    override fun onDestroy() {
        super.onDestroy()
        disconnect()
    }
    companion object{
        private const val TAG = "Information"

        fun activityStart(context: Context, fscDevice: FscDevice){
            val intent = Intent(context, ParameterModifyInformationActivity::class.java)
            intent.putExtra("FscDevice", fscDevice)
            context.startActivity(intent)
        }
    }

    override fun getLayout() = R.layout.activity_parameter_modify_information
}