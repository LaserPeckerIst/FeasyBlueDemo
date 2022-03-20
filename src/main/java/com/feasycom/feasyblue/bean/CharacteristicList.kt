package com.feasycom.feasyblue.bean

import android.bluetooth.BluetoothGattCharacteristic

class CharacteristicList(var bluetoothGattCharacteristicList: List<BluetoothGattCharacteristic>) {
    fun getItem(position: Int): BluetoothGattCharacteristic {
        return bluetoothGattCharacteristicList[position]
    }

    fun getCount(): Int {
        return bluetoothGattCharacteristicList.size
    }
}