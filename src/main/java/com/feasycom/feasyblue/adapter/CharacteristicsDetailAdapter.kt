package com.feasycom.feasyblue.adapter

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feasycom.ble.controler.FscBleCentralApi
import com.feasycom.ble.controler.FscBleCentralApiImp
import com.feasycom.common.utils.*
import com.feasycom.feasyblue.R
import kotlinx.android.synthetic.main.property_item.view.*

class CharacteristicsDetailAdapter(private val address: String, private val bluetoothGattCharacteristic: BluetoothGattCharacteristic): RecyclerView.Adapter<CharacteristicsDetailAdapter.ViewHolder>(){

    private val TAG = "CharacteristicsDetailAd"
    private var propertyList = mutableListOf<String>()
    private var descriptors: MutableList<BluetoothGattDescriptor> = bluetoothGattCharacteristic.descriptors
    private var mNotificationEnabled = false
    private var mIndicateEnabled = false
    private var mWriteNoResponseEnabled = false
    private var mWriteEnabled = false
    private var mBleApi: FscBleCentralApi = FscBleCentralApiImp.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.property_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val writeCharacteristic = mBleApi.getWriteCharacteristic(address)
        val notifyCharacteristicList = mBleApi.getNotifyCharacteristicList(address)
        if (writeCharacteristic == null){
            return
        }
        holder.itemView.property.text = propertyList[position]
        if (propertyList[position] == "Notify") {

            holder.itemView.check.isChecked = mNotificationEnabled
        }

        if (propertyList[position] == "Indicate") {
            holder.itemView.check.isChecked = mIndicateEnabled
        }
        if (writeCharacteristic.uuid == bluetoothGattCharacteristic.uuid){
            if (propertyList[position] == "Write") {
                holder.itemView.check.isChecked = mWriteEnabled
            }

            if (propertyList[position] == "Write Without Response") {
                holder.itemView.check.isChecked = mWriteNoResponseEnabled
            }
        }

        holder.itemView.setOnClickListener {
            val propertiesString: String = propertyList[position]
            val properties: Int = bluetoothGattCharacteristic.properties

            when(propertiesString) {
                "Notify" -> {
                    mNotificationEnabled = if (mNotificationEnabled) {
                        if (notifyCharacteristicList.size > 1){
                            mBleApi.setCharacteristic(address, bluetoothGattCharacteristic, Constant.DISABLE_CHARACTERISTIC_NOTIFICATION)
                            false
                        }else true
                    } else {
                        mBleApi.setCharacteristic(address, bluetoothGattCharacteristic, Constant.ENABLE_CHARACTERISTIC_NOTIFICATION)
                        true
                    }
                }
                "Indicate" -> {
                    mBleApi.setCharacteristic(address, bluetoothGattCharacteristic, if (mIndicateEnabled){
                        mIndicateEnabled = false
                        Constant.DISABLE_CHARACTERISTIC_INDICATE
                    }else {
                        mIndicateEnabled = true
                        mNotificationEnabled = false
                        Constant.ENABLE_CHARACTERISTIC_INDICATE
                    })
                }
                "Write" -> {
                    if (properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE == 0 && mWriteEnabled) {
                        return@setOnClickListener
                    }
                    mBleApi.setCharacteristic(address, bluetoothGattCharacteristic, if (mWriteEnabled) {
                        mWriteEnabled = false
                        mWriteNoResponseEnabled = true
                        Constant.CHARACTERISTIC_WRITE_NO_RESPONSE
                    }else {
                        mWriteEnabled = true
                        mWriteNoResponseEnabled = false
                        Constant.CHARACTERISTIC_WRITE
                    })
                }
                "Write Without Response" -> {
                    if (properties and BluetoothGattCharacteristic.PROPERTY_WRITE == 0 && mWriteNoResponseEnabled) {
                        return@setOnClickListener
                    }
                    mBleApi.setCharacteristic(address, bluetoothGattCharacteristic, if (mWriteNoResponseEnabled){
                        mWriteNoResponseEnabled = false
                        mWriteEnabled = true
                        Constant.CHARACTERISTIC_WRITE
                    }else {
                        mWriteNoResponseEnabled = true
                        mWriteEnabled = false
                        Constant.CHARACTERISTIC_WRITE_NO_RESPONSE
                    })
                }
                "Read" -> {
                    mBleApi.read(address, bluetoothGattCharacteristic)
                }
            }
            notifyDataSetChanged()
        }

    }

    override fun getItemCount() = propertyList.size


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    init {
        val writeCharacteristic = mBleApi.getWriteCharacteristic(address)
        val properties = bluetoothGattCharacteristic.properties
        if (properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE != 0) {
            propertyList.add("Write Without Response")
        }
        if (properties and BluetoothGattCharacteristic.PROPERTY_WRITE != 0) {
            propertyList.add("Write")
        }

        if (properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0) {
            propertyList.add("Notify")
        }

        if (properties and BluetoothGattCharacteristic.PROPERTY_INDICATE != 0) {
            propertyList.add("Indicate")
        }
        if (properties and BluetoothGattCharacteristic.PROPERTY_READ != 0) {
            propertyList.add("Read")
        }

        if (descriptors.size > 0) {
            for (descriptor in descriptors) {
                if (BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE.contentEquals(descriptor.value)) {
                    mNotificationEnabled = true
                    mIndicateEnabled = false
                } else if (BluetoothGattDescriptor.ENABLE_INDICATION_VALUE.contentEquals(descriptor.value)) {
                    mNotificationEnabled = false
                    mIndicateEnabled = true
                }
            }
        }
        if (bluetoothGattCharacteristic.uuid == writeCharacteristic.uuid){
            if (bluetoothGattCharacteristic.writeType == BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT) {
                mWriteEnabled = true
                mWriteNoResponseEnabled = false
            } else if (bluetoothGattCharacteristic.writeType == BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE) {
                mWriteEnabled = false
                mWriteNoResponseEnabled = true
            }
        }
    }
}