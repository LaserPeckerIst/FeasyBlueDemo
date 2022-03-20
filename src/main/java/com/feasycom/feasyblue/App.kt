package com.feasycom.feasyblue

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import com.feasycom.ble.controler.FscBleCentralApi
import com.feasycom.ble.controler.FscBleCentralApiImp
import com.feasycom.feasyblue.dao.DeviceDao
import com.feasycom.feasyblue.dao.DeviceDatabase
import com.feasycom.feasyblue.utils.getStr
import com.feasycom.feasyblue.utils.putStr
import com.feasycom.network.bean.Devices
import com.feasycom.spp.controler.FscSppCentralApi
import com.feasycom.spp.controler.FscSppCentralApiImp
import com.google.gson.Gson
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.logging.Logger

class App: Application() {
    private lateinit var sFscBleApi: FscBleCentralApi
    private lateinit var sFscSppCentralApi: FscSppCentralApi

    private val deviceDao: DeviceDao by lazy {
        DeviceDatabase.getDataBase(applicationContext).deviceDao()
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        CrashReport.initCrashReport(applicationContext, "8557035c67", false)
        MultiDex.install(this)
        sFscBleApi = FscBleCentralApiImp.getInstance(this)
        sFscBleApi.initialize()
        sFscSppCentralApi = FscSppCentralApiImp.getInstance(this)
        sFscSppCentralApi.initialize()
        // sFscBleApi.isShowLog(true)

        val data = getStr("hash", "")
        if (data.isEmpty()){
            val json = assetsToJsonString("Device.json")
            val gson = Gson()
            val devices = gson.fromJson(json, Devices::class.java)
            putStr("hash",devices.data?.hash?: "" )
            MainScope().launch(Dispatchers.IO){
                deviceDao.deleteAllBeacons()
                deviceDao.addDevices(devices.data!!.list)
            }
        }
    }

    private fun Context.assetsToJsonString(fileName: String): String{
        //将json数据变成字符串
        val stringBuilder = StringBuilder()
        try {
            //通过管理器打开文件并读取
            val bf = BufferedReader(
                InputStreamReader(
                    assets.open(fileName)
                )
            )
            var line: String?
            while (bf.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }


    companion object{
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
    }
}