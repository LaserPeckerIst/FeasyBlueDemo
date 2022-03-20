package com.feasycom.feasyblue.activity

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.feasycom.ble.controler.FscBleCentralApi
import com.feasycom.ble.controler.FscBleCentralApiImp
import com.feasycom.common.bean.FscDevice
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.adapter.ServicesExpandableListAdapter
import com.feasycom.feasyblue.utils.bytesToHex
import kotlinx.android.synthetic.main.activity_service_select.*

class ServiceSelectActivity: BaseActivity() {

    private lateinit var mFscDevice: FscDevice
    private lateinit var mBleApi: FscBleCentralApi
    private lateinit var mServiceList: MutableList<BluetoothGattService>

    private lateinit var mServicesExpandableListAdapter: ServicesExpandableListAdapter

    private lateinit var mFlag: TextView
    private lateinit var mFlagLL: LinearLayout
    private lateinit var mIncompleteServiceUUID16Bit: TextView
    private lateinit var mIncompleteServiceUUID16BitLL: LinearLayout
    private lateinit var mIncompleteServiceUUID128Bit: TextView
    private lateinit var mIncompleteServiceUUID128BitLL: LinearLayout
    private lateinit var mCompleteLocalName: TextView
    private lateinit var mCompleteLocalNameLL: LinearLayout
    private lateinit var mServiceData: TextView
    private lateinit var mServiceDataLL: LinearLayout
    private lateinit var mTxPowerLevel: TextView
    private lateinit var mTxPowerLevelLL: LinearLayout
    private lateinit var mManufacturerSpecificData: TextView
    private lateinit var mManufacturerSpecificDataLL: LinearLayout
    private lateinit var mAdvData: TextView
    private lateinit var mListViewHeader: View

    @RequiresApi(Build.VERSION_CODES.N)
    override fun initView() {
        intent.getParcelableExtra<FscDevice>("FscDevice")?.let {
            mFscDevice = it
        }?: finish()
        if (!::mFscDevice.isInitialized){
            return
        }
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbarTitle.text = mFscDevice.device.name
        toolbarTitle.text = getString(R.string.transmissionConfiguration)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        mBleApi = FscBleCentralApiImp.getInstance()
        mtuView.setProgress(mBleApi.getMtu(mFscDevice.address))
        mtuView.setMtu = {
            mBleApi.requestMtu(mFscDevice.address, it)
        }

        mServiceList = mBleApi.getBluetoothGattServices(mFscDevice.address)

        mServicesExpandableListAdapter = ServicesExpandableListAdapter(this, mServiceList)

        mListViewHeader = layoutInflater.inflate(R.layout.peripheral_list_services_header, null)
        mFlag = mListViewHeader.findViewById(R.id.advFlag)
        mFlagLL = mListViewHeader.findViewById(R.id.advFlagLL)
        mIncompleteServiceUUID16Bit =
            mListViewHeader.findViewById(R.id.incompleteServiceUUIDs_16bit)
        mIncompleteServiceUUID16BitLL =
            mListViewHeader.findViewById(R.id.incompleteServiceUUIDs_16bit_LL)
        mIncompleteServiceUUID128Bit =
            mListViewHeader.findViewById(R.id.incompleteServiceUUIDs_128bit)
        mIncompleteServiceUUID128BitLL =
            mListViewHeader.findViewById(R.id.incompleteServiceUUIDs_128bit_LL)
        mCompleteLocalName = mListViewHeader.findViewById(R.id.completeLocalName)
        mCompleteLocalNameLL =
            mListViewHeader.findViewById(R.id.completeLocalNameLL)
        mServiceData = mListViewHeader.findViewById(R.id.serviceData)
        mServiceDataLL = mListViewHeader.findViewById(R.id.serviceDataLL)
        mTxPowerLevel = mListViewHeader.findViewById(R.id.txPowerLevel)
        mTxPowerLevelLL = mListViewHeader.findViewById(R.id.txPowerLevelLL)
        mManufacturerSpecificData =
            mListViewHeader.findViewById(R.id.manufacturerSpecificData)
        mManufacturerSpecificDataLL =
            mListViewHeader.findViewById(R.id.manufacturerSpecificDataLL)

        mListViewHeader.setOnClickListener({ })
        mAdvData = mListViewHeader.findViewById(R.id.advData)

        Log.e("TAG", "initView: " + mFscDevice.scanRecord?.let { it.bytesToHex() } )
        mAdvData.text = mFscDevice.scanRecord?.let { it.bytesToHex() }
        expandableListView.addHeaderView(mListViewHeader)
        expandableListView.setAdapter(mServicesExpandableListAdapter)
        for (i in 0 until mServicesExpandableListAdapter.groupCount) {
            expandableListView.expandGroup(i)
        }
        mServicesExpandableListAdapter.notifyDataSetChanged()
        if (mFscDevice.flag == null) {
            mFlagLL.visibility = View.GONE
        }
        mFlag.text = mFscDevice.flag

        if (mFscDevice.incompleteServiceUUIDs_16bit == null) {
            mIncompleteServiceUUID16BitLL.visibility = View.GONE
        }
        mIncompleteServiceUUID16Bit.text = mFscDevice.incompleteServiceUUIDs_16bit
        if (mFscDevice.incompleteServiceUUIDs_128bit == null) {
            mIncompleteServiceUUID128BitLL.visibility = View.GONE
        }
        mIncompleteServiceUUID128Bit.text = mFscDevice.incompleteServiceUUIDs_128bit
        mCompleteLocalName.text = mFscDevice.name
        if (mFscDevice.serviceData == null) {
            mServiceDataLL.visibility = View.GONE
        }
        mServiceData.text = mFscDevice.serviceData
        if (mFscDevice.txPowerLevel == null) {
            mTxPowerLevelLL.visibility = View.GONE
        }
        mTxPowerLevel.text = mFscDevice.txPowerLevel
        if (mFscDevice.manufacturerSpecificData == null) {
            mManufacturerSpecificDataLL.visibility = View.GONE
        }
        mManufacturerSpecificData.text = mFscDevice.manufacturerSpecificData


        expandableListView.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            bluetoothGattCharacteristic =
                mServiceList[groupPosition].characteristics[childPosition]
            CharacteristicDetailActivity.activityStart(this, mFscDevice.address)
            false
        }

    }

    override fun getLayout() = R.layout.activity_service_select

    companion object{
        var bluetoothGattCharacteristic: BluetoothGattCharacteristic? = null

        fun activityStart(context: Context, fscDevice: FscDevice){
            val intent = Intent(context, ServiceSelectActivity::class.java)
            intent.putExtra("FscDevice", fscDevice)
            context.startActivity(intent)
        }
    }
}