package com.feasycom.feasyblue.fragment

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.graphics.Rect
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feasycom.ble.controler.FscBleCentralApi
import com.feasycom.ble.controler.FscBleCentralApiImp
import com.feasycom.ble.controler.FscBleCentralCallbacksImp
import com.feasycom.common.bean.ConnectType
import com.feasycom.common.bean.FscDevice
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.activity.*
import com.feasycom.feasyblue.adapter.DeviceAdapter
import com.feasycom.feasyblue.utils.*
import com.feasycom.spp.controler.FscSppCentralApi
import com.feasycom.spp.controler.FscSppCentralApiImp
import com.feasycom.spp.controler.FscSppCentralCallbacksImp
import kotlinx.android.synthetic.main.fragment_communication.*
import java.lang.Exception
import java.util.*

class CommunicationFragment: BaseFragment(), View.OnClickListener,
    RadioGroup.OnCheckedChangeListener{

    private var filterRssiSwitch: Boolean = false
    private var filterRssi: Int = -100
    private var filterNameSwitch: Boolean = false
    private var filterName: String = ""

    private val devices = mutableListOf<FscDevice>()
    private val sFscSppCentralApi: FscSppCentralApi by lazy {
        FscSppCentralApiImp.getInstance(requireContext())
    }

    private val mFscBleCentralApi: FscBleCentralApi by lazy {
        FscBleCentralApiImp.getInstance(requireContext())
    }

    override fun getLayoutId() = R.layout.fragment_communication

    private var mDeviceAdapter: DeviceAdapter? = null
    override fun initView() {
        activity?.let {
            toolbarTitle.text = when(it){
                is MainActivity -> {
                    getString(R.string.communication)
                }
                is ParameterModificationDeviceListActivity -> {
                    getString(R.string.parameterDefining)
                }
                is OtaSearchDeviceActivity -> {
                    getString(R.string.OTAUpgrade)
                }
                else -> getString(R.string.communication)
            }

            if (it is MainActivity){
                toolbarImageButton.visibility = View.GONE
            }else {
                toolbarImageButton.visibility = View.VISIBLE
            }
        }
        sppCheck.isChecked = true

        mDeviceAdapter = DeviceAdapter(devices)
        mDeviceAdapter?.mOnClickListener = { position ->
            stopScan()
            activity?.let {
                when(it){
                    is MainActivity -> {
                        if (devices.isNotEmpty()){
                            ThroughputActivity.activityStart(requireContext(), devices[position])
                        }
                    }
                    is ParameterModificationDeviceListActivity -> {
                        ParameterModifyInformationActivity.activityStart(
                            requireContext(),
                            devices[position]
                        )
                    }
                    is OtaSearchDeviceActivity -> {
                        OtaActivity.activityStart(requireContext(), devices[position])
                        // requireActivity().finish()
                    }
                }
            }
        }

        with(deviceRecyclerView){
            // 解决局部刷新导致页面闪烁的问题
            itemAnimator = null
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mDeviceAdapter

            addItemDecoration(object: RecyclerView.ItemDecoration(){
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    with(UIUtil.dip2px(context, 1.0)){
                        outRect.top = this
                        outRect.bottom = this
                        outRect.left = this
                        outRect.right = this
                    }
                }
            })
        }

        smartRefreshLayout.setOnRefreshListener {
            devices.clear()
            mDeviceAdapter?.notifyDataSetChanged()
            startScan()
            it.closeHeaderOrFooter()
        }

        bluetooth.visibility = if(mFscBleCentralApi.isEnabled()) View.GONE else View.VISIBLE
        val enabled: Boolean = isLocationEnabled(requireContext())
        gps.visibility = if (enabled) View.GONE else View.VISIBLE
        initEvent()

        // openSdpService()

        registerBroadcastReceivers()
    }


    private fun initEvent(){
        sortLinearLayout.setOnClickListener(this)
        filterLinearLayout.setOnClickListener(this)
        header_right_LL.setOnCheckedChangeListener(this)
        toolbarImageButton.setOnClickListener(this)
        bluetooth_enable.setOnClickListener(this)
        gps_enable.setOnClickListener(this)
        gps_more.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        devices.clear()
        mDeviceAdapter?.notifyDataSetChanged()
        with(requireContext()){
            filterRssiSwitch = getBoolean("rssiSwitch", false)
            filterRssi = if(filterRssiSwitch){
                getInt("rssiValue", -100)
            }else {
                -100
            }
            filterNameSwitch = getBoolean("nameSwitch", false)
            filterName = if (filterNameSwitch){
                getStr("nameValue", "")
            }else {
                ""
            }
        }

        mFscBleCentralApi.setCallbacks(object : FscBleCentralCallbacksImp() {
            override fun blePeripheralFound(bleDevice: FscDevice, rssi: Int, record: ByteArray?) {
                super.blePeripheralFound(bleDevice, rssi, record)
                if (bleCheck?.isChecked == true) {
                    uiFoundDevice(bleDevice)
                }
            }
            override fun startScan() {
                super.startScan()
                if (bleCheck?.isChecked == true) {
                    uiStartScan()
                }
            }
            override fun stopScan() {
                super.stopScan()
                if (bleCheck?.isChecked == true) {
                    uiStopScan()
                }
            }
        })

        sFscSppCentralApi.setCallbacks(object : FscSppCentralCallbacksImp() {
            override fun sppPeripheralFound(sppDevice: FscDevice, rssi: Int) {
                if (sppCheck?.isChecked == true) {
                    uiFoundDevice(sppDevice)
                }
            }
            override fun startScan() {
                if (sppCheck?.isChecked == true) {
                    uiStartScan()
                }

            }
            override fun stopScan() {
                if (sppCheck?.isChecked == true) {
                    uiStopScan()
                }
            }

            override fun sppPeripheralConnected(device: BluetoothDevice, connectType: ConnectType) {
                Log.e(TAG, "sppPeripheralConnected: 连接成功" )
                stopScan()
                ThroughputActivity.activityStart(requireContext(), device)
            }
        })
        startScan()
    }



    override fun onClick(v: View?) {
        when(v?.id){
            R.id.sortLinearLayout -> {
                with(devices) {
                    val pairedDevices = filter {
                        it.device.bondState == BluetoothDevice.BOND_BONDED
                    }
                    val unpairedDevice = filterNot {
                        it.device.bondState == BluetoothDevice.BOND_BONDED
                    }.sortedByDescending {
                        it.rssi
                    }
                    clear()
                    addAll(pairedDevices)
                    addAll(unpairedDevice)
                }
                mDeviceAdapter?.notifyDataSetChanged()
            }
            R.id.filterLinearLayout -> {
                FilterActivity.activityStart(requireContext())
            }
            R.id.toolbarImageButton -> {
                MainActivity.activityStart(requireContext(), 1)
                requireActivity().finish()
            }
            R.id.bluetooth_enable -> {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, 1)
            }
            R.id.gps_enable -> {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent, 2)
            }
            R.id.gps_more -> {
                AlertDialog.Builder(requireContext()).setTitle(getString(R.string.gps_more_title)) //设置对话框标题
                    .setMessage(getString(R.string.gps_more_text_bluetooth)) //设置显示的内容
                    .setPositiveButton("OK") { dialog: DialogInterface, _: Int -> dialog.dismiss() }.show() //显示此对话框
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        devices.clear()
        mDeviceAdapter?.notifyDataSetChanged()
        Log.e(TAG, "onCheckedChanged: 扫描 2" )
        startScan()
    }

    private fun startScan(){
        if (bluetooth.visibility == View.VISIBLE || gps.visibility == View.VISIBLE){
            return
        }
        if (header_right_LL.checkedRadioButtonId == R.id.bleCheck){
            sFscSppCentralApi.stopScan()
            mFscBleCentralApi.startScan()
            uiStartScan()
        }else {
            // mFscBleCentralApi.stopScan()
            sFscSppCentralApi.startScan()
            uiStartScan()
        }
    }

    private fun stopScan(){
        try {
            if (bluetooth.visibility == View.VISIBLE || gps.visibility == View.VISIBLE){
                return
            }
        }catch (e: NullPointerException){
            e.printStackTrace()
        }finally {
            try {
                sFscSppCentralApi.stopScan()
                mFscBleCentralApi.stopScan()
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    private fun uiStartScan(){
        if (isAdded){
            toolbarSubtitle.text = resources.getString(R.string.searching)
        }
    }

    private fun uiStopScan(){
        if (isAdded){
            toolbarSubtitle?.text = resources.getString(R.string.searched)
        }
    }

    private  fun uiFoundDevice(fscDevice: FscDevice){
        if(isAdded){
            if (fscDevice.rssi == 127) return
            if (filterNameSwitch){
                try{
                    val name = filterName.lowercase(Locale.ROOT)
                    fscDevice.device.name?.let {
                        if(name !in it.lowercase(Locale.ROOT) && name !in fscDevice.completeLocalName.lowercase(
                                Locale.ROOT)){return
                        }
                    }?: if(name !in fscDevice.completeLocalName.lowercase(Locale.ROOT)) {
                        return
                    }
                }catch (e: NullPointerException){
                    e.printStackTrace()
                    return
                }
            }
            if(fscDevice.rssi > filterRssi){
                val index = devices.indexOf(fscDevice)
                if (index == -1){
                    devices.add(fscDevice)
                    deviceRecyclerView?.adapter?.notifyItemChanged(devices.size - 1)
                }else{
                    devices[index] = fscDevice
                    deviceRecyclerView?.adapter?.notifyItemChanged(index)
                }
            }
        }
    }

    /**
     * Broadcast receiver to monitor the changes in the location provider
     */
    private val mLocationProviderChangedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val enabled: Boolean = isLocationEnabled(requireContext())
            if (enabled) {
                stopScan()
            }
            gps.visibility = if (enabled) View.GONE else View.VISIBLE
        }
    }

    /**
     * Broadcast receiver to monitor the changes in the bluetooth adapter
     */
    private val mBluetoothStateBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)
            val previousState = intent.getIntExtra(
                BluetoothAdapter.EXTRA_PREVIOUS_STATE,
                BluetoothAdapter.STATE_OFF
            )
            when (state) {
                BluetoothAdapter.STATE_ON -> {
                    Log.e(TAG, "onReceive: 蓝牙已开启")
                    bluetooth.visibility = View.GONE
                }
                BluetoothAdapter.STATE_TURNING_OFF, BluetoothAdapter.STATE_OFF -> if (previousState != BluetoothAdapter.STATE_TURNING_OFF && previousState != BluetoothAdapter.STATE_OFF) {
                    stopScan()
                    Log.e(TAG, "onReceive: 蓝牙已关闭" )
                    bluetooth.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * Register for required broadcast receivers.
     */
    private fun registerBroadcastReceivers() {
        requireContext().registerReceiver(
            mBluetoothStateBroadcastReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
        if (isMarshmallowOrAbove()) {
            requireContext().registerReceiver(
                mLocationProviderChangedReceiver,
                IntentFilter(LocationManager.MODE_CHANGED_ACTION)
            )
        }
    }

    /**
     * Unregister for required broadcast receivers.
     */
    private fun unregisterBroadcastReceivers() {
        requireContext().unregisterReceiver(mBluetoothStateBroadcastReceiver)
        if (isMarshmallowOrAbove()) {
            requireContext().unregisterReceiver(mLocationProviderChangedReceiver)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        closeSdpService()
        unregisterBroadcastReceivers()
        stopScan()
    }

    private fun closeSdpService() {
        activity?.let {
            if (it !is MainActivity){
                sFscSppCentralApi.closeSdpService()
            }
        }
    }

    companion object{
        private const val TAG = "CommunicationFragment"
    }


}