package com.feasycom.feasyblue.activity

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.MediaStore
import android.text.TextUtils
import android.text.method.TextKeyListener
import android.util.Log
import android.view.View
import android.widget.Chronometer
import androidx.core.net.toFile
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.feasycom.ble.controler.FscBleCentralApi
import com.feasycom.ble.controler.FscBleCentralApiImp
import com.feasycom.ble.controler.FscBleCentralCallbacksImp
import com.feasycom.common.bean.ConnectType
import com.feasycom.common.bean.FscDevice
import com.feasycom.common.utils.Constant
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.utils.*
import com.feasycom.spp.controler.FscSppCentralApi
import com.feasycom.spp.controler.FscSppCentralApiImp
import com.feasycom.spp.controler.FscSppCentralCallbacksImp
import com.swallowsonny.convertextlibrary.hex2ByteArray
import com.swallowsonny.convertextlibrary.toHexString
import kotlinx.android.synthetic.main.activity_ota.*
import kotlinx.android.synthetic.main.activity_throughput.*
import kotlinx.android.synthetic.main.activity_throughput.toolbar
import kotlinx.android.synthetic.main.activity_throughput.toolbarTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.toHexString
import java.util.*
import java.util.zip.CRC32
import kotlin.system.measureTimeMillis

class ThroughputActivity : BaseActivity(), View.OnClickListener {

    lateinit var mFscDevice: FscDevice
    lateinit var mDevice: BluetoothDevice
    lateinit var mBleApi: FscBleCentralApi
    lateinit var mSppApi: FscSppCentralApi
    private var mBytesToBeSent = 0
    private var mReceiveBufferHex = StringBuffer() // 接收数据缓存buffer hex string 类型
    private var mReceiveBuffer = StringBuffer() // 接收数据缓存buffer string类型
    private var mReceiveByteCount = 0
    private var mReceivePackageCount = 0
    private var mSendByteCountSend = 0
    private var mSendPackageCountSend = 0
    private var mDeviceMode = "BLE"
    private var mSendCRC = CRC32() // 发送crc
    private var mReceiveCRC = CRC32() // 接收crc

    private var mSendFileByte = 0

    private var mIsPause = false

    private var mIsSending = false

    private var mResultCode: Int = 0
    private var mData: Intent? = null
    private var mLastStopTime: Long = 0L

    private var mByteArray: ByteArray = byteArrayOf()

    private var address: String =""

    private val hexKeyListener: HexKeyListener by lazy {
        HexKeyListener()
    }



    var handler = Handler(Looper.myLooper() ?: Looper.getMainLooper())
    var hexClickRunnable = Runnable {
        hexCheck.isEnabled = true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        initToolbar()
        intent?.getParcelableExtra<FscDevice>("FscDevice")?.let {
            Log.e("TAG", "initView: FscDevice   ->   ${it}   name   ->   ${it.name}" )
            mFscDevice = it
            toolbarTitle.text = it.name
            mDevice = it.device
            mDeviceMode = it.mode
            toolbarSubtitle.text = resources.getString(R.string.connecting)
            address = mFscDevice.address
            sendButton.isEnabled = false
            sendFileButton.isEnabled = false
        }
        intent?.getParcelableExtra<BluetoothDevice>("BluetoothDevice")?.let {
            mDevice = it
            toolbarTitle.text = it.name
            mDeviceMode = Constant.SPP_MODE
            address = mDevice.address
            toolbarSubtitle.text = resources.getString(R.string.connected)
        }
        if (!::mFscDevice.isInitialized && !::mDevice.isInitialized){
            return
        }
        if (mDeviceMode == Constant.BLE_MODE){
            with(switchServiceButton){
                visibility = View.VISIBLE
                isEnabled = false
            }
            mBleApi = FscBleCentralApiImp.getInstance()
            mBleApi.setCallbacks(object: FscBleCentralCallbacksImp(){
                override fun blePeripheralConnected(
                    gatt: BluetoothGatt?,
                    address: String,
                    connectType: ConnectType
                ) {
                    uiDeviceConnected()
                }

                override fun blePeripheralDisconnected(
                    gatt: BluetoothGatt?,
                    address: String,
                    status: Int
                ) {
                    uiDeviceDisconnected()
                }

                override fun packetReceived(
                    address: String,
                    strValue: String,
                    hexString: String,
                    data: ByteArray
                ) {
                    uiReceiveData(strValue, hexString, data)
                }


                override fun sendPacketProgress(
                    address: String,
                    percentage: Int,
                    sendByte: ByteArray
                ) {
                    uiSendDataProgress(percentage, sendByte.size)
                }

                override fun packetSend(address: String, strValue: String, data: ByteArray) {
                    uiSendData(data)
                }
            })
            if (::mFscDevice.isInitialized){
                mBleApi.connect(address)
            }
            // mBleApi.connect(mFscDevice.address)
        }else{
            with(switchServiceButton){
                visibility = View.GONE
                isEnabled = false
            }
            mSppApi = FscSppCentralApiImp.getInstance()
            mSppApi.setCallbacks(object: FscSppCentralCallbacksImp(){
                override fun sppPeripheralConnected(device: BluetoothDevice, connectType: ConnectType) {
                    Log.e("TAG", "sppPeripheralConnected: 连接成功" )
                    uiDeviceConnected()
                }

                override fun sppPeripheralDisconnected(address: String) {
                    uiDeviceDisconnected()
                }

                override fun packetReceived(address: String, strValue: String, hexString: String, data: ByteArray) {
                    super.packetReceived(address, strValue, hexString, data)
                    uiReceiveData(strValue, hexString, data)
                    Log.e(TAG, "packetReceived: ")
                }

                override fun sendPacketProgress(
                    address: String,
                    percentage: Int,
                    sendByte: ByteArray
                ) {
                    uiSendDataProgress(percentage, sendByte.size)
                }

                override fun packetSend(address: String?, strValue: String, hexString: String, data: ByteArray) {
                    uiSendData(data)
                }
            })
            if (::mFscDevice.isInitialized){
                Log.e("TAG", "initView: 开始连接" )
                mSppApi.connect(address)
            }
        }
        rateText.text = getString(R.string.rate, 0)
        hexCheck.isOpened = getBoolean("hexCheck")
        mByteArray = getByteArray("sendEdit")
        editByteCount.text = getString(R.string.byteString, mByteArray.size)
        if (hexCheck.isOpened){
            with(sendEdit){
                keyListener = hexKeyListener
                setText(mByteArray.toHexString())
            }
        }else {
            with(sendEdit){
                keyListener = TextKeyListener.getInstance()
                setText(String(mByteArray))
            }
            sendEdit.keyListener = TextKeyListener.getInstance()
        }
        initEvent()
        initUi()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false)
            it.setDisplayHomeAsUpEnabled(true)
        }
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initEvent() {
        sendButton.setOnClickListener(this)
        sendFileButton.setOnClickListener(this)
        send_status_btn.setOnClickListener(this)
        switchServiceButton.setOnClickListener(this)
        clearButton.setOnClickListener(this)
        intervalSendCheck.setOnClickListener(this)
        hexCheck.setOnClickListener(this)

        intervalSendTime.addTextChangedListener {
            if (it?.isBlank() == true){
                setSendInterval(0L)
            }else {
                setSendInterval(it.toString().toLong())
            }
        }


        sendEdit.addTextChangedListener(onTextChanged = { s, start, before, count ->
            Log.e(TAG, "initEvent: s -> ${s} start -> ${start}  before -> $before  count -> $count" )
            mByteArray = if (s!!.isEmpty()){
                byteArrayOf()
            }else {
                if (hexCheck.isOpened){
                    s.toString().hex2ByteArray()
                }else {
                    s.toString().toByteArray()
                }
            }

            putByteArray("sendEdit", mByteArray)
            mBytesToBeSent = mByteArray.size
            editByteCount.text = getString(R.string.byteString, mBytesToBeSent)
        })



        stop_btn.setOnClickListener {
            mIsPause = false
            mIsSending = false
            send_status_btn.setImageDrawable(resources.getDrawable(R.drawable.start))
            stopSend()
            group2.visibility = View.VISIBLE
            group3.visibility = View.GONE
        }
        chronometer.onChronometerTickListener = Chronometer.OnChronometerTickListener {
            runOnUiThread {
                try {
                    if((SystemClock.elapsedRealtime() - chronometer.base) != 0L){
                        rateText.text = getString(R.string.rate, mSendFileByte / ((SystemClock.elapsedRealtime() - chronometer.base) / 1000))
                    }
                }catch (e: ArithmeticException){
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setSendInterval(interval: Long) {
        if (mDeviceMode == Constant.BLE_MODE) {
            mBleApi.setSendInterval(address, interval)
        } else {
            mSppApi.setSendInterval(address, interval)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "onActivityResult: ${requestCode}    ${resultCode}" )
        if (requestCode != SELECT_FILE){
            return
        }
        if (resultCode == FILE_PATH || resultCode == FILE_SIZE){
            mResultCode = resultCode
            mData = data
            sendFile()
        }
    }

    private fun sendFile() {
        rateText.text = getString(R.string.rate, 0)
        when (mResultCode) {
            FILE_SIZE -> {
                val size = mData?.getIntExtra("size", 0)!!
                val sizeStr = mData?.getStringExtra("sizeStr")!!
                selected_file_tv.text =
                    getString(R.string.selectedFile, "${sizeStr} ${getString(R.string.testFile)}")
                if (mIsSending) {
                    stopSend()
                }
                sendFile {
                    if (mDeviceMode == Constant.BLE_MODE) {
                        mBleApi.sendFile(size)
                    } else {
                        mSppApi.sendFile(size)
                    }
                }
            }
            FILE_PATH -> {
                mData?.data?.let {
                    selected_file_tv.text = getString(R.string.selectedFile, getFileNameByUri(it))
                    contentResolver.openInputStream(it)?.let { inputStream ->
                        if (mIsSending) {
                            stopSend()
                        }
                        sendFile {
                            if (mDeviceMode == Constant.BLE_MODE) {
                                mBleApi.sendFile(inputStream)
                            } else {
                                mSppApi.sendFile(inputStream)
                            }
                        }
                    }
                }
            }
        }
    }


    private fun getFileNameByUri(uri: Uri): String{
        return when(uri.scheme){
            "content" -> {
                try {
                    contentResolver.query(uri, null, null, null, null)?.use {
                        if (it.moveToFirst()) {
                            it.getString(
                                it.getColumnIndexOrThrow(
                                    MediaStore.Downloads.DISPLAY_NAME
                                )
                            )
                        }else {
                            " "
                        }
                    }?: " "
                } catch (e: Exception) {
                    " "
                }
            }
            "file" -> {
                uri.toFile().name
            }
            else -> " "
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnect()
        handler.removeCallbacks(hexClickRunnable)
    }

    private fun disconnect() {
        if (mDeviceMode == Constant.BLE_MODE) {
            mBleApi.disconnect()
        } else {
            mSppApi.disconnect()
        }
    }

    fun uiDeviceConnected(){
        runOnUiThread {
            toolbarSubtitle.text = resources.getString(R.string.connected)
            sendButton.isEnabled = true
            sendFileButton.isEnabled = true
            send_status_btn.isEnabled = true
            if (mDeviceMode == Constant.BLE_MODE){
                switchServiceButton.isEnabled = true
                sendFileButton.isEnabled = true
            }

        }
    }

    fun uiDeviceDisconnected(){
        runOnUiThread {
            toolbarSubtitle.text = resources.getString(R.string.disconnected)
            sendButton.isEnabled = false
            sendFileButton.isEnabled = false
            send_status_btn.isEnabled = false
            send_status_btn.setImageDrawable(resources.getDrawable(R.drawable.start))
            Log.e(TAG, "uiDeviceDisconnected: false 1"  )
            mIsSending = false
            if (mDeviceMode == Constant.BLE_MODE){
                switchServiceButton.isEnabled = false
            }
            chronometer.stop()
        }
    }

    fun uiReceiveData(strValue: String, hexString: String, data: ByteArray){
        if (mReceiveBuffer.length > 1024) {
            mReceiveBuffer.delete(0, mReceiveBuffer.length)
            mReceiveBuffer.setLength(0)
            mReceiveBufferHex.delete(
                0,
                mReceiveBufferHex.length
            )
            mReceiveBufferHex.setLength(0)
        }

        mReceiveBuffer.append(strValue)
        mReceiveBufferHex.append(hexString)
        mReceiveCRC.update(data)
        runOnUiThread {
            receiveEdit.setText(String(if (hexCheck.isOpened) mReceiveBufferHex else mReceiveBuffer))
            receiveEdit.setSelection(receiveEdit.text.length, receiveEdit.text.length)
            crcReceive.text = getString(R.string.CRC, mReceiveCRC.value.toHexString().uppercase(Locale.ROOT) )
            mReceiveByteCount += data.size
            mReceivePackageCount += 1
            receiveTextView.text = getString(R.string.receive, mReceiveByteCount, mReceivePackageCount)
        }
    }

    @SuppressLint("SetTextI18n")
    fun uiSendDataProgress(progress: Int, byteSize: Int){
        runOnUiThread {
            numberProgressBar.progress = progress
            mSendFileByte += byteSize
            if (progress == 100){
                mIsSending = false
                mIsPause = false
                switchServiceButton.isEnabled = true
                sendFileButton.isEnabled = true
                send_status_btn.setImageDrawable(resources.getDrawable(R.drawable.start))
                rateText.text = getString(R.string.rate,try {
                    mSendFileByte / ((SystemClock.elapsedRealtime() - chronometer.base) / 1000)
                }catch (e: ArithmeticException){
                    0
                })
                chronometer.stop()
            }
        }
    }


    @SuppressLint("SetTextI18n")
    fun uiSendData(data: ByteArray){
        mSendCRC.update(data)
        mSendByteCountSend += data.size
        runOnUiThread {
            crcSend.text = getString(R.string.CRC, mSendCRC.value.toHexString().uppercase(Locale.ROOT))
            mSendPackageCountSend += 1
            sendTextView.text = getString(R.string.send, mSendByteCountSend, mSendPackageCountSend)
        }
    }


    private fun send(){
        if (sendEdit.text.isNotBlank()){
            if (mDeviceMode == Constant.BLE_MODE){
                mBleApi.send(mByteArray)
            }else {
                mSppApi.send(mByteArray)
            }
        }
    }

    private fun sendFile(sendFileFunction: () -> Unit){
        mIsPause = false
        mIsSending = true
        sendFileFunction()
        numberProgressBar.progress = 0
        group2.visibility = View.GONE
        group3.visibility = View.VISIBLE
        switchServiceButton.isEnabled = false
        sendFileButton.isEnabled = false
        send_status_btn.setImageDrawable(resources.getDrawable(R.drawable.pause))
        mSendFileByte = 0
        chronometer.base = SystemClock.elapsedRealtime()
        val hour = ((SystemClock.elapsedRealtime() - chronometer.base) / 1000 / 60)
        chronometer.format = "0${hour}:%s"
        chronometer.start()
    }

    override fun getLayout() = R.layout.activity_throughput

    override fun onClick(v: View?) {
        intervalSendTime.clearFocus()
        when(v?.id){
            R.id.sendButton -> {
                send()
            }
            R.id.sendFileButton -> {
                val intent = Intent(this@ThroughputActivity, SelectFileActivity::class.java)
                startActivityForResult(intent, SELECT_FILE)
            }
            R.id.send_status_btn -> {
                if (mIsPause){
                    continueSend()
                    send_status_btn.setImageDrawable(resources.getDrawable(R.drawable.pause))
                    mIsPause = false
                    mIsSending = true
                }else {
                    if(mIsSending){
                        pauseSend()
                        send_status_btn.setImageDrawable(resources.getDrawable(R.drawable.start))
                        mIsPause = true
                        mIsSending = true
                    }else {
                        sendFile()
                        Log.e(TAG, "onClick: 设置为没有暂停，正在发送文件" )
                        mIsPause = false
                        mIsSending = true
                    }
                }
            }
            R.id.switchServiceButton -> {
                ServiceSelectActivity.activityStart(this, mFscDevice)
            }
            R.id.clearButton -> {
                initUi()
            }
            R.id.intervalSendCheck -> {
                if(sendEdit.text.isBlank()) return
                if(!intervalSendCheck.isOpened) return
                lifecycleScope.launch(Dispatchers.IO){
                    while(intervalSendCheck.isOpened){
                        send()
                        delay(try {
                            intervalSendTime.text.toString().toLong()
                        } catch (e: NumberFormatException) {
                            intervalSendTime.hint.toString().toLong()
                        })
                    }
                }
            }
            R.id.hexCheck -> {
                // 解决重复快速点击导致hexCheck时 发送框内容转换出现错误的bug
                // 不使用定时器，在方法最底部加上hexCheck.isEnabled = true 无效果
                hexCheck.isEnabled = false
                handler.postDelayed(hexClickRunnable, 300)
                val measureTimeMillis = measureTimeMillis {
                    putBoolean("hexCheck",hexCheck.isOpened)
                    if(hexCheck.isOpened){
                        receiveEdit.setText(mReceiveBufferHex)

                        val hexString = mByteArray.toHexString()
                        with(sendEdit){
                            keyListener = hexKeyListener
                            setText(hexString)
                            try {
                                setSelection(hexString.length)
                            }catch (e: IndexOutOfBoundsException){
                                setSelection(hexString.length - 1)
                            }
                        }
                    }else {
                        receiveEdit.setText(mReceiveBuffer)
                        // val string = String(mByteArray)
                        if (mByteArray.isNotEmpty()){
                            val string = mByteArray.getEncoding()
                            runOnUiThread {
                                with(sendEdit){
                                    Log.e(TAG, "onClick: 切换成普通的" )
                                    keyListener = TextKeyListener.getInstance()
                                    setText(string)
                                    try {
                                        setSelection(string.length)
                                    }catch (e: IndexOutOfBoundsException){
                                        setSelection(string.length - 1)
                                    }
                                }
                            }
                        }else {
                            runOnUiThread {
                                with(sendEdit){
                                    Log.e(TAG, "onClick: 切换成普通的" )
                                    keyListener = TextKeyListener.getInstance()
                                }
                            }
                        }
                    }
                    receiveEdit.setSelection(receiveEdit.text.length, receiveEdit.text.length)
                }
                Log.e("ly", "耗时  ${measureTimeMillis}")
            }
        }
    }




    private fun initUi() {
        mReceiveBuffer.delete(0, mReceiveBuffer.length)
        mReceiveBufferHex.delete(0, mReceiveBufferHex.length)
        receiveEdit.text.clear()
        mReceiveByteCount = 0
        mSendFileByte = 0
        mReceivePackageCount = 0
        mSendByteCountSend = 0
        mSendPackageCountSend = 0

        receiveTextView.text = getString(R.string.receive, mReceiveByteCount, mReceivePackageCount)
        sendTextView.text = getString(R.string.send, mSendByteCountSend, mSendPackageCountSend)
        crcSend.text = getString(R.string.CRC, "00000000")
        crcReceive.text = getString(R.string.CRC, "00000000")
        // sendByteText.text = getString(R.string.sendByte, mSendFileByte)
        mSendCRC.reset()
        mReceiveCRC.reset()
    }

    private fun stopSend() {
        switchServiceButton.isEnabled = true
        sendFileButton.isEnabled = true
        if (mDeviceMode == Constant.BLE_MODE) {
            mBleApi.stopSend()
        } else {
            mSppApi.stopSend()
        }
        chronometer.stop()
    }




    private fun pauseSend(){
        switchServiceButton.isEnabled = true
        sendFileButton.isEnabled = true
        if (mDeviceMode == Constant.BLE_MODE) {
            mBleApi.pauseSend(address)
        } else {
            mSppApi.pauseSend(address)
        }
        chronometer.stop()
        mLastStopTime = SystemClock.elapsedRealtime()
    }

    private fun continueSend(){
        switchServiceButton.isEnabled = false
        sendFileButton.isEnabled = false
        if (mDeviceMode == Constant.BLE_MODE) {
            mBleApi.continueSend(address)
        } else {
            mSppApi.continueSend(address)
        }
        val intervalOnPause = SystemClock.elapsedRealtime() - mLastStopTime
        chronometer.base = chronometer.base + intervalOnPause
        chronometer.start()
    }


    companion object{
        private const val TAG = "ThroughputActivity"
        fun activityStart(context: Context, fscDevice: FscDevice){
            val intent = Intent(context, ThroughputActivity::class.java)
            Log.e(TAG, "activityStart: " + fscDevice.scanRecord )
            intent.putExtra("FscDevice", fscDevice)
            context.startActivity(intent)
        }

        fun activityStart(context: Context, device: BluetoothDevice){
            val intent = Intent(context, ThroughputActivity::class.java)
            intent.putExtra("BluetoothDevice", device)
            context.startActivity(intent)
        }
        const val SELECT_FILE = 0x0001
    }
}