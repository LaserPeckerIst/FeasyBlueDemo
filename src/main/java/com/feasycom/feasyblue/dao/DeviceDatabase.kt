package com.feasycom.feasyblue.dao

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.feasycom.network.bean.DeviceInfo

@Database(version = 1, entities = [DeviceInfo::class], exportSchema = false)
abstract class DeviceDatabase: RoomDatabase() {

    abstract fun deviceDao(): DeviceDao

    companion object{
        private var instance: DeviceDatabase? = null


        fun getDataBase(context: Context): DeviceDatabase {
            Log.e("TAG", "getDataBase: " + context.packageName )
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext, DeviceDatabase::class.java, context.packageName+"_app_database" )
                .build()
                .apply { instance = this }
        }
    }

}