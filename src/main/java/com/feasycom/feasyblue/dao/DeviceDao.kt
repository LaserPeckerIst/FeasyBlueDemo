package com.feasycom.feasyblue.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.feasycom.network.bean.DeviceInfo

@Dao
interface DeviceDao {

    @Insert
    fun addDevices(beaconItems: List<DeviceInfo>)

    @Query("Select * from DeviceInfo where name = :name")
    fun queryDeviceByName(name: String): DeviceInfo?

    @Query("Select * from DeviceInfo where number = :number")
    fun queryDeviceByNumber(number: Int): DeviceInfo?

    @Query("Select number from DeviceInfo where deviceType = :deviceType")
    fun queryAllBeaconByDeviceType(deviceType: Int): IntArray

    @Query("delete from DeviceInfo")
    fun deleteAllBeacons()


}