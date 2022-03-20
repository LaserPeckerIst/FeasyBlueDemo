package com.feasycom.feasyblue

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class BleConnectTest {

    private lateinit var mUiDevice: UiDevice
    private var mDeviceAddress = "11:11:FF:EE:11:11"

    private var mConnectNumber = 0
    @Before
    fun initView(){
        getUiDevice().apply {
            startApp()
            Log.e(TAG, "initView: 启动" )
            mUiDevice = this
        }
        mConnectNumber = 0
        mUiDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "bleCheck")), mCommonTimeOut).let {
            it.click()
            while (true){
                connect()
                mConnectNumber++
                Log.e(TAG, "第${mConnectNumber}次连接成功")
            }
        }
    }


    @Test
    fun connect(){
        mUiDevice.wait(Until.findObject(By.text(mDeviceAddress)), mConnectTimeOut)?.let {
            it.click()
            mUiDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "toolbarSubtitle"))?.let {
                while ("已连接" != it.text){
                    if ("连接断开" == it.text){
                        Log.e(TAG, "第${mConnectNumber}次连接失败")
                    }else {
                        Thread.sleep(300)
                    }
                }
                mUiDevice.click(75,143)
            }
        }
    }

    private val TAG = "ly"
}